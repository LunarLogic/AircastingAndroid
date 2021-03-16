package io.lunarlogic.aircasting.screens.new_session.choose_location

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.models.Session


class ChooseLocationViewMvcImpl: BaseObservableViewMvc<ChooseLocationViewMvc.Listener>, ChooseLocationViewMvc{

    private val session: Session

    private val MAX_ZOOM = 20.0f
    private val MIN_ZOOM = 5.0f
    private val DEFAULT_ZOOM = 13f

    private val mDefaultLatitude: Double
    private val mDefaultLongitude: Double


    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        supportFragmentManager: FragmentManager?,
        session: Session,
        errorHandler: ErrorHandler
    ): super() {
        this.rootView = inflater.inflate(R.layout.fragment_choose_location, parent, false)
        this.session = session

        mDefaultLatitude = session.location?.latitude ?: Session.Location.DEFAULT_LOCATION.latitude
        mDefaultLongitude = session.location?.longitude ?: Session.Location.DEFAULT_LOCATION.longitude



        val continueButton = rootView?.findViewById<Button>(R.id.continue_button)
        continueButton?.setOnClickListener {
            onContinueClicked()
        }
    }



    private fun onContinueClicked() {
        val latitude = 200.0
        val longitude = 200.0
        session.location = Session.Location(latitude, longitude)

        for (listener in listeners) {
            listener.onContinueClicked(session)
        }
    }

}
