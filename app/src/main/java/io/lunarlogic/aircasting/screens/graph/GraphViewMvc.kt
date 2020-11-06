package io.lunarlogic.aircasting.screens.graph

import io.lunarlogic.aircasting.screens.common.ObservableViewMvc
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.models.Measurement
import io.lunarlogic.aircasting.models.MeasurementStream
import io.lunarlogic.aircasting.models.SensorThreshold

interface GraphViewMvc: ObservableViewMvc<GraphViewMvc.Listener> {
    fun bindSession(sessionPresenter: SessionPresenter?)

    fun addMeasurement(measurement: Measurement)

    interface Listener {
        fun onSensorThresholdChanged(sensorThreshold: SensorThreshold)
        fun onHLUDialogValidationFailed()
    }
}
