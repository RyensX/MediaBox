package com.skyd.imomoe.view.adapter

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.AnimeCover3ViewHolder
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible

class SearchAdapter(
    val activity: Activity,
    private val dataList: List<AnimeCoverBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover3ViewHolder -> {
                holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, item.cover?.url)
                if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == item.cover?.url) {
                    holder.ivAnimeCover3Cover.loadImage(
                        item.cover?.url ?: "",
                        referer = item.cover?.referer
                    )
                }
                holder.tvAnimeCover3Title.text = item.title
                if (item.episode == "") {
                    holder.tvAnimeCover3Episode.gone()
                } else {
                    holder.tvAnimeCover3Episode.visible()
                    holder.tvAnimeCover3Episode.text = item.episode
                }
                item.animeType?.let {
                    holder.flAnimeCover3Type.removeAllViews()
                    for (i in it.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeCover3Type,
                                false
                            ) as TextView
                        tvFlowLayout.text = it[i].title
                        tvFlowLayout.setOnClickListener { _ ->
                            if (it[i].actionUrl.isBlank()) return@setOnClickListener
                            //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                            val actionUrl = it[i].actionUrl.run {
                                if (endsWith("/")) "${this}${it[i].title}"
                                else "${this}/${it[i].title}"
                            }
                            process(
                                activity,
                                Const.ActionUrl.ANIME_CLASSIFY + actionUrl
                            )
                        }
                        holder.flAnimeCover3Type.addView(tvFlowLayout)
                    }
                }
                holder.tvAnimeCover3Describe.text = item.describe
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}