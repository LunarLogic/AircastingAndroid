package io.lunarlogic.aircasting.screens.dashboard

import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.models.Session


class SessionsComparator: DiffUtil.ItemCallback<SessionWithStreamsDBObject>() {
    override fun areItemsTheSame(
        oldItem: SessionWithStreamsDBObject,
        newItem: SessionWithStreamsDBObject
    ): Boolean {
        return oldItem.session.id == newItem.session.id && oldItem.streams.size == newItem.streams.size
    }

    override fun areContentsTheSame(
        oldItem: SessionWithStreamsDBObject,
        newItem: SessionWithStreamsDBObject
    ): Boolean {
        return !Session(newItem).hasChangedFrom(Session(oldItem))
    }
}

abstract class SessionsRecyclerAdapter<ListenerType>:
    PagingDataAdapter<SessionWithStreamsDBObject, SessionsRecyclerAdapter<ListenerType>.MyViewHolder>(SessionsComparator()) {

    inner class MyViewHolder(private val mViewMvc: SessionViewMvc<ListenerType>):
        RecyclerView.ViewHolder(mViewMvc.rootView!!) {
        val view: SessionViewMvc<ListenerType> get() = mViewMvc
    }

    private var mSessionUUIDS: List<String> = emptyList()
    private var mSessionPresenters: HashMap<String, SessionPresenter> = hashMapOf()


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        val dbSessionWithStreams = item ?: return

//        val sessionPresenter = mSessionPresenters[dbSessionWithStreams.session.uuid]
        val session = Session(dbSessionWithStreams)
        val sessionPresenter = SessionPresenter(session, HashMap())
        sessionPresenter?.let {
            holder.view.bindSession(sessionPresenter)
        }
    }

    suspend fun bindData(data: PagingData<SessionWithStreamsDBObject>) {
        submitData(data)
    }

//    private fun removeObsoleteSessions() {
//        mSessionPresenters.keys
//            .filter { uuid -> !mSessionUUIDS.contains(uuid) }
//            .forEach { uuid -> mSessionPresenters.remove(uuid) }
//    }

//    fun bindSessions(dbSessions: PagingData<SessionWithStreamsDBObject>, sensorThresholds: HashMap<String, SensorThreshold>) {
//        submitData(dbSessions)
//
//        val sessions = dbSessions.map { Session(it) }
//        mSessionUUIDS = sessions.map { session -> session.uuid }
//        removeObsoleteSessions()
//
//        sessions.forEach { session ->
//            if (mSessionPresenters.containsKey(session.uuid)) {
//                val sessionPresenter = mSessionPresenters[session.uuid]
//                sessionPresenter!!.session = session
//                sessionPresenter!!.chartData = ChartData(session)
//            } else {
//                val sessionPresenter = SessionPresenter(session, sensorThresholds)
//                mSessionPresenters[session.uuid] = sessionPresenter
//            }
//        }

//        notifyDataSetChanged()
//    }

    fun showLoaderFor(session: Session) {
//        val sessionPresenter = mSessionPresenters[session.uuid]
//        sessionPresenter?.loading = true
//
////        notifyDataSetChanged()
    }

    fun hideLoaderFor(session: Session) {
//        val sessionPresenter = mSessionPresenters[session.uuid]
//        sessionPresenter?.loading = false
//
////        notifyDataSetChanged()
    }
}
