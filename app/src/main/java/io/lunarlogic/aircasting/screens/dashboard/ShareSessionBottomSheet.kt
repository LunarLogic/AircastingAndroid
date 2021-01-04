package io.lunarlogic.aircasting.screens.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.ShareHelper
import io.lunarlogic.aircasting.models.Session

class ShareSessionBottomSheet(
    private val mListener: ShareSessionBottomSheet.Listener,
    val session: Session
): BottomSheetDialogFragment() {
    interface Listener{
        fun onShareLinkPressed()
        fun onShareFilePressed()
        fun onCancelPressed()
    }

    var emailInput: EditText? = null
    var radioGroup: RadioGroup? = null
    lateinit var chosenSensor: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.share_session_bottom_sheet, container, false)

        emailInput = view?.findViewById(R.id.email_input)
        radioGroup = view?.findViewById(R.id.stream_choose_radio_group)

        val radioButtons = session.streams.map { stream -> stream.detailedType } //todo: clean up
        radioButtons.forEach{
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = it
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            radioButton.layoutParams = layoutParams
            radioGroup?.addView(radioButton)
        }

        radioGroup?.setOnCheckedChangeListener( object: RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                val fieldValues = session.streams.map { stream -> stream.sensorName }
                    chosenSensor = fieldValues.get(checkedId - 1)
                }
            })

        val shareLinkButton = view?.findViewById<Button>(R.id.share_link_button)
        shareLinkButton?.setOnClickListener {
            mListener.onShareLinkPressed()
        }

        val shareFileButton = view?.findViewById<Button>(R.id.share_file_button)
        shareFileButton?.setOnClickListener {
            mListener.onShareFilePressed()
            emailInput?.setText("")
        }

        val cancelButton = view?.findViewById<Button>(R.id.cancel_button)
        cancelButton?.setOnClickListener {
            mListener.onCancelPressed()
        }

        val closeButton = view?.findViewById<ImageView>(R.id.close_button)
        closeButton?.setOnClickListener {
            mListener.onCancelPressed()
        }

        return view
    }

    fun shareFilePressed(): String{
        return emailInput?.text.toString().trim()
    }

    private fun setRadioButtonsForChosenSession(){

    }
}
