package io.lunarlogic.aircasting.screens.new_session.select_device

import android.bluetooth.BluetoothDevice

open class DeviceItem(private val mBluetoothDevice: BluetoothDevice) {
    open val name: String
    open val address: String
    open val id: String
    open val type: Type

    companion object {
        private val AIRBEAM2_NAME_REGEX = "airbeam2"
        private val AIRBEAM3_NAME_REGEX = "airbeam3"
    }

    init {
        name = mBluetoothDevice.name ?: "Unknown"
        address = mBluetoothDevice.address
        id = name.split(":").last()
        type = getType(name)
    }

    enum class Type(val value: Int) {
        OTHER(-1),
        AIRBEAM2(0),
        AIRBEAM3(1)
    }

    val bluetoothDevice get() = mBluetoothDevice

    fun displayName(): String {
        if (isAirBeam()) {
            return name.split(":", "-").first()
        }

        return name
    }

    fun isAirBeam(): Boolean {
        return arrayOf(Type.AIRBEAM2, Type.AIRBEAM3).contains(type)
    }

    private fun getType(name: String): Type {
        if (name.contains(AIRBEAM2_NAME_REGEX, true)) {
            return Type.AIRBEAM2
        }

        if (name.contains(AIRBEAM3_NAME_REGEX, true)) {
            return Type.AIRBEAM3
        }

        return Type.OTHER
    }
}
