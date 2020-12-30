package io.lunarlogic.aircasting.networking.responses

class UpdateSessionResponse(
    val type: String,
    val uuid: String,
    val title: String,
    val tag_list: ArrayList<String>,
    val start_time: String,
    val end_time: String,
    val latitude: Double,
    val longitude: Double,
    val deleted: Boolean,
    val contribute: Boolean,
    val version: Int,
    val streams: HashMap<String, SessionStreamResponse>
)