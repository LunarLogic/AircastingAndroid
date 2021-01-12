package io.lunarlogic.aircasting.screens.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.models.TAGS_SEPARATOR
import kotlinx.android.synthetic.main.edit_session_bottom_sheet.view.*

class EditSessionBottomSheet(private val mListener: Listener, private val session: Session): BottomSheetDialogFragment() {
    interface Listener{
        fun onEditDataPressed(session: Session, name: String, tags: ArrayList<String>)
    }

    private val TAG = "EditSessionBottomSheet"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_session_bottom_sheet, container, false)

        val sessionNameInput = view?.findViewById<EditText>(R.id.session_name_input)
        sessionNameInput?.setText(session.name)

        val tagsInput = view?.findViewById<EditText>(R.id.tags_input)
        tagsInput?.setText(session.tags.joinToString(TAGS_SEPARATOR))

        val editDataButton = view?.findViewById<Button>(R.id.edit_data_button)
        editDataButton?.setOnClickListener {
            onEditSessionPressed()
        }

        val cancelButton = view?.findViewById<Button>(R.id.cancel_button)
        cancelButton?.setOnClickListener {
            dismiss()
        }

        val closeButton = view?.findViewById<ImageView>(R.id.close_button)
        closeButton?.setOnClickListener {
            dismiss()
        }

        return view
    }

    fun show(manager: FragmentManager) {
        show(manager, TAG)
    }

    private fun onEditSessionPressed() {
        val name = view?.session_name_input?.text.toString().trim()
        val tags = view?.tags_input?.text.toString().trim()
        val tagList = ArrayList(tags.split(TAGS_SEPARATOR))
        dismiss()
        mListener.onEditDataPressed(session, name, tagList)
    }
}
