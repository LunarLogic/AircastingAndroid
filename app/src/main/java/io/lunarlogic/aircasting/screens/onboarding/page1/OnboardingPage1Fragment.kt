package io.lunarlogic.aircasting.screens.onboarding.page1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OnboardingPage1Fragment: Fragment() {
    private var controller: OnboardingPage1Controller? = null
    lateinit var listener: OnboardingPage1ViewMvc.Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = OnboardingPage1ViewMvcImpl(layoutInflater, null)
        controller = OnboardingPage1Controller(view)

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
