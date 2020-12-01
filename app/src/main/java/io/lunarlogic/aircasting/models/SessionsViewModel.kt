package io.lunarlogic.aircasting.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.*
import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.data_classes.SensorThresholdDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.screens.dashboard.SessionsTab

class Foo {
    private val mSessionsDao = DatabaseProvider.get().sessions()

    fun load(tab: SessionsTab, limit: Int, offset: Int): List<SessionWithStreamsDBObject> {
        return when (tab) {
            SessionsTab.FOLLOWING -> loadFollowing(limit, offset)
            SessionsTab.MOBILE_ACTIVE -> loadActive(limit, offset)
            SessionsTab.MOBILE_DORMANT -> loadDormant(limit, offset)
            SessionsTab.FIXED -> loadFixed(limit, offset)
        }
    }

    private fun loadFollowing(limit: Int, offset: Int): List<SessionWithStreamsDBObject> {
        return mSessionsDao.loadFollowingWithMeasurements(limit, offset)
    }

    private fun loadActive(limit: Int, offset: Int): List<SessionWithStreamsDBObject> {
        return mSessionsDao.loadAllByTypeAndStatusWithMeasurements(
            Session.Type.MOBILE, Session.Status.RECORDING, limit, offset)
    }

    private fun loadDormant(limit: Int, offset: Int): List<SessionWithStreamsDBObject> {
        return mSessionsDao.loadAllByTypeAndStatusWithMeasurements(
            Session.Type.MOBILE, Session.Status.FINISHED, limit, offset)
    }

    private fun loadFixed(limit: Int, offset: Int): List<SessionWithStreamsDBObject> {
        return mSessionsDao.loadAllByType(Session.Type.FIXED, limit, offset)
    }
}

class SessionsDataSource(val tab: SessionsTab): PositionalDataSource<SessionPresenter>() {
    val foo = Foo()

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<SessionPresenter>
    ) {
        val limit = params.requestedLoadSize
        val offset = params.requestedStartPosition
        val dbItems = foo.load(tab, limit, offset)
        println("ANIA loadInitial " + limit + ", " + params.requestedStartPosition + " " + dbItems.size)
        val items = dbItems.map { SessionPresenter(Session(it), hashMapOf()) }
        callback.onResult(items, offset)
    }

    override fun loadRange(
        params: LoadRangeParams,
        callback: LoadRangeCallback<SessionPresenter>
    ) {
        val limit = params.loadSize
        println("ANIA loadRange " + limit + ", " + params.startPosition)
        val offset = params.startPosition
        val dbItems = foo.load(tab, limit, offset)
        val items = dbItems.map { SessionPresenter(Session(it), hashMapOf()) }
        callback.onResult(items)
    }
}

class SessionsDataSourceFactory(tab: SessionsTab): DataSource.Factory<Int, SessionPresenter>() {
    val sourceLiveData = MutableLiveData<SessionsDataSource>()
    var latestSource: SessionsDataSource = SessionsDataSource(tab)
    override fun create(): DataSource<Int, SessionPresenter> {
        sourceLiveData.postValue(latestSource)
        return latestSource
    }
}

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

    fun loadFollowingSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        val datasourceFactory = SessionsDataSourceFactory(SessionsTab.FOLLOWING)
        return datasourceFactory.toLiveData(CONFIG)
    }

    fun loadMobileActiveSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        val datasourceFactory = SessionsDataSourceFactory(SessionsTab.MOBILE_ACTIVE)
        return datasourceFactory.toLiveData(CONFIG)
    }

    fun loadMobileDormantSessionsWithMeasurements(): LiveData<PagedList<SessionPresenter>> {
        val datasourceFactory = SessionsDataSourceFactory(SessionsTab.MOBILE_DORMANT)
        return datasourceFactory.toLiveData(CONFIG)
    }

    fun loadFixedSessions(): LiveData<PagedList<SessionPresenter>> {
        val datasourceFactory = SessionsDataSourceFactory(SessionsTab.FIXED)
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
}
