package io.lunarlogic.aircasting.screens.onboarding.your_privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OnboardingYourPrivacyFragment: Fragment() {
    private var controller: OnboardingYourPrivacyController? = null
    lateinit var listener: OnboardingYourPrivacyViewMvc.Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = OnboardingYourPrivacyViewMvcImpl(layoutInflater, null)
        controller = OnboardingYourPrivacyController(view)

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
