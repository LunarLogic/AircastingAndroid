package io.lunarlogic.aircasting.screens.lets_start

import android.content.Context
import android.widget.Toast
import io.lunarlogic.aircasting.R
import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import io.lunarlogic.aircasting.screens.new_session.NewSessionActivity
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.networking.services.ConnectivityManager
import io.lunarlogic.aircasting.sensor.AirBeamSyncService
import io.lunarlogic.aircasting.sensor.airbeam3.AirBeam3Configurator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class LetsStartController(
    private val mRootActivity: FragmentActivity?,
    private val mViewMvc: LetsStartViewMvc,
    private val mContext: Context?,
    private val mAirBeamSyncService: AirBeamSyncService
): LetsStartViewMvc.Listener {
    private var syncProgressDialog: AlertDialog? = null // TOOD: remove it after implementing proper sync

    fun onCreate() {
        mViewMvc.registerListener(this)
        EventBus.getDefault().register(this) // TOOD: remove it after implementing proper sync
    }

    fun onDestroy() {
        mViewMvc.unregisterListener(this)
        EventBus.getDefault().unregister(this) // TOOD: remove it after implementing proper sync
    }

    override fun onFixedSessionSelected() {
        if (!ConnectivityManager.isConnected(mContext)) {
            Toast.makeText(mContext, mContext?.getString(R.string.fixed_session_no_internet_connection), Toast.LENGTH_LONG).show()
            return
        }

        NewSessionActivity.start(mRootActivity, Session.Type.FIXED)
    }

    override fun onMobileSessionSelected() {
        NewSessionActivity.start(mRootActivity, Session.Type.MOBILE)
    }

    override fun onSyncSelected() {
        mAirBeamSyncService.run()
        syncProgressDialog = AlertDialog.Builder(mRootActivity).setMessage("Sync started").show()
    }

    // TOOD: remove this method after implementing proper sync
    @Subscribe
    fun onMessageEvent(event: AirBeam3Configurator.SyncEvent) {
        syncProgressDialog?.setMessage(event.message)
    }

    // TOOD: remove this method after implementing proper sync
    @Subscribe
    fun onMessageEvent(event: AirBeam3Configurator.SyncFinishedEvent) {
        syncProgressDialog?.cancel()
        syncProgressDialog = AlertDialog.Builder(mRootActivity).setMessage(event.message).show()
    }

    override fun onClearSDCardSelected() {
        mAirBeamSyncService.run(true)
        syncProgressDialog = AlertDialog.Builder(mRootActivity).setMessage("Clear SD card started").show()
    }

    override fun onMoreInfoClicked() {
        mViewMvc.showMoreInfoDialog()
    }
}
