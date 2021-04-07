package io.lunarlogic.aircasting.screens.dashboard.active

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.AnimatedLoader
import io.lunarlogic.aircasting.models.Note
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.BottomSheet
import kotlinx.android.synthetic.main.edit_note_bottom_sheet.view.*

class EditNoteBottomSheet(
    private val mListener: Listener,
    private var mSession: Session?,
    private val noteNumber: Int
): BottomSheet() {
    interface Listener {
        fun saveChangesNotePressed(note: Note?, session: Session?)
        fun deleteNotePressed(note: Note?, session: Session?)
    }
    private var mNote: Note? = null
    private var noteInput: EditText? = null
    private var mLoader: ImageView? = null

    override fun setup() {
        noteInput = contentView?.note_input
        mNote = mSession?.notes?.find { note -> note.number == noteNumber }
        noteInput?.setText(mNote?.text)

        mLoader = contentView?.edit_note_loader

        val saveChangesButton = contentView?.save_changes_button
        saveChangesButton?.setOnClickListener {
            saveChanges()
            dismiss()
        }

        val deleteNoteButton = contentView?.delete_note_button
        deleteNoteButton?.setOnClickListener {
            deleteNote()
            dismiss()
        }

        val cancelButton = contentView?.cancel_button
        cancelButton?.setOnClickListener {
            dismiss()
        }

        val closeButton = contentView?.close_button
        closeButton?.setOnClickListener {
            dismiss()
        }
    }

    private fun saveChanges() {
        val noteText = noteInput?.text.toString().trim()
        mNote?.text = noteText
        mListener.saveChangesNotePressed(mNote, mSession)
    }

    fun reload(session: Session) {
        mSession = session
        noteInput?.setText(mSession?.notes?.find{ note -> note.number == noteNumber }.toString())
    }

    fun showLoader() {
        AnimatedLoader(mLoader).start()
        mLoader?.visibility = View.VISIBLE
        noteInput?.isEnabled = false
    }

    fun hideLoader() {
        mLoader?.visibility = View.GONE
        noteInput?.isEnabled = true
    }

    private fun deleteNote() {
        mListener.deleteNotePressed(mNote, mSession)
    }

    override fun layoutId(): Int {
        return R.layout.edit_note_bottom_sheet
    }

}
