package com.su.mediabox.view.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.plugin.AppRouteProcessor.matchAndGetParams
import com.su.mediabox.pluginapi.Constant.ActionUrl
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.util.*
import com.su.mediabox.view.activity.AnimeDownloadActivity
import com.su.mediabox.view.activity.SimplePlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AnimeDownloadAdapter(
    val activity: AnimeDownloadActivity,
    private val dataList: List<AnimeCoverBean>
) : BaseRvAdapter(dataList) {

    private val animeWorkCoroutineScope by lazy(LazyThreadSafetyMode.NONE) {
        CoroutineScope(
            Dispatchers.IO
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, viewType).apply {
            //剧集列表/播放剧集
            setOnClickListener(itemView) { pos ->
                dataList[pos].also {
                    matchAndGetParams(it.actionUrl, ActionUrl.ANIME_ANIME_DOWNLOAD_PLAY) {
                        //参数：URL/标题
                        activity.startActivity(
                            Intent(activity, SimplePlayActivity::class.java)
                                .putExtra(SimplePlayActivity.URL, it[0])
                                .putExtra(SimplePlayActivity.TITLE, it[1])
                        )
                    }
                }
            }
            //剧集列表长按删除
            setOnLongClickListener(itemView) { pos ->
                dataList[pos].also {
                    if (it.actionUrl.startsWith(ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE)) {
                        showAnimeDeleteDialog(it)
                    }
                }
                true
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is AnimeCover7ViewHolder -> {
                holder.tvAnimeCover7Title.isFocused = true
                holder.tvAnimeCover7Title.text = item.title
                holder.tvAnimeCover7Size.isFocused = true
                holder.tvAnimeCover7Size.text = item.size
                if (item.actionUrl.startsWith(ActionUrl.ANIME_ANIME_DOWNLOAD_EPISODE)) {
                    holder.tvAnimeCover7Episodes.text = item.episodeCount
                    holder.tvAnimeCover7Episodes.visible()
                } else {
                    holder.tvAnimeCover7Episodes.invisible()
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }

    private fun showAnimeDeleteDialog(anime: AnimeCoverBean) {
        val animeName = anime.title
        MaterialDialog(activity).show {
            title(res = R.string.download_anime_delete_tip)
            message(
                text = """
                名称：$animeName
                集数：${anime.episodeCount ?: 0}
            """.trimIndent()
            )
            positiveButton {
                animeWorkCoroutineScope.launch {
                    File(Const.DownloadAnime.animeFilePath, animeName).deleteRecursively().also {
                        if (it)
                            activity.getAnimeCover()
                        else
                            withContext(Dispatchers.Main) {
                                "删除失败，请检查权限".showToast()
                            }
                    }
                }
            }
            negativeButton { dismiss() }
        }
    }
}