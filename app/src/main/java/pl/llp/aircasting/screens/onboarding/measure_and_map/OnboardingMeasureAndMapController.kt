package pl.llp.aircasting.screens.onboarding.measure_and_map

import pl.llp.aircasting.screens.common.BaseController

class OnboardingMeasureAndMapController(
    var viewMvc: OnboardingMeasureAndMapViewMvcImpl?
) :  BaseController<OnboardingMeasureAndMapViewMvcImpl>(viewMvc) {

    fun registerListener(listener: OnboardingMeasureAndMapViewMvc.Listener) {
        mViewMvc?.registerListener(listener)
    }

    fun unregisterListener(listener: OnboardingMeasureAndMapViewMvc.Listener) {
        mViewMvc?.unregisterListener(listener)
    }

}
