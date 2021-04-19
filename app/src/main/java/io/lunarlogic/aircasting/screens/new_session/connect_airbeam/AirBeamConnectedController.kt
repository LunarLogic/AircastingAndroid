package io.lunarlogic.aircasting.screens.new_session.connect_airbeam

import io.lunarlogic.aircasting.screens.common.BaseController

class AirBeamConnectedController(viewMvc: AirBeamConnectedViewMvcImpl?): BaseController<AirBeamConnectedViewMvcImpl>(viewMvc) {
    fun registerListener(listener: AirBeamConnectedViewMvc.Listener) {
        mViewMvc?.registerListener(listener)
    }

    fun unregisterListener(listener: AirBeamConnectedViewMvc.Listener) {
        mViewMvc?.unregisterListener(listener)
    }
}
