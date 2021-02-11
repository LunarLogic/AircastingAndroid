package io.lunarlogic.aircasting.screens.settings

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.screens.settings.myaccount.MyAccountActivity

class SettingsController(
    private val mContext: Context?,
    private val mViewMvc: SettingsViewMvc,
    private val mSettings: Settings,
    private val fragmentManager: FragmentManager
) : SettingsViewMvc.Listener,
    SettingsViewMvc.BackendSettingsDialogListener,
    SettingsViewMvc.MicrophoneSettingsDialogListener {

    fun onStart(){
        mViewMvc.registerListener(this)
    }

    fun onStop(){
        mViewMvc.unregisterListener(this)
    }

    override fun onMyAccountClicked() {
        MyAccountActivity.start(mContext)
    }

    override fun onBackendSettingsClicked() {
        startBackendSettingsDialog()
    }

    override fun onToggleCrowdMapEnabled() {
        mSettings.toggleCrowdMapEnabled()
    }

    override fun onToggleMapsEnabled() {
        mSettings.toggleMapSettingsEnabled()
    }

    override fun onClearSDCardClicked() {
        TODO("Not yet implemented") // going to location services screen
    }

    override fun onMicrophoneSettingsClicked() {
        startMicrophoneSettingsDialog()
    }

    override fun confirmClicked(urlValue: String, portValue: String) {
        mSettings.backendSettingsChanged(urlValue, portValue)
    }

    override fun confirmMicrophoneSettingsClicked(calibration: Int) {
        mSettings.microphoneSettingsChanged(calibration)
    }

    private fun startMicrophoneSettingsDialog() {
        val calibration = mSettings.getCalibrationValue()
        MicrophoneSettingsDialog(fragmentManager, calibration, this).show()
    }

    fun startBackendSettingsDialog(){
        val url = mSettings.getBackendUrl()
        val port = mSettings.getBackendPort()
        BackendSettingsDialog(fragmentManager, url, port, this).show()
    }
}
