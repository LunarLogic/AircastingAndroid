package pl.llp.aircasting.screens.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.llp.aircasting.R
import pl.llp.aircasting.models.MeasurementStream
import pl.llp.aircasting.models.SensorThreshold
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.screens.common.BaseObservableViewMvc

abstract class SessionsViewMvcImpl<ListenerType>: BaseObservableViewMvc<SessionsViewMvc.Listener>, SessionsViewMvc {
    private var mRecordSessionButton: Button? = null

    private var mRecyclerSessions: RecyclerView? = null
    private var mEmptyView: View? = null
    private val mAdapter: SessionsRecyclerAdapter<ListenerType>
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var mDidYouKnowBox: ConstraintLayout? = null

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager
    ): super() {
        this.rootView = inflater.inflate(R.layout.fragment_sessions_tab, parent, false)

        mEmptyView = rootView?.findViewById(layoutId())
        mRecordSessionButton = rootView?.findViewById(recordNewSessionButtonId())
        mRecordSessionButton?.setOnClickListener {
            onRecordNewSessionClicked()
        }

        mRecyclerSessions = findViewById(R.id.recycler_sessions)
        mRecyclerSessions?.setLayoutManager(LinearLayoutManager(rootView!!.context))

        mDidYouKnowBox = rootView?.findViewById(R.id.did_you_know_box)
        mDidYouKnowBox?.setOnClickListener {
            onDidYouKnowBoxClicked()
        }

        mAdapter = buildAdapter(inflater, supportFragmentManager)
        mRecyclerSessions?.setAdapter(mAdapter)

        setupSwipeToRefreshLayout()
    }

    abstract fun layoutId(): Int
    abstract fun showDidYouKnowBox(): Boolean
    abstract fun recordNewSessionButtonId(): Int

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

    override fun showSessionsView(sessions: List<Session>, sensorThresholds: HashMap<String, SensorThreshold>) {
        if (recyclerViewCanBeUpdated()) {
            mAdapter.bindSessions(sessions, sensorThresholds)
            mRecyclerSessions?.visibility = View.VISIBLE
            mEmptyView?.visibility = View.INVISIBLE
            mDidYouKnowBox?.visibility = View.GONE
        }
    }

    override fun showEmptyView() {
        mEmptyView?.visibility = View.VISIBLE
        mRecyclerSessions?.visibility = View.INVISIBLE
        if (showDidYouKnowBox()) {
            mDidYouKnowBox?.visibility = View.VISIBLE
        } else {
            mDidYouKnowBox?.visibility = View.GONE
        }
    }

    override fun showLoaderFor(session: Session) {
        mAdapter.showLoaderFor(session)
    }

    override fun hideLoaderFor(session: Session) {
        mAdapter.hideLoaderFor(session)
    }

    override fun hideLoaderFor(deviceId: String) {
        mAdapter.hideLoaderFor(deviceId)
    }

    override fun showReconnectingLoaderFor(session: Session) {
        mAdapter.showReconnectingLoaderFor(session)
    }

    override fun hideReconnectingLoaderFor(session: Session) {
        mAdapter.hideReconnectingLoaderFor(session)
    }

    override fun reloadSession(session: Session) {
        mAdapter.reloadSession(session)
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

    fun onDidYouKnowBoxClicked() {
        for (listener in listeners) {
            listener.onDidYouKnowBoxClicked()
        }
    }
}
