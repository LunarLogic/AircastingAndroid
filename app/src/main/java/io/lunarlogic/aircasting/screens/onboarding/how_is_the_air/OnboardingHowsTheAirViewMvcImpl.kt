package io.lunarlogic.aircasting.screens.onboarding.how_is_the_air

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc

class OnboardingHowsTheAirViewMvcImpl: BaseObservableViewMvc<OnboardingHowsTheAirViewMvc.Listener>, OnboardingHowsTheAirViewMvc {

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): super() {
        this.rootView = inflater.inflate(R.layout.onboarding_how_is_the_air, parent, false)

        val continueButton = rootView?.findViewById<Button>(R.id.continue_button)
        continueButton?.setOnClickListener {
            onContinueButtonClicked()
        }

    }

    private fun onContinueButtonClicked() {
        for (listener in listeners) {
            listener.onContinueHowsTheAirClicked()
        }
    }
}
