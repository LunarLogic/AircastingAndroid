package io.lunarlogic.aircasting.screens.dashboard.active

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import io.lunarlogic.aircasting.bluetooth.BluetoothManager
import io.lunarlogic.aircasting.events.DisconnectExternalSensorsEvent
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.exceptions.BLENotSupported
import io.lunarlogic.aircasting.lib.NavigationController
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.models.observers.ActiveSessionsObserver
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.screens.dashboard.DashboardPagerAdapter
import io.lunarlogic.aircasting.screens.dashboard.SessionsController
import io.lunarlogic.aircasting.screens.dashboard.SessionsViewMvc
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem
import io.lunarlogic.aircasting.sensor.AirBeamConnector
import io.lunarlogic.aircasting.sensor.AirBeamConnectorFactory
import io.lunarlogic.aircasting.sensor.AirBeamReconnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class MobileActiveController(
    mRootActivity: FragmentActivity?,
    private val mViewMvc: SessionsViewMvc,
    private val mSessionsViewModel: SessionsViewModel,
    mLifecycleOwner: LifecycleOwner,
    mSettings: Settings,
    mApiServiceFactory: ApiServiceFactory,
    private val airBeamReconnector: AirBeamReconnector
): SessionsController(mRootActivity, mViewMvc, mSessionsViewModel, mSettings, mApiServiceFactory),
    SessionsViewMvc.Listener {

    private var mSessionsObserver = ActiveSessionsObserver(mLifecycleOwner, mSessionsViewModel, mViewMvc)

    override fun registerSessionsObserver() {
        mSessionsObserver.observe(mSessionsViewModel.loadMobileActiveSessionsWithMeasurements())
    }

    override fun unregisterSessionsObserver() {
        mSessionsObserver.stop()
    }

    override fun onRecordNewSessionClicked() {
        startNewSession(Session.Type.MOBILE)
    }

    override fun onStopSessionClicked(sessionUUID: String) {
        val event = StopRecordingEvent(sessionUUID)
        EventBus.getDefault().post(event)

        val tabId = DashboardPagerAdapter.tabIndexForSessionType(
            Session.Type.MOBILE,
            Session.Status.FINISHED
        )
        NavigationController.goToDashboard(tabId)
    }

    override fun onEditSessionClicked(session: Session) {
        // do nothing
    }

    override fun onDeleteSessionClicked(sessionUUID: String) {
        // do nothing
    }

    override fun onExpandSessionCard(session: Session) {
        // do nothing
    }

    override fun onDisconnectSessionClicked(session: Session) {
        airBeamReconnector.disconnect(session)
    }

    override fun onReconnectSessionClicked(session: Session) {
        mViewMvc.showReconnectingLoaderFor(session)
        airBeamReconnector.reconnect(session, {
            GlobalScope.launch(Dispatchers.Main) {
                mViewMvc.hideReconnectingLoaderFor(session)
            }
        })
    }
}
