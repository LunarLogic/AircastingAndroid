package io.lunarlogic.aircasting.screens.session_view

import androidx.appcompat.app.AppCompatActivity
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.repositories.SessionsRepository
import io.lunarlogic.aircasting.events.NewMeasurementEvent
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.lib.safeRegister
import io.lunarlogic.aircasting.location.LocationHelper
import io.lunarlogic.aircasting.models.Measurement
import io.lunarlogic.aircasting.models.SensorThreshold
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.models.observers.SessionObserver
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.networking.services.SessionDownloadService
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.screens.session_view.hlu.HLUValidationErrorToast
import kotlinx.coroutines.CoroutineScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class SessionDetailsViewController(
    protected val rootActivity: AppCompatActivity,
    protected val mSessionsViewModel: SessionsViewModel,
    protected var mViewMvc: SessionDetailsViewMvc?,
    sessionUUID: String,
    private var sensorName: String?,
    private val mSettings: Settings,
    mApiServiceFactory: ApiServiceFactory
): SessionDetailsViewMvc.Listener {
    private var mSessionPresenter = SessionPresenter(sessionUUID, sensorName)
    private val mSessionObserver = SessionObserver(rootActivity, mSessionsViewModel, mSessionPresenter, this::onSessionChanged)

    protected val mErrorHandler = ErrorHandler(rootActivity)
    private val mApiService =  mApiServiceFactory.get(mSettings.getAuthToken()!!)
    protected val mDownloadService = SessionDownloadService(mApiService, mErrorHandler)
    protected val mSessionRepository = SessionsRepository()

    fun onCreate() {
        EventBus.getDefault().safeRegister(this);
        mViewMvc?.registerListener(this)

        mSessionObserver.observe()
    }

    private fun onSessionChanged(coroutineScope: CoroutineScope) {
        DatabaseProvider.backToUIThread(coroutineScope) {
            mViewMvc?.bindSession(mSessionPresenter)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMeasurementEvent) {
        if (event.sensorName == mSessionPresenter?.selectedStream?.sensorName) {
            val location = LocationHelper.lastLocation()
            val measurement = Measurement(event, location?.latitude , location?.longitude)

            mViewMvc?.addMeasurement(measurement)
        }
    }

    override fun onSensorThresholdChanged(sensorThreshold: SensorThreshold) {
        DatabaseProvider.runQuery {
            mSessionsViewModel.updateSensorThreshold(sensorThreshold)
        }
    }

    override fun onHLUDialogValidationFailed() {
        HLUValidationErrorToast.show(rootActivity)
    }

    fun onDestroy() {
        EventBus.getDefault().unregister(this);
        mViewMvc?.unregisterListener(this)
        mViewMvc = null
    }
}
