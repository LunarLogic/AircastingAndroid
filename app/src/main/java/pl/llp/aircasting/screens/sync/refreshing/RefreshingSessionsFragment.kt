package pl.llp.aircasting.screens.sync.refreshing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.llp.aircasting.screens.common.BaseFragment

class RefreshingSessionsFragment(): BaseFragment<RefreshingSessionsViewMvcImpl, RefreshingSessionsController>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = RefreshingSessionsViewMvcImpl(layoutInflater, null)
        controller = RefreshingSessionsController()

        return view?.rootView
    }
}
