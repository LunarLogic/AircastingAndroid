package io.lunarlogic.aircasting

import android.app.Application
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import okhttp3.mockwebserver.MockResponse
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.lunarlogic.aircasting.di.AppModule
import io.lunarlogic.aircasting.di.SettingsModule
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.lib.SettingsInterface
import io.lunarlogic.aircasting.screens.main.MainActivity
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton


class FakeSettings(application: Application): SettingsInterface {
    override fun getAuthToken(): String? {
        return "fake!"
    }
}

class TestSettingsModule: SettingsModule() {
    override fun providesSettings(application: AircastingApplication): SettingsInterface = FakeSettings(application)
}

@RunWith(AndroidJUnit4::class)
class ConnectAirbeamInstrumentedTest {
    @Inject
    lateinit var settings: SettingsInterface


    val gson = Gson()
    val body = mapOf(
        "email" to "ania@example.org",
        "username" to "ania",
        "username" to "ania",
        "authentication_token" to "XYZ123FAKETOKEN"
    )
    val mockResponse = MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(gson.toJson(body))

    @get:Rule
    val myTestRule = MockResponseRule(mockResponse)

    @get:Rule
    val testRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setup() {
        val app = InstrumentationRegistry.getTargetContext().applicationContext as AircastingApplication
        val testAppComponent = DaggerTestAppComponent.builder()
            .appModule(AppModule(app))
            .settingsModule(TestSettingsModule())
            .build()
        app.appComponent = testAppComponent
        testAppComponent.inject(this)
        println("==== TestAppComponent injected")
    }

    @Test
    fun verifySelectDeviceFlow() {
        testRule.launchActivity(null)

        onView(withId(R.id.login_button)).perform(click())
    }
}