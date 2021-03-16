package io.lunarlogic.aircasting.screens.dashboard.active

import android.content.Context
import android.widget.EditText
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.models.Note
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.common.BottomSheet
import kotlinx.android.synthetic.main.add_note_bottom_sheet.view.*
import java.time.Instant.now
import java.util.*

class AddNoteBottomSheet(
    private val mListener: Listener,
    private var mSession: Session,
    private val mContext: Context?
) : BottomSheet() {
    interface Listener {
        fun addNotePressed(session: Session, note: Note)
    }

    private var noteInput: EditText? = null

    override fun layoutId(): Int {
        return R.layout.add_note_bottom_sheet
    }

    override fun setup() {
        noteInput = contentView?.note_input

        // button listeners
        val addNoteButton = contentView?.add_note_button
        addNoteButton?.setOnClickListener {
            addNote(mSession)
            dismiss()
        }

        val cancelButton = contentView?.cancel_button
        cancelButton?.setOnClickListener {
            dismiss()
        }
    }

    private fun addNote(mSession: Session) {
        // TODO: here we need to take date, text of note and the rest things to model
        //todo: where should i send the NoteCreatedEvent <??>
        val noteText = noteInput?.text.toString().trim()
        val date = Date().time
        val note = Note(0, date, noteText, mSession.location?.latitude, mSession.location?.longitude) // , 0, "photoPath" todo: random data for now

        mListener.addNotePressed(mSession, note)
    }
}
