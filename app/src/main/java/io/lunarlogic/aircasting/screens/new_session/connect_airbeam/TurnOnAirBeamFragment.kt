package io.lunarlogic.aircasting.screens.new_session.connect_airbeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.lunarlogic.aircasting.models.Session

class TurnOnAirBeamFragment() : Fragment() {
    private var controller: TurnOnAirBeamController? = null
    private var view: TurnOnAirBeamViewMvcImpl? = null
    var listener: TurnOnAirBeamViewMvc.Listener? = null
    lateinit var sessionType: Session.Type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =
            TurnOnAirBeamViewMvcImpl(
                layoutInflater,
                null,
                sessionType
            )
        controller =
            TurnOnAirBeamController(
                context,
                view
            )

        return view?.rootView
    }

    override fun onStart() {
        super.onStart()
        listener?.let { controller?.registerListener(it) }
    }

    override fun onStop() {
        super.onStop()
        listener?.let { controller?.unregisterListener(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        view = null
        controller?.onDestroy()
        controller = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view = null
        controller?.onDestroy()
        controller = null
    }
}
