package io.lunarlogic.aircasting.screens.graph

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import io.lunarlogic.aircasting.screens.common.MeasurementsTableContainer
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.screens.map.HLUDialog
import io.lunarlogic.aircasting.screens.map.HLUSlider
import io.lunarlogic.aircasting.screens.map.MapViewMvc
import io.lunarlogic.aircasting.screens.map.StatisticsContainer
import io.lunarlogic.aircasting.sensor.Measurement
import io.lunarlogic.aircasting.sensor.MeasurementStream
import io.lunarlogic.aircasting.sensor.SensorThreshold
import kotlinx.android.synthetic.main.activity_graph.view.*


class GraphViewMvcImpl: BaseObservableViewMvc<GraphViewMvc.Listener>, GraphViewMvc, MapViewMvc.HLUDialogListener {
    private val mFragmentManager: FragmentManager?
    private var mListener: GraphViewMvc.Listener? = null

    private val mSessionDateTextView: TextView?
    private val mSessionNameTextView: TextView?
    private val mSessionTagsTextView: TextView?

    private var mSessionPresenter: SessionPresenter? = null

    private val mMeasurementsTableContainer: MeasurementsTableContainer
//    private val mMapContainer: MapContainer
    private val mStatisticsContainer: StatisticsContainer
    private val mMoreButton: ImageView?
    private val mHLUSlider: HLUSlider

    private var mOnSensorThresholdChanged: ((sensorThreshold: SensorThreshold) -> Unit)? = null

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager?
    ): super() {
        this.mFragmentManager = supportFragmentManager

        this.rootView = inflater.inflate(R.layout.activity_graph, parent, false)

        mSessionDateTextView = this.rootView?.session_date
        mSessionNameTextView = this.rootView?.session_name
        mSessionTagsTextView = this.rootView?.session_info

        mMeasurementsTableContainer = MeasurementsTableContainer(
            context,
            inflater,
            this.rootView,
            true,
            true
        )
//        mMapContainer = MapContainer(rootView, context, supportFragmentManager)
        mStatisticsContainer = StatisticsContainer(this.rootView, context)
        mMoreButton = this.rootView?.more_button
        mMoreButton?.setOnClickListener {
            onMoreButtonPressed()
        }
        mHLUSlider = HLUSlider(this.rootView, context, this::onSensorThresholdChanged)
    }

    override fun registerListener(listener: GraphViewMvc.Listener) {
        super.registerListener(listener)
        mListener = listener
//        mMapContainer.registerListener(listener)
    }

    override fun unregisterListener(listener: GraphViewMvc.Listener) {
        super.unregisterListener(listener)
        mListener = null
//        mMapContainer.unregisterListener()
    }

    override fun addMeasurement(measurement: Measurement) {
//        mMapContainer.addMeasurement(measurement)
        mStatisticsContainer.addMeasurement(measurement)
    }

    override fun bindSession(sessionPresenter: SessionPresenter?, onSensorThresholdChanged: (sensorThreshold: SensorThreshold) -> Unit) {
        mSessionPresenter = sessionPresenter
        mOnSensorThresholdChanged = onSensorThresholdChanged

        bindSessionDetails()
        if (sessionPresenter?.selectedStream != null) showSlider()

//        mMapContainer.bindSession(mSessionPresenter)
        mMeasurementsTableContainer.bindSession(mSessionPresenter, this::onMeasurementStreamChanged)
        mStatisticsContainer.bindSession(mSessionPresenter)
        mHLUSlider.bindSensorThreshold(sessionPresenter?.selectedSensorThreshold())
    }

    fun showSlider() {
        mMoreButton?.visibility = View.VISIBLE
        mHLUSlider.show()
    }

    private fun bindSessionDetails() {
        val session = mSessionPresenter?.session
        session ?: return

        mSessionDateTextView?.text = session.durationString()
        mSessionNameTextView?.text = session.name
        mSessionTagsTextView?.text = session.infoString()
    }

    private fun onMeasurementStreamChanged(measurementStream: MeasurementStream) {
        mSessionPresenter?.selectedStream = measurementStream
//        mMapContainer.refresh(mSessionPresenter)
        mStatisticsContainer.refresh(mSessionPresenter)
        mHLUSlider.refresh(mSessionPresenter?.selectedSensorThreshold())

        mListener?.onMeasurementStreamChanged(measurementStream)
    }

    private fun onSensorThresholdChanged(sensorThreshold: SensorThreshold) {
        mMeasurementsTableContainer.refresh()
//        mMapContainer.refresh(mSessionPresenter)
        mStatisticsContainer.refresh(mSessionPresenter)

        mOnSensorThresholdChanged?.invoke(sensorThreshold)
    }

    override fun onSensorThresholdChangedFromDialog(sensorThreshold: SensorThreshold) {
        onSensorThresholdChanged(sensorThreshold)
        mHLUSlider.refresh(sensorThreshold)
    }

    override fun onValidationFailed() {
        mListener?.onHLUDialogValidationFailed()
    }

    private fun onMoreButtonPressed() {
        mFragmentManager?.let {
            val sensorThreshold = mSessionPresenter?.selectedSensorThreshold()
            val measurementStream = mSessionPresenter?.selectedStream
            HLUDialog(sensorThreshold, measurementStream, mFragmentManager, this).show()
        }
    }
}