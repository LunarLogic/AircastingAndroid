package io.lunarlogic.aircasting.screens.dashboard.mobile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsAndMeasurementsDBObject
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.lib.NavigationController
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.models.ActiveSessionsObserver
import io.lunarlogic.aircasting.screens.dashboard.DashboardPagerAdapter
import io.lunarlogic.aircasting.screens.dashboard.SessionsController
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.screens.dashboard.SessionsViewMvc
import io.lunarlogic.aircasting.models.Session
import org.greenrobot.eventbus.EventBus

class MobileActiveController(
    mRootActivity: FragmentActivity?,
    mViewMvc: SessionsViewMvc,
    private val mSessionsViewModel: SessionsViewModel,
    mLifecycleOwner: LifecycleOwner,
    mSettings: Settings
): SessionsController(mRootActivity, mViewMvc, mSessionsViewModel, mLifecycleOwner, mSettings),
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

        val tabId = DashboardPagerAdapter.tabIndexForSessionType(Session.Type.MOBILE, Session.Status.FINISHED)
        NavigationController.goToDashboard(tabId)
    }

    override fun onDeleteSessionClicked(sessionUUID: String) {
        // do nothing
    }
}
