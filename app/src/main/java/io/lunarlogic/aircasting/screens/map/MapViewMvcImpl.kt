package io.lunarlogic.aircasting.screens.map

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import io.lunarlogic.aircasting.screens.common.MeasurementsTableContainer
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.sensor.Measurement
import io.lunarlogic.aircasting.sensor.MeasurementStream
import io.lunarlogic.aircasting.sensor.SensorThreshold
import kotlinx.android.synthetic.main.activity_map.view.*


class MapViewMvcImpl: BaseObservableViewMvc<MapViewMvc.Listener>, MapViewMvc {
    private val mSessionDateTextView: TextView?
    private val mSessionNameTextView: TextView?
    private val mSessionTagsTextView: TextView?

    private var mSessionPresenter: SessionPresenter? = null

    private val mMeasurementsTableContainer: MeasurementsTableContainer
    private val mMapContainer: MapContainer
    private val mStatisticsContainer: StatisticsContainer
    private val mHLUSlider: HLUSlider

    private var mOnSensorThresholdChanged: ((sensorThreshold: SensorThreshold) -> Unit)? = null

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager?
    ): super() {
        this.rootView = inflater.inflate(R.layout.activity_map, parent, false)

        mSessionDateTextView = this.rootView?.session_date
        mSessionNameTextView = this.rootView?.session_name
        mSessionTagsTextView = this.rootView?.session_tags

        mMeasurementsTableContainer = MeasurementsTableContainer(
            context,
            inflater,
            this.rootView,
            true,
            true
        )
        mMapContainer = MapContainer(rootView, context, supportFragmentManager)
        mStatisticsContainer = StatisticsContainer(this.rootView, context)
        mHLUSlider = HLUSlider(this.rootView, context, this::onSensorThresholdChanged)
    }

    override fun registerListener(listener: MapViewMvc.Listener) {
        super.registerListener(listener)
        mMapContainer.registerListener(listener)
    }

    override fun unregisterListener(listener: MapViewMvc.Listener) {
        super.unregisterListener(listener)
        mMapContainer.unregisterListener()
    }

    override fun addMeasurement(measurement: Measurement) {
        mMapContainer.addMeasurement(measurement)
        mStatisticsContainer.addMeasurement(measurement)
    }

    override fun bindSession(sessionPresenter: SessionPresenter?, onSensorThresholdChanged: (sensorThreshold: SensorThreshold) -> Unit) {
        mSessionPresenter = sessionPresenter
        mOnSensorThresholdChanged = onSensorThresholdChanged

        bindSessionDetails()

        mMapContainer.bindSession(mSessionPresenter)
        mMeasurementsTableContainer.bindSession(mSessionPresenter, this::onMeasurementStreamChanged)
        mStatisticsContainer.bindSession(mSessionPresenter)
        mHLUSlider.bindSensorThreshold(sessionPresenter?.selectedSensorThreshold())
    }

    override fun centerMap(location: Location) {
        mMapContainer.centerMap(location)
    }

    private fun bindSessionDetails() {
        val session = mSessionPresenter?.session
        session ?: return

        mSessionDateTextView?.text = session.durationString()
        mSessionNameTextView?.text = session.name

        if (session.tags.isEmpty()) {
            mSessionTagsTextView?.visibility = View.GONE
        } else {
            mSessionTagsTextView?.text = session.tagsString()
        }
    }

    private fun onMeasurementStreamChanged(measurementStream: MeasurementStream) {
        mSessionPresenter?.selectedStream = measurementStream
        mMapContainer.refresh(mSessionPresenter)
        mStatisticsContainer.refresh(mSessionPresenter)
        mHLUSlider.refresh(mSessionPresenter?.selectedSensorThreshold())
    }

    private fun onSensorThresholdChanged(sensorThreshold: SensorThreshold) {
        mMeasurementsTableContainer.refresh()
        mMapContainer.refresh(mSessionPresenter)
        mStatisticsContainer.refresh(mSessionPresenter)

        mOnSensorThresholdChanged?.invoke(sensorThreshold)
    }
}
