package io.lunarlogic.aircasting.screens.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BaseViewMvc
import io.lunarlogic.aircasting.sensor.Session

class DashboardViewMvcImpl: BaseViewMvc, DashboardViewMvc {
    private val mPager: ViewPager?

    constructor(
        inflater: LayoutInflater, parent: ViewGroup?,
        fragmentManager: FragmentManager?
    ): super() {
        this.rootView = inflater.inflate(R.layout.fragment_dashboard, parent, false)
        mPager = rootView?.findViewById<ViewPager>(R.id.pager)
        fragmentManager?.let { mPager?.adapter = DashboardPagerAdapter(context, it) }
    }

    override fun goToTab(sessionType: Session.Type) {
        mPager?.currentItem = DashboardPagerAdapter.tabIndexForSessionType(sessionType)
    }
}