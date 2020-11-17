package io.lunarlogic.aircasting.screens.new_session.confirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.models.Session

class MobileSessionConfirmationViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    supportFragmentManager: FragmentManager?,
    session: Session
) : ConfirmationViewMvcImpl(inflater, parent, supportFragmentManager, session) {

    override fun layoutId(): Int {
        return R.layout.fragment_mobile_session_confirmation
    }

    override fun updateLocation(latitude: Double?, longitude: Double?) {
        updateMarkerPosition(latitude, longitude)
    }
}
