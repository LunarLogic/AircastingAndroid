package io.lunarlogic.aircasting.screens.new_session.connect_airbeam

import android.content.Context
import io.lunarlogic.aircasting.exceptions.ExceptionHandler
import io.lunarlogic.aircasting.screens.new_session.select_device.items.DeviceItem
import io.lunarlogic.aircasting.sensor.airbeam2.AirBeam2Connector

class ConnectingAirBeamController(
    mContext: Context,
    private val deviceItem: DeviceItem,
    private val mListener: Listener
) {
    interface Listener {
        fun onConnectionSuccessful()
    }

    val exceptionHandler = ExceptionHandler(mContext)
    val airbeam2Connector = AirBeam2Connector(exceptionHandler)

    fun onStart() {
        airbeam2Connector.connect(deviceItem.bluetoothDevice)
    }

    fun onStop() {
        // TODO: cancel connecting?
    }
}