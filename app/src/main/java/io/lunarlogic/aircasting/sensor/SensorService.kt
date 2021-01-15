package io.lunarlogic.aircasting.sensor

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.screens.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


abstract class SensorService : Service() {
    private val CHANNEL_ID = "Aircasting ForegroundService"

    companion object {
        val MESSAGE_KEY = "inputExtraMessage"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        EventBus.getDefault().register(this);

       startSensor(intent)
        val message = intent?.getStringExtra(MESSAGE_KEY)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aircasting: Sensor Service")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        EventBus.getDefault().unregister(this);
        onStopService()

        return super.stopService(name)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    abstract fun startSensor(intent: Intent?)
    abstract fun onStopService()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    @Subscribe
    fun onMessageEvent(event: StopRecordingEvent) {
        stopSelf()
    }
}
