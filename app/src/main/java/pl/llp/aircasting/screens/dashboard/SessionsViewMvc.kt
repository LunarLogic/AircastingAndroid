package pl.llp.aircasting.screens.dashboard

import pl.llp.aircasting.models.SensorThreshold
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.screens.common.ObservableViewMvc
import pl.llp.aircasting.screens.dashboard.active.FinishSessionListener


interface SessionsViewMvc : ObservableViewMvc<SessionsViewMvc.Listener> {

    interface Listener : FinishSessionListener {
        fun onRecordNewSessionClicked()
        fun onSwipeToRefreshTriggered()
        fun onDisconnectSessionClicked(session: Session)
        fun addNoteClicked(session: Session)
        fun onReconnectSessionClicked(session: Session)
        fun onEditSessionClicked(session: Session)
        fun onShareSessionClicked(session: Session)
        fun onDeleteSessionClicked(session: Session)
        fun onFollowButtonClicked(session: Session)
        fun onUnfollowButtonClicked(session: Session)
        fun onMapButtonClicked(session: Session, sensorName: String?)
        fun onGraphButtonClicked(session: Session, sensorName: String?)
        fun onExpandSessionCard(session: Session)
        fun onDidYouKnowBoxClicked()

    }

    fun showSessionsView(sessions: List<Session>, sensorThresholds: HashMap<String, SensorThreshold>)
    fun showEmptyView()
    fun showLoaderFor(session: Session)
    fun hideLoaderFor(session: Session)
    fun hideLoaderFor(deviceId: String)
    fun reloadSession(session: Session)
    fun showReconnectingLoaderFor(session: Session)
    fun hideReconnectingLoaderFor(session: Session)
    fun showLoader()
    fun hideLoader()
}
