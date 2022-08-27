package com.su.mediabox.util

import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.pluginapi.util.UIUtil.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val toastView by unsafeLazy {
    TextView(App.context, null, R.style.App_Theme).apply {
        setPaddingHorizontal(15.dp)
        setPaddingVertical(9.dp)
        setTextColor(Color.WHITE)
        setBackgroundResource(R.drawable.shape_fill_circle_corner_main_color_2_50_skin)
    }
}

fun CharSequence.showToast(duration: Int = Toast.LENGTH_LONG) {
    appCoroutineScope.launch(Dispatchers.Main) {
        val toast = Toast(App.context)
        toastView.text = this@showToast
        toast.view = toastView
        toast.duration = duration
        toast.show()
    }
}
