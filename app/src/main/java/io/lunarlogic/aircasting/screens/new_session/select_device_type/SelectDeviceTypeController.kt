package io.lunarlogic.aircasting.screens.new_session.select_device

import io.lunarlogic.aircasting.screens.common.BaseController

class SelectDeviceTypeController(
    var viewMvc: SelectDeviceTypeViewMvcImpl?
) : BaseController<SelectDeviceTypeViewMvcImpl>(viewMvc){

    fun registerListener(listener: SelectDeviceTypeViewMvc.Listener) {
        mViewMvc?.registerListener(listener)
    }

    fun unregisterListener(listener: SelectDeviceTypeViewMvc.Listener) {
        mViewMvc?.unregisterListener(listener)
    }
}
