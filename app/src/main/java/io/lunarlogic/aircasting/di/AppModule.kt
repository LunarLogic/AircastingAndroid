package io.lunarlogic.aircasting.di

import dagger.Module
import dagger.Provides
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.database.repositories.MeasurementStreamsRepository
import io.lunarlogic.aircasting.database.repositories.MeasurementsRepository
import io.lunarlogic.aircasting.database.repositories.SessionsRepository
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.networking.services.ApiServiceFactory
import io.lunarlogic.aircasting.networking.services.SessionsSyncService
import javax.inject.Singleton

@Module
class AppModule(private val app: AircastingApplication) {
    @Provides
    @Singleton
    fun providesApp(): AircastingApplication = app

    @Provides
    @Singleton
    fun providesErrorHandler(): ErrorHandler = ErrorHandler(app)

    @Provides
    @Singleton
    fun providesSessionsRepository(): SessionsRepository = SessionsRepository()

    @Provides
    @Singleton
    fun providesMeasurementStreamsRepository(): MeasurementStreamsRepository = MeasurementStreamsRepository()

    @Provides
    @Singleton
    fun providesMeasurementsRepository(): MeasurementsRepository = MeasurementsRepository()

    @Provides
    fun providesSessionsSyncService(
        apiServiceFactory: ApiServiceFactory,
        settings: Settings,
        errorHandler: ErrorHandler
    ): SessionsSyncService? {
        val authToken = settings.getAuthToken() ?: return null

        val apiService = apiServiceFactory.get(authToken)
        return SessionsSyncService.get(apiService, errorHandler, settings)
    }
}
