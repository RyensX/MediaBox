package com.su.mediabox.util

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import com.su.mediabox.App
import com.su.mediabox.view.activity.MainActivity

class IntentProcessor : Activity() {

    companion object {

        private const val PROCESS_ID = "PROCESS_ID"
        private val processBlockMap by unsafeLazy { mutableMapOf<String, (String) -> Unit>() }

        /**
         * 这个方法必须在App启动时调用注册才有效
         */
        fun processorIntent(id: String, block: (id: String) -> Unit): Intent {
            processBlockMap[id] = block
            return Intent(App.context, IntentProcessor::class.java).apply {
                action = Intent.ACTION_DEFAULT
                putExtra(PROCESS_ID, id)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        intent?.getStringExtra(PROCESS_ID)?.also { id ->
            processBlockMap[id]?.also {
                it(id)
                // processBlockMap.remove(id)
            }
        }
        finish()
    }

}