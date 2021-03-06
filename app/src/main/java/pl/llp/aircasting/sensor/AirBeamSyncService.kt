package pl.llp.aircasting.sensor

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat
import pl.llp.aircasting.AircastingApplication
import pl.llp.aircasting.screens.new_session.select_device.DeviceItem
import pl.llp.aircasting.sensor.airbeam3.sync.SDCardSyncService
import javax.inject.Inject

class AirBeamSyncService: AirBeamService() {
    @Inject
    lateinit var airBeamDiscoveryService: AirBeamDiscoveryService

    @Inject
    lateinit var sdCardSyncService: SDCardSyncService

    companion object {
        val DEVICE_ITEM_KEY = "inputExtraDeviceItem"

        fun startService(context: Context, deviceItem: DeviceItem) {
            val startIntent = Intent(context, AirBeamSyncService::class.java)

            startIntent.putExtra(DEVICE_ITEM_KEY, deviceItem as Parcelable)

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

        val deviceItem = intent.getParcelableExtra(AirBeamRecordSessionService.DEVICE_ITEM_KEY) as DeviceItem

        connect(deviceItem)
    }

    override fun onConnectionSuccessful(deviceItem: DeviceItem, sessionUUID: String?) {
        val airBeamConnector = mAirBeamConnector ?: return
        sdCardSyncService.run(airBeamConnector, deviceItem)
    }
}
