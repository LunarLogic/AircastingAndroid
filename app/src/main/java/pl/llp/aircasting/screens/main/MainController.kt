package pl.llp.aircasting.screens.main

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import pl.llp.aircasting.events.DisconnectExternalSensorsEvent
import pl.llp.aircasting.events.LocationPermissionsResultEvent
import pl.llp.aircasting.exceptions.ErrorHandler
import pl.llp.aircasting.lib.ResultCodes
import pl.llp.aircasting.lib.Settings
import pl.llp.aircasting.networking.services.ApiService
import pl.llp.aircasting.networking.services.ApiServiceFactory
import pl.llp.aircasting.networking.services.ConnectivityManager
import pl.llp.aircasting.networking.services.SessionsSyncService
import pl.llp.aircasting.screens.new_session.LoginActivity
import pl.llp.aircasting.screens.onboarding.OnboardingActivity
import pl.llp.aircasting.sensor.SessionManager
import org.greenrobot.eventbus.EventBus

class MainController(
    private val rootActivity: AppCompatActivity,
    private val mViewMvc: MainViewMvc,
    private val mSettings: Settings,
    private val mApiServiceFactory: ApiServiceFactory
) {
    private var mSessionManager: SessionManager? = null
    private var mConnectivityManager: ConnectivityManager? = null
    private val mErrorHandler = ErrorHandler(rootActivity)

    fun onCreate() {
        if (!mSettings.onboardingDisplayed() && mSettings.getAuthToken() == null) {
            showOnboardingScreen()
        } else if (mSettings.getAuthToken() == null) {
            showLoginScreen()
        } else {
            setupDashboard()
        }

        mSessionManager?.onStart()
    }

    fun onDestroy() {
        unregisterConnectivityManager()
        mSessionManager?.onStop()
        EventBus.getDefault().post(DisconnectExternalSensorsEvent())
    }

    private fun showLoginScreen() {
        LoginActivity.start(rootActivity)
        rootActivity.finish()
    }

    private fun showOnboardingScreen() {
        OnboardingActivity.start(rootActivity)
        rootActivity.finish()
    }

    private fun setupDashboard() {
        mErrorHandler.registerUser(mSettings.getEmail())

        val apiService =  mApiServiceFactory.get(mSettings.getAuthToken()!!)
        mSessionManager = SessionManager(rootActivity, apiService, mSettings)

        mConnectivityManager = ConnectivityManager(apiService, rootActivity, mSettings)
        registerConnectivityManager()

        sync(apiService)
    }

    private fun sync(apiService: ApiService) {
        val mMobileSessionsSyncService = SessionsSyncService.get(apiService, mErrorHandler, mSettings)

        mMobileSessionsSyncService.sync(
            onStartCallback = { mViewMvc.showLoader() },
            finallyCallback = { mViewMvc.hideLoader() }
        )
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            ResultCodes.AIRCASTING_PERMISSIONS_REQUEST_LOCATION -> {
                EventBus.getDefault().post(LocationPermissionsResultEvent(grantResults))
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun registerConnectivityManager() {
        val filter = IntentFilter(ConnectivityManager.ACTION)
        mConnectivityManager?.let { rootActivity.registerReceiver(it, filter) }
    }

    private fun unregisterConnectivityManager() {
        mConnectivityManager?.let { rootActivity.unregisterReceiver(it) }
    }
}
