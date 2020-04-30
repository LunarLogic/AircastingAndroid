package io.lunarlogic.aircasting.bluetooth

import android.bluetooth.BluetoothSocket
import android.os.Message
import android.os.Messenger
import com.google.common.io.CharStreams
import com.google.common.io.LineProcessor
import io.lunarlogic.aircasting.lib.ResultCodes
import io.lunarlogic.aircasting.sensor.ResponseParser
import io.lunarlogic.aircasting.sensor.SensorEvent
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class BluetoothService(private val mMessenger: Messenger) {
//    fun perform(socket: BluetoothSocket) {
//        val thread = ServiceThread(socket)
//        thread.start()
//    }

//    private inner class ServiceThread(private val mmSocket: BluetoothSocket) : Thread() {
        val responseParser = ResponseParser()

        fun run(mmSocket: BluetoothSocket) {
            val mInputStream: InputStream = mmSocket.inputStream
            val inputStreamReader = InputStreamReader(mInputStream)

            CharStreams.readLines(
                inputStreamReader,
                lineProcessor()
            )
        }

        private fun lineProcessor(): LineProcessor<Void> {
            return object : LineProcessor<Void> {
                @Throws(IOException::class)
                override fun processLine(line: String): Boolean {
                    val sensorEvent = process(line)
                    mMessenger.send(Message.obtain().apply {
                        what = ResultCodes.AIRCASTING_AIR_BEAM2_EVENT
                        obj = sensorEvent
                    })
                    return !Thread.interrupted()
                }

                fun process(line: String): SensorEvent {
                    return responseParser.parse(line)
                }

                override fun getResult(): Void? {
                    return null
                }
            }
        }

//        // Call this method from the main activity to shut down the connection.
//        fun cancel() {
//            try {
//                mmSocket.close()
//            } catch (e: IOException) {
//                println("READING: error - Could not close the connect socket")
//            }
//        }
//    }
}