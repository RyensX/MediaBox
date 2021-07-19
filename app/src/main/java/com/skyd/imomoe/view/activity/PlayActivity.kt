package com.skyd.imomoe.view.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadSampleListener
import com.liulishuo.filedownloader.FileDownloader
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_DETAIL
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivityPlayBinding
import com.skyd.imomoe.util.AnimeEpisode2ViewHolder
import com.skyd.imomoe.util.MD5.getMD5
import com.skyd.imomoe.util.html.SnifferVideo
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.getDetailLinkByEpisodeLink
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.openVideoByExternalPlayer
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.view.adapter.PlayAdapter
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.PlaySpanSize
import com.skyd.imomoe.view.component.player.AnimeVideoPlayer
import com.skyd.imomoe.view.component.player.DanmakuVideoPlayer
import com.skyd.imomoe.view.component.player.DetailPlayerActivity
import com.skyd.imomoe.view.fragment.MoreDialogFragment
import com.skyd.imomoe.view.fragment.ShareDialogFragment
import com.skyd.imomoe.viewmodel.PlayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File


class PlayActivity : DetailPlayerActivity<AnimeVideoPlayer>() {
    private lateinit var mBinding: ActivityPlayBinding
    private var isFavorite: Boolean = false
    private var favoriteBeanDataReady: Int = 0
        set(value) {
            field = value
            if (value == 2) mBinding.ivPlayActivityFavorite.isEnabled = true
        }
    private lateinit var videoPlayer: AnimeVideoPlayer
    private var partUrl: String = ""
    private var detailPartUrl: String = ""
    private lateinit var viewModel: PlayViewModel
    private lateinit var adapter: PlayAdapter
    private var isFirstTime = true
    private var danmuUrl: String = ""
    private var danmuParamMap: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setColorStatusBar(window, Color.BLACK)

        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        adapter = PlayAdapter(this, viewModel.playBeanDataList)

        videoPlayer = findViewById(R.id.avp_play_activity)
        initVideoBuilderMode()

        videoPlayer.getDownloadButton()?.setOnClickListener {
            getSheetDialog("download").show()
        }

        //设置返回按键功能
        videoPlayer.backButton?.setOnClickListener { onBackPressed() }

        partUrl = intent.getStringExtra("partUrl") ?: ""
        detailPartUrl = intent.getStringExtra("detailPartUrl") ?: ""

        // 如果没有传入详情页面的网址，则通过播放页面的网址计算出详情页面的网址
        if (detailPartUrl.isBlank() || detailPartUrl == ANIME_DETAIL)
            detailPartUrl = getDetailLinkByEpisodeLink(partUrl)

        //分享按钮
        videoPlayer.getShareButton()?.setOnClickListener {
            ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                .show(supportFragmentManager, "share_dialog")
        }

        //更多按钮
        videoPlayer.getMoreButton()?.setOnClickListener {
            MoreDialogFragment().run {
                setOnClickListener(
                    arrayOf(View.OnClickListener { dismiss() },
                        View.OnClickListener {
                            startActivity(
                                Intent(this@PlayActivity, DlnaActivity::class.java)
                                    .putExtra("url", videoPlayer.getUrl())
                                    .putExtra("title", videoPlayer.getTitle())
                            )
                            dismiss()
                        }, View.OnClickListener {
                            if (!openVideoByExternalPlayer(
                                    this@PlayActivity,
                                    viewModel.animeEpisodeDataBean.videoUrl
                                )
                            ) getString(R.string.matched_app_not_found).showToast()
                            dismiss()
                        })
                )
                show(supportFragmentManager, "more_dialog")
            }
        }

        mBinding.run {
            rvPlayActivity.layoutManager = GridLayoutManager(this@PlayActivity, 4)
                .apply { spanSizeLookup = PlaySpanSize(adapter) }
            // 复用AnimeShow的ItemDecoration
            rvPlayActivity.addItemDecoration(AnimeShowItemDecoration())
            rvPlayActivity.setHasFixedSize(true)
            rvPlayActivity.adapter = adapter

            srlPlayActivity.setOnRefreshListener { viewModel.getPlayData(partUrl) }
            srlPlayActivity.setColorSchemeResources(R.color.main_color)
        }

