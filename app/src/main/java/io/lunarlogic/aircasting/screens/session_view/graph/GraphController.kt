package io.lunarlogic.aircasting.screens.session_view.graph

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.events.NoteCreatedEvent
import io.lunarlogic.aircasting.events.StopRecordingEvent
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.models.Note
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.screens.dashboard.active.AddNoteBottomSheet
import io.lunarlogic.aircasting.screens.dashboard.active.EditNoteBottomSheet
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewController
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewMvc
import org.greenrobot.eventbus.EventBus


class GraphController(
    rootActivity: AppCompatActivity,
    mSessionsViewModel: SessionsViewModel,
    mViewMvc: SessionDetailsViewMvc?,
    sessionUUID: String,
    sensorName: String?,
    val fragmentManager: FragmentManager
): SessionDetailsViewController(rootActivity, mSessionsViewModel, mViewMvc, sessionUUID, sensorName),
    SessionDetailsViewMvc.Listener,
    AddNoteBottomSheet.Listener,
    EditNoteBottomSheet.Listener {
    private val mErrorHandler = ErrorHandler(rootActivity)
    override fun locateRequested() {}

    open fun onResume() {
        mViewMvc?.registerListener(this)
    }

    open fun onPause() {
        mViewMvc?.unregisterListener(this)
    }

    override fun addNoteClicked(session: Session) {
        AddNoteBottomSheet(this, session, rootActivity, mErrorHandler).show(fragmentManager)
    }

    override fun onFinishSessionConfirmed(session: Session) {
        val event = StopRecordingEvent(session.uuid)
        EventBus.getDefault().post(event)
        rootActivity.finish()
    }

    override fun addNotePressed(session: Session, note: Note) {
        val event = NoteCreatedEvent(session, note)
        EventBus.getDefault().post(event)
    }

    override fun noteMarkerClicked(session: Session?, noteNumber: Int) {
        // TODO: this is not working now, displaying note from graph view will be added in "Ready"
        EditNoteBottomSheet(this, session, noteNumber).show(fragmentManager)
    }

    override fun saveChangesNotePressed(note: Note?, session: Session?) {
        TODO("Not yet implemented")  //to be filled when implementing edit note functionality
    }

    override fun deleteNotePressed(note: Note?) {
        TODO("Not yet implemented") //to be filled when implementing delete note functionality
    }
}
