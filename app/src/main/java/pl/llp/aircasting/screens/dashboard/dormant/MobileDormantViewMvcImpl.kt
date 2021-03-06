package pl.llp.aircasting.screens.dashboard.dormant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import pl.llp.aircasting.R
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.screens.dashboard.SessionsRecyclerAdapter
import pl.llp.aircasting.screens.dashboard.SessionsViewMvcImpl


class MobileDormantViewMvcImpl(
    inflater: LayoutInflater,
    parent: ViewGroup?,
    supportFragmentManager: FragmentManager
): SessionsViewMvcImpl<MobileDormantSessionViewMvc.Listener>(inflater, parent, supportFragmentManager),
    MobileDormantSessionViewMvc.Listener {

    override fun buildAdapter(
        inflater: LayoutInflater,
        supportFragmentManager: FragmentManager
    ): SessionsRecyclerAdapter<MobileDormantSessionViewMvc.Listener> {
        return MobileDormantRecyclerAdapter(
            inflater,
            this,
            supportFragmentManager
        )
    }

    override fun onSessionEditClicked(session: Session) {
        for (listener in listeners) {
            listener.onEditSessionClicked(session)
        }
    }

    override fun onSessionShareClicked(session: Session) {
        for (listener in listeners) {
            listener.onShareSessionClicked(session)
        }
    }

    override fun onSessionDeleteClicked(session: Session) {
        for (listener in listeners) {
            listener.onDeleteSessionClicked(session)
        }
    }

    override fun layoutId(): Int {
        return R.id.empty_mobile_dashboard
    }

    override fun showDidYouKnowBox(): Boolean {
        return true
    }

    override fun recordNewSessionButtonId(): Int {
        return R.id.dashboard_mobile_record_new_session_button
    }
}
