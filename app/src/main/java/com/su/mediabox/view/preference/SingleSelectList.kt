package com.su.mediabox.view.preference

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.su.mediabox.R
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.*

class SingleSelectListPreference(context: Context) :
    Preference(context) {

    private lateinit var dataList: Array<String>
    private lateinit var dataTextList: Array<String>
    internal var selectIndex = 0
    internal var currentValueText: String = getPersistedString("")
        private set

    init {
        summaryProvider = SingleSelectListSummaryProvider
    }

    private fun updateSummary(value: Any? = getPersistedString("")) {
        runCatching {
            selectIndex = dataList.indexOf(value)
            dataTextList.getOrNull(selectIndex)?.let {
                if (currentValueText == it)
                    return
                currentValueText = it
                popListWindow.setAdapter(
                    SingleSelectListAdapter(
                        this@SingleSelectListPreference,
                        dataTextList
                    ).apply {
                        popListWindow.width = measureWidth(context).coerceAtLeast(150.dp)
                    })
                notifyChanged()
            }
        }
    }

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)
        //以兼容DSL后设置key更新summary
        updateSummary()
    }

    override fun setDefaultValue(defaultValue: Any?) {
        super.setDefaultValue(defaultValue)
        updateSummary(defaultValue)
    }

    fun dataList(dataList: Array<String>) {
        this.dataList = dataList
        updateSummary()
    }

    fun dataListRes(@ArrayRes dataListRes: Int) {
        dataList(context.getStringArray(dataListRes))
    }

    fun dataTextList(dataTextList: Array<String>) {
        this.dataTextList = dataTextList
        updateSummary()
    }

    fun dataTextListRes(@ArrayRes dataTextListRes: Int) {
        dataTextList(context.getStringArray(dataTextListRes))
    }

    private val popListWindow by unsafeLazy {
        ListPopupWindow(context).apply {
            setOnItemClickListener { _, _, position, _ ->
                selectIndex = position
                currentValueText = dataTextList[position]
                persistString(dataList[selectIndex])
                notifyChanged()
                dismiss()
            }
        }
    }

    private var bindView: View? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        bindView = holder.itemView.findViewById(android.R.id.summary)
        super.onBindViewHolder(holder)
    }

    override fun onClick() {
        super.onClick()
        selectIndex = dataList.indexOf(getPersistedString(""))
        popListWindow.apply {
            anchorView = bindView
            show()
        }
    }

    override fun onDetached() {
        bindView = null
        super.onDetached()
    }
}

private class SingleSelectListAdapter(
    private val preference: SingleSelectListPreference,
    private val list: Array<String>
) : BaseAdapter() {

    private val context = preference.context

    private val colorPrimary = context.resolveThemedColor(R.attr.colorPrimary)
    private val colorOnPrimary = context.resolveThemedColor(R.attr.colorOnPrimary)
    private val colorControlNormal = Util.getResColor(R.color.foreground_black_skin)

    override fun getCount(): Int = list.size

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: context.layoutInflater
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        val text: TextView = view.findViewById(android.R.id.text1)

        text.text = getItem(position)

        if (position == preference.selectIndex) {
            text.setBackgroundColor(
                Color.argb(
                    200,
                    Color.red(colorPrimary),
                    Color.green(colorPrimary),
                    Color.blue(colorPrimary)
                )
            )
            text.setTextColor(colorOnPrimary)
        } else {
            text.setBackgroundColor(Color.TRANSPARENT)
            text.setTextColor(colorControlNormal)
        }

        return view
    }

}

private object SingleSelectListSummaryProvider :
    Preference.SummaryProvider<SingleSelectListPreference> {

    override fun provideSummary(preference: SingleSelectListPreference) =
        preference.currentValueText

}