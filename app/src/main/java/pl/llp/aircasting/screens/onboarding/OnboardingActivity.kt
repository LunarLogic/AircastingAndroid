package pl.llp.aircasting.screens.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.llp.aircasting.AircastingApplication
import pl.llp.aircasting.lib.Settings
import javax.inject.Inject

class OnboardingActivity: AppCompatActivity() {
    private var controller: OnboardingController? = null

    @Inject
    lateinit var settings: Settings

    companion object {
        fun start(contextActivity: AppCompatActivity?) {
            contextActivity?.let{
                val intent = Intent(it, OnboardingActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as AircastingApplication)
            .appComponent.inject(this)

        val view = OnboardingViewMvcImpl(layoutInflater, null)
        controller = OnboardingController(this, view, supportFragmentManager, settings)

        controller?.onCreate()

        setContentView(view.rootView)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller?.onBackPressed()
    }

}
