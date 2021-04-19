package io.lunarlogic.aircasting.screens.new_session.choose_location

import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.ObservableViewMvc


interface ChooseLocationViewMvc : ObservableViewMvc<ChooseLocationViewMvc.Listener> {

    interface Listener {
        fun onContinueClicked(session: Session)
    }

    fun onDestroy()
}
