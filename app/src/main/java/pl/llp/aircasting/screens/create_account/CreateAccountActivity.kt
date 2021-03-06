package pl.llp.aircasting.screens.create_account

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.llp.aircasting.AircastingApplication
import pl.llp.aircasting.R
import pl.llp.aircasting.lib.Settings
import pl.llp.aircasting.networking.services.ApiServiceFactory
import javax.inject.Inject

/**
 * Created by Maria Turnau on 02/09/2020.
 */

class CreateAccountActivity: AppCompatActivity() {
    private var controller: CreateAccountController? = null

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var apiServiceFactory: ApiServiceFactory

    companion object {
        val FROM_ONBOARDING_KEY = "fromOnboarding"
        fun start(contextActivity: AppCompatActivity?, fromOnboarding: Boolean? = false) {
            contextActivity?.let{
                val intent = Intent(it, CreateAccountActivity::class.java)
                intent.putExtra(FROM_ONBOARDING_KEY, fromOnboarding)
                it.startActivity(intent)
                it.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromOnboarding = intent.extras?.get(FROM_ONBOARDING_KEY) as Boolean?

        (application as AircastingApplication)
            .appComponent.inject(this)

        val view = CreateAccountViewMvcImpl(layoutInflater, null, settings, fromOnboarding)
        controller = CreateAccountController(this, view, settings, apiServiceFactory, fromOnboarding)

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
