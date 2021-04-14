package io.lunarlogic.aircasting.screens.new_session

import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.bluetooth.BluetoothManager
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.BaseWizardNavigator
import io.lunarlogic.aircasting.screens.new_session.choose_location.ChooseLocationFragment
import io.lunarlogic.aircasting.screens.new_session.choose_location.ChooseLocationViewMvc
import io.lunarlogic.aircasting.screens.new_session.confirmation.ConfirmationFragment
import io.lunarlogic.aircasting.screens.new_session.confirmation.ConfirmationViewMvc
import io.lunarlogic.aircasting.screens.new_session.connect_airbeam.*
import io.lunarlogic.aircasting.screens.new_session.select_device.*
import io.lunarlogic.aircasting.screens.new_session.session_details.SessionDetailsFragment
import io.lunarlogic.aircasting.screens.new_session.session_details.SessionDetailsViewMvc

class NewSessionWizardNavigator(
    private val mViewMvc: NewSessionViewMvc,
    private val mFragmentManager: FragmentManager
): BaseWizardNavigator(mViewMvc, mFragmentManager, R.id.new_session_fragment_container) {
    override val STEP_PROGRESS = 10

    fun goToSelectDeviceType(listener: SelectDeviceTypeViewMvc.Listener) {
        incrementStepProgress()
        val fragment = SelectDeviceTypeFragment()
        fragment.listener = listener
        unregisterBackPressedListener()
        goToFragment(fragment)
        updateProgressBarView()
    }

    fun goToSelectDevice(bluetoothManager: BluetoothManager, listener: SelectDeviceViewMvc.Listener) {
        incrementStepProgress()
        val fragment = SelectDeviceFragment()
        fragment.bluetoothManager = bluetoothManager
        fragment.listener = listener
        unregisterBackPressedListener()
        goToFragment(fragment)
    }

    fun goToTurnOnBluetooth(listener: TurnOnBluetoothViewMvc.Listener) {
        incrementStepProgress()
        val fragment = TurnOnBluetoothFragment()
        fragment.listener = listener
        goToFragment(fragment)
    }

    fun goToTurnOnLocationServices(
        listener: TurnOnLocationServicesViewMvc.Listener,
        areMapsDisabled: Boolean,
        sessionType: Session.Type
    ) {
        incrementStepProgress()
        val useDetailedExplanation = (areMapsDisabled && sessionType == Session.Type.MOBILE)
        val fragment = TurnOnLocationServicesFragment(useDetailedExplanation, areMapsDisabled)
        fragment.listener = listener
        goToFragment(fragment)
    }

    fun goToTurnOffLocationServices(deviceItem: DeviceItem, sessionUUID: String, listener: TurnOffLocationServicesViewMvc.Listener) {
        incrementStepProgress()
        val fragment = TurnOffLocationServicesFragment()
        fragment.listener = listener
        fragment.deviceItem = deviceItem
        fragment.sessionUUID = sessionUUID
        goToFragment(fragment)
    }

    fun goToTurnOnAirBeam(sessionType: Session.Type, listener: TurnOnAirBeamViewMvc.Listener) {
        incrementStepProgress()

        val fragment = TurnOnAirBeamFragment()
        fragment.listener = listener
        fragment.sessionType = sessionType
        unregisterBackPressedListener()
        goToFragment(fragment)
    }

    fun goToConnectingAirBeam() {
        incrementStepProgress()
        val fragment = ConnectingAirBeamFragment(mFragmentManager)
        registerBackPressed(fragment)
        goToFragment(fragment)
    }

    fun goToAirBeamConnected(deviceItem: DeviceItem, sessionUUID: String, listener: AirBeamConnectedViewMvc.Listener) {
        incrementStepProgress()
        val fragment = AirBeamConnectedFragment()
        fragment.deviceItem = deviceItem
        fragment.sessionUUID = sessionUUID
        fragment.listener = listener
        unregisterBackPressedListener()
        goToFragment(fragment)
    }

    fun goToSessionDetails(sessionUUID: String, sessionType: Session.Type, deviceItem: DeviceItem, listener: SessionDetailsViewMvc.Listener) {
        incrementStepProgress()
        val fragment = SessionDetailsFragment()
        fragment.listener = listener
        fragment.deviceItem = deviceItem
        fragment.sessionUUID = sessionUUID
        fragment.sessionType = sessionType
        goToFragment(fragment)
    }

    fun goToChooseLocation(session: Session, listener: ChooseLocationViewMvc.Listener, errorHandler: ErrorHandler) {
        incrementStepProgress()
        val fragment = ChooseLocationFragment()
        fragment.session = session
        fragment.listener = listener
        fragment.errorHandler = errorHandler
        goToFragment(fragment)
    }

    fun goToConfirmation(session: Session, listener: ConfirmationViewMvc.Listener) {
        incrementStepProgress()
        val fragment = ConfirmationFragment()
        fragment.listener = listener
        fragment.session = session
        goToFragment(fragment)
    }
}
