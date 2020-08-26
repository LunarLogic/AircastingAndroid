package io.lunarlogic.aircasting

import dagger.Component
import io.lunarlogic.aircasting.di.*
import io.lunarlogic.aircasting.screens.dashboard.fixed.FixedFragment
import io.lunarlogic.aircasting.screens.dashboard.following.FollowingFragment
import io.lunarlogic.aircasting.screens.dashboard.mobile.MobileActiveFragment
import io.lunarlogic.aircasting.screens.dashboard.mobile.MobileDormantFragment
import io.lunarlogic.aircasting.screens.main.MainActivity
import io.lunarlogic.aircasting.screens.new_session.LoginActivity
import io.lunarlogic.aircasting.screens.new_session.NewSessionActivity
import io.lunarlogic.aircasting.screens.new_session.session_details.SessionDetailsFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SettingsModule::class,
        PermissionsModule::class,
        SensorsModule::class,
        NewSessionWizardModule::class
    ]
)
interface AppComponent {
    fun inject(app: AircastingApplication)
    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(fragment: FollowingFragment)
    fun inject(fragment: MobileActiveFragment)
    fun inject(fragment: MobileDormantFragment)
    fun inject(fragment: FixedFragment)
    fun inject(activity: NewSessionActivity)
    fun inject(fragment: SessionDetailsFragment)
}
