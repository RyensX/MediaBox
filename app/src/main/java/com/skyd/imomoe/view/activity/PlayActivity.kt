package com.skyd.imomoe.view.activity

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivityPlayBinding
import com.skyd.imomoe.util.AnimeEpisode2ViewHolder
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.view.adapter.AnimeDetailAdapter
import com.skyd.imomoe.view.adapter.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.PlayAdapter
import com.skyd.imomoe.view.fragment.ShareDialogFragment
import com.skyd.imomoe.view.widget.AnimeVideoPlayer
import com.skyd.imomoe.viewmodel.PlayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayActivity : GSYBaseActivityDetail<AnimeVideoPlayer>() {
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

        //分享按钮
        videoPlayer.getShareButton()?.setOnClickListener {
            ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                .show(supportFragmentManager, "share_dialog")
        }

        mBinding.run {
            rvPlayActivity.layoutManager = LinearLayoutManager(this@PlayActivity)
            rvPlayActivity.setHasFixedSize(true)
            rvPlayActivity.adapter = adapter

            srlPlayActivity.setOnRefreshListener { viewModel.getPlayData(partUrl) }
            srlPlayActivity.setColorSchemeResources(R.color.main_color)
        }

        GlobalScope.launch(Dispatchers.IO) {
            val favoriteAnime = getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
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
            AnimeDownloadHelper.instance.downloadAnime(
                this,
                url,
                viewModel.playBean?.title?.title + "/" +
                        viewModel.episodesList[it].title
            )
        })

        mBinding.srlPlayActivity.isRefreshing = true
        viewModel.getPlayData(partUrl)
        viewModel.getAnimeCover(detailPartUrl)
    }

    fun startPlay(url: String, currentEpisodeIndex: Int, title: String) {
        viewModel.mldAnimeEpisodeDataRefreshed.observe(this, Observer {
            if (it) {
                videoPlayer.currentPlayer.startPlay()
            }
        })
        viewModel.refreshAnimeEpisodeData(url, currentEpisodeIndex, title)
    }

    fun startPlay2(url: String, title: String) {
        videoPlayer.startPlay(url, title)
    }

    private fun GSYBaseVideoPlayer.startPlay(url: String = "", title: String = "") {
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
            setUp(
                viewModel.animeEpisodeDataBean.videoUrl,
                false, viewModel.animeEpisodeDataBean.title
            )
        } else {
            if (!isDestroyed) {
                viewModel.updateFavoriteData(
                    detailPartUrl, viewModel.partUrl, title,
                    System.currentTimeMillis()
                )
                viewModel.insertHistoryData(detailPartUrl)
            }
            setUp(url, false, title)
        }
        //开始播放
        startPlayLogic()
    }

    override fun onPlayError(url: String?, vararg objects: Any?) {
        super.onPlayError(url, *objects)
        (objects[0].toString() + ", " + getString(R.string.get_data_failed)).showToast()
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        super.onPrepared(url, *objects)
        //调整触摸滑动快进的比例
        //毫秒,刚好划一屏1分35秒
        videoPlayer.seekRatio = videoPlayer.duration / 90_000f
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

    override fun clickForFullScreen() {
    }

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