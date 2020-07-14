package io.lunarlogic.aircasting.database.data_classes

import androidx.lifecycle.LiveData
import androidx.room.*
import io.lunarlogic.aircasting.sensor.Session
import java.util.*
import kotlin.collections.ArrayList

@Entity(
    tableName = "sessions",
    indices = [
        Index("device_id")
    ]
)
data class SessionDBObject(
    @ColumnInfo(name = "uuid") val uuid: String,
    @ColumnInfo(name = "type") val type: Session.Type,
    @ColumnInfo(name = "device_id") val deviceId: String?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "tags") val tags: ArrayList<String> = arrayListOf(),
    @ColumnInfo(name = "start_time") val startTime: Date,
    @ColumnInfo(name = "end_time") val endTime: Date?,
    @ColumnInfo(name = "status") val status: Session.Status = Session.Status.NEW,
    @ColumnInfo(name = "version") val version: Int = 0,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(session: Session):
            this(
                session.uuid,
                session.type,
                session.deviceId,
                session.name,
                session.tags,
                session.startTime,
                session.endTime,
                session.status
            )
}


class SessionWithStreamsDBObject {
    @Embedded
    lateinit var session: SessionDBObject

    @Relation(
        parentColumn = "id",
        entityColumn = "session_id",
        entity = MeasurementStreamDBObject::class
    )
    lateinit var streams: List<StreamWithMeasurementsDBObject>
}


class StreamWithMeasurementsDBObject {
    @Embedded
    lateinit var stream: MeasurementStreamDBObject

    @Relation(
        parentColumn = "id",
        entityColumn = "measurement_stream_id",
        entity = MeasurementDBObject::class
    )
    lateinit var measurements: List<MeasurementDBObject>
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type AND status=:status ORDER BY start_time DESC")
    fun loadAllByTypeAndStatusWithMeasurements(type: Session.Type, status: Session.Status): LiveData<List<SessionWithStreamsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type ORDER BY start_time DESC")
    fun loadAllByType(type: Session.Type): LiveData<List<SessionWithStreamsDBObject>>

    @Query("SELECT * FROM sessions WHERE status=:status")
    fun byStatus(status: Session.Status): List<SessionDBObject>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(session: SessionDBObject): Long

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun loadSessionAndMeasurementsByUUID(uuid: String): SessionWithStreamsDBObject?

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun loadSessionByUUID(uuid: String): SessionDBObject?

    @Query("SELECT * FROM sessions WHERE device_id=:deviceId AND status=:status AND deleted=0")
    fun loadSessionByByDeviceIdAndStatus(deviceId: String, status: Session.Status): SessionDBObject?

    @Query("UPDATE sessions SET name=:name, tags=:tags, end_time=:endTime, status=:status WHERE uuid=:uuid")
    fun update(uuid: String, name: String, tags: ArrayList<String>, endTime: Date, status: Session.Status)

    @Query("UPDATE sessions SET status=:status, end_time=:endTime WHERE type=:type")
    fun updateStatusAndEndTimeForSessionType(status: Session.Status, endTime: Date, type: Session.Type)

    @Query("DELETE FROM sessions")
    fun deleteAll()

    @Query("UPDATE sessions SET deleted=1 WHERE uuid in (:uuids)")
    fun markForRemoval(uuids: List<String>)

    @Query("DELETE FROM sessions WHERE deleted=1")
    fun deleteMarkedForRemoval()

    @Query("DELETE FROM sessions WHERE uuid in (:uuids)")
    fun delete(uuids: List<String>)
}
