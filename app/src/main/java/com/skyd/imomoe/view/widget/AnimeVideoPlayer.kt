package com.skyd.imomoe.view.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.util.SearchHistory1ViewHolder
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.visible
import kotlinx.android.synthetic.main.layout_anime_video_player.view.*
import java.io.File
import java.io.Serializable


class AnimeVideoPlayer : StandardGSYVideoPlayer {
    companion object {
        val mScaleStrings = listOf(
            Pair("默认比例", GSYVideoType.SCREEN_TYPE_DEFAULT),
            Pair("16:9", GSYVideoType.SCREEN_TYPE_16_9),
            Pair("4:3", GSYVideoType.SCREEN_TYPE_4_3),
            Pair("全屏", GSYVideoType.SCREEN_TYPE_FULL),
            Pair("拉伸全屏", GSYVideoType.SCREEN_MATCH_FULL)
        )
    }

    protected var mDownloadButton: ImageView? = null

    private var initFirstLoad = true

    //记住切换数据源类型
    private var mScaleIndex = 0

    //4:3  16:9等
    private var mMoreScaleTextView: TextView? = null

    //倍速按钮
    private var mSpeedTextView: TextView? = null

    //右侧弹出栏
    private var mRightContainer: ViewGroup? = null
    private var mRightContainerRecyclerView: RecyclerView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = R.layout.layout_anime_video_player

    override fun init(context: Context?) {
        super.init(context)

        mDownloadButton = findViewById(R.id.iv_download)
        mMoreScaleTextView = findViewById(R.id.tv_more_scale)
        mSpeedTextView = findViewById(R.id.tv_speed)
        mRightContainer = findViewById(R.id.layout_right)
        mRightContainerRecyclerView = findViewById(R.id.rv_right)

        mRightContainer?.gone()
        mSpeedTextView?.setOnClickListener {
            mRightContainer?.let {
                val adapter = RightRecyclerViewAdapter(
                    listOf(
                        RightRecyclerViewBean("speed", "", "0.5"),
                        RightRecyclerViewBean("speed", "", "0.75"),
                        RightRecyclerViewBean("speed", "", "1"),
                        RightRecyclerViewBean("speed", "", "1.25"),
                        RightRecyclerViewBean("speed", "", "1.5"),
                        RightRecyclerViewBean("speed", "", "2")
                    )
                )
                mRightContainerRecyclerView?.layoutManager = LinearLayoutManager(context)
                mRightContainerRecyclerView?.adapter = adapter
                adapter.notifyDataSetChanged()
                it.translationX = dp2px(150f).toFloat()
                it.visible()
                val animator = ObjectAnimator.ofFloat(
                    it,
                    "translationX", dp2px(170f).toFloat(), 0f
                )
                animator.duration = 300
                animator.start()
                //取消xx秒后隐藏控制界面
                cancelDismissControlViewTimer()
            }
        }
//        mScaleIndex = mScaleStrings.indexOfFirst { GSYVideoType.getShowType() == it.second }
        mMoreScaleTextView?.text = mScaleStrings[mScaleIndex].first
        //切换清晰度
        mMoreScaleTextView?.setOnClickListener(OnClickListener {
            if (!mHadPlay) {
                return@OnClickListener
            }
            mScaleIndex = (mScaleIndex + 1) % mScaleStrings.size
            resolveTypeUI()
        })
    }

    override fun hideAllWidget() {
        super.hideAllWidget()
        setViewShowState(mRightContainer, INVISIBLE)
    }

    override fun onClickUiToggle(e: MotionEvent?) {
        mRightContainer?.let {
            //如果右侧栏显示，则隐藏
            if (it.visibility == View.VISIBLE) {
                it.gone()
                //因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                startDismissControlViewTimer()
                return
            }
        }
        super.onClickUiToggle(e)
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val player = super.startWindowFullscreen(
            context,
            actionBar,
            statusBar
        ) as AnimeVideoPlayer
        player.mScaleIndex = mScaleIndex
        player.resolveTypeUI()
        return player
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        if (gsyVideoPlayer != null) {
            val player = gsyVideoPlayer as AnimeVideoPlayer
            mScaleIndex = player.mScaleIndex
            resolveTypeUI()
        }
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    @SuppressLint("SetTextI18n")
    private fun resolveTypeUI() {
        if (!mHadPlay) {
            return
        }
        mMoreScaleTextView?.text = mScaleStrings[mScaleIndex].first
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()
    }

    override fun setUp(
        url: String?,
        cacheWithPlay: Boolean,
        cachePath: File?,
        title: String?
    ): Boolean {
        val result = super.setUp(url, cacheWithPlay, cachePath, title)
        mTitleTextView?.let {
            if (it is TypefaceTextView) {
                it.setIsFocused(true)
            }
        }
        return result
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_pause_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_pause_main_color_bg)
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_play_main_color_bg)
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageResource(R.drawable.ic_refresh_white_24)
                    imageView.setBackgroundResource(0)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
                    imageView.setBackgroundResource(R.drawable.sel_play_main_color_bg)
                }
            }

        } else {
            super.updateStartImage()
        }
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        initFirstLoad = true
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        mBottomContainer.visibility = View.GONE
        mStartButton.visibility = View.GONE
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        if (initFirstLoad) {
            mBottomContainer.visibility = View.GONE
            mStartButton.visibility = View.GONE
        }
        initFirstLoad = false
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        mBottomContainer.visibility = View.GONE
    }

    fun getDownloadButton() = mDownloadButton

    fun getBottomContainer() = mBottomContainer

    class RightRecyclerViewBean(
        override var type: String,
        override var actionUrl: String,
        var title: String
    ) : BaseBean, Serializable

    inner class RightRecyclerViewAdapter(val list: List<RightRecyclerViewBean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class RightRecyclerViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle = view as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return RightRecyclerViewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_list_item_1, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]

            when (holder) {
                is RightRecyclerViewViewHolder -> {
                    if (item.title.toFloat() == speed) {
                        holder.tvTitle.setTextColor(App.context.resources.getColor(R.color.main_color_2))
                    }
                    holder.tvTitle.text = item.title
                    holder.itemView.setOnClickListener {
                        if (item.type == "speed") {
                            setSpeed(item.title.toFloat(), true)
                        }
                        mRightContainer?.gone()
                        //因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                        startDismissControlViewTimer()
                    }
                }
            }
        }

        override fun getItemCount(): Int = list.size
    }
}