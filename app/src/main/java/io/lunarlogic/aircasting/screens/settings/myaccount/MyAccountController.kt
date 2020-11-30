package io.lunarlogic.aircasting.screens.settings.myaccount

import android.content.Context
import androidx.room.Database
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.screens.new_session.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        mSettings.logout()
        GlobalScope.launch(Dispatchers.IO) {
            DatabaseProvider.mAppDatabase?.clearAllTables() //metoda do odzyskania mAppDatabase <jakiś g
            // zrobić metode na DatabaseProviderze <?>, obczaić jak na tych Repository to działa
        }
        LoginActivity.start(mContext) //upewnic się że nie moge wrócić do wcześniejszego ekranu!
    }

}