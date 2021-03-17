package io.lunarlogic.aircasting.sensor

import android.content.Context
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.repositories.SessionsRepository
import io.lunarlogic.aircasting.events.AirBeamConnectionFailedEvent
import io.lunarlogic.aircasting.events.AirBeamConnectionSuccessfulEvent
import io.lunarlogic.aircasting.events.SensorDisconnectedEvent
import io.lunarlogic.aircasting.lib.safeRegister
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class AirBeamReconnector(
    private val mContext: Context,
    private val mSessionsRepository: SessionsRepository,
    private val mAirBeamDiscoveryService: AirBeamDiscoveryService
) {
    private var mSession: Session? = null
    private var mErrorCallback: (() -> Unit)? = null
    private var mFinallyCallback: (() -> Unit)? = null

    fun disconnect(session: Session) {
        sendDisconnectedEvent(session)
        updateSessionStatus(session, Session.Status.DISCONNECTED)
    }

    fun reconnect(session: Session, errorCallback: () -> Unit, finallyCallback: () -> Unit) {
        EventBus.getDefault().safeRegister(this)

        // disconnecting first to make sure the connector thread is stopped correctly etc
        sendDisconnectedEvent(session)

        mSession = session
        mErrorCallback = errorCallback
        mFinallyCallback = finallyCallback

        mAirBeamDiscoveryService.find(
            deviceSelector = { deviceItem -> deviceItem.id == session.deviceId },
            onDiscoverySuccessful = { deviceItem -> reconnect(deviceItem) },
            onDiscoveryFailed = { onDiscoveryFailed() }
        )
    }

    private fun reconnect(deviceItem: DeviceItem) {
        AirBeamReconnectSessionService.startService(mContext, deviceItem, mSession?.uuid)
    }

    private fun onDiscoveryFailed() {
        mFinallyCallback?.invoke()
    }

    private fun sendDisconnectedEvent(session: Session) {
        val deviceId = session.deviceId
        deviceId?.let { EventBus.getDefault().post(SensorDisconnectedEvent(deviceId)) }
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
        updateSessionStatus(mSession, Session.Status.RECORDING)

        mFinallyCallback?.invoke()
    }

    @Subscribe
    fun onMessageEvent(event: AirBeamConnectionFailedEvent) {
        mErrorCallback?.invoke()
        mFinallyCallback?.invoke()
    }
}
