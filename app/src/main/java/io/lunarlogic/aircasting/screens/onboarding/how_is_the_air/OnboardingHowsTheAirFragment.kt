package io.lunarlogic.aircasting.screens.onboarding.how_is_the_air

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OnboardingHowsTheAirFragment: Fragment() {
    private var controller: OnboardingHowsTheAirController? = null
    lateinit var listener: OnboardingHowsTheAirViewMvc.Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = OnboardingHowsTheAirViewMvcImpl(layoutInflater, null)
        controller = OnboardingHowsTheAirController(view)

        return view.rootView
    }

    override fun onStart() {
        super.onStart()
        controller?.registerListener(listener)
    }

    override fun onStop() {
        super.onStop()
        controller?.unregisterListener(listener)
    }
}