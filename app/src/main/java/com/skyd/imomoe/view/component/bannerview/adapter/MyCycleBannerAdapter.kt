package com.skyd.imomoe.view.component.bannerview.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.util.AnimeCover6ViewHolder
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.ViewHolderUtil
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible

/**
 * Created by Sky_D on 2021-02-08.
 */
class MyCycleBannerAdapter(
    private val activity: Activity,
    private val dataList: List<BaseBean>
) : CycleBannerAdapter() {
    override fun getItemType(position: Int): Int =
        ViewHolderUtil.getItemViewType(dataList[position])

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val viewHolder = ViewHolderUtil.getViewHolder(parent, viewType)
        //vp2的item必须是MATCH_PARENT的
        val layoutParams = viewHolder.itemView.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewHolder.itemView.layoutParams = layoutParams
        return viewHolder
    }

    override fun onBind(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is AnimeCover6ViewHolder -> {
                if (item is AnimeCoverBean) {
                    holder.tvAnimeCover6Night.setBackgroundColor(activity.getResColor(R.color.transparent_skin))
                    holder.ivAnimeCover6Cover.loadImage(
                        item.cover?.url ?: "",
                        referer = item.cover?.referer
                    )
                    holder.tvAnimeCover6Title.text = item.title
                    holder.tvAnimeCover6Episode.text = item.episodeClickable?.title
                    if (item.describe.isNullOrEmpty()) {
                        holder.tvAnimeCover6Describe.gone()
                    } else {
                        holder.tvAnimeCover6Describe.visible()
                        holder.tvAnimeCover6Describe.text = item.describe
                    }
                    holder.itemView.setOnClickListener {
                        process(activity, item.actionUrl)
                    }
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }

    override fun getCount(): Int = dataList.size
}