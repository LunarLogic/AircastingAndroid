package io.lunarlogic.aircasting.screens.common

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.models.MeasurementStream
import io.lunarlogic.aircasting.models.SensorThreshold
import kotlinx.android.synthetic.main.hlu_dialog.view.*

class HLUDialog(
    private var mSensorThreshold: SensorThreshold?,
    private val mMeasurementStream: MeasurementStream?,
    mFragmentManager: FragmentManager,
    private val listener: HLUDialogListener
): BaseDialog(mFragmentManager) {
    private lateinit var mView: View

    override fun setupView(inflater: LayoutInflater): View {
        mView = inflater.inflate(R.layout.hlu_dialog, null)

        setupView()

        mView.save_button.setOnClickListener {
            okButtonClicked()
        }

        mView.close_button.setOnClickListener {
            dismiss()
        }

        mView.reset_button.setOnClickListener {
            resetToDefaultsClicked()
        }

        return mView
    }

    private fun setupView() {
        mView.hlu_dialog_min.setText(mSensorThreshold?.thresholdVeryLow.toString())
        mView.hlu_dialog_low.setText(mSensorThreshold?.thresholdLow.toString())
        mView.hlu_dialog_medium.setText(mSensorThreshold?.thresholdMedium.toString())
        mView.hlu_dialog_high.setText(mSensorThreshold?.thresholdHigh.toString())
        mView.hlu_dialog_max.setText(mSensorThreshold?.thresholdVeryHigh.toString())
    }

    private fun okButtonClicked() {
        mSensorThreshold ?: return

        val min = getValue(mView.hlu_dialog_min)
        val low = getValue(mView.hlu_dialog_low)
        val medium = getValue(mView.hlu_dialog_medium)
        val high = getValue(mView.hlu_dialog_high)
        val max = getValue(mView.hlu_dialog_max)

        if (validate(min, low, medium, high, max)) {
            min?.let { mSensorThreshold!!.thresholdVeryLow = min }
            low?.let { mSensorThreshold!!.thresholdLow = low }
            medium?.let { mSensorThreshold!!.thresholdMedium = medium }
            high?.let { mSensorThreshold!!.thresholdHigh = high }
            max?.let { mSensorThreshold!!.thresholdVeryHigh = max }

            listener.onSensorThresholdChangedFromDialog(mSensorThreshold!!)

            dismiss()
        } else {
            listener.onValidationFailed()
        }
    }

    private fun validate(min: Int?, low: Int?, medium: Int?, high: Int?, max: Int?): Boolean {
        return min != null && low != null && medium != null && high != null && max != null &&
                min < low && low < medium && medium < high && high < max
    }

    private fun resetToDefaultsClicked() {
        if (mSensorThreshold == null || mMeasurementStream == null) return

        mSensorThreshold!!.thresholdVeryLow = mMeasurementStream.thresholdVeryLow
        mSensorThreshold!!.thresholdLow = mMeasurementStream.thresholdLow
        mSensorThreshold!!.thresholdMedium = mMeasurementStream.thresholdMedium
        mSensorThreshold!!.thresholdHigh = mMeasurementStream.thresholdHigh
        mSensorThreshold!!.thresholdVeryHigh = mMeasurementStream.thresholdVeryHigh

        listener.onSensorThresholdChangedFromDialog(mSensorThreshold!!)

        dismiss()
    }

    private fun getValue(input: TextInputEditText): Int? {
        val stringValue = input.text.toString().trim()
        
        if (stringValue.isEmpty()) return null

        return stringValue.toInt()
    }
}
