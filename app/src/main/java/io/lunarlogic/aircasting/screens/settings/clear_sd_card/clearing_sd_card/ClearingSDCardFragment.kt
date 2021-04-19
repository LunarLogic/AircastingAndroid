package io.lunarlogic.aircasting.screens.settings.clear_sd_card.clearing_sd_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.screens.common.BaseFragment
import io.lunarlogic.aircasting.screens.common.BaseWizardNavigator

class ClearingSDCardFragment(
    private val mFragmentManager: FragmentManager
): BaseFragment<ClearingSDCardViewMvcImpl, ClearingSDCardController>(), BaseWizardNavigator.BackPressedListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity?.application as AircastingApplication)
            .appComponent.inject(this)

        view = ClearingSDCardViewMvcImpl(layoutInflater, null)
        controller = ClearingSDCardController(mFragmentManager)

        return view?.rootView
    }

    override fun onBackPressed() {
        controller?.onBackPressed()
    }
}
