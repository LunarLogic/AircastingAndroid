package io.lunarlogic.aircasting.screens.dashboard.active

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.BaseDialog
import kotlinx.android.synthetic.main.finish_session_confirmation_dialog.view.*

class FinishSessionConfirmationDialog(
    mFragmentManager: FragmentManager,
    private val mListener: FinishSessionListener,
    private val mSession: Session
) : BaseDialog(mFragmentManager) {
    private lateinit var mView: View

    override fun setupView(inflater: LayoutInflater): View {
        mView = inflater.inflate(R.layout.finish_session_confirmation_dialog, null)

        mView.informations_text_view.text = buildDescription()
        mView.header.text = buildHeader()

        mView.finish_recording_button.setOnClickListener {
            finishSessionConfirmed()
        }

        mView.cancel_button.setOnClickListener {
            dismiss()
        }

        return mView
    }

    private fun finishSessionConfirmed(){
        mListener.onStopSessionClicked(mSession)
    }

    private fun buildDescription(): SpannableStringBuilder {
        val blueColor = context?.let {
            ResourcesCompat.getColor(it.resources, R.color.aircasting_blue_400, null)
        } ?: Color.GRAY

        return SpannableStringBuilder()
            .append(getString(R.string.dialog_finish_recording_text_part1))
            .append(" ")
            .color(blueColor, { bold { append(getString(R.string.dialog_finish_recording_text_part2)) } })
            .append(" ")
            .append(getString(R.string.dialog_finish_recording_text_part3))
    }

    private fun buildHeader(): SpannableStringBuilder {
        val blueColor = context?.let{
            ResourcesCompat.getColor(it.resources, R.color.aircasting_blue_400, null)
        } ?: Color.GRAY

        return SpannableStringBuilder()
            .append(getString(R.string.dialog_finish_recording_header_part1))
            .append(" ")
            .color(blueColor, { bold { append(mSession.name) } })
            .append(" ")
            .append(getString(R.string.dialog_finish_recording_header_part3))
    }
}