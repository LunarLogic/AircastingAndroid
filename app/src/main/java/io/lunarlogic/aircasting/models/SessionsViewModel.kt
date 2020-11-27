package io.lunarlogic.aircasting.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.data_classes.SensorThresholdDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsShallowDBObject
class FooBoundaryCallback : PagedList.BoundaryCallback<SessionDBObject>() {
    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        println("ANIA onZeroItemsLoaded")
    }

    override fun onItemAtEndLoaded(itemAtEnd: SessionDBObject) {
        super.onItemAtEndLoaded(itemAtEnd)
        println("ANIA onItemAtEndLoaded")
    }

    override fun onItemAtFrontLoaded(itemAtFront: SessionDBObject) {
        super.onItemAtFrontLoaded(itemAtFront)
        println("ANIA onItemAtFrontLoaded")
    }
}

val fooBoundaryCallback = FooBoundaryCallback()

class SessionsViewModel(): ViewModel() {
    private val CONFIG = PagedList.Config.Builder()
        .setPageSize(5)
        .setPrefetchDistance(5)
        .setInitialLoadSizeHint(10)
        .setEnablePlaceholders(false)
        .build()

    private val mDatabase = DatabaseProvider.get()

    fun loadSessionWithMeasurements(uuid: String): LiveData<SessionWithStreamsDBObject?> {
        return mDatabase.sessions().loadLiveDataSessionAndMeasurementsByUUID(uuid)
    }

    fun loadFollowingSessionsWithMeasurements(): LiveData<PagedList<SessionDBObject>> {
        val foo = LivePagedListBuilder(mDatabase.sessions().loadFollowingWithMeasurements(), CONFIG)
        foo.setBoundaryCallback(fooBoundaryCallback)

        return foo.build()
    }

    fun loadMobileActiveSessionsWithMeasurements(): LiveData<PagedList<SessionDBObject>> {
        return loadAllMobileByStatusWithMeasurements(Session.Status.RECORDING)
    }

    fun loadMobileDormantSessionsWithMeasurements(): LiveData<PagedList<SessionDBObject>> {
        return loadAllMobileByStatusWithMeasurements(Session.Status.FINISHED)
    }

    fun loadFixedSessions(): LiveData<PagedList<SessionDBObject>> {
        val foo = LivePagedListBuilder(mDatabase.sessions().loadAllShallowByType(Session.Type.FIXED), CONFIG)
        foo.setBoundaryCallback(fooBoundaryCallback)

        return foo.build()
    }

    fun findOrCreateSensorThresholds(session: Session): List<SensorThreshold> {
        return findOrCreateSensorThresholds(session.streams)
    }

    fun findOrCreateSensorThresholds(streams: List<MeasurementStream>): List<SensorThreshold> {
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

    private fun loadAllMobileByStatusWithMeasurements(status: Session.Status): LiveData<PagedList<SessionDBObject>> {
        val foo = LivePagedListBuilder(mDatabase.sessions().loadAllByTypeAndStatusWithMeasurements(Session.Type.MOBILE, status), CONFIG)
        foo.setBoundaryCallback(fooBoundaryCallback)

        return foo.build()
    }
}
