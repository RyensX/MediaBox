package com.su.mediabox.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.su.mediabox.App
import com.su.mediabox.R

private var uiThreadHandler: Handler = Handler(Looper.getMainLooper())

fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    uiThreadHandler.post {
        val toast = Toast(App.context)
        val view: View =
            (App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.toast_1, null)
        view.findViewById<TextView>(R.id.tv_toast_1).apply {
            background = ContextCompat.getDrawable(
                App.context,
                R.drawable.shape_fill_circle_corner_main_color_2_50_skin
            )
            this.text = this@showToast
        }
        toast.view = view
        toast.duration = duration
        toast.show()
    }
}
