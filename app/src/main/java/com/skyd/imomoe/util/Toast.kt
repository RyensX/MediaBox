package com.skyd.imomoe.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.skin.core.SkinResourceProcessor

private var uiThreadHandler: Handler = Handler(Looper.getMainLooper())

fun CharSequence.showToast(duration: Int = Toast.LENGTH_SHORT) {
    uiThreadHandler.post {
        val toast = Toast(App.context)
        val view: View =
            (App.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.toast_1, null)
        view.findViewById<TextView>(R.id.tv_toast_1).apply {
            if (SkinResourceProcessor.isInitialized()) {
                background =
                    getResDrawable(R.drawable.shape_fill_circle_corner_main_color_2_50_skin)
            }
            this.text = this@showToast
        }
        toast.view = view
        toast.duration = duration
        toast.show()
    }
}
