package pl.llp.aircasting.screens.session_view.graph

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import pl.llp.aircasting.R
import pl.llp.aircasting.models.Measurement
import pl.llp.aircasting.models.MeasurementStream
import pl.llp.aircasting.models.Note
import pl.llp.aircasting.models.SensorThreshold
import pl.llp.aircasting.screens.dashboard.SessionPresenter
import pl.llp.aircasting.screens.session_view.SessionDetailsViewMvc
import pl.llp.aircasting.screens.session_view.SessionDetailsViewMvcImpl
import kotlinx.android.synthetic.main.activity_graph.view.*
import java.util.*


abstract class GraphViewMvcImpl: SessionDetailsViewMvcImpl {
    private var graphContainer: GraphContainer?
    private val mLoader: ImageView?

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager?
    ): super(inflater, parent, supportFragmentManager) {
        graphContainer = GraphContainer(rootView, context, defaultZoomSpan(), this::onTimeSpanChanged, this::measurementsSample, notes())
        mLoader = rootView?.loader_graph
        showLoader(mLoader)
    }

    abstract fun defaultZoomSpan(): Int?

    open fun measurementsSample(): List<Measurement> {
        return mSessionPresenter?.selectedStream?.measurements ?: listOf<Measurement>()
    }

    open fun notes(): List<Note> {
        return mSessionPresenter?.session?.notes ?: listOf<Note>()
    }

    override fun layoutId(): Int {
        return R.layout.activity_graph
    }

    override fun registerListener(listener: SessionDetailsViewMvc.Listener) {
        super.registerListener(listener)
        graphContainer?.registerListener(listener)
    }

    override fun unregisterListener(listener: SessionDetailsViewMvc.Listener) {
        super.unregisterListener(listener)
        graphContainer?.unregisterListener()
    }

    override fun bindSession(sessionPresenter: SessionPresenter?) {
        super.bindSession(sessionPresenter)
        graphContainer?.bindSession(mSessionPresenter)
        if (mSessionPresenter?.selectedStream?.measurements?.isNotEmpty() == true) hideLoader(mLoader)
    }

    override fun onMeasurementStreamChanged(measurementStream: MeasurementStream) {
        super.onMeasurementStreamChanged(measurementStream)
        graphContainer?.refresh(mSessionPresenter)
    }

    override fun onSensorThresholdChanged(sensorThreshold: SensorThreshold) {
        super.onSensorThresholdChanged(sensorThreshold)
        graphContainer?.refresh(mSessionPresenter)
    }

    override fun addNote(note: Note) {
        graphContainer?.refresh(mSessionPresenter)
    }

    override fun deleteNote(note: Note) {
        graphContainer?.refresh(mSessionPresenter)
    }

    private fun onTimeSpanChanged(timeSpan: ClosedRange<Date>) {
        mSessionPresenter?.visibleTimeSpan = timeSpan
        mStatisticsContainer?.refresh(mSessionPresenter)
    }

    override fun onDestroy() {
        graphContainer?.destroy()
        graphContainer = null
    }
}
