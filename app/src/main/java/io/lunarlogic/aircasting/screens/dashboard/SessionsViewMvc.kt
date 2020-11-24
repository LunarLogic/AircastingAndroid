package io.lunarlogic.aircasting.screens.dashboard

import androidx.paging.PagedList
import androidx.paging.PagingData
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.screens.common.ObservableViewMvc
import io.lunarlogic.aircasting.models.SensorThreshold
import io.lunarlogic.aircasting.models.Session


interface SessionsViewMvc : ObservableViewMvc<SessionsViewMvc.Listener> {

    interface Listener {
        fun onRecordNewSessionClicked()
        fun onSwipeToRefreshTriggered()
        fun onStopSessionClicked(sessionUUID: String)
        fun onDeleteSessionClicked(sessionUUID: String)
        fun onFollowButtonClicked(session: Session)
        fun onUnfollowButtonClicked(session: Session)
        fun onMapButtonClicked(session: Session, sensorName: String?)
        fun onGraphButtonClicked(session: Session, sensorName: String?)
        fun onExpandSessionCard(session: Session)
    }

    suspend fun bindSessions(data: PagingData<SessionWithStreamsDBObject>, sensorThresholds: HashMap<String, SensorThreshold>)
    fun showSessionsView(sensorThresholds: HashMap<String, SensorThreshold>)
    fun showEmptyView()
    fun showLoaderFor(session: Session)
    fun hideLoaderFor(session: Session)
    fun showLoader()
    fun hideLoader()
}
