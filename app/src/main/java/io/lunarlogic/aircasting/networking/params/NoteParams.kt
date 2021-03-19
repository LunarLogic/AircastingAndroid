package io.lunarlogic.aircasting.networking.params

import io.lunarlogic.aircasting.lib.DateConverter
import io.lunarlogic.aircasting.models.Note

class NoteParams {
    constructor(note: Note) {
        this.date = DateConverter.toDateString(note.date)
        this.text = note.text
        this.latitude = note.latitude
        this.longitude = note.longitude
        this.number = note.number
    }

    val date: String
    val text: String
    val latitude: Double?
    val longitude: Double?
    val number: Int
}
