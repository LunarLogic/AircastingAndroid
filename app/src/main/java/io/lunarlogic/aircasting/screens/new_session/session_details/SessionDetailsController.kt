package io.lunarlogic.aircasting.screens.new_session.session_details

import android.content.Context
import io.lunarlogic.aircasting.screens.common.BaseController
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewMvcImpl

open class SessionDetailsController(
    private val mContext: Context?,
    private var mViewMvc: SessionDetailsViewMvc?
) {

    fun registerListener(listener: SessionDetailsViewMvc.Listener) {
        mViewMvc?.registerListener(listener)
    }

    fun unregisterListener(listener: SessionDetailsViewMvc.Listener) {
        mViewMvc?.unregisterListener(listener)
    }

    open fun onCreate() {}

    fun onDestroy() {
        mViewMvc = null
    }
}
