package io.lunarlogic.aircasting.sensor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.screens.main.MainActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Sessions are recorded in foreground service, so they are still recorded
 * if the app is in the background or the screen is off
 */

abstract class SensorService : Service() {
    // all devices need to connect in the Foreground Service,
    // otherwise measurements will not record when the app is in background
    // https://trello.com/c/ysfz8lDq/1087-mobile-active-measurements-should-continue-recording-when-app-is-in-background

    private val CHANNEL_ID = "Aircasting ForegroundService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerToEventBus()

        startSensor(intent)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(notificationMessage())
            .setSmallIcon(R.drawable.aircasting)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_REDELIVER_INTENT
    }

    override fun stopService(name: Intent?): Boolean {
        unregisterFromEventBus()
        onStopService()

        return super.stopService(name)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    abstract fun startSensor(intent: Intent?)
    abstract fun onStopService()
    abstract fun notificationMessage() : String

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    @Subscribe
    fun onMessageEvent(event: StopRecordingEvent) {
        stopSelf()
    }

    protected fun registerToEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected fun unregisterFromEventBus() {
        EventBus.getDefault().unregister(this);
    }
}