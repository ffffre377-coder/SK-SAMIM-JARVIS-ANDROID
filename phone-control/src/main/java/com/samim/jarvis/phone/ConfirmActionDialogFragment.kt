package com.samim.jarvis.phone

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

/**
 * Simple confirmation dialog fragment.
 * Host should set `confirmListener` before showing.
 */
class ConfirmActionDialogFragment : DialogFragment() {

    var confirmListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE) ?: "Confirm"
        val message = arguments?.getString(ARG_MESSAGE) ?: ""
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Confirm") { _, _ -> confirmListener?.invoke() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"

        fun newInstance(title: String, message: String): ConfirmActionDialogFragment {
            val f = ConfirmActionDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            f.arguments = args
            return f
        }
    }
}
