package io.lunarlogic.aircasting.screens.new_session

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import javax.inject.Inject

class LoginActivity: AppCompatActivity() {
    private var controller: LoginController? = null

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var apiServiceFactory: ApiServiceFactory

    companion object {
        fun start(context: Context?) {
            context?.let {
                val intent = Intent(it, LoginActivity::class.java)
                it.startActivity(intent)
            }
        }

        fun startAfterSignOut(context: Context?) {
            context?.let {
                val intent = Intent(it, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                it.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as AircastingApplication)
            .appComponent.inject(this)

        val view = LoginViewMvcImpl(layoutInflater, null)
        controller = LoginController(this, view, settings, apiServiceFactory)

        setContentView(view.rootView)
    }

    override fun onStart() {
        super.onStart()
        controller!!.onStart()
    }


    override fun onStop() {
        super.onStop()
        controller!!.onStop()
    }
}
