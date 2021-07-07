package pl.llp.aircasting.sensor

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import pl.llp.aircasting.database.DatabaseProvider
import pl.llp.aircasting.database.repositories.SessionsRepository
import pl.llp.aircasting.events.AirBeamConnectionFailedEvent
import pl.llp.aircasting.events.AirBeamConnectionSuccessfulEvent
import pl.llp.aircasting.events.SensorDisconnectedEvent
import pl.llp.aircasting.lib.safeRegister
import pl.llp.aircasting.models.Session
import pl.llp.aircasting.screens.new_session.select_device.DeviceItem
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlin.concurrent.timerTask

class AirBeamReconnector(
    private val mContext: Context,
    private val mSessionsRepository: SessionsRepository,
    private val mAirBeamDiscoveryService: AirBeamDiscoveryService
) {
    interface Listener {
        fun beforeReconnection(session: Session)
        fun errorCallback()
        fun finallyCallback(session: Session)
    }

    private var mSession: Session? = null
    private var mErrorCallback: (() -> Unit)? = null
    private var mFinallyCallback: (() -> Unit)? = null
    private var mListener: AirBeamReconnector.Listener? = null

    var mReconnectionTriesNumber: Int? = null
    private val RECONNECTION_TRIES_MAX = 5
    private val RECONNECTION_TRIES_INTERVAL = 15000L // 15s between reconnection tries

    private val RECONNECTION_TRIES_RESET_DELAY = RECONNECTION_TRIES_INTERVAL + 5000L // we need to have delay
    // greater than interval between tries so we don't trigger another try after we successfully reconnected

    fun registerListener(listener: AirBeamReconnector.Listener) {
        mListener = listener
    }

    fun disconnect(session: Session) {
        sendDisconnectedEvent(session)
        updateSessionStatus(session, Session.Status.DISCONNECTED)
    }

    fun reconnect(session: Session, errorCallback: () -> Unit, finallyCallback: () -> Unit) {
        EventBus.getDefault().safeRegister(this)

        if (mReconnectionTriesNumber != null) {
            mReconnectionTriesNumber?.let { tries ->
                if (tries > RECONNECTION_TRIES_MAX) {
                    return
                }
            }
        } else {
            // disconnecting first to make sure the connector thread is stopped correctly etc
            sendDisconnectedEvent(session)
        }

        mSession = session
        mErrorCallback = errorCallback
        mFinallyCallback = finallyCallback

        mAirBeamDiscoveryService.find(
            deviceSelector = { deviceItem -> deviceItem.id == session.deviceId },
            onDiscoverySuccessful = { deviceItem -> reconnect(deviceItem) },
            onDiscoveryFailed = { onDiscoveryFailed() }
        )
    }

    fun initReconnectionTries(session: Session) {
        if (mReconnectionTriesNumber != null) return
        mListener?.beforeReconnection(session)
        mReconnectionTriesNumber = 1
        reconnect(session, { mListener?.errorCallback() }, { mListener?.finallyCallback(session) })
    }

    private fun reconnect(deviceItem: DeviceItem) {
        AirBeamReconnectSessionService.startService(mContext, deviceItem, mSession?.uuid)
    }

    private fun onDiscoveryFailed() {
        if (mReconnectionTriesNumber != null && mReconnectionTriesNumber!! < RECONNECTION_TRIES_MAX) {
            mReconnectionTriesNumber = mReconnectionTriesNumber?.plus(1)
            Thread.sleep(RECONNECTION_TRIES_INTERVAL)
            if (mSession != null && mErrorCallback != null && mFinallyCallback != null) {
                reconnect(mSession!!, mErrorCallback!!, mFinallyCallback!!)
            }
        } else {
            mFinallyCallback?.invoke()
        }
    }

    private fun sendDisconnectedEvent(session: Session) {
        val deviceId = session.deviceId
        deviceId?.let { EventBus.getDefault().post(SensorDisconnectedEvent(deviceId, session.uuid)) }
    }

    private fun updateSessionStatus(session: Session?, status: Session.Status) {
        session?.let { session ->
            DatabaseProvider.runQuery {
                mSessionsRepository.updateSessionStatus(session, status)
            }
        }
    }

    @Subscribe
    fun onMessageEvent(event: AirBeamConnectionSuccessfulEvent) {
        resetTriesNumberWithDelay()

        updateSessionStatus(mSession, Session.Status.RECORDING)

        mFinallyCallback?.invoke()
        unregisterFromEventBus()
    }

    private fun resetTriesNumberWithDelay() {
        if (mReconnectionTriesNumber != null) {
            val timerTask = timerTask {
                mReconnectionTriesNumber = null
            }
            Timer().schedule(timerTask, RECONNECTION_TRIES_RESET_DELAY)
        }
    }

    @Subscribe
    fun onMessageEvent(event: AirBeamConnectionFailedEvent) {
        if (mReconnectionTriesNumber != null) {
            mReconnectionTriesNumber?.let { tries ->
                if (tries > RECONNECTION_TRIES_MAX) {
                    // TODO: should we invoke callbacks here?
                    return
                } else {
                    mReconnectionTriesNumber = mReconnectionTriesNumber?.plus(1)
                    Thread.sleep(RECONNECTION_TRIES_INTERVAL)
                    reconnect(event.deviceItem)
                }
            }
        } else {
            mErrorCallback?.invoke()
            mFinallyCallback?.invoke()
            unregisterFromEventBus()
        }
    }

    private fun unregisterFromEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
