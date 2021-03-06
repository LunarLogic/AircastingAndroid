package pl.llp.aircasting.screens.sync

import android.view.LayoutInflater
import android.view.ViewGroup
import pl.llp.aircasting.R
import pl.llp.aircasting.screens.common.BaseViewMvc

class SyncViewMvcImpl : BaseViewMvc, SyncViewMvc {
    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): super() {
        this.rootView = inflater.inflate(R.layout.activity_sync, parent, false)
    }
}
