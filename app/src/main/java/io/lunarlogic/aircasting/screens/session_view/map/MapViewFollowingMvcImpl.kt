package io.lunarlogic.aircasting.screens.session_view.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R


class MapViewFollowingMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    supportFragmentManager: FragmentManager?
): MapViewMvcImpl(inflater, parent, supportFragmentManager) {

    override fun bindSessionMeasurementsDescription() {
        mSessionMeasurementsDescription?.text = context.getString(R.string.session_last_min_measurements_description)
    }
}
