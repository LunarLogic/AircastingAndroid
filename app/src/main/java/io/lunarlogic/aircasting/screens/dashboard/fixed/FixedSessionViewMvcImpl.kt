package io.lunarlogic.aircasting.screens.dashboard.fixed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.screens.common.BottomSheet
import io.lunarlogic.aircasting.screens.dashboard.SessionActionsBottomSheet
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.screens.dashboard.SessionViewMvcImpl
import kotlinx.android.synthetic.main.session_card.view.*

class FixedSessionViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup,
    supportFragmentManager: FragmentManager
):
    SessionViewMvcImpl<FixedSessionViewMvc.Listener>(inflater, parent, supportFragmentManager),
    FixedSessionViewMvc,
    SessionActionsBottomSheet.Listener
{

    override fun showMeasurementsTableValues(): Boolean {
        return false
    }

    override fun showExpandedMeasurementsTableValues() = false

    override fun bindCollapsedMeasurementsDesctription() {
        mMeasurementsDescription?.text = context.getString(R.string.parameters)
    }

    override fun bindExpandedMeasurementsDesctription() {
        mMeasurementsDescription?.text = context.getString(R.string.parameters)
    }

    override fun buildBottomSheet(sessionPresenter: SessionPresenter?): BottomSheet {
        return SessionActionsBottomSheet(this)
    }

    override fun showChart() = false

    override fun bindFollowButtons(sessionPresenter: SessionPresenter) {
        if (sessionPresenter.session?.followed == true) {
            mFollowButton.visibility = View.GONE
            mUnfollowButton.visibility = View.VISIBLE
        } else {
            mFollowButton.visibility = View.VISIBLE
            mUnfollowButton.visibility = View.GONE
        }
    }

    override fun editSessionPressed() {
        for (listener in listeners) {
            listener.onSessionEditClicked(mSessionPresenter!!.session!!)
        }
        dismissBottomSheet()
    }

    override fun shareSessionPressed() {
        for(listener in listeners){
            listener.onSessionShareClicked(mSessionPresenter!!.session!!)
        }
    }

    override fun deleteSessionPressed() {
        for (listener in listeners) {
            listener.onSessionDeleteClicked(mSessionPresenter!!.session!!)
        }
        dismissBottomSheet()
    }
}
