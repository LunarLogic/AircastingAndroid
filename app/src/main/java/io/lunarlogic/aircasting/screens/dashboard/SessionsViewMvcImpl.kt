package io.lunarlogic.aircasting.screens.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedList
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import io.lunarlogic.aircasting.models.MeasurementStream
import io.lunarlogic.aircasting.models.SensorThreshold
import io.lunarlogic.aircasting.models.Session

abstract class SessionsViewMvcImpl<ListenerType>: BaseObservableViewMvc<SessionsViewMvc.Listener>, SessionsViewMvc {
    private var mRecordSessionButton: Button? = null

    private var mRecyclerSessions: RecyclerView? = null
    private var mEmptyView: View? = null
    private val mAdapter: SessionsRecyclerAdapter<ListenerType>
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager
    ): super() {
        this.rootView = inflater.inflate(R.layout.fragment_sessions_tab, parent, false)

        mEmptyView = rootView?.findViewById(R.id.empty_dashboard)
        mRecordSessionButton = rootView?.findViewById(R.id.dashboard_record_new_session_button)
        mRecordSessionButton?.setOnClickListener {
            onRecordNewSessionClicked()
        }

        mRecyclerSessions = findViewById(R.id.recycler_sessions)
        mRecyclerSessions?.setLayoutManager(LinearLayoutManager(rootView!!.context))
        mRecyclerSessions?.itemAnimator = null
        mAdapter = buildAdapter(inflater, supportFragmentManager)
        mRecyclerSessions?.setAdapter(mAdapter)

        setupSwipeToRefreshLayout()
    }

    abstract fun buildAdapter(
        inflater: LayoutInflater,
        supportFragmentManager: FragmentManager
    ): SessionsRecyclerAdapter<ListenerType>

    private fun onRecordNewSessionClicked() {
        for (listener in listeners) {
            listener.onRecordNewSessionClicked()
        }
    }

    private fun onSwipeToRefreshTriggered() {
        for (listener in listeners) {
            listener.onSwipeToRefreshTriggered()
        }
    }

    override suspend fun bindSessions(data: PagingData<SessionWithStreamsDBObject>, sensorThresholds: HashMap<String, SensorThreshold>) {
        mAdapter.bindSessions(data, sensorThresholds)
    }

    override fun showSessionsView(sensorThresholds: HashMap<String, SensorThreshold>) {
//        mAdapter.bindSessions(dbSessions, sensorThresholds)
//        mAdapter.bindData(dbSessions)
        mRecyclerSessions?.visibility = View.VISIBLE
        mEmptyView?.visibility = View.INVISIBLE
//        mAdapter.notifyDataSetChanged()
    }

    override fun showEmptyView() {
        mEmptyView?.visibility = View.VISIBLE
        mRecyclerSessions?.visibility = View.INVISIBLE
    }

    override fun showLoaderFor(session: Session) {
        mAdapter.showLoaderFor(session)
    }

    override fun hideLoaderFor(session: Session) {
        mAdapter.hideLoaderFor(session)
    }

    override fun showLoader() {
        mSwipeRefreshLayout?.isRefreshing = true
    }

    override fun hideLoader() {
        mSwipeRefreshLayout?.isRefreshing = false
    }

    private fun recyclerViewCanBeUpdated(): Boolean {
        return mRecyclerSessions?.isComputingLayout == false
                && mRecyclerSessions?.scrollState == RecyclerView.SCROLL_STATE_IDLE
    }

    private fun setupSwipeToRefreshLayout() {
        mSwipeRefreshLayout = rootView?.findViewById<SwipeRefreshLayout>(R.id.refresh_sessions)
        mSwipeRefreshLayout?.let { layout ->
            layout.setColorSchemeResources(R.color.aircasting_blue_400)
            layout.setOnRefreshListener {
                onSwipeToRefreshTriggered()
            }
        }
    }

    fun onExpandSessionCard(session: Session) {
        for (listener in listeners) {
            listener.onExpandSessionCard(session)
        }
    }

    fun onFollowButtonClicked(session: Session) {
        for (listener in listeners) {
            listener.onFollowButtonClicked(session)
        }
    }

    fun onUnfollowButtonClicked(session: Session) {
        for (listener in listeners) {
            listener.onUnfollowButtonClicked(session)
        }
    }

    fun onMapButtonClicked(session: Session, measurementStream: MeasurementStream?) {
        for (listener in listeners) {
            listener.onMapButtonClicked(session, measurementStream?.sensorName)
        }
    }

    fun onGraphButtonClicked(session: Session, measurementStream: MeasurementStream?) {
        for (listener in listeners) {
            listener.onGraphButtonClicked(session, measurementStream?.sensorName)
        }
    }
}
