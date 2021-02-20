package com.example.authorizationapp.view

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.authorizationapp.R

class AlertDialogFragment : DialogFragment() {
    companion object {
        const val TITLE_KEY = "titleKey"
        const val MESSAGE_KEY = "messageKey"
        const val BUTTON_KEY = "buttonKey"
    }

    var onClick: (DialogFragment) -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(TITLE_KEY) ?: getString(R.string.error)
        val message = arguments?.getString(MESSAGE_KEY) ?: getString(R.string.error)
        val buttonText = arguments?.getString(BUTTON_KEY) ?: getString(R.string.ok)
        val alertDialog = AlertDialog.Builder(requireContext())
                .setTitle(title).setMessage(message)
                .setPositiveButton(buttonText) { _, _ ->
                    dismiss()
                    onClick(this)
                }
        return alertDialog.create()
    }
}