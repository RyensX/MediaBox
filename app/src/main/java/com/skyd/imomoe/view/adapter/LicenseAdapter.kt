package com.skyd.imomoe.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.LicenseBean
import com.skyd.imomoe.util.License1ViewHolder
import com.skyd.imomoe.util.LicenseHeader1ViewHolder
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getItemViewType
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.activity.LicenseActivity

class LicenseAdapter(
    val activity: LicenseActivity,
    private val dataList: List<LicenseBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = getItemViewType(dataList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        getViewHolder(parent, viewType)

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is LicenseHeader1ViewHolder -> {
            }
            is License1ViewHolder -> {
                holder.tvLicense1Name.text = item.title
                holder.tvLicense1License.text = item.license
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl + item.url)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}