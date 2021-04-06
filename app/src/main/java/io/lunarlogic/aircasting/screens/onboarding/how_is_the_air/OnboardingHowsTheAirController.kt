package io.lunarlogic.aircasting.screens.onboarding.how_is_the_air

import io.lunarlogic.aircasting.screens.common.BaseController


class OnboardingHowsTheAirController(
    var viewMvc: OnboardingHowsTheAirViewMvcImpl?
) : BaseController<OnboardingHowsTheAirViewMvcImpl>(viewMvc){

    fun registerListener(listener: OnboardingHowsTheAirViewMvc.Listener) {
        mViewMvc?.registerListener(listener)
    }

    fun unregisterListener(listener: OnboardingHowsTheAirViewMvc.Listener) {
        mViewMvc?.unregisterListener(listener)
    }
}
