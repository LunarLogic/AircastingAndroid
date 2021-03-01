package io.lunarlogic.aircasting

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.lunarlogic.aircasting.database.AppDatabase
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.di.*
import io.lunarlogic.aircasting.di.TestSettingsModule
import io.lunarlogic.aircasting.helpers.getMockWebServerFrom
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.screens.new_session.LoginActivity
import io.lunarlogic.aircasting.screens.settings.clear_sd_card.my_account.MyAccountActivity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MyAccountTest {

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var apiServiceFactory: ApiServiceFactory

    @get:Rule
    val testRule : ActivityTestRule<MyAccountActivity>
            = ActivityTestRule(MyAccountActivity::class.java, false, false)

    val app = ApplicationProvider.getApplicationContext<AircastingApplication>()

    lateinit var db : AppDatabase

    private fun setupDagger(){
        val permissionsModule = PermissionsModule()
        val testAppComponent = DaggerTestAppComponent.builder()
            .appModule(AppModule(app))
            .apiModule(TestApiModule())
            .settingsModule(TestSettingsModule())
            .permissionsModule(permissionsModule)
            .build()
        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    private fun setupDatabase(){
        DatabaseProvider.setup(app)
        db = DatabaseProvider.get()
    }

    @Before
    fun setup(){
        setupDagger()
        setupDatabase()
        getMockWebServerFrom(apiServiceFactory).start()
    }

    @After
    fun cleanup(){
        testRule.finishActivity()
        getMockWebServerFrom(apiServiceFactory).shutdown()
    }

    @Test
    fun MyAccountTest(){
        Intents.init()
        settings.login("michal@lunarlogic.io", "XYZ123FAKETOKEN")

        testRule.launchActivity(null)

        // checking if text on text view matches what I want:
        onView(withId(R.id.header)).check(matches(withText("You are currently logged in as\n${settings.getEmail()}")))

        //  performing click on button:
        onView(withId(R.id.sign_out_button)).perform(click())

        Thread.sleep(2000)

        assertEquals(null, settings.getAuthToken())
        assertEquals(null, settings.getEmail())

        // checking if database tables are empty:
        val measurements = db.measurements().getAll()
        val streams = db.measurementStreams().getAll()
        val sessions = db.sessions().getAll()
        val sensorThresholds = db.sensorThresholds().getAll()

        assert(measurements.isEmpty())
        assert(streams.isEmpty())
        assert(sessions.isEmpty())
        assert(sensorThresholds.isEmpty())

        // checking if LoginActivity is launched:
        intended(hasComponent(LoginActivity::class.java.name))

        Espresso.pressBackUnconditionally()
        assertTrue(testRule.activity.isDestroyed)
    }

}
