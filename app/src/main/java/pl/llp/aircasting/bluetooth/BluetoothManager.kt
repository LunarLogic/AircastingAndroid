package pl.llp.aircasting.bluetooth

import android.bluetooth.BluetoothAdapter
import pl.llp.aircasting.exceptions.BluetoothNotSupportedException
import pl.llp.aircasting.screens.new_session.select_device.DeviceItem

open class BluetoothManager {
    val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    open fun pairedDeviceItems(): List<DeviceItem> {
        val devices = adapter?.bondedDevices?: setOf()
        return devices.map { DeviceItem(it) }
    }

    open fun isBluetoothEnabled() : Boolean {
        if (adapter == null) {
            throw BluetoothNotSupportedException()
        }

        return adapter.isEnabled
    }

    fun startDiscovery() {
        adapter?.startDiscovery()
    }

    fun cancelDiscovery() {
        adapter?.cancelDiscovery()
    }
}
