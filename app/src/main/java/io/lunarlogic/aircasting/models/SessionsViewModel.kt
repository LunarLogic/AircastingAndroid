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
    private val mPagerConfig = PagingConfig(pageSize = 10)

    val followingSessionsLiveData = Pager(mPagerConfig) { followingSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)
    val mobileActiveSessionsLiveData = Pager(mPagerConfig) { mobileActiveSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)
    val mobileDormantSessionsLiveData = Pager(mPagerConfig) { mobileDormantSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)
    val fixedSessionsLiveData = Pager(mPagerConfig) { fixedSessionsSourceFactory() }.liveData.cachedIn(viewModelScope)

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

    private fun mobileSourceFactoryByStatus(status: Session.Status): PagingSource<Int, SessionWithStreamsDBObject> {
        return mDatabase.sessions().loadAllByTypeAndStatusWithMeasurements(Session.Type.MOBILE, status)
    }
}
