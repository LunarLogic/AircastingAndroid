package io.lunarlogic.aircasting.screens.settings.clearingSDCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.screens.settings.clearSDCard.ClearSDCardWizardNavigator

class ClearingSDCardFragment: Fragment(), ClearSDCardWizardNavigator.BackPressedListener {
    private var controller: ClearingSDCardController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity?.application as AircastingApplication)
            .appComponent.inject(this)

        val view = ClearingSDCardViewMvcImpl(layoutInflater, null)
        controller = ClearingSDCardController()

        return view.rootView
    }

    override fun onBackPressed() {
        controller?.onBackPressed()
    }

}