package io.lunarlogic.aircasting.models

import io.lunarlogic.aircasting.database.data_classes.NoteDBObject
import java.util.*

class Note(
    val date: Date,
    var text: String,
    val latitude: Double?,
    val longitude: Double?,
    val number: Int,
    val photoPath: String?) {

    constructor(noteDBObject: NoteDBObject): this(
        noteDBObject.date,
        noteDBObject.text,
        noteDBObject.latitude,
        noteDBObject.longitude,
        noteDBObject.number,
        noteDBObject.photoPath
    )
}


