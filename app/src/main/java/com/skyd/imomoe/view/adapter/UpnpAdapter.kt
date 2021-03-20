package com.skyd.imomoe.view.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const.ViewHolderTypeInt
import com.skyd.imomoe.util.UpnpDevice1ViewHolder
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.activity.DlnaActivity
import com.skyd.imomoe.view.activity.DlnaControlActivity
import org.fourthline.cling.model.meta.Device

class UpnpAdapter(
    val activity: DlnaActivity,
    private val dataList: List<Device<*, *, *>?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = ViewHolderTypeInt.UPNP_DEVICE_1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        getViewHolder(parent, viewType)

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is UpnpDevice1ViewHolder -> {
                holder.tvUpnpDevice1Title.text = item?.details?.friendlyName
                holder.itemView.setOnClickListener {
                    val key = System.currentTimeMillis().toString()
                    DlnaControlActivity.deviceHashMap[key] = item
                    activity.startActivity(
                        Intent(activity, DlnaControlActivity::class.java)
                            .putExtra("url", activity.url)
                            .putExtra("title", activity.title)
                            .putExtra("deviceKey", key)
                    )
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}