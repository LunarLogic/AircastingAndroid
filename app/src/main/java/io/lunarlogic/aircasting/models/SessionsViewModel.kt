package io.lunarlogic.aircasting.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.data_classes.SensorThresholdDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject

class SessionsViewModel(): ViewModel() {
    private val mDatabase = DatabaseProvider.get()

//    val followingSessionsFlow = flowBy(followingSessionsSourceFactory()).liveData.cachedIn(viewModelScope)
    val mobileActiveSessionsFlow = Pager(PagingConfig(pageSize = 10, enablePlaceholders = false, maxSize = 50)) { mobileActiveSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)
    val mobileDormantSessionsFlow = Pager(PagingConfig(pageSize = 10)) { mobileDormantSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)
//    val fixedSessionsFlow = flowBy(fixedSessionsSourceFactory()).flow.cachedIn(viewModelScope)

    fun loadSessionWithMeasurements(uuid: String): LiveData<SessionWithStreamsDBObject?> {
        return mDatabase.sessions().loadLiveDataSessionAndMeasurementsByUUID(uuid)
    }

    fun findOrCreateSensorThresholds(session: Session): List<SensorThreshold> {
        return findOrCreateSensorThresholds(session.streams)
    }

    fun findOrCreateSensorThresholds(): List<SensorThreshold> {
        val streams = mDatabase.measurementStreams().unique().map { MeasurementStream(it) }
        return findOrCreateSensorThresholds(streams)
    }

    private fun findOrCreateSensorThresholds(streams: List<MeasurementStream>): List<SensorThreshold> {
        val existingThresholds = findSensorThresholds(streams)
        var newThresholds = createSensorThresholds(streams, existingThresholds)

        val thresholds = mutableListOf<SensorThreshold>()
        thresholds.addAll(existingThresholds)
        thresholds.addAll(newThresholds)

        return thresholds
    }

    private fun findSensorThresholds(streams: List<MeasurementStream>): List<SensorThreshold> {
        val sensorNames = streams.map { it.sensorName }
        return mDatabase.sensorThresholds()
            .allBySensorNames(sensorNames)
            .map { SensorThreshold(it) }
    }

    private fun createSensorThresholds(streams: List<MeasurementStream>, existingThreshols: List<SensorThreshold>): List<SensorThreshold> {
        val existingSensorNames = existingThreshols.map { it.sensorName }
        val toCreate = streams.filter { !existingSensorNames.contains(it.sensorName) }

        return toCreate.map { stream ->
            val sensorThresholdDBObject = SensorThresholdDBObject(stream)
            mDatabase.sensorThresholds().insert(sensorThresholdDBObject)
            SensorThreshold(sensorThresholdDBObject)
        }
    }

    fun updateSensorThreshold(sensorThreshold: SensorThreshold) {
        mDatabase.sensorThresholds().update(
            sensorThreshold.sensorName,
            sensorThreshold.thresholdVeryLow,
            sensorThreshold.thresholdLow,
            sensorThreshold.thresholdMedium,
            sensorThreshold.thresholdHigh,
            sensorThreshold.thresholdVeryHigh
        )
    }

    fun updateFollowedAt(session: Session) {
        mDatabase.sessions().updateFollowedAt(session.uuid, session.followedAt)
    }

    private fun followingSessionsSourceFactory(): PagingSource<Int, SessionWithStreamsDBObject> {
        return mDatabase.sessions().loadFollowingWithMeasurements()
    }

    private fun mobileActiveSessionsSourceFactory(): PagingSource<Int, SessionWithStreamsDBObject> {
        return mobileSourceFactoryByStatus(Session.Status.RECORDING)
    }

    private fun mobileDormantSessionsSourceFactory(): PagingSource<Int, SessionWithStreamsDBObject> {
        return mobileSourceFactoryByStatus(Session.Status.FINISHED)
    }

    fun fixedSessionsSourceFactory(): PagingSource<Int, SessionWithStreamsDBObject> {
        return mDatabase.sessions().loadAllByType(Session.Type.FIXED)
    }

    private fun flowBy(pagingSourceFactory: PagingSource<Int, SessionWithStreamsDBObject>): Pager<Int, SessionWithStreamsDBObject> {
        return Pager(PagingConfig(pageSize = 10)) { pagingSourceFactory }
    }

    private fun mobileSourceFactoryByStatus(status: Session.Status): PagingSource<Int, SessionWithStreamsDBObject> {
        return mDatabase.sessions().loadAllByTypeAndStatusWithMeasurements(Session.Type.MOBILE, status)
    }
}
