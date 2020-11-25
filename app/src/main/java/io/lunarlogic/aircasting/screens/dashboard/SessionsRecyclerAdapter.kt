package io.lunarlogic.aircasting.screens.dashboard

import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.models.SensorThreshold
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.dashboard.charts.ChartData


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

class SessionPresenterFactory {
    private var mSessionUUIDS: List<String> = emptyList()
    private var mSessionPresenters: HashMap<String, SessionPresenter> = hashMapOf()
    private var mSensorThresholds: HashMap<String, SensorThreshold> = hashMapOf()

    fun bindSensorThresholds(sensorThresholds: HashMap<String, SensorThreshold>) {
        mSensorThresholds = sensorThresholds
    }

    fun getOrBuild(dbSession: SessionWithStreamsDBObject): SessionPresenter {
        val session = Session(dbSession)

        var sessionPresenter: SessionPresenter?

        if (mSessionPresenters.containsKey(session.uuid)) {
            sessionPresenter = mSessionPresenters[session.uuid]
            sessionPresenter!!.session = session
            sessionPresenter!!.chartData = ChartData(session)
        } else {
            sessionPresenter = SessionPresenter(session, mSensorThresholds)
            mSessionPresenters[session.uuid] = sessionPresenter
        }

        return sessionPresenter
    }
}

abstract class SessionsRecyclerAdapter<ListenerType>:
    PagingDataAdapter<SessionWithStreamsDBObject, SessionsRecyclerAdapter<ListenerType>.MyViewHolder>(SessionsComparator()) {
    val mSessionPresenterFactory = SessionPresenterFactory()

    inner class MyViewHolder(private val mViewMvc: SessionViewMvc<ListenerType>):
        RecyclerView.ViewHolder(mViewMvc.rootView!!) {
        val view: SessionViewMvc<ListenerType> get() = mViewMvc
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dbSession = getItem(position) ?: return
        val sessionPresenter = mSessionPresenterFactory.getOrBuild(dbSession)
        holder.view.bindSession(sessionPresenter)
    }

    suspend fun bindSessions(dbSessions: PagingData<SessionWithStreamsDBObject>, sensorThresholds: HashMap<String, SensorThreshold>) {
        mSessionPresenterFactory.bindSensorThresholds(sensorThresholds)
        submitData(dbSessions)
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
// //    }

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
