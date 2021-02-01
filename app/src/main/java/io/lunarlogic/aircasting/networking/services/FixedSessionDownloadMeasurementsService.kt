package io.lunarlogic.aircasting.networking.services

import io.lunarlogic.aircasting.database.repositories.MeasurementStreamsRepository
import io.lunarlogic.aircasting.database.repositories.MeasurementsRepository
import io.lunarlogic.aircasting.database.repositories.SessionsRepository
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.lib.DateConverter
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.networking.responses.SessionWithMeasurementsResponse
import kotlinx.coroutines.*
import retrofit2.Call
import java.util.*


class FixedSessionDownloadMeasurementsService(private val apiService: ApiService, private val errorHandler: ErrorHandler) {
    private val sessionsRepository = SessionsRepository()
    private val thread = DownloadThread()
    private val downloadMeasurementsService = DownloadMeasurementsService(apiService, errorHandler)

    fun start() {
        thread.start()
    }

    fun stop() {
        thread.cancel()
    }

    fun pause() {
        thread.paused = true
    }

    fun resume() {
        thread.paused = false
    }

    // TODO: consider using WorkManager
    // https://developer.android.com/topic/libraries/architecture/workmanager/basics
    private inner class DownloadThread() : Thread() {
        private val POLL_INTERVAL = 60 * 1000L // 1 minute
        var paused = false
        private var call: Call<SessionWithMeasurementsResponse>? = null


        override fun run() {
            try {
                while (!isInterrupted) {
                    downloadMeasurements()
                    sleep(POLL_INTERVAL)

                    while (paused) {
                        sleep(1000)
                    }
                }
            } catch (e: InterruptedException) {
                return
            }
        }

        fun cancel() {
            interrupt()
            call?.cancel()
        }

        private fun downloadMeasurements() {
            val dbSessions = sessionsRepository.fixedSessions()
            dbSessions.forEach { dbSession ->
                val session = Session(dbSession)
                downloadMeasurements(dbSession.id, session)
            }
        }

        private fun downloadMeasurements(sessionId: Long, session: Session) {
            GlobalScope.launch(Dispatchers.IO) {
                call =
                    downloadMeasurementsService.enqueueDownloadingMeasurements(sessionId, session)
            }
        }
    }
}
