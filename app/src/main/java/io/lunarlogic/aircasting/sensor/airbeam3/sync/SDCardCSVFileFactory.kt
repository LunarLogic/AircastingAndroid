package io.lunarlogic.aircasting.sensor.airbeam3.sync

import android.content.Context
import java.io.File

class SDCardCSVFileFactory(private val mContext: Context) {
    private val DIR_NAME = "sync"
    private val FILE_NAME = "sync.csv"

    enum class Header(val value: Int) {
        INDEX(0),
        UUID(1),
        DATE(2),
        TIME(3),
        LATITUDE(4),
        LONGITUDE(5),
        F(6),
        C(7),
        K(8),
        RH(9),
        PM1(10),
        PM2_5(11),
        PM10(12);

        companion object {
            fun fromInt(value: Int) = values().first { it.value == value }
        }
    }
    fun get(): File {
        val dir = mContext.getExternalFilesDir(DIR_NAME)
        return File(dir, FILE_NAME)
    }
}
