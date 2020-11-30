package io.lunarlogic.aircasting.screens.dashboard

import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.lunarlogic.aircasting.database.data_classes.SessionDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsShallowDBObject
import io.lunarlogic.aircasting.screens.dashboard.charts.ChartData
import io.lunarlogic.aircasting.models.SensorThreshold
import io.lunarlogic.aircasting.models.Session


class DiffCallback: DiffUtil.ItemCallback<SessionPresenter>() {
    override fun areItemsTheSame(
        oldItem: SessionPresenter,
        newItem: SessionPresenter
    ): Boolean {
        return oldItem.session?.uuid == newItem.session?.uuid
    }

    override fun areContentsTheSame(
        oldItem: SessionPresenter,
        newItem: SessionPresenter
    ): Boolean {
        return oldItem.session?.uuid == newItem.session?.uuid
    }
}

abstract class SessionsRecyclerAdapter<ListenerType>(
    private val mInflater: LayoutInflater,
    protected val supportFragmentManager: FragmentManager
): PagedListAdapter<SessionPresenter, SessionsRecyclerAdapter<ListenerType>.MyViewHolder>(DiffCallback()) {

    inner class MyViewHolder(private val mViewMvc: SessionViewMvc<ListenerType>) :
        RecyclerView.ViewHolder(mViewMvc.rootView!!) {
        val view: SessionViewMvc<ListenerType> get() = mViewMvc
    }

    private var mSessionUUIDS: List<String> = emptyList()
    private var mSessionPresenters: HashMap<String, SessionPresenter> = hashMapOf()

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sessionPresenter = getItem(position) ?: return

        holder.view.bindSession(sessionPresenter)
    }

    private fun removeObsoleteSessions() {
        mSessionPresenters.keys
            .filter { uuid -> !mSessionUUIDS.contains(uuid) }
            .forEach { uuid -> mSessionPresenters.remove(uuid) }
    }

    fun bindSessions(dbSessions: PagedList<SessionPresenter>, sensorThresholds: HashMap<String, SensorThreshold>) {


//        val sessions = dbSessions.map { Session(it) }
//        mSessionUUIDS = sessions.map { session -> session.uuid }
//        removeObsoleteSessions()
//
//        sessions.forEach { session ->
//            if (mSessionPresenters.containsKey(session.uuid)) {
//                val sessionPresenter = mSessionPresenters[session.uuid]
//                sessionPresenter!!.session = session
//                sessionPresenter!!.chartData?.refresh(session)
//            } else {
//                val sessionPresenter = SessionPresenter(session, sensorThresholds)
//                mSessionPresenters[session.uuid] = sessionPresenter
//            }
//        }

        submitList(dbSessions)
    }

    fun showLoaderFor(session: Session) {
        val sessionPresenter = mSessionPresenters[session.uuid]
        sessionPresenter?.loading = true

        notifyDataSetChanged()
    }

    fun hideLoaderFor(session: Session) {
        val sessionPresenter = mSessionPresenters[session.uuid]
        sessionPresenter?.loading = false

        notifyDataSetChanged()
    }
}
