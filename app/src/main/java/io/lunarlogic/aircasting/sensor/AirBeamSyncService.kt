package io.lunarlogic.aircasting.sensor

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.bluetooth.BluetoothManager
import io.lunarlogic.aircasting.exceptions.BLENotSupported
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem
import io.lunarlogic.aircasting.sensor.airbeam3.DownloadFromSDCardService
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AirBeamSyncService: SensorService(),
    AirBeamConnector.Listener {

    @Inject
    lateinit var airBeamDiscoveryService: AirBeamDiscoveryService

    @Inject
    lateinit var airbeamConnectorFactory: AirBeamConnectorFactory

    @Inject
    lateinit var bluetoothManager: BluetoothManager

    @Inject
    lateinit var errorHandler: ErrorHandler

    private var mDeviceItem: DeviceItem? = null
    private var mAirBeamConnector: AirBeamConnector? = null

    private var clearSDCard = false

    companion object {
        val CLEAR_SD_CARD_KEY = "inputExtraClearSDCard"

        fun startService(context: Context, clearSDCard: Boolean = false) {
            val startIntent = Intent(context, AirBeamSyncService::class.java)

            startIntent.putExtra(CLEAR_SD_CARD_KEY, clearSDCard)

            ContextCompat.startForegroundService(context, startIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val app = application as AircastingApplication
        val appComponent = app.appComponent
        appComponent.inject(this)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun startSensor(intent: Intent?) {
        intent ?: return

        this.clearSDCard = intent.getBooleanExtra(CLEAR_SD_CARD_KEY, false)

        airBeamDiscoveryService.find(
            deviceSelector = { deviceItem -> deviceItem.isSyncable() },
            onDiscoverySuccessful = { deviceItem -> connect(deviceItem) },
            onDiscoveryFailed = { onDiscoveryFailed() }
        )
    }

    private fun connect(deviceItem: DeviceItem) {
        mDeviceItem = deviceItem
        mAirBeamConnector = airbeamConnectorFactory.get(deviceItem)

        mAirBeamConnector?.registerListener(this)
        try {
            mAirBeamConnector?.connect(deviceItem)
        } catch (e: BLENotSupported) {
            errorHandler.handleAndDisplay(e)
            onConnectionFailed(deviceItem.id)
        }
    }

    override fun onStopService() {
        // nothing
    }

    override fun notificationMessage(): String {
        return getString(R.string.ab_sync_service_notification_message)
    }

    override fun onConnectionSuccessful(deviceItem: DeviceItem, sessionUUID: String?) {
        showInfo("Connection to ${deviceItem.name} successful.")

        if (clearSDCard) {
            mAirBeamConnector?.clearSDCard()
        } else {
            mAirBeamConnector?.triggerSDCardDownload()
        }
    }

    override fun onConnectionFailed(deviceId: String) {
        // TODO: temporary thing
        showInfo("Connection to ${mDeviceItem?.name} failed.")
    }

    override fun onDisconnect(deviceId: String) {
        stopSelf()
    }

    private fun onDiscoveryFailed() {
        // TODO: temporary thing
        showInfo("Discovery failed.")
    }

    private fun showInfo(info: String) {
        EventBus.getDefault().post(DownloadFromSDCardService.SyncEvent(info))
    }
}
