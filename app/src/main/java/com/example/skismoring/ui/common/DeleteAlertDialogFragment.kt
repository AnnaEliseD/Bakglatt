package com.example.skismoring.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.skismoring.ui.settings.SettingsViewModel


class DeleteAlertDialogFragment(private val viewModel: SettingsViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        builder.setMessage("Slett lagrede favoritter og smørning?")
                .setPositiveButton("Nullstill") { _, _ ->
                    viewModel.clearAllData()
                    Toast.makeText(requireContext(), "Data slettet", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Avbryt") { _, _ ->
                }
        // Create the AlertDialog object and return it
        val dialog = builder.create()
        dialog.setOnShowListener{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
        }

        return dialog
    }

}
