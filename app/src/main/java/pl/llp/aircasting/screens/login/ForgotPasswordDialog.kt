package pl.llp.aircasting.screens.login

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import pl.llp.aircasting.R
import pl.llp.aircasting.screens.common.BaseDialog
import pl.llp.aircasting.screens.new_session.LoginViewMvc
import kotlinx.android.synthetic.main.forgot_password_dialog.view.*

class ForgotPasswordDialog(
    mFragmentManager: FragmentManager,
    private val listener: LoginViewMvc.ForgotPasswordDialogListener
    ): BaseDialog(mFragmentManager) {
    private lateinit var mView: View

    override fun setupView(inflater: LayoutInflater): View {
        mView = inflater.inflate(R.layout.forgot_password_dialog, null)

        mView.ok_button.setOnClickListener {
            forgotPasswordConfirmed()
        }

        mView.cancel_button.setOnClickListener {
            dismiss()
        }

        return mView
    }

    private fun forgotPasswordConfirmed() {
        val emailValue = mView.email_input.text.toString().trim()
        listener.confirmClicked(emailValue)
        dismiss()
    }
}
