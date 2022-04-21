package com.su.mediabox.view.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.LicenseBean
import com.su.mediabox.util.License1ViewHolder
import com.su.mediabox.util.LicenseHeader1ViewHolder
import com.su.mediabox.util.showToast
import com.su.mediabox.view.activity.LicenseActivity

class LicenseAdapter(
    val activity: LicenseActivity,
    private val dataList: List<LicenseBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is LicenseHeader1ViewHolder -> {
            }
            is License1ViewHolder -> {
                holder.tvLicense1Name.text = item.title
                holder.tvLicense1License.text = item.license
                holder.itemView.setOnClickListener {

                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}