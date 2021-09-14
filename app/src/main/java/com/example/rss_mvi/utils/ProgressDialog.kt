package com.example.rss_mvi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.rss_mvi.R

class ProgressDialog {
    companion object {
        private var dialog: AlertDialog? = null

        @SuppressLint("SetTextI18n")
        @JvmStatic
        fun show(context: Context, message: String) {
            if (dialog == null) dialog = AlertDialog.Builder(context, R.style.progress_dialog_box).create()

            val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)

            dialog!!.setView(view, 0, 0, 0, 0)
            dialog!!.setCancelable(false)

            view.findViewById<TextView>(R.id.progressDialogMessageTextView).text = message
            dialog!!.show()
        }

        @JvmStatic
        fun dismiss() {
            if (dialog != null && dialog!!.isShowing) {
                dialog!!.dismiss()
                dialog = null
            }
        }
    }
}