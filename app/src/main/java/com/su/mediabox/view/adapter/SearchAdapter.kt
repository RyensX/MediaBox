package com.su.mediabox.view.adapter

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.config.Api
import com.su.mediabox.util.AnimeCover3ViewHolder
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.util.showToast
import com.su.mediabox.util.gone
import com.su.mediabox.util.visible

class SearchAdapter(
    val activity: Activity,
    private val dataList: List<AnimeCoverBean>
) : BaseRvAdapter(dataList) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover3ViewHolder -> {
                holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, item.cover)
                if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == item.cover) {
                    holder.ivAnimeCover3Cover.loadImage(
                        item.cover?: "",
                        referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
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
                            process(it[i].actionUrl)
                        }
                        holder.flAnimeCover3Type.addView(tvFlowLayout)
                    }
                }
                holder.tvAnimeCover3Describe.text = item.describe
                holder.itemView.setOnClickListener {
                    process(item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}