package io.lunarlogic.aircasting.screens.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import io.lunarlogic.aircasting.events.NewMeasurementEvent

class DashboardViewMvcImpl : BaseObservableViewMvc<DashboardViewMvc.Listener>, DashboardViewMvc {
    constructor(
        inflater: LayoutInflater, parent: ViewGroup?): super() {
        this.rootView = inflater.inflate(R.layout.fragment_dashboard, parent, false)

        val button = rootView?.findViewById<Button>(R.id.dashboard_record_new_session_button)
        button?.setOnClickListener {
            onRecordNewSessionClicked()
        }
    }

    override fun updateMeasurements(newMeasurementEvent: NewMeasurementEvent) {
        val textView = rootView?.findViewById<TextView>(R.id.text_dashboard)
        if (newMeasurementEvent.sensorName == "AirBeam2-PM2.5") {
            textView?.text = "Current PM 2.5: " + newMeasurementEvent.measuredValue
        }
    }

    private fun onRecordNewSessionClicked() {
        for (listener in listeners) {
            listener.onRecordNewSessionClicked()
        }
    }
}