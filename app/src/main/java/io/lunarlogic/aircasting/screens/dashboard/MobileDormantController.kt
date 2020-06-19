package io.lunarlogic.aircasting.screens.dashboard

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.lunarlogic.aircasting.events.DeleteSessionEvent
import io.lunarlogic.aircasting.screens.new_session.NewSessionActivity
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.networking.services.SyncService
import io.lunarlogic.aircasting.sensor.Session
import org.greenrobot.eventbus.EventBus

class MobileDormantController(
    private val mContext: Context?,
    private val mViewMvc: MobileDormantViewMvc,
    private val mSessionsViewModel: SessionsViewModel,
    private val mLifecycleOwner: LifecycleOwner
) : MobileDormantViewMvc.Listener {
    private val mSettings = Settings(mContext!!)
    private val mErrorHandler = ErrorHandler(mContext!!)
    private val mApiService =  ApiServiceFactory.get(mSettings.getAuthToken()!!)
    private val mSessionSyncService = SyncService(mApiService, mErrorHandler)

    fun onCreate() {
        mSessionsViewModel.loadDormantSessionsWithMeasurements().observe(mLifecycleOwner, Observer { sessions ->
            if (sessions.size > 0) {
                mViewMvc.showSessionsView(sessions.map { session ->
                    Session(session)
                })
            } else {
                mViewMvc.showEmptyView()
            }
        })

        mViewMvc.registerListener(this)
    }

    fun onDestroy() {
        mViewMvc.unregisterListener(this)
    }

    override fun onRecordNewSessionClicked() {
        NewSessionActivity.start(mContext)
    }
    
    override fun onDeleteSessionClicked(sessionUUID: String) {
        val event = DeleteSessionEvent(sessionUUID)
        EventBus.getDefault().post(event)
    }

    override fun onSwipeToRefreshTriggered(callback: () -> Unit) {
        mSessionSyncService.sync(callback)
    }
}