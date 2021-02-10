package io.lunarlogic.aircasting.sensor.airbeam3.sync

import android.content.Context
import android.util.Log
import io.lunarlogic.aircasting.sensor.airbeam3.sync.SDCardReader.Step
import io.lunarlogic.aircasting.events.sdcard.SDCardReadFinished
import io.lunarlogic.aircasting.events.sdcard.SDCardReadEvent
import io.lunarlogic.aircasting.events.sdcard.SDCardReadStepStartedEvent
import io.lunarlogic.aircasting.lib.safeRegister
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.FileWriter

class SDCardDownloadService(mContext: Context) {
    private val DOWNLOAD_TAG = "SYNC"
    private val mCSVFileFactory = SDCardCSVFileFactory(mContext)

    private var fileWriter: FileWriter? = null
    private var counter = 0
    private var steps: ArrayList<Step> = ArrayList()

    private var mOnDownloadFinished: ((steps: List<Step>) -> Unit)? = null
    private var mOnLinesDownloaded: ((step: Step, linesCount: Int) -> Unit)? = null

    init {
        EventBus.getDefault().safeRegister(this)
    }

    fun run(
        onLinesDownloaded: (step: Step, linesCount: Int) -> Unit,
        onDownloadFinished: (steps: List<Step>) -> Unit
    ) {
        mOnLinesDownloaded = onLinesDownloaded
        mOnDownloadFinished = onDownloadFinished

        steps = ArrayList()

        openSyncFile()
    }

    @Subscribe
    fun onEvent(event: SDCardReadEvent) {
        val lines = event.lines
        val linesString = lines.map { "$it\n" }.joinToString("")
        writeToSyncFile(linesString)

        val linesCount = lines.size
        counter += linesCount

        val step = steps.lastOrNull()
        step?.let { mOnLinesDownloaded?.invoke(it, counter) }
    }

    @Subscribe
    fun onEvent(event: SDCardReadStepStartedEvent) {
        counter = 0
        val step = event.step
        steps.add(step)
    }

    @Subscribe
    fun onEvent(event: SDCardReadFinished) {
        Log.d(DOWNLOAD_TAG, "Sync finished")
        closeSyncFile()

        mOnDownloadFinished?.invoke(steps)
    }

    private fun openSyncFile() {
        val file = mCSVFileFactory.get()
        fileWriter = FileWriter(file)
    }

    private fun writeToSyncFile(lines: String) {
        fileWriter?.write(lines)
    }

    private fun closeSyncFile() {
        fileWriter?.flush()
        fileWriter?.close()
    }
}
