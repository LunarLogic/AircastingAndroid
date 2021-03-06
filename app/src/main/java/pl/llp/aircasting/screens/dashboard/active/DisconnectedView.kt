package pl.llp.aircasting.screens.dashboard.active

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import pl.llp.aircasting.R
import pl.llp.aircasting.lib.AnimatedLoader
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.screens.dashboard.SessionPresenter
import kotlinx.android.synthetic.main.disconnected_view.view.*

class DisconnectedView {
    private val mContext: Context
    private val mListener: MobileActiveSessionViewMvc.DisconnectedViewListener
    private val mSupportFragmentManager: FragmentManager

    private val mDisconnectedView: View?
    private val mHeader: TextView?
    private val mDescription: TextView?
    private val mPrimaryButton: Button?
    private val mSecondaryButton: Button?
    private val mReconnectingLoader: ImageView?

    constructor(context: Context, rootView: View?, supportFragmentManager: FragmentManager, listener: MobileActiveSessionViewMvc.DisconnectedViewListener) {
        mContext = context
        mListener = listener
        mSupportFragmentManager = supportFragmentManager

        mDisconnectedView = rootView?.disconnected_view
        mHeader = rootView?.disconnected_view_bluetooth_device_header
        mDescription = rootView?.disconnected_view_bluetooth_device_description
        mPrimaryButton = rootView?.disconnected_view_bluetooth_device_reconnect_button
        mSecondaryButton = rootView?.disconnected_view_bluetooth_device_finish_button
        mReconnectingLoader = rootView?.reconnecting_loader
    }

    fun show(sessionPresenter: SessionPresenter) {
        val session = sessionPresenter.session
        session ?: return

        if (session.isAirBeam3()) {
            bindAirBeam3(session)
        } else {
            bindBluetoothDevice(session)
        }

        bindReconnectingLoader(sessionPresenter)

        mDisconnectedView?.visibility = View.VISIBLE
    }

    fun hide() {
        mDisconnectedView?.visibility = View.GONE
    }

    private fun bindReconnectingLoader(sessionPresenter: SessionPresenter) {
        if (sessionPresenter.reconnecting) {
            showReconnectingLoader()
        } else {
            hideReconnectingLoader()
        }
    }

    private fun bindBluetoothDevice(session: Session) {
        mHeader?.text = mContext.getString(R.string.disconnected_view_bluetooth_device_header)
        mDescription?.text = mContext.getString(R.string.disconnected_view_bluetooth_device_description)
        mPrimaryButton?.text = mContext.getString(R.string.disconnected_view_bluetooth_device_reconnect_button)
        mSecondaryButton?.text = mContext.getString(R.string.disconnected_view_bluetooth_device_finish_button)

        mPrimaryButton?.setOnClickListener { mListener.onSessionReconnectClicked(session) }
        mSecondaryButton?.setOnClickListener { FinishSessionConfirmationDialog(mSupportFragmentManager, mListener, session).show() }
    }

    private fun bindAirBeam3(session: Session) {
        mHeader?.text = mContext.getString(R.string.disconnected_view_airbeam3_header)
        mDescription?.text = mContext.getString(R.string.disconnected_view_airbeam3_description)
        mPrimaryButton?.text = mContext.getString(R.string.disconnected_view_airbeam3_sync_button)
        mSecondaryButton?.text = mContext.getString(R.string.disconnected_view_airbeam3_finish_button)

        mPrimaryButton?.setOnClickListener {
            FinishAndSyncSessionConfirmationDialog(mSupportFragmentManager, mListener, session).show()
        }
        mSecondaryButton?.setOnClickListener { FinishSessionConfirmationDialog(mSupportFragmentManager, mListener, session).show() }
    }

    private fun showReconnectingLoader() {
        AnimatedLoader(mReconnectingLoader).start()
        mReconnectingLoader?.visibility = View.VISIBLE
        mPrimaryButton?.isEnabled = false
    }

    private fun hideReconnectingLoader() {
        mReconnectingLoader?.visibility = View.GONE
        mPrimaryButton?.isEnabled = true
    }
}
