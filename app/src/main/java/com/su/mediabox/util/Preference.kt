package com.su.mediabox.util

import android.content.Context
import android.view.View
import androidx.preference.*

fun interface OnPreferenceLongClickListener {
    fun onPreferenceLongClick(preference: Preference): Boolean
}

interface PreferenceBindView {
    var bindView: View?
}

class SwitchPreferenceLongClickWrapper(
    context: Context,
    var onPreferenceLongClickListener: OnPreferenceLongClickListener? = null
) : SwitchPreferenceCompat(context), PreferenceBindView {

    override var bindView: View? = null

    private val onViewLongClickListener = View.OnLongClickListener {
        onPreferenceLongClickListener?.onPreferenceLongClick(this) ?: false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        bindView = holder.itemView
        super.onBindViewHolder(holder)
        holder.itemView.setOnLongClickListener(onViewLongClickListener)
    }
}

class EditTextPreferenceLongClickWrapper(
    context: Context,
    var onPreferenceLongClickListener: OnPreferenceLongClickListener? = null
) : EditTextPreference(context), PreferenceBindView {

    override var bindView: View? = null

    private val onViewLongClickListener = View.OnLongClickListener {
        onPreferenceLongClickListener?.onPreferenceLongClick(this) ?: false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        bindView = holder.itemView
        super.onBindViewHolder(holder)
        holder.itemView.setOnLongClickListener(onViewLongClickListener)
    }
}