package io.lunarlogic.aircasting.screens.dashboard.mobile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.lib.Settings
import javax.inject.Inject
import io.lunarlogic.aircasting.screens.dashboard.SessionsViewModel


class MobileDormantFragment : Fragment() {
    private lateinit var controller: MobileDormantController
    private val sessionsViewModel by activityViewModels<SessionsViewModel>()

    @Inject
    lateinit var settings: Settings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity?.application as AircastingApplication)
            .appComponent.inject(this)

        val view = MobileDormantViewMvcImpl(
            layoutInflater,
            null,
            childFragmentManager
        )
        controller = MobileDormantController(
            activity,
            view,
            sessionsViewModel,
            viewLifecycleOwner,
            settings
        )

        return view.rootView
    }

    override fun onResume() {
        super.onResume()
        controller.onResume()
    }

    override fun onPause() {
        super.onPause()
        controller.onPause()
    }
}
