package pl.llp.aircasting.networking.params

import pl.llp.aircasting.models.Measurement
import pl.llp.aircasting.sensor.airbeam3.sync.CSVMeasurement
import java.util.*

val MILLISECONDS_IN_SECOND = 1000

class MeasurementParams {
    constructor(measurement: Measurement) {
        this.value = measurement.value
        this.latitude = measurement.latitude
        this.longitude = measurement.longitude
        this.time = measurement.time
        this.milliseconds = measurement.time.time.rem(MILLISECONDS_IN_SECOND).toInt()
    }

    constructor(csvMeasurement: CSVMeasurement) {
        this.value = csvMeasurement.value
        this.latitude = csvMeasurement.latitude
        this.longitude = csvMeasurement.longitude
        this.time = csvMeasurement.time
        this.milliseconds = csvMeasurement.time.time.rem(MILLISECONDS_IN_SECOND).toInt()
    }

    val longitude: Double?
    val latitude: Double?
    val milliseconds: Int
    val time: Date
    val value: Double?
}
