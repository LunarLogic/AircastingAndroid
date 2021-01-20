package io.lunarlogic.aircasting.database.data_classes

import androidx.lifecycle.LiveData
import androidx.room.*
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.new_session.select_device.DeviceItem
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
    @ColumnInfo(name = "device_type") val deviceType: DeviceItem.Type?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "tags") val tags: ArrayList<String> = arrayListOf(),
    @ColumnInfo(name = "start_time") val startTime: Date,
    @ColumnInfo(name = "end_time") val endTime: Date?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "status") val status: Session.Status = Session.Status.NEW,
    @ColumnInfo(name = "version") val version: Int = 0,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false,
    @ColumnInfo(name = "followed_at") val followedAt: Date? = null,
    @ColumnInfo(name = "contribute") val contribute: Boolean = false,
    @ColumnInfo(name = "locationless") val locationless: Boolean = false,
    @ColumnInfo(name = "url_location") val urlLocation: String? = null

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(session: Session):
            this(
                session.uuid,
                session.type,
                session.deviceId,
                session.deviceType,
                session.name,
                session.tags,
                session.startTime,
                session.endTime,
                session.location?.latitude,
                session.location?.longitude,
                session.status,
                session.version,
                session.deleted,
                session.followedAt,
                session.contribute,
                session.locationless,
                session.urlLocation
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
    lateinit var streams: List<MeasurementStreamDBObject>
}

class SessionWithStreamsAndMeasurementsDBObject {
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
    @Query("SELECT * FROM sessions")
    fun getAll() : List<SessionDBObject>

    @Query("SELECT count(id) FROM sessions")
    fun getCount() : Int

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type AND status=:status ORDER BY start_time DESC")
    fun loadAllByTypeAndStatusWithMeasurements(type: Session.Type, status: Session.Status): LiveData<List<SessionWithStreamsAndMeasurementsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type AND status IN (:statuses) ORDER BY start_time DESC")
    fun loadAllByTypeAndStatusWithMeasurements(type: Session.Type, statuses: List<Int>): LiveData<List<SessionWithStreamsAndMeasurementsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type AND status=:status ORDER BY start_time DESC")
    fun loadAllByTypeAndStatus(type: Session.Type, status: Session.Status): LiveData<List<SessionWithStreamsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type ORDER BY start_time DESC")
    fun loadAllByType(type: Session.Type): LiveData<List<SessionWithStreamsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND followed_at IS NOT NULL ORDER BY followed_at DESC")
    fun loadFollowingWithMeasurements(): LiveData<List<SessionWithStreamsAndMeasurementsDBObject>>

    @Query("SELECT * FROM sessions WHERE deleted=0 AND type=:type ORDER BY start_time DESC")
    fun byType(type: Session.Type): List<SessionDBObject>

    @Query("SELECT * FROM sessions WHERE status=:status")
    fun byStatus(status: Session.Status): List<SessionDBObject>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(session: SessionDBObject): Long

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun loadSessionAndMeasurementsByUUID(uuid: String): SessionWithStreamsAndMeasurementsDBObject?

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun loadLiveDataSessionAndMeasurementsByUUID(uuid: String): LiveData<SessionWithStreamsAndMeasurementsDBObject?>

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun reloadSessionAndMeasurementsByUUID(uuid: String): SessionWithStreamsAndMeasurementsDBObject?

    @Query("SELECT * FROM sessions WHERE uuid=:uuid AND deleted=0")
    fun loadSessionByUUID(uuid: String): SessionDBObject?

    @Query("SELECT * FROM sessions WHERE device_id=:deviceId AND status=:status AND type=:type AND deleted=0")
    fun loadSessionByDeviceIdStatusAndType(deviceId: String, status: Session.Status, type: Session.Type): SessionDBObject?

    @Query("UPDATE sessions SET name=:name, tags=:tags, end_time=:endTime, status=:status WHERE uuid=:uuid")
    fun update(uuid: String, name: String, tags: ArrayList<String>, endTime: Date, status: Session.Status)

    @Query("UPDATE sessions SET followed_at=:followedAt WHERE uuid=:uuid")
    fun updateFollowedAt(uuid: String, followedAt: Date?)

    @Query("UPDATE sessions SET status=:status WHERE uuid=:uuid")
    fun updateStatus(uuid: String, status: Session.Status)

    @Query("UPDATE sessions SET status=:newStatus WHERE device_id=:deviceId AND status=:existingStatus")
    fun disconnectSession(newStatus: Session.Status, deviceId: String, existingStatus: Session.Status)

    @Query("UPDATE sessions SET status=:newStatus WHERE type=:type AND device_type!=:deviceType AND status=:existingStatus")
    fun disconnectSessions(newStatus: Session.Status, deviceType: DeviceItem.Type?, type: Session.Type, existingStatus: Session.Status)

    @Query("UPDATE sessions SET status=:newStatus, end_time=:endTime WHERE type=:type AND device_type=:deviceType AND status=:existingStatus")
    fun finishSessions(newStatus: Session.Status, endTime: Date, deviceType: DeviceItem.Type?, type: Session.Type, existingStatus: Session.Status)

    @Query("DELETE FROM sessions")
    fun deleteAll()

    @Query("UPDATE sessions SET deleted=1 WHERE uuid in (:uuids)")
    fun markForRemoval(uuids: List<String>)

    @Query("DELETE FROM sessions WHERE deleted=1")
    fun deleteMarkedForRemoval()

    @Query("DELETE FROM sessions WHERE uuid in (:uuids)")
    fun delete(uuids: List<String>)
}
