package com.example.menuapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

class DialogOrder : AppCompatDialogFragment() {

    var choices: String = ""
    var price: Double = 0.00

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DialogeTheme)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_meal, null))
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}