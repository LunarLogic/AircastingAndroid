package io.lunarlogic.aircasting.screens.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.MeasurementsTableContainer
import kotlinx.android.synthetic.main.activity_map.view.*

class MapViewMobileDormantMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    supportFragmentManager: FragmentManager?): MapViewMvcImpl(inflater, parent, supportFragmentManager), MapViewMvc, MapViewMvc.HLUDialogListener {

    override fun bindStatisticsContainer() {
        mStatisticsContainer = null
    }

    override fun bindSessionMeasurementsDescription() {
        mSessionMeasurementsDescription?.text = context.getString(R.string.session_avg_measurements_description)
    }
}