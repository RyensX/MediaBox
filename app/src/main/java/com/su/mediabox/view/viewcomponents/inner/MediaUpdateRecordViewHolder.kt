package com.su.mediabox.view.viewcomponents.inner

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.su.mediabox.R
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.databinding.ViewComponentUpdateBinding
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.util.Util
import com.su.mediabox.util.appCoroutineScope
import com.su.mediabox.util.friendlyTime
import com.su.mediabox.util.unsafeLazy
import com.su.mediabox.view.adapter.type.TypeViewHolder
import kotlinx.coroutines.launch


class MediaUpdateRecordViewHolder private constructor(private val binding: ViewComponentUpdateBinding) :
    TypeViewHolder<MediaUpdateRecord>(binding.root) {

    companion object {
        private val updateTagTextPool by unsafeLazy { mutableMapOf<String, SpannableStringBuilder>() }
    }

    private val styleColor = Util.getResColor(R.color.main_color_2_skin)
    private var tmpData: MediaUpdateRecord? = null

    constructor(parent: ViewGroup) : this(
        ViewComponentUpdateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) {
        binding.root.setOnClickListener {
            tmpData?.apply {
                //打开详情
                DetailAction.obtain(targetMedia).go(bindingContext)
                //点开则直接确认
                appCoroutineScope.launch { getOfflineDatabase().mediaUpdateDao().confirmed(time) }
            }

        }
    }

    override fun onBind(data: MediaUpdateRecord) {
        super.onBind(data)
        tmpData = data
        binding.apply {
            vcUpdatePoint.isVisible = !data.confirmed
            vcUpdateName.text = data.targetMediaLabel
            vcUpdateTag.text =
                getUpdateTagText(data.oldTag ?: "", data.newTag)
            vcUpdateTime.text = friendlyTime(data.time)
        }
    }

    private fun getUpdateTagText(oldTag: String, newTag: String): SpannableStringBuilder {
        updateTagTextPool[oldTag + newTag]?.also {
            return it
        }
        val rawText = bindingContext.getString(R.string.media_update_desc, oldTag, newTag)
        val style = SpannableStringBuilder(rawText)
        val oldIndex = rawText.indexOf(oldTag)
        style.setSpan(
            ForegroundColorSpan(styleColor),
            oldIndex, oldIndex + oldTag.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        val newIndex = rawText.indexOf(newTag)
        style.setSpan(
            ForegroundColorSpan(styleColor),
            newIndex, newIndex + newTag.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return style
    }

    override fun onViewDetachedFromWindow() {
        updateTagTextPool.clear()
        super.onViewDetachedFromWindow()
    }
}