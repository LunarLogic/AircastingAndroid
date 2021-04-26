package io.lunarlogic.aircasting.screens.dashboard.dormant

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.models.observers.DormantSessionsObserver
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.screens.dashboard.EditSessionBottomSheet
import io.lunarlogic.aircasting.screens.dashboard.SessionsController
import io.lunarlogic.aircasting.screens.dashboard.SessionsViewMvc

class MobileDormantController(
    mRootActivity: FragmentActivity?,
    private val mViewMvc: SessionsViewMvc?,
    private val mSessionsViewModel: SessionsViewModel,
    mLifecycleOwner: LifecycleOwner,
    mSettings: Settings,
    mApiServiceFactory: ApiServiceFactory,
    fragmentManager: FragmentManager,
    private val mContext: Context?
): SessionsController(mRootActivity, mViewMvc, mSessionsViewModel, mSettings, mApiServiceFactory, fragmentManager, mContext),
    SessionsViewMvc.Listener, EditSessionBottomSheet.Listener {

    private var mSessionsObserver = DormantSessionsObserver(mLifecycleOwner, mSessionsViewModel, mViewMvc)


    override fun registerSessionsObserver() {
        mSessionsObserver.observe(mSessionsViewModel.loadMobileDormantSessionsWithMeasurements())
    }

    override fun unregisterSessionsObserver() {
        mSessionsObserver.stop()
    }

    override fun onRecordNewSessionClicked() {
        startNewSession(Session.Type.MOBILE)
    }
    
    override fun onFinishSessionConfirmed(session: Session) {
        // do nothing
    }

    override fun onFinishAndSyncSessionConfirmed(session: Session) {
        // do nothing
    }

    override fun onMapButtonClicked(session: Session, sensorName: String?) {
        val onDownloadSuccess = { session: Session ->
            DatabaseProvider.runQuery {
                mSessionRepository.update(session)
            }
        }

        val finallyCallback = {}

        super.onMapButtonClicked(session, sensorName)
        mDownloadService.download(session.uuid, onDownloadSuccess, finallyCallback)
    }

    override fun onGraphButtonClicked(session: Session, sensorName: String?) {
        val onDownloadSuccess = { session: Session ->
            DatabaseProvider.runQuery {
                mSessionRepository.update(session)
            }
        }

        val finallyCallback = {}

        super.onGraphButtonClicked(session, sensorName)
        mDownloadService.download(session.uuid, onDownloadSuccess, finallyCallback)
    }

}
