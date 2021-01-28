package io.lunarlogic.aircasting.screens.dashboard

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.ValidationHelper
import io.lunarlogic.aircasting.models.MeasurementStream
import io.lunarlogic.aircasting.models.Session

class ShareSessionBottomSheet(
    private val mListener: ShareSessionBottomSheet.Listener,
    val session: Session
): BottomSheetDialogFragment() {
    interface Listener{
        fun onShareLinkPressed(session: Session, sensor: String)
        fun onShareFilePressed(session: Session, emailInput: String)
    }

    class CurrentSessionStreams(
        val sensorName: String,
        val detailedType: String?
    )

    private val TAG = "ShareSessionBottomSheet"

    val fieldValues = hashMapOf<Int, CurrentSessionStreams>()
    var emailInputLayout: TextInputLayout? = null
    var emailInput: EditText? = null
    var radioGroup: RadioGroup? = null
    lateinit var chosenSensor: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.share_session_bottom_sheet, container, false)

        emailInputLayout = view?.findViewById(R.id.email_text_input_layout)
        emailInput = view?.findViewById(R.id.email_input)
        radioGroup = view?.findViewById(R.id.stream_choose_radio_group)

        setRadioButtonsForChosenSession()

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            chosenSensor = fieldValues[checkedId]?.sensorName.toString()
        }

        val shareLinkButton = view?.findViewById<Button>(R.id.share_link_button)
        shareLinkButton?.setOnClickListener {
            shareLinkPressed()
        }

        val shareFileButton = view?.findViewById<Button>(R.id.share_file_button)
        shareFileButton?.setOnClickListener {
            shareFilePressed()
        }

        val cancelButton = view?.findViewById<Button>(R.id.cancel_button)
        cancelButton?.setOnClickListener {
            dismiss()
        }

        val closeButton = view?.findViewById<ImageView>(R.id.close_button)
        closeButton?.setOnClickListener {
            dismiss()
        }

        return view
    }

    fun show(manager: FragmentManager){
        show(manager, TAG)
    }

    fun shareFilePressed(){
        val emailInput = emailInput?.text.toString().trim()
        if (!ValidationHelper.isValidEmail(emailInput)){
            showError()
            return
        }
        mListener.onShareFilePressed(session, emailInput)
        dismiss()
    }

    private fun showError() {
        emailInputLayout?.error = " "
    }

    fun shareLinkPressed(){
        mListener.onShareLinkPressed(session, chosenSensor)
        dismiss()
    }

    private fun setRadioButtonsForChosenSession(){
        fieldValues.clear()
        val currentSessionStreams = session.streams
        currentSessionStreams.forEach { stream ->
            setRadioButtonProperties(stream)
        }
        radioGroup?.check(fieldValues.keys.min() ?: 0)
        chosenSensor = fieldValues[fieldValues.keys.min()]?.sensorName.toString()
    }

    private fun setRadioButtonProperties(stream: MeasurementStream){
            val radioButton = RadioButton(context)
            radioButton.id = View.generateViewId()
            radioButton.text = stream.detailedType
            val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            radioButton.layoutParams = layoutParams
            radioButton.buttonTintList = ColorStateList.valueOf(resources.getColor(R.color.aircasting_blue_400))
            radioButton.setTextColor(resources.getColor(R.color.aircasting_grey_700))
            radioButton.setTextAppearance(context, R.style.TextAppearance_Aircasting_StreamValue2)
            radioGroup?.addView(radioButton)
            fieldValues[radioButton.id] = CurrentSessionStreams(stream.sensorName, stream.detailedType)
    }
}
