package com.su.mediabox.view.adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.su.mediabox.App
import com.su.mediabox.plugin.PluginManager.process
import com.su.mediabox.R
import com.su.mediabox.config.Api
import com.su.mediabox.util.*
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.showToast
import com.su.mediabox.view.fragment.AnimeShowFragment
import com.su.mediabox.view.component.bannerview.adapter.MyCycleBannerAdapter
import com.su.mediabox.view.component.bannerview.indicator.DotIndicator
import com.su.mediabox.pluginapi.Constant.ViewHolderTypeString
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.IAnimeShowBean
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.util.Util.getResDrawable
import com.su.mediabox.view.adapter.decoration.AnimeCoverItemDecoration
import com.su.skin.SkinManager

class AnimeShowAdapter(
    val fragment: AnimeShowFragment,
    private val dataList: List<IAnimeShowBean>,
    private val childViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
) : BaseRvAdapter(dataList) {

    private val gridItemDecoration = AnimeCoverItemDecoration()

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is Banner1ViewHolder -> {
                holder.banner1.stopPlay()
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is Banner1ViewHolder -> {
                holder.banner1.startPlay(5000)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        when (holder) {
            is GridRecyclerView1ViewHolder -> {
                holder.rvGridRecyclerView1.setRecycledViewPool(childViewPool)
                holder.rvGridRecyclerView1.setHasFixedSize(true)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]
        when {
            holder is GridRecyclerView1ViewHolder -> {
                item.animeCoverList?.let {
                    if (it.isNotEmpty()) {
                        val itemDecorationCount = holder.rvGridRecyclerView1.itemDecorationCount
                        when (it[0].type) {
                            ViewHolderTypeString.ANIME_COVER_3, ViewHolderTypeString.ANIME_COVER_5 -> {
                                holder.rvGridRecyclerView1.layoutManager =
                                    LinearLayoutManager(fragment.activity)
                                holder.rvGridRecyclerView1.post {
                                    holder.rvGridRecyclerView1.setPadding(0, 0, 0, 0)
                                }
                                for (i in 0 until itemDecorationCount) {
                                    holder.rvGridRecyclerView1.removeItemDecorationAt(i)
                                }
                            }
                            ViewHolderTypeString.ANIME_COVER_1 -> {
                                holder.rvGridRecyclerView1.layoutManager =
                                    GridLayoutManager(fragment.activity, 4)
                                if (itemDecorationCount == 0) {
                                    holder.rvGridRecyclerView1.post {
                                        holder.rvGridRecyclerView1.setPadding(16.dp, 0, 16.dp, 0)
                                    }
                                    holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                                }
                            }
                            ViewHolderTypeString.ANIME_COVER_4 -> {
                                holder.rvGridRecyclerView1.layoutManager =
                                    GridLayoutManager(fragment.activity, 4)
                                if (itemDecorationCount == 0) {
                                    holder.rvGridRecyclerView1.post {
                                        holder.rvGridRecyclerView1.setPadding(16.dp, 0, 16.dp, 0)
                                    }
                                    holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                                }
                            }
                            else -> {
                                return@let
                            }
                        }
                    }

                    holder.rvGridRecyclerView1.adapter =
                        fragment.activity?.let { it1 -> GridRecyclerView1Adapter(it1, it) }
                }
            }
            holder is Header1ViewHolder -> {
                fragment.activity?.let {
                    holder.tvHeader1Title.setTextColor(it.getResColor(R.color.foreground_main_color_2_skin))
                }
                holder.tvHeader1Title.text = item.title
            }
            holder is Banner1ViewHolder -> {
                fragment.activity?.let {
                    item.animeCoverList?.let { it1 ->
                        holder.banner1.setAdapter(MyCycleBannerAdapter(it, it1))
                        holder.banner1.setIndicator(DotIndicator(it).apply {
                            SkinManager.setViewTag(this)
                        })
                        holder.banner1.startPlay(5000)
                    }
                }
            }
            holder is AnimeCover1ViewHolder && item is AnimeCoverBean -> {
                holder.ivAnimeCover1Cover.setTag(R.id.image_view_tag, item.cover)
                fragment.activity?.let {
                    if (holder.ivAnimeCover1Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover1Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                }
                holder.tvAnimeCover1Title.text = item.title
                if (item.episode.isBlank()) {
                    holder.tvAnimeCover1Episode.gone()
                } else {
                    holder.tvAnimeCover1Episode.visible()
                    holder.tvAnimeCover1Episode.text = item.episode
                }
                holder.itemView.setOnClickListener {
                    process(item.actionUrl)
                }
            }
            holder is AnimeCover3ViewHolder && item is AnimeCoverBean -> {
                holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, item.cover)
                fragment.activity?.let { activity ->
                    if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover3Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                }
                holder.tvAnimeCover3Title.text = item.title
                if (item.episode.isBlank()) {
                    holder.tvAnimeCover3Episode.gone()
                } else {
                    holder.tvAnimeCover3Episode.visible()
                    holder.tvAnimeCover3Episode.text = item.episode
                }
                item.animeType?.let {
                    holder.flAnimeCover3Type.removeAllViews()
                    for (i in it.indices) {
                        val tvFlowLayout: TextView = fragment.activity?.layoutInflater
                            ?.inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeCover3Type,
                                false
                            ) as TextView
                        tvFlowLayout.text = it[i].title
                        tvFlowLayout.setOnClickListener { _ ->
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
            holder is AnimeCover4ViewHolder && item is AnimeCoverBean -> {
                holder.ivAnimeCover4Cover.setTag(R.id.image_view_tag, item.cover)
                fragment.activity?.let { activity ->
                    if (holder.ivAnimeCover4Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover4Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                }
                holder.tvAnimeCover4Title.text = item.title
                holder.itemView.setOnClickListener {
                    process(item.actionUrl)
                }
            }
            holder is AnimeCover5ViewHolder && item is AnimeCoverBean -> {
                holder.tvAnimeCover5Rank.gone()
                if (item.area == null || item.area?.title == "") {
                    holder.tvAnimeCover5Area.gone()
                    holder.tvAnimeCover5Date.post {
                        holder.tvAnimeCover5Date.setPadding(0, 0, 0, 0)
                    }
                } else {
                    holder.tvAnimeCover5Area.background =
                        getResDrawable(R.drawable.shape_fill_circle_corner_main_color_2_50_skin)
                    holder.tvAnimeCover5Area.visible()
                    holder.tvAnimeCover5Date.post {
                        holder.tvAnimeCover5Date.setPadding(12.dp, 0, 0, 0)
                    }
                }
                if (item.date == null || item.date == "") {
                    holder.tvAnimeCover5Date.gone()
                } else {
                    holder.tvAnimeCover5Date.visible()
                }
                holder.tvAnimeCover5Title.text = item.title
                holder.tvAnimeCover5Area.text = item.area?.title
                holder.tvAnimeCover5Date.text = item.date
                holder.tvAnimeCover5Episode.text = item.episodeClickable?.title
                if (holder.tvAnimeCover5Area.visibility == View.GONE &&
                    holder.tvAnimeCover5Date.visibility == View.GONE
                ) {
                    holder.tvAnimeCover5Title.post {
                        holder.tvAnimeCover5Title.setPadding(
                            holder.tvAnimeCover5Title.paddingStart, 12.dp,
                            holder.tvAnimeCover5Title.paddingEnd, 12.dp
                        )
                    }
                }
                View.OnClickListener {
                    process(item.actionUrl)
                }.apply {
                    holder.itemView.setOnClickListener(this)
                    holder.tvAnimeCover5Area.setOnClickListener(this)
                    holder.tvAnimeCover5Title.setOnClickListener(this)
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }

    class GridRecyclerView1Adapter(
        private val activity: Activity,
        private val dataList: List<AnimeCoverBean>,
        private var titleColor: Int = activity.getResColor(R.color.foreground_black_skin)
    ) : BaseRvAdapter(dataList) {
        //必须四个参数都不是-1才生效
        var padding = Rect(-1, -1, -1, -1)

        //是否显示排行榜排行，目前仅支持animeCover5
        var showRankNumber = false

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            if (padding.left != -1 && padding.top != -1 && padding.right != -1 && padding.bottom != -1) {
                holder.itemView.post {
                    holder.itemView.setPadding(
                        padding.left,
                        padding.top,
                        padding.right,
                        padding.bottom
                    )
                }
            }

            when (holder) {
                is AnimeCover1ViewHolder -> {
                    holder.viewAnimeCover1Night.setBackgroundColor(activity.getResColor(R.color.transparent_skin))
                    holder.tvAnimeCover1Title.setTextColor(titleColor)
                    holder.tvAnimeCover1Episode.setTextColor(activity.getResColor(R.color.main_color_skin))
                    holder.ivAnimeCover1Cover.setTag(R.id.image_view_tag, item.cover)
                    if (holder.ivAnimeCover1Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover1Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                    holder.tvAnimeCover1Title.text = item.title
                    if (item.episode.isBlank()) {
                        holder.tvAnimeCover1Episode.gone()
                    } else {
                        holder.tvAnimeCover1Episode.visible()
                        holder.tvAnimeCover1Episode.text = item.episode
                    }
                    holder.itemView.setOnClickListener {
                        process(item.actionUrl)
                    }
                }
                is AnimeCover3ViewHolder -> {
                    holder.viewAnimeCover3Night.setBackgroundColor(activity.getResColor(R.color.transparent_skin))
                    holder.tvAnimeCover3Title.setTextColor(titleColor)
                    holder.tvAnimeCover3Episode.setTextColor(activity.getResColor(R.color.main_color_skin))
                    holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, item.cover)
                    if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover3Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                    holder.tvAnimeCover3Title.text = item.title
                    if (item.episode.isBlank()) {
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
                is AnimeCover4ViewHolder -> {
                    holder.viewAnimeCover4Night.setBackgroundColor(activity.getResColor(R.color.transparent_skin))
                    holder.tvAnimeCover4Title.setTextColor(titleColor)
                    holder.ivAnimeCover4Cover.setTag(R.id.image_view_tag, item.cover)
                    if (holder.ivAnimeCover4Cover.getTag(R.id.image_view_tag) == item.cover) {
                        holder.ivAnimeCover4Cover.loadImage(
                            item.cover ?: "",
                            referer = item.cover?.also { Api.refererProcessor?.processor(it) } ?: ""
                        )
                    }
                    holder.tvAnimeCover4Title.text = item.title
                    holder.itemView.setOnClickListener {
                        process(item.actionUrl)
                    }
                }
                is AnimeCover5ViewHolder -> {
                    if (showRankNumber) {
                        holder.tvAnimeCover5Rank.setTextColor(Color.WHITE)
                        holder.tvAnimeCover5Rank.text = (position + 1).toString()
                        holder.tvAnimeCover5Rank.background = if (position in 0..2) {
                            val backgrounds = intArrayOf(
                                R.drawable.shape_fill_circle_corner_golden_50,
                                R.drawable.shape_fill_circle_corner_silvery_50,
                                R.drawable.shape_fill_circle_corner_coppery_50
                            )
                            getResDrawable(backgrounds[position])
                        } else {
                            getResDrawable(R.drawable.shape_fill_circle_corner_main_color_2_50_skin)
                        }
                        holder.tvAnimeCover5Rank.visible()
                    } else {
                        holder.tvAnimeCover5Rank.gone()
                    }
                    holder.tvAnimeCover5Title.setTextColor(titleColor)
                    if (item.area == null || item.area?.title == "") {
                        holder.tvAnimeCover5Area.gone()
                        holder.tvAnimeCover5Date.post {
                            holder.tvAnimeCover5Date.setPadding(0, 0, 0, 0)
                        }
                    } else {
                        holder.tvAnimeCover5Area.background =
                            getResDrawable(R.drawable.shape_fill_circle_corner_main_color_2_50_skin)
                        holder.tvAnimeCover5Area.visible()
                        holder.tvAnimeCover5Date.post {
                            holder.tvAnimeCover5Date.setPadding(12.dp, 0, 0, 0)
                        }
                    }
                    if (item.date == null || item.date == "") {
                        holder.tvAnimeCover5Date.gone()
                    } else {
                        holder.tvAnimeCover5Date.setTextColor(activity.getResColor(R.color.main_color_skin))
                        holder.tvAnimeCover5Date.visible()
                    }
                    holder.tvAnimeCover5Title.text = item.title
                    holder.tvAnimeCover5Area.text = item.area?.title
                    holder.tvAnimeCover5Date.text = item.date
                    holder.tvAnimeCover5Episode.setTextColor(activity.getResColor(R.color.foreground_main_color_2_skin))
                    holder.tvAnimeCover5Episode.text = item.episodeClickable?.title
                    if (holder.tvAnimeCover5Area.visibility == View.GONE &&
                        holder.tvAnimeCover5Date.visibility == View.GONE
                    ) {
                        holder.tvAnimeCover5Title.post {
                            holder.tvAnimeCover5Title.setPadding(
                                holder.tvAnimeCover5Title.paddingStart, 12.dp,
                                holder.tvAnimeCover5Title.paddingEnd, 12.dp
                            )
                        }
                    }
                    View.OnClickListener {
                        process(item.actionUrl)
                    }.apply {
                        holder.itemView.setOnClickListener(this)
                        holder.tvAnimeCover5Area.setOnClickListener(this)
                        holder.tvAnimeCover5Title.setOnClickListener(this)
                    }
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
                }
            }
        }
    }
}