package pl.llp.aircasting.screens.new_session.connect_airbeam

import pl.llp.aircasting.screens.common.ObservableViewMvc

interface TurnOnBluetoothViewMvc : ObservableViewMvc<TurnOnBluetoothViewMvc.Listener> {
    interface Listener {
        fun onTurnOnBluetoothOkClicked()
    }
}
