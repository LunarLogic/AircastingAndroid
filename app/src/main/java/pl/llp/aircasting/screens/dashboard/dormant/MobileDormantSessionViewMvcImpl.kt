package pl.llp.aircasting.screens.dashboard.dormant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import pl.llp.aircasting.R
import pl.llp.aircasting.screens.common.BottomSheet
import pl.llp.aircasting.screens.dashboard.SessionActionsBottomSheet
import pl.llp.aircasting.screens.dashboard.SessionPresenter
import pl.llp.aircasting.screens.dashboard.SessionViewMvcImpl

class MobileDormantSessionViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup,
    supporFragmentManager: FragmentManager
):
    SessionViewMvcImpl<MobileDormantSessionViewMvc.Listener>(inflater, parent, supporFragmentManager),
    MobileDormantSessionViewMvc,
    SessionActionsBottomSheet.Listener
{
    override fun showMeasurementsTableValues(): Boolean {
        return false
    }

    override fun showExpandedMeasurementsTableValues() = true

    override fun bindCollapsedMeasurementsDesctription() {
        mMeasurementsDescription?.text = context.getString(R.string.parameters)
    }

    override fun bindExpandedMeasurementsDesctription() {
        mMeasurementsDescription?.text = context.getString(R.string.session_avg_measurements_description)
    }

    override fun buildBottomSheet(sessionPresenter: SessionPresenter?): BottomSheet {
        return SessionActionsBottomSheet(this)
    }

    override fun showChart() = false

    override fun editSessionPressed() {
        val session = mSessionPresenter?.session ?: return

        for(listener in listeners) {
            listener.onSessionEditClicked(session)
        }
        dismissBottomSheet()
    }

    override fun shareSessionPressed() {
        val session = mSessionPresenter?.session ?: return

        for(listener in listeners){
            listener.onSessionShareClicked(session)
        }
        dismissBottomSheet()
    }

    override fun deleteSessionPressed() {
        val session = mSessionPresenter?.session ?: return

        for (listener in listeners) {
            listener.onSessionDeleteClicked(session)
        }
        dismissBottomSheet()
    }
}
