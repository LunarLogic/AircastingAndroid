package io.lunarlogic.aircasting.screens.new_session.select_device

import io.lunarlogic.aircasting.screens.common.ObservableViewMvc
import io.lunarlogic.aircasting.screens.new_session.select_device.items.DeviceItem

interface SelectDeviceViewMvc : ObservableViewMvc<SelectDeviceViewMvc.Listener> {
    interface Listener {
        fun onDeviceItemSelected(deviceItem: DeviceItem)
    }

    fun bindDeviceItems(deviceItems: List<DeviceItem>)
    fun addDeviceItem(deviceItem: DeviceItem)
}