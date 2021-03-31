package io.lunarlogic.aircasting.screens.session_view

import androidx.appcompat.app.AppCompatActivity
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.events.NewMeasurementEvent
import io.lunarlogic.aircasting.lib.safeRegister
import io.lunarlogic.aircasting.location.LocationHelper
import io.lunarlogic.aircasting.models.*
import io.lunarlogic.aircasting.screens.session_view.hlu.HLUValidationErrorToast
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class SessionDetailsViewController(
    protected val rootActivity: AppCompatActivity,
    protected val mSessionsViewModel: SessionsViewModel,
    protected var mViewMvc: SessionDetailsViewMvc?,
    sessionUUID: String,
    private var sensorName: String?
): SessionDetailsViewMvc.Listener {
    private var mSessionPresenter = SessionPresenter(sessionUUID, sensorName)

    fun onCreate() {
        EventBus.getDefault().safeRegister(this);
        mViewMvc?.registerListener(this)
        reloadSession()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NewMeasurementEvent) {
        reloadSession()

        if (event.sensorName == mSessionPresenter?.selectedStream?.sensorName) {
            val location = LocationHelper.lastLocation()
            val measurement = Measurement(event, location?.latitude , location?.longitude)

            mViewMvc?.addMeasurement(mSessionPresenter, measurement)
        }
    }

    private fun reloadSession() {
        val sessionUUID = mSessionPresenter?.sessionUUID ?: return

        DatabaseProvider.runQuery { coroutineScope ->
            val dbSession = mSessionsViewModel.reloadSession(sessionUUID)
            dbSession?.let {
                val session = Session(dbSession)
                mSessionPresenter?.session = session

                var selectedSensorName = mSessionPresenter.initialSensorName
                if (mSessionPresenter.selectedStream != null) {
                    selectedSensorName = mSessionPresenter.selectedStream!!.sensorName
                }

                val measurementStream =
                    session.streams.firstOrNull { it.sensorName == selectedSensorName }
                mSessionPresenter.selectedStream = measurementStream

                val sensorThresholds = mSessionsViewModel.findOrCreateSensorThresholds(session)
                mSessionPresenter.setSensorThresholds(sensorThresholds)
            }
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
