package io.lunarlogic.aircasting.screens.new_session.session_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.lunarlogic.aircasting.sensor.Session

class SessionDetailsFragment() : Fragment() {
    private lateinit var controller: SessionDetailsController
    lateinit var listener: SessionDetailsViewMvc.Listener
    lateinit var deviceId: String
    lateinit var sessionType: Session.Type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = SessionDetailsViewFactory.get(inflater, container, deviceId, sessionType)
        controller = SessionDetailsControllerFactory.get(context, view, sessionType)

        return view.rootView
    }

    override fun onStart() {
        super.onStart()
        listener.let { controller.registerListener(it) }
        controller.onStart()
    }

    override fun onStop() {
        super.onStop()
        listener.let { controller.unregisterListener(it) }
    }
}
