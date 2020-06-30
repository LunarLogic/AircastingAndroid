package io.lunarlogic.aircasting

import dagger.Component
import io.lunarlogic.aircasting.di.AppModule
import io.lunarlogic.aircasting.di.SettingsModule
import io.lunarlogic.aircasting.screens.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, SettingsModule::class])
interface AppComponent {
    fun inject(app: AircastingApplication)
    fun inject(activity: MainActivity)
}