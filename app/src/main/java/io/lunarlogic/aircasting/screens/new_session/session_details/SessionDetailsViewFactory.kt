package io.lunarlogic.aircasting.screens.new_session.session_details

import android.view.LayoutInflater
import android.view.ViewGroup
import io.lunarlogic.aircasting.screens.dashboard.FixedSessionDetailsViewMvcImpl
import io.lunarlogic.aircasting.screens.dashboard.SessionDetailsViewMvc
import io.lunarlogic.aircasting.screens.dashboard.MobileSessionDetailsViewMvcImpl
import io.lunarlogic.aircasting.sensor.Session

class SessionDetailsViewFactory() {
    companion object {
        fun get(
            sessionType: Session.Type,
            inflater: LayoutInflater,
            parent: ViewGroup?,
            deviceId: String
        ): SessionDetailsViewMvc {
            return when(sessionType) {
                Session.Type.MOBILE -> MobileSessionDetailsViewMvcImpl(inflater, parent, deviceId)
                Session.Type.FIXED -> FixedSessionDetailsViewMvcImpl(inflater, parent, deviceId)
            }
        }
    }
}