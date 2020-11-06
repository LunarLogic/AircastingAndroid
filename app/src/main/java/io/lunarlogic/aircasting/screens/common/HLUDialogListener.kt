package io.lunarlogic.aircasting.screens.common

import io.lunarlogic.aircasting.models.SensorThreshold

interface HLUDialogListener {
    fun onSensorThresholdChangedFromDialog(sensorThreshold: SensorThreshold)
    fun onValidationFailed()
}
