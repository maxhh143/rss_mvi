package com.example.rss_mvi.presentation

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.rss_mvi.R
import com.example.rss_mvi.utils.BackButtonPressed
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        if (EventBus.getDefault().hasSubscriberForEvent(BackButtonPressed::class.java)) {
            EventBus.getDefault().post(BackButtonPressed)
            return
        }

        AlertDialog.Builder(this, R.style.alert_dialog_white)
            .setMessage("Вы действительно хотите выйти из приложения?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog: DialogInterface, _ -> finishAndRemoveTask() }
            .setNegativeButton("Нет") { dialog: DialogInterface, _ -> dialog.cancel() }
            .create()
            .show()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}