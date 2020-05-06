package io.lunarlogic.aircasting.screens.new_session.connect_airbeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AirBeamConnectedFragment() : Fragment() {

    private var controller: AirBeamConnectedController? = null
    var listener: AirBeamConnectedViewMvc.Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            AirBeamConnectedViewMvcImpl(
                layoutInflater,
                null
            )
        controller =
            AirBeamConnectedController(context!!, view)

        return view.rootView
    }

    override fun onStart() {
        super.onStart()
        listener?.let { controller?.registerListener(it) }
    }

    override fun onStop() {
        super.onStop()
        listener?.let { controller?.unregisterListener(it) }
    }
}