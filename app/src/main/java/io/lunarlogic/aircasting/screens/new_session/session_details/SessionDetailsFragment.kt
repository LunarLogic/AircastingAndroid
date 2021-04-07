package io.lunarlogic.aircasting.screens.new_session.session_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.BaseFragment
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem

import javax.inject.Inject

class SessionDetailsFragment() : BaseFragment<SessionDetailsViewMvcImpl, SessionDetailsController>() {
    lateinit var listener: SessionDetailsViewMvc.Listener
    lateinit var deviceItem: DeviceItem
    lateinit var sessionUUID: String
    lateinit var sessionType: Session.Type

    @Inject
    lateinit var sessionDetailsControllerFactory: SessionDetailsControllerFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity?.application as AircastingApplication)
            .appComponent.inject(this)

        view = SessionDetailsViewFactory.get(inflater, container, childFragmentManager, deviceItem, sessionUUID, sessionType)
        controller = sessionDetailsControllerFactory.get(activity, view, sessionType, childFragmentManager)
        controller?.onCreate()

        return view?.rootView
    }

    override fun onStart() {
        super.onStart()
        listener.let { controller?.registerListener(it) }
    }

    override fun onStop() {
        super.onStop()
        listener.let { controller?.unregisterListener(it) }
    }
}
