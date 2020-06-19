package io.lunarlogic.aircasting.screens.dashboard

import androidx.lifecycle.LiveData
import io.lunarlogic.aircasting.events.NewMeasurementEvent
import io.lunarlogic.aircasting.screens.common.ObservableViewMvc
import io.lunarlogic.aircasting.sensor.Session
import java.util.*


interface MobileActiveViewMvc : ObservableViewMvc<MobileActiveViewMvc.Listener> {

    interface Listener {
        fun onRecordNewSessionClicked()
        fun onStopSessionClicked(sessionUUID: String)
        fun onSwipeToRefreshTriggered(callback: () -> Unit)
    }

    fun showSessionsView(sessions: List<Session>)
    fun showEmptyView()
}