package io.lunarlogic.aircasting

import dagger.Component
import io.lunarlogic.aircasting.di.AppModule
import io.lunarlogic.aircasting.di.MockWebServerModule
import io.lunarlogic.aircasting.di.SettingsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, SettingsModule::class, MockWebServerModule::class])
interface TestAppComponent: AppComponent {
//    fun inject(test: LoginTest)
    fun inject(test: MobileSessionTest)
}