package io.lunarlogic.aircasting.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.*
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.data_classes.SensorThresholdDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsShallowDBObject
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter

class SessionsDataSource(): PositionalDataSource<SessionPresenter>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<SessionPresenter>
    ) {
        val limit = params.requestedLoadSize
        println("ANIA loadInitial " + limit + ", " + params.requestedStartPosition)
        val offset = params.requestedStartPosition
        val dbItems = DatabaseProvider.get().sessions().allByTypeAndStatus(Session.Type.MOBILE, Session.Status.FINISHED, limit, offset)
        val items = dbItems.map { SessionPresenter(Session(it), hashMapOf()) }
        callback.onResult(items, offset, limit)
    }

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<SessionPresenter>
    ) {
        val limit = params.loadSize
        println("ANIA loadRange " + limit + ", " + params.startPosition)
        val offset = params.startPosition
        val dbItems = DatabaseProvider.get().sessions().allByTypeAndStatus(Session.Type.MOBILE, Session.Status.FINISHED, limit, offset)
        val items = dbItems.map { SessionPresenter(Session(it), hashMapOf()) }
        callback.onResult(items)
    }
}

class SessionsDataSourceFactory: DataSource.Factory<Int, SessionPresenter>() {
    val sourceLiveData = MutableLiveData<SessionsDataSource>()
    var latestSource: SessionsDataSource = SessionsDataSource()
    override fun create(): DataSource<Int, SessionPresenter> {
        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}

val datasourceFactory = SessionsDataSourceFactory()

class SessionsViewModel(): ViewModel() {
    private val CONFIG = PagedList.Config.Builder()
        .setPageSize(5)
        .setPrefetchDistance(5)
        .setInitialLoadSizeHint(10)
        .setEnablePlaceholders(false)
        .build()

    private val mDatabase = DatabaseProvider.get()

    fun loadSessionWithMeasurements(uuid: String): LiveData<SessionWithStreamsShallowDBObject?> {
        return mDatabase.sessions().loadLiveDataSessionAndMeasurementsByUUID(uuid)
    }

    fun loadFollowingSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        return datasourceFactory.toLiveData(CONFIG)
    }

    fun loadMobileActiveSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        return loadAllMobileByStatusWithMeasurements(Session.Status.RECORDING)
    }

    fun loadMobileDormantSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        return loadAllMobileByStatusWithMeasurements(Session.Status.FINISHED)
    }

    fun loadFixedSessions(): LiveData<PagedList<SessionPresenter>> {
        return datasourceFactory.toLiveData(CONFIG)
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

    private fun loadAllMobileByStatusWithMeasurements(status: Session.Status): LiveData<PagedList<SessionPresenter>> {
        return datasourceFactory.toLiveData(CONFIG)
    }
}
