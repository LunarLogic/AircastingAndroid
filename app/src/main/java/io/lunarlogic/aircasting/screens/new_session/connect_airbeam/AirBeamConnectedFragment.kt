package io.lunarlogic.aircasting.screens.new_session.connect_airbeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem

class AirBeamConnectedFragment() : Fragment() {

    private var controller: AirBeamConnectedController? = null
    lateinit var listener: AirBeamConnectedViewMvc.Listener
    lateinit var deviceItem: DeviceItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            AirBeamConnectedViewMvcImpl(
                layoutInflater,
                null,
                deviceItem
            )
        controller =
            AirBeamConnectedController(requireContext(), view)

        return view.rootView
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
