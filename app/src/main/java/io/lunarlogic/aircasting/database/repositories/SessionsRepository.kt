package io.lunarlogic.aircasting.database.repositories

import io.lunarlogic.aircasting.database.DatabaseProvider
import io.lunarlogic.aircasting.database.data_classes.SessionDBObject
import io.lunarlogic.aircasting.sensor.Session
import java.util.*

class SessionsRepository {
    private val mDatabase = DatabaseProvider.get()

    fun insert(session: Session): Long {
        val sessionDBObject =
            SessionDBObject(session)
        return mDatabase.sessions().insert(sessionDBObject)
    }

    fun getActiveSessionIdByDeviceId(deviceId: String): Long? {
        val sessionDBObject = mDatabase.sessions().loadSessionByByDeviceIdAndStatus(deviceId, Session.Status.RECORDING)

        if (sessionDBObject != null) {
            return sessionDBObject.id
        } else {
            return null
        }
    }

    fun loadSessionAndMeasurementsByUUID(uuid: String): Session? {
        val sessionDBObject = mDatabase.sessions().loadSessionAndMeasurementsByUUID(uuid)

        if (sessionDBObject != null) {
            return Session(sessionDBObject)
        } else {
            return null
        }
    }

    fun update(session: Session) {
        mDatabase.sessions().update(session.uuid, session.name, session.tags,
            session.endTime!!, session.status)
    }

    fun alreadyExistsForDeviceId(deviceId: String): Boolean {
        return mDatabase.sessions().loadSessionByByDeviceIdAndStatus(deviceId, Session.Status.RECORDING) != null
    }

    fun stopSessions() {
        mDatabase.sessions().updateStatusAndEndTime(Session.Status.FINISHED, Date())
    }

    fun finishedSessions(): List<Session> {
        return mDatabase.sessions().byStatus(Session.Status.FINISHED)
            .map { dbObject -> Session(dbObject) }
    }

    fun delete(uuids: List<String>) {
        if (!uuids.isEmpty()) {
            mDatabase.sessions().delete(uuids)
        }
    }

    fun markForRemoval(uuids: List<String>) {
        mDatabase.sessions().markForRemoval(uuids)
    }

    fun deleteMarkedForRemoval() {
        mDatabase.sessions().deleteMarkedForRemoval()
    }

    fun updateOrCreate(session: Session): Long? {
        val sessionDbObject = mDatabase.sessions().loadSessionByUUID(session.uuid)
        if (sessionDbObject == null) {
            return insert(session)

        } else {
            update(session)
            return null
        }
    }
}