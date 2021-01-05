package io.lunarlogic.aircasting.screens.dashboard

import android.app.Dialog
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

    val fieldValues = session.streams.map { stream -> stream.sensorName } // hashmapa idkow i tych nowych obiektow
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

        setRadioButtonsForChosenSession()

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            chosenSensor = fieldValues[checkedId - 1] // uzyc hash mapy
        }

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
        val radioButtons = session.streams.map { stream -> stream.detailedType }
        // foreach session.stream
        radioButtons.forEach{ stream ->
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = stream.detailedType
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            radioButton.layoutParams = layoutParams
            radioGroup?.addView(radioButton)
            // check this radioButton id here
            // add id to hashmap[id] obiekt tej nowej klasy w ktorej bedziesz miec sensorName, detailedType?
        }
    }
}
