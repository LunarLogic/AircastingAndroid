package io.lunarlogic.aircasting.screens.onboarding.measure_and_map

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import kotlinx.android.synthetic.main.onboarding_measure_and_map.view.*

class OnboardingMeasureAndMapViewMvcImpl: BaseObservableViewMvc<OnboardingMeasureAndMapViewMvc.Listener>, OnboardingMeasureAndMapViewMvc {
    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): super() {
        this.rootView = inflater.inflate(R.layout.onboarding_measure_and_map, parent, false)

        val continueButton = rootView?.continue_button
        continueButton?.setOnClickListener {
            onContinueClicked()
        }

        val learnMoreButton = rootView?.learn_more_button
        learnMoreButton?.setOnClickListener {
            onLearnMoreClicked()
        }

    }

    private fun onContinueClicked() {
        for (listener in listeners) {
            listener.onContinueMeasureAndMapClicked()
        }
    }

    private fun onLearnMoreClicked() {
        for (listener in listeners) {
            listener.onLearnMoreMeasureAndMapClicked()
        }
    }
}
