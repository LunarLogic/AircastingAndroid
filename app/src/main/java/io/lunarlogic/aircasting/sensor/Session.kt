package io.lunarlogic.aircasting.sensor

import io.lunarlogic.aircasting.database.data_classes.SessionDBObject
import io.lunarlogic.aircasting.database.data_classes.SessionWithStreamsDBObject
import java.util.*
import kotlin.collections.ArrayList

val TAGS_SEPARATOR = " "

class Session(
    val deviceId: String?,
    private val mType: Type,
    private var mName: String,
    private var mTags: ArrayList<String>,
    private var mStatus: Status,
    private val mStartTime: Date = Date(),
    private var mEndTime: Date? = null,
    val uuid: String = UUID.randomUUID().toString(),
    var version: Int = 0,
    var deleted: Boolean = false,
    private var mStreams: List<MeasurementStream> = listOf()
) {
    constructor(sessionDBObject: SessionDBObject): this(
        sessionDBObject.deviceId,
        sessionDBObject.type,
        sessionDBObject.name,
        sessionDBObject.tags,
        sessionDBObject.status,
        sessionDBObject.startTime,
        sessionDBObject.endTime,
        sessionDBObject.uuid,
        sessionDBObject.version,
        sessionDBObject.deleted
    )

    constructor(sessionWithStreamsDBObject: SessionWithStreamsDBObject):
            this(sessionWithStreamsDBObject.session) {
        this.mStreams = sessionWithStreamsDBObject.streams.map { streamWithMeasurementsDBObject ->
            MeasurementStream(streamWithMeasurementsDBObject)
        }
    }

    enum class Type(val value: Int){
        MOBILE(0),
        FIXED(1)
    }

    enum class Status(val value: Int){
        NEW(-1),
        RECORDING(0),
        FINISHED(1)
    }

    val type get() = mType
    val name get() = mName
    val tags get() = mTags
    val startTime get() = mStartTime
    val endTime get() = mEndTime

    val status get() = mStatus
    val streams get() = mStreams

    fun startRecording() {
        mStatus = Status.RECORDING
    }

    fun stopRecording() {
        mEndTime = Date()
        mStatus = Status.FINISHED
    }

    fun isUploadable(): Boolean {
        // TODO: handle false if mobile && locationless
        return true
    }
}