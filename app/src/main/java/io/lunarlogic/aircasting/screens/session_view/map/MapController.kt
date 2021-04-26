package io.lunarlogic.aircasting.screens.session_view.map

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import io.lunarlogic.aircasting.events.*
import io.lunarlogic.aircasting.exceptions.ErrorHandler
import io.lunarlogic.aircasting.location.LocationHelper
import io.lunarlogic.aircasting.models.Note
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.dashboard.active.AddNoteBottomSheet
import io.lunarlogic.aircasting.models.SessionsViewModel
import io.lunarlogic.aircasting.screens.dashboard.active.EditNoteBottomSheet
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewController
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewMvc
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MapController(
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
    private var mLocateRequested = false
    protected var editNoteDialog: EditNoteBottomSheet? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LocationChanged) {
        if (mLocateRequested) {
            val location = LocationHelper.lastLocation()
            location?.let { mViewMvc?.centerMap(location) }
            mLocateRequested = false
        }
    }

    open fun onResume() {
        mViewMvc?.registerListener(this)
    }

    open fun onPause() {
        mViewMvc?.unregisterListener(this)
    }

    override fun locateRequested() {
        val location = LocationHelper.lastLocation()
        if (location == null) {
            requestLocation()
        } else {
            mViewMvc?.centerMap(location)
        }
    }

    private fun requestLocation() {
        mLocateRequested = true
        LocationHelper.checkLocationServicesSettings(rootActivity)
    }

    fun onLocationSettingsSatisfied() {
        LocationHelper.start()
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
        mViewMvc?.addNote(note)
        mViewMvc?.addNote(note)
    }

    override fun noteMarkerClicked(session: Session?, noteNumber: Int) {
        startEditNoteDialog(session, noteNumber)
    }

    override fun saveChangesNotePressed(note: Note?, session: Session?) { // buttons from edit note bottom sheet
        val event = NoteEditedEvent(note, session)
        EventBus.getDefault().post(event)
    }

    override fun deleteNotePressed(note: Note?, session: Session?) { // Delete session on EditNoteBottomSheet pressed
        val event = NoteDeletedEvent(note, session)
        EventBus.getDefault().post(event)
        mViewMvc?.deleteNote(note!!)
    }

    fun startEditNoteDialog(session: Session?, noteNumber: Int) {
        editNoteDialog = EditNoteBottomSheet(this, session, noteNumber)
        editNoteDialog?.show(fragmentManager)
    }

}
