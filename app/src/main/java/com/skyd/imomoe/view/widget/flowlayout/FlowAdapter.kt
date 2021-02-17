package com.skyd.imomoe.view.widget.flowlayout

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.showToast

class FlowAdapter(
    val activity: Activity,
    private val dataList: List<AnimeEpisodeDataBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class FlowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAnimeEpisode1 = view.findViewById<TextView>(R.id.tv_anime_episode_1)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return FlowViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_episode_1, parent, false)
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is FlowViewHolder -> {
                holder.tvAnimeEpisode1.text = item.title
                holder.tvAnimeEpisode1.setBackgroundResource(R.drawable.shape_circle_corner_edge_white_ripper_50)
                holder.tvAnimeEpisode1.setTextColor(Color.WHITE)
                holder.itemView.setOnClickListener {
                    process(activity, item.actionUrl, item.actionUrl)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}