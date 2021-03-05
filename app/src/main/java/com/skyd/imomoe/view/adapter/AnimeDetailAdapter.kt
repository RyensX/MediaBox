package com.skyd.imomoe.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDetailBean
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.glide.GlideUtil.loadImage
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.process
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getItemViewType
import com.skyd.imomoe.util.ViewHolderUtil.Companion.getViewHolder
import com.skyd.imomoe.view.activity.AnimeDetailActivity
import com.skyd.imomoe.view.widget.BottomSheetRecyclerView

class AnimeDetailAdapter(
    val activity: AnimeDetailActivity,
    private val dataList: List<AnimeDetailBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val gridItemDecoration = AnimeCoverItemDecoration()
    private val animeEpisodeItemDecoration = AnimeEpisodeItemDecoration()

    override fun getItemViewType(position: Int): Int = getItemViewType(dataList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        getViewHolder(parent, viewType)

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is Header1ViewHolder -> {
                holder.tvHeader1Title.textSize = 15f
                holder.tvHeader1Title.text = item.title
                holder.tvHeader1Title.setTextColor(
                    activity.resources.getColor(R.color.foreground_white)
                )
            }
            is GridRecyclerView1ViewHolder -> {
                item.animeCoverList?.let {
                    val layoutManager = GridLayoutManager(activity, 4)
                    holder.rvGridRecyclerView1.post {
                        holder.rvGridRecyclerView1.setPadding(
                            dp2px(16f), 0,
                            dp2px(16f), 0
                        )
                    }
                    if (holder.rvGridRecyclerView1.itemDecorationCount == 0) {
                        holder.rvGridRecyclerView1.addItemDecoration(gridItemDecoration)
                    }
                    holder.rvGridRecyclerView1.layoutManager = layoutManager
                    holder.rvGridRecyclerView1.adapter =
                        AnimeShowAdapter.GridRecyclerView1Adapter(
                            activity, it,
                            activity.resources.getColor(R.color.foreground_white)
                        )
                }
            }
            is HorizontalRecyclerView1ViewHolder -> {
                item.episodeList?.let {
                    if (holder.rvHorizontalRecyclerView1.adapter == null) {
                        holder.rvHorizontalRecyclerView1.adapter =
                            EpisodeRecyclerView1Adapter(activity, it)
                    }
                    holder.ivHorizontalRecyclerView1More.setOnClickListener { it1 ->
                        showEpisodeSheetDialog(it).show()
                    }
                }
            }
            is AnimeDescribe1ViewHolder -> {
                holder.tvAnimeDescribe1.text = item.describe
                holder.tvAnimeDescribe1.setOnClickListener { }
                holder.tvAnimeDescribe1.setTextColor(
                    activity.resources.getColor(R.color.foreground_white)
                )
            }
            is AnimeInfo1ViewHolder -> {
                item.headerInfo?.let {
                    holder.ivAnimeInfo1Cover.setTag(R.id.image_view_tag, it.cover.url)
                    if (holder.ivAnimeInfo1Cover.getTag(R.id.image_view_tag) == it.cover.url) {
                        holder.ivAnimeInfo1Cover.loadImage(
                            activity,
                            it.cover.url,
                            referer = it.cover.referer,
                            placeholder = 0,
                            error = 0
                        )
                    }
                    holder.tvAnimeInfo1Title.text = it.title
                    holder.tvAnimeInfo1Alias.text = it.alias
                    holder.tvAnimeInfo1Area.text = it.area
                    holder.tvAnimeInfo1Year.text = it.year
                    holder.tvAnimeInfo1Index.text =
                        App.context.getString(R.string.anime_detail_index) + it.index
                    holder.tvAnimeInfo1Info.text = it.info
                    holder.flAnimeInfo1Type.removeAllViews()
                    for (i in it.animeType.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeInfo1Type,
                                false
                            ) as TextView
                        tvFlowLayout.text = it.animeType[i].title
                        tvFlowLayout.setOnClickListener { it1 ->
                            //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                            process(
                                activity,
                                Const.ActionUrl.ANIME_CLASSIFY +
                                        "${it.animeType[i].actionUrl}类型/${it.animeType[i].title}"
                            )
                        }
                        holder.flAnimeInfo1Type.addView(tvFlowLayout)
                    }

                    holder.flAnimeInfo1Tag.removeAllViews()
                    for (i in it.tag.indices) {
                        val tvFlowLayout: TextView = activity.layoutInflater
                            .inflate(
                                R.layout.item_anime_type_1,
                                holder.flAnimeInfo1Tag,
                                false
                            ) as TextView
                        tvFlowLayout.text = it.tag[i].title
                        tvFlowLayout.setOnClickListener { it1 ->
                            //此处是”标签“，由于分类没有这一大项，因此传入”“串
                            process(
                                activity,
                                Const.ActionUrl.ANIME_CLASSIFY +
                                        "${it.tag[i].actionUrl}/${it.tag[i].title}"
                            )
                        }
                        holder.flAnimeInfo1Tag.addView(tvFlowLayout)
                    }
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showEpisodeSheetDialog(dataList: List<AnimeEpisodeDataBean>): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(activity, R.layout.dialog_bottom_sheet_2, null)
        bottomSheetDialog.setContentView(contentView)
        val recyclerView =
            contentView.findViewById<BottomSheetRecyclerView>(R.id.rv_dialog_bottom_sheet_2)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.post {
            recyclerView.setPadding(
                dp2px(16f), dp2px(16f),
                dp2px(16f), dp2px(16f)
            )
            recyclerView.scrollToPosition(0)
        }
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(animeEpisodeItemDecoration)
        }
        recyclerView.adapter = EpisodeRecyclerView1Adapter(activity, dataList, bottomSheetDialog, 1)
        return bottomSheetDialog
    }

    open class EpisodeRecyclerView1Adapter(
        private val activity: Activity,
        private val dataList: List<AnimeEpisodeDataBean>,
        private val dialog: Dialog? = null,
        private val showType: Int = 0    //0是横向，1是三列
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int =
            getItemViewType(dataList[position])

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder =
            getViewHolder(parent, viewType)

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            when (holder) {
                is AnimeEpisode2ViewHolder -> {
                    holder.tvAnimeEpisode2.text = item.title
                    if (showType == 0) {
                        holder.itemView.setBackgroundResource(R.drawable.shape_circle_corner_edge_white_ripper_5)
                        val layoutParams = holder.itemView.layoutParams
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        if (layoutParams is ViewGroup.MarginLayoutParams) {
                            layoutParams.setMargins(0, dp2px(5f), dp2px(10f), dp2px(5f))
                        }
                        holder.itemView.layoutParams = layoutParams
                        holder.tvAnimeEpisode2.setTextColor(activity.resources.getColor(R.color.foreground_white))
                    } else {
                        holder.itemView.setBackgroundResource(R.drawable.shape_circle_corner_edge_main_color_2_ripper_5)
                        val layoutParams = holder.itemView.layoutParams
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        holder.itemView.setPadding(0, dp2px(10f), 0, dp2px(10f))
                        holder.itemView.layoutParams = layoutParams
                        holder.tvAnimeEpisode2.setTextColor(activity.resources.getColor(R.color.foreground_main_color_2))
                    }
                    holder.itemView.setOnClickListener {
                        process(activity, item.actionUrl, item.actionUrl)
                        dialog?.dismiss()
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