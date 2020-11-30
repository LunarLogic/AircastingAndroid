package io.lunarlogic.aircasting.screens.dashboard.mobile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import io.lunarlogic.aircasting.database.data_classes.SessionDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsShallowDBObject
import io.lunarlogic.aircasting.events.DeleteSessionEvent
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.screens.dashboard.SessionsController
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.screens.dashboard.SessionsViewMvc
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import org.greenrobot.eventbus.EventBus

class MobileDormantController(
    private val mRootActivity: FragmentActivity?,
    private val mViewMvc: SessionsViewMvc,
    private val mSessionsViewModel: SessionsViewModel,
    private val mLifecycleOwner: LifecycleOwner,
    mSettings: Settings
): SessionsController(mRootActivity, mViewMvc, mSessionsViewModel, mLifecycleOwner, mSettings),
    SessionsViewMvc.Listener {

    init {
        mSessionsLiveData = loadSessions()
    }

    override fun loadSessions(): LiveData<PagedList<SessionPresenter>> {
        return mSessionsViewModel.loadMobileDormantSessionsWithMeasurements()
    }

    override fun onRecordNewSessionClicked() {
        startNewSession(Session.Type.MOBILE)
    }

    override fun onDeleteSessionClicked(sessionUUID: String) {
        val event = DeleteSessionEvent(sessionUUID)
        EventBus.getDefault().post(event)
    }

    override fun onStopSessionClicked(sessionUUID: String) {
        // do nothing
    }
}