        GlobalScope.launch(Dispatchers.IO) {
            val favoriteAnime = getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
            withContext(Dispatchers.Main) {
                isFavorite = if (favoriteAnime == null) {
                    mBinding.ivPlayActivityFavorite.setImageResource(R.drawable.ic_star_border_main_color_2_24)
                    false
                } else {
                    mBinding.ivPlayActivityFavorite.setImageResource(R.drawable.ic_star_main_color_2_24)
                    true
                }
                mBinding.ivPlayActivityFavorite.setOnClickListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        if (isFavorite) {
                            getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(detailPartUrl)
                            withContext(Dispatchers.Main) {
                                isFavorite = false
                                mBinding.ivPlayActivityFavorite.setImageResource(R.drawable.ic_star_border_main_color_2_24)
                                getString(R.string.remove_favorite_succeed).showToast()
                            }
                        } else {
                            getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                                FavoriteAnimeBean(
                                    Const.ViewHolderTypeString.ANIME_COVER_8, "",
                                    detailPartUrl,
                                    viewModel.playBean?.title?.title ?: "",
                                    System.currentTimeMillis(),
                                    viewModel.animeCover,
                                    lastEpisodeUrl = viewModel.partUrl,
                                    lastEpisode = viewModel.animeEpisodeDataBean.title
                                )
                            )
                            withContext(Dispatchers.Main) {
                                isFavorite = true
                                mBinding.ivPlayActivityFavorite.setImageResource(R.drawable.ic_star_main_color_2_24)
                                getString(R.string.favorite_succeed).showToast()
                            }
                        }
                    }
                }
            }
        }
        mBinding.ivPlayActivityFavorite.isEnabled = false

        viewModel.mldAnimeCover.observe(this, Observer {
            if (it) {
                favoriteBeanDataReady++
            }
        })

        viewModel.mldPlayBean.observe(this, Observer {
            mBinding.srlPlayActivity.isRefreshing = false

            val title = viewModel.playBean?.title?.title
            mBinding.tvPlayActivityTitle.text = title

            adapter.notifyDataSetChanged()

            favoriteBeanDataReady++

            if (isFirstTime) {
                videoPlayer.startPlay()
                isFirstTime = false
            }
        })

        //缓存番剧调用getAnimeEpisodeData()来获取视频url
        viewModel.mldGetAnimeEpisodeData.observe(this, Observer {
            val url = viewModel.episodesList[it].videoUrl
            if (url.endsWith("\$qzz", true)) {
                SnifferVideo.getQzzVideoUrl(
                    this@PlayActivity, viewModel.episodesList[it].actionUrl, detailPartUrl
                ) { videoUrl, paramMap ->
//                    danmuUrl = danMuUrl
//                    danmuParamMap.clear()
//                    danmuParamMap.putAll(paramMap)
                    runOnUiThread {
                        AnimeDownloadHelper.instance.downloadAnime(
                            this, videoUrl, getMD5(videoUrl),
                            viewModel.playBean?.title?.title + "/" +
                                    viewModel.episodesList[it].title
                        )
                    }
                }
                return@Observer
            } else {
                AnimeDownloadHelper.instance.downloadAnime(
                    this, url, getMD5(url),
                    viewModel.playBean?.title?.title + "/" +
                            viewModel.episodesList[it].title
                )
            }
        })

        viewModel.mldAnimeEpisodeDataRefreshed.observe(this, Observer {
            if (it) videoPlayer.currentPlayer.startPlay(partUrl = viewModel.animeEpisodeDataBean.actionUrl)
        })

        mBinding.srlPlayActivity.isRefreshing = true
        viewModel.getPlayData(partUrl)
        viewModel.getAnimeCover(detailPartUrl)

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    fun startPlay(url: String, currentEpisodeIndex: Int, title: String) {
        viewModel.refreshAnimeEpisodeData(url, currentEpisodeIndex, title)
    }

    fun startPlay2(url: String, title: String, partUrl: String = this@PlayActivity.partUrl) {
        videoPlayer.startPlay(url, title, partUrl)
    }

    private fun GSYBaseVideoPlayer.startPlay(
        url: String = "",
        title: String = "",
        partUrl: String = this@PlayActivity.partUrl
    ) {
        PlayerFactory.setPlayManager(Exo2PlayerManager().javaClass)
        GSYVideoType.disableMediaCodec()        // 关闭硬解码
        //设置播放URL
        if (url.isBlank()) {
            if (!isDestroyed) {
                viewModel.updateFavoriteData(
                    detailPartUrl,
                    viewModel.partUrl,
                    viewModel.animeEpisodeDataBean.title,
                    System.currentTimeMillis()
                )
                viewModel.insertHistoryData(detailPartUrl)
            }
            if (!viewModel.animeEpisodeDataBean.videoUrl.endsWith("\$qzz", true)) {
                danmuUrl = ""
                setUp(
                    viewModel.animeEpisodeDataBean.videoUrl,
                    false, viewModel.animeEpisodeDataBean.title
                )
            } else {
                SnifferVideo.getQzzVideoUrl(
                    this@PlayActivity,
                    partUrl,
                    detailPartUrl
                ) { videoUrl, paramMap ->
                    danmuParamMap.clear()
                    danmuParamMap.putAll(paramMap)
                    danmuUrl = paramMap[SnifferVideo.DANMU_URL] ?: ""
                    runOnUiThread {
                        setUp(videoUrl, false, viewModel.animeEpisodeDataBean.title)
                        //开始播放
                        startPlayLogic()
                    }
                }
                return
            }
        } else {
            if (!isDestroyed) {
                viewModel.updateFavoriteData(
                    detailPartUrl, viewModel.partUrl, title,
                    System.currentTimeMillis()
                )
                viewModel.insertHistoryData(detailPartUrl)
            }
            if (!url.endsWith("\$qzz", true)) {
                danmuUrl = ""
                setUp(url, false, title)
            } else {
                SnifferVideo.getQzzVideoUrl(this@PlayActivity, partUrl, detailPartUrl)
                { videoUrl, paramMap ->
                    danmuParamMap.clear()
                    danmuParamMap.putAll(paramMap)
                    danmuUrl = paramMap[SnifferVideo.DANMU_URL] ?: ""
                    setUp(videoUrl, false, title)
                    // 开始播放
                    startPlayLogic()
                }
                return
            }
        }
        //开始播放
        startPlayLogic()
    }

    override fun onPlayError(url: String?, vararg objects: Any?) {
        super.onPlayError(url, *objects)
        "${objects[0].toString()}, ${getString(R.string.get_data_failed)}".showToast()
//        SnifferVideo.askSnifferDialog(
//            this, objects[0].toString() + ", " + getString(R.string.get_data_failed),
//            getString(R.string.will_you_try_to_sniffer_video),
//            partUrl, detailPartUrl
//        ) { videoUrl, danMuUrl ->
//            videoPlayer.startPlay(videoUrl, viewModel.animeEpisodeDataBean.title)
//        }
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        super.onPrepared(url, *objects)
        //调整触摸滑动快进的比例
        //毫秒,刚好划一屏1分35秒
        videoPlayer.currentPlayer.apply {
            seekRatio = duration / 90_000f
            if (danmuUrl.isNotBlank() && this is DanmakuVideoPlayer && !this@PlayActivity.isDestroyed) {
                this@PlayActivity.getString(R.string.the_video_has_danmu).showToast()
                this.setDanmaKuUrl(danmuUrl, paramMap = danmuParamMap)
            }
        }
    }

    override fun getGSYVideoPlayer(): AnimeVideoPlayer = videoPlayer

    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {
        return GSYVideoOptionBuilder()
            .setReleaseWhenLossAudio(false)         //音频焦点冲突时是否释放
            .setPlayTag(this.javaClass.simpleName)  //防止错位设置
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false)            //打开动画
            .setNeedLockFull(true)
            .setDismissControlTime(5000)
    }

    override fun clickForFullScreen() {}

    override fun getDetailOrientationRotateAuto(): Boolean = true

    fun getSheetDialog(action: String): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(this, R.layout.dialog_bottom_sheet_2, null)
        bottomSheetDialog.setContentView(contentView)
        val tvTitle =
            contentView.findViewById<TextView>(R.id.tv_dialog_bottom_sheet_2_title)
        tvTitle.text = when (action) {
            "play" -> getString(R.string.play_list)
            "download" -> getString(R.string.download_anime)
            else -> ""
        }
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.rv_dialog_bottom_sheet_2)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.post {
            recyclerView.setPadding(
                dp2px(16f), dp2px(16f),
                dp2px(16f), dp2px(16f)
            )
            recyclerView.scrollToPosition(0)
        }
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(AnimeEpisodeItemDecoration())
        }
        val adapter = EpisodeRecyclerViewAdapter(
            this,
            viewModel.episodesList,
            bottomSheetDialog,
            1,
            action
        )
        recyclerView.adapter = adapter
        viewModel.mldEpisodesList.observe(this, Observer {
            adapter.notifyDataSetChanged()
            mBinding.avpPlayActivity.setEpisodeAdapter(
                PlayerEpisodeRecyclerViewAdapter(
                    this,
                    viewModel.episodesList
                )
            )
        })
        return bottomSheetDialog
    }

    class EpisodeRecyclerViewAdapter(
        private val activity: PlayActivity,
        private val dataList: List<AnimeEpisodeDataBean>,
        private val dialog: Dialog? = null,
        private val showType: Int = 0,    //0是横向，1是三列
        private val action: String = "play"
    ) : AnimeDetailAdapter.EpisodeRecyclerView1Adapter(activity, dataList, dialog, showType) {

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            when (holder) {
                is AnimeEpisode2ViewHolder -> {
                    holder.tvAnimeEpisode2.text = item.title
                    holder.tvAnimeEpisode2.setTextColor(
                        activity.getResColor(R.color.foreground_main_color_2)
                    )
                    val layoutParams = holder.itemView.layoutParams
                    if (showType == 0) {
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        if (layoutParams is ViewGroup.MarginLayoutParams) {
                            layoutParams.setMargins(0, dp2px(5f), dp2px(10f), dp2px(5f))
                        }
                        holder.itemView.layoutParams = layoutParams
                    } else {
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        holder.itemView.setPadding(0, dp2px(10f), 0, dp2px(10f))
                        holder.itemView.layoutParams = layoutParams
                    }
                    if (action == "play") {
                        holder.itemView.setOnClickListener {
                            activity.startPlay(item.actionUrl, position, item.title)
                            dialog?.dismiss()
                        }
                    } else if (action == "download") {
                        holder.itemView.setOnClickListener {
                            activity.getString(R.string.parsing_video).showToast()
                            activity.viewModel.getAnimeEpisodeData(item.actionUrl, position)
                        }
                    }
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
                }
            }
        }
    }

    class PlayerEpisodeRecyclerViewAdapter(
        private val activity: PlayActivity,
        private val dataList: List<AnimeEpisodeDataBean>,
    ) : AnimeVideoPlayer.EpisodeRecyclerViewAdapter(activity, dataList) {

        override val currentIndex: Int
            get() = activity.viewModel.currentEpisodeIndex

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            when (holder) {
                is AnimeVideoPlayer.RightRecyclerViewViewHolder -> {
                    holder.tvTitle.setTextColor(
                        activity.getResColor(
                            if (item.title == activity.viewModel.animeEpisodeDataBean.title)
                                R.color.unchanged_main_color_2
                            else R.color.foreground_white
                        )
                    )
                    holder.tvTitle.text = item.title
                    holder.itemView.setOnClickListener {
                        activity.mBinding.avpPlayActivity.currentPlayer.run {
                            if (this is AnimeVideoPlayer) {
                                getRightContainer()?.gone()
                                // 因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                                enableDismissControlViewTimer(true)
                            }
                        }
                        activity.startPlay(item.actionUrl, position, item.title)
                    }
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
                }
            }
        }
    }

    companion object {
        const val TAG = "PlayActivity"
    }
}