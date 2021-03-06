package pl.llp.aircasting.screens.lets_start

import android.content.Context
import pl.llp.aircasting.exceptions.ErrorHandler
import androidx.fragment.app.FragmentActivity
import pl.llp.aircasting.R
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.networking.services.ConnectivityManager
import pl.llp.aircasting.screens.common.BaseController
import pl.llp.aircasting.screens.new_session.NewSessionActivity
import pl.llp.aircasting.screens.sync.SyncActivity


class LetsStartController(
    private var mRootActivity: FragmentActivity?,
    private var viewMvc: LetsStartViewMvcImpl?,
    private var mContext: Context?,
    private val mErrorHandler: ErrorHandler
): BaseController<LetsStartViewMvcImpl>(viewMvc), LetsStartViewMvc.Listener {

    fun onCreate() {
        mViewMvc?.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewMvc?.unregisterListener(this)
        mRootActivity = null
        mContext = null
    }

    override fun onFixedSessionSelected() {
        if (!ConnectivityManager.isConnected(mContext)) {
            val header = mContext?.getString(R.string.fixed_session_no_internet_connection_header)
            val description = mContext?.getString(R.string.fixed_session_no_internet_connection)
            mErrorHandler.showErrorDialog(mRootActivity?.supportFragmentManager, header, description)
            return
        }

        NewSessionActivity.start(mRootActivity, Session.Type.FIXED)
    }

    override fun onMobileSessionSelected() {
        NewSessionActivity.start(mRootActivity, Session.Type.MOBILE)
    }

    override fun onSyncSelected() {
        SyncActivity.start(mRootActivity)
    }

    override fun onMoreInfoClicked() {
        mViewMvc?.showMoreInfoDialog()
    }
}
