package io.lunarlogic.aircasting.screens.dashboard.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.models.SessionsViewModel
import javax.inject.Inject

class MobileActiveFragment : Fragment() {
    private var controller: MobileActiveController? = null
    private val sessionsViewModel by activityViewModels<SessionsViewModel>()

    @Inject
    lateinit var settings: Settings

    private var sessionsRequested = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity?.application as AircastingApplication)
            .appComponent.inject(this)

        val view = MobileActiveViewMvcImpl(
            layoutInflater,
            null,
            childFragmentManager
        )
        controller = MobileActiveController(
            activity,
            view,
            sessionsViewModel,
            viewLifecycleOwner,
            settings
        )

        if (sessionsRequested) {
            controller?.onCreate()
            sessionsRequested = false
        }

        return view.rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionsRequested = true
    }

    override fun onResume() {
        super.onResume()
        controller?.onResume()
    }

    override fun onPause() {
        super.onPause()
        controller?.onPause()
    }
}