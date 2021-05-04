package io.lunarlogic.aircasting.screens.session_view.graph

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.CalendarUtils
import io.lunarlogic.aircasting.models.Measurement
import io.lunarlogic.aircasting.models.Note
import java.util.*
import kotlin.collections.ArrayList

class GraphDataGenerator(
    private val mContext: Context
) {
    private var cumulativeValue = 0.0
    private var cumulativeTime: Long = 0
    private var count = 0
    private var startTime = Date()

    private val DEFAULT_LIMIT = 60

    class Result(val entries: List<Entry>, val midnightPoints: List<Float>)

    fun generate(samples: List<Measurement>, notes: List<Note>?, limit: Int = DEFAULT_LIMIT): Result {
        reset()

        val entries = ArrayList<Entry>()
        val midnightPoints = ArrayList<Float>()
        val fillFactor = 1.0 * limit / samples.size
        var fill = 0.0

        val firstMeasurement = samples.firstOrNull()
        firstMeasurement ?: return Result(entries, midnightPoints)
        startTime = firstMeasurement.time

        var lastDateDayOfMonth = CalendarUtils.dayOfMonth(startTime)

        for (measurement in samples) {
            add(measurement)
            fill += fillFactor

            //if (measurement == samples.last()) break

            if (fill > 1) {
                fill = 0.0 // przy zmianie z fill -= 1.0 na fill = 0.0 graf zamiast przeskakiwać przez cały czas przeskakuje co tyle sekund/measurementów ile jest podane w limit
                val date = getAverageDate()

                entries.add(buildAverageEntry(date, hasNote = false))

                if (notes != null) {
                    for (note in notes) {
                        if (isSameDate(note, date)) {
                            entries.add(buildAverageEntry(date, hasNote = true))
                        }
                    }
                }

                val dateOfMonth = CalendarUtils.dayOfMonth(date)

                if (lastDateDayOfMonth != dateOfMonth) {
                    lastDateDayOfMonth = dateOfMonth
                    midnightPoints.add(convertDateToFloat(date))
                }

                reset()
            }
        }

        if (count > 0) {
            val date = getAverageDate()
            entries.add(buildAverageEntry(date))
        }

        return Result(entries, midnightPoints)
    }

    fun dateFromFloat(float: Float): Date {
        return Date(float.toLong() + startTime.time)
    }

    private fun getAverageDate(): Date {
        return Date(cumulativeTime / count)
    }

    private fun getAverageValue(): Double {
        return (cumulativeValue / count)
    }

    private fun buildAverageEntry(date: Date, hasNote: Boolean = false): Entry {
        val time = convertDateToFloat(date)
        val value = getAverageValue().toFloat()

        if (hasNote) {
            return Entry(time, value, ContextCompat.getDrawable(mContext, R.drawable.ic_note_icon))
        } else {
            return Entry(time, value)
        }
    }

    private fun convertDateToFloat(date: Date): Float {
        // we need to substract startTime because
        // otherwise we lose precision while converting Long to Float
        // and Float is needed for the MPAndroidChart library
        return (date.time - startTime.time).toFloat()
    }

    private fun add(measurement: Measurement) {
        cumulativeValue += measurement.value
        cumulativeTime += measurement.time.time
        count += 1
    }

    private fun reset() {
        count = 0
        cumulativeTime = count.toLong()
        cumulativeValue = cumulativeTime.toDouble()
    }

    private fun isSameDate(note: Note, date: Date): Boolean {
        return note.date.month == date.month &&
                note.date.day == date.day &&
                note.date.hours == date.hours &&
                note.date.minutes == date.minutes &&
                note.date.seconds == date.seconds
    }
}
