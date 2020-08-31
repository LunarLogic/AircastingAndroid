package io.lunarlogic.aircasting.screens.new_session

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseDialog
import kotlinx.android.synthetic.main.network_password_dialog.view.*

class TurnOnWifiDialog(
    private val mFragmentManager: FragmentManager,
    private val listener: NewSessionViewMvc.TurnOnWifiDialogListener
): BaseDialog(mFragmentManager) {
    private lateinit var mView: View

    override fun setupView(inflater: LayoutInflater): View {
        mView = inflater.inflate(R.layout.turn_on_wifi_dialog, null)

        mView.ok_button.setOnClickListener {
            okButtonClicked()
        }

        mView.cancel_button.setOnClickListener {
            dismiss()
        }

        return mView
    }

    private fun okButtonClicked() {
        listener.turnOnWifiClicked()
        dismiss()
    }
}
