package io.lunarlogic.aircasting.sensor.airbeam2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import io.lunarlogic.aircasting.events.ApplicationClosed
import io.lunarlogic.aircasting.events.ConfigureSession
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.exceptions.*
import io.lunarlogic.aircasting.lib.ResultCodes
import io.lunarlogic.aircasting.screens.new_session.connect_airbeam.ConnectingAirBeamController
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem
import io.lunarlogic.aircasting.sensor.Session
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


open class AirBeam2Connector(
    private val mErrorHandler: ErrorHandler,
    private val mAirBeamConfigurator: AirBeam2Configurator,
    private val mAirBeam2Reader: AirBeam2Reader
) {
    private val connectionStarted = AtomicBoolean(false)
    private val cancelStarted = AtomicBoolean(false)
    private val SPP_SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var mThread: ConnectThread? = null
    private val ESTIMATED_CONNECTING_TIME_SECONDS = 3000L

    lateinit var listener: ConnectingAirBeamController.Listener

    open fun connect(deviceItem: DeviceItem) {
        if (connectionStarted.get() == false) {
            connectionStarted.set(true)
            registerToEventBus()
            mThread = ConnectThread(deviceItem)
            mThread?.start()
        }
    }

    fun cancel() {
        mThread?.cancel()
    }

    fun configureSession(session: Session, wifiSSID: String?, wifiPassword: String?) {
        mThread?.configureSession(session, wifiSSID, wifiPassword)
    }

    private inner class ConnectThread(private val deviceItem: DeviceItem) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            val device = deviceItem.bluetoothDevice
            device.createRfcommSocketToServiceRecord(SPP_SERIAL)
        }

        private lateinit var mOutputStream: OutputStream

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter?.cancelDiscovery()

            try {
                mmSocket?.use { socket ->
                    socket.connect()
                    // wait until connection is finished before sending anything to AirBeam
                    sleep(ESTIMATED_CONNECTING_TIME_SECONDS)

                    mOutputStream = socket.outputStream

                    listener.onConnectionSuccessful(deviceItem.id)
                    mAirBeam2Reader.run(socket.inputStream)
                }
            } catch(e: IOException) {
                if (cancelStarted.get() == false) {
                    val message = mErrorHandler.obtainMessage(
                        ResultCodes.AIR_BEAM2_CONNECTION_OPEN_FAILED,
                        AirBeam2ConnectionOpenFailed(e)
                    )
                    message.sendToTarget()
                    cancel()
                }
            } catch(e: Exception) {
                val message = mErrorHandler.obtainMessage(ResultCodes.AIRCASTING_UNKNOWN_ERROR, UnknownError(e))
                message.sendToTarget()
                cancel()
            }
        }

        fun cancel() {
            unregisterFromEventBus()

            if (cancelStarted.get() == false) {
                cancelStarted.set(true)
                connectionStarted.set(false)
                try {
                    mmSocket?.close()
                } catch (e: IOException) {
                    mErrorHandler.handle(AirBeam2ConnectionCloseFailed(e))
                }
            }
        }

        fun configureSession(session: Session, wifiSSID: String?, wifiPassword: String?) {
            try {
                mAirBeamConfigurator.configureSessionType(session, mOutputStream)
                if (session.isFixed()) {
                    mAirBeamConfigurator.configureFixedSessionDetails(
                        session.location!!,
                        session.streamingMethod!!,
                        wifiSSID,
                        wifiPassword,
                        mOutputStream
                    )
                }
            } catch (e: IOException) {
                mErrorHandler.handle(AirBeam2ConfiguringFailed(e))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onMessageEvent(event: ConfigureSession) {
        configureSession(event.session, event.wifiSSID, event.wifiPassword)
    }

    @Subscribe
    fun onMessageEvent(event: ApplicationClosed) {
        cancel()
    }

    @Subscribe
    fun onMessageEvent(event: StopRecordingEvent) {
        cancel()
    }

    private fun registerToEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private fun unregisterFromEventBus() {
        EventBus.getDefault().unregister(this);
    }
}
