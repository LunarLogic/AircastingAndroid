package io.lunarlogic.aircasting.screens.settings.myaccount

import android.content.Context
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.events.LogoutEvent
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.services.SessionsSyncService
import io.lunarlogic.aircasting.screens.new_session.LoginActivity
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MyAccountController(
    private val mContext: Context,
    private val mViewMvc: MyAccountViewMvc,
    private val mSettings: Settings
) : MyAccountViewMvc.Listener{

    fun onStart(){
        mViewMvc.registerListener(this)
        mViewMvc.bindAccountDetail(mSettings.getEmail())
    }

    fun onStop(){
        mViewMvc.unregisterListener(this)
    }

    override fun onSignOutClicked() {
        val event = LogoutEvent()
        EventBus.getDefault().post(event)

        mSettings.logout()
        SessionsSyncService.cancel()
        SessionsSyncService.destroy()

        runBlocking {
            val query = GlobalScope.async(Dispatchers.IO) {
                DatabaseProvider.get().clearAllTables()
            }
            query.await()
        }

        DatabaseProvider.runQuery {
            val count = DatabaseProvider.get().sessions().getCount()
            println("MARYSIA: sessions count ${count}")
        }

        Thread.sleep(3000)
        LoginActivity.startAfterSignOut(mContext)
    }

    fun count() : Int = runBlocking {
        DatabaseProvider.get().sessions().getCount()
    }
}
