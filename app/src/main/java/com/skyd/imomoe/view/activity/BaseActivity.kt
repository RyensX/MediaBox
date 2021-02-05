package com.skyd.imomoe.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.setColorStatusBar

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        setColorStatusBar(window, resources.getColor(R.color.main_color_2))
    }
}