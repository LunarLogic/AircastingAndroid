package io.lunarlogic.aircasting.networking.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

// I know it's deprecated, but it's needed to support Android 5.0
import android.net.NetworkInfo

import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.lib.Settings

class ConnectivityManager(apiService: ApiService, context: Context, settings: Settings): BroadcastReceiver() {
    val sessionSyncService = SessionsSyncService.get(apiService, ErrorHandler(context), settings)

    companion object {
        val ACTION = ConnectivityManager.CONNECTIVITY_ACTION
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!isInitialStickyBroadcast && isConnected(context)) {
            sessionSyncService.sync()
        }
    }

    fun isConnected(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
