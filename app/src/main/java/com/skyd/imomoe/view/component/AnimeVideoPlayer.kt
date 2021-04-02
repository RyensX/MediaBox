package com.skyd.imomoe.view.component

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.*
import android.view.View.OnClickListener
import android.widget.*
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.util.Util.dp2px
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.activity.DlnaActivity
import com.skyd.imomoe.view.component.textview.TypefaceTextView
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

        const val NO_REVERSE = 0
        const val HORIZONTAL_REVERSE = 1
        const val VERTICAL_REVERSE = 2
    }

    private var mDownloadButton: ImageView? = null

    private var initFirstLoad = true

    //记住切换数据源类型
    private var mScaleIndex = 0

    //4:3  16:9等
    private var mMoreScaleTextView: TextView? = null

    //倍速按钮
    private var mSpeedTextView: TextView? = null
    private var mSpeedRecyclerView: RecyclerView? = null

    //投屏按钮
    private var mClingImageView: ImageView? = null

    //分享按钮
    private var mShareImageView: ImageView? = null

    //下一集按钮
    private var mNextImageView: ImageView? = null

    //选集
    private var mEpisodeTextView: TextView? = null
    private var mEpisodeTextViewVisibility: Int = View.VISIBLE
    private var mEpisodeButtonOnClickListener: OnClickListener? = null
    private var mEpisodeRecyclerView: RecyclerView? = null
    private var mEpisodeAdapter: EpisodeRecyclerViewAdapter? = null

    // 设置
    private var mSettingContainer: ViewGroup? = null
    private var mSettingImageView: ImageView? = null

    // 镜像RadioGroup
    private var mReverseRadioGroup: RadioGroup? = null
    private var mReverseValue: Int? = null
    private var mTextureViewTransform: Int = NO_REVERSE

    // 底部进度条CheckBox
    private var mBottomProgressCheckBox: CheckBox? = null
    private var mBottomProgressCheckBoxValue: Boolean = true

    //底部进度调
    private var mBottomProgress: ProgressBar? = null

    //右侧弹出栏
    private var mRightContainer: ViewGroup? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = if (mIfCurrentIsFullscreen)
        R.layout.layout_anime_video_player_land else R.layout.layout_anime_video_player

    override fun init(context: Context?) {
        super.init(context)

        mDownloadButton = findViewById(R.id.iv_download)
        mMoreScaleTextView = findViewById(R.id.tv_more_scale)
        mSpeedTextView = findViewById(R.id.tv_speed)
        mClingImageView = findViewById(R.id.iv_cling)
        mRightContainer = findViewById(R.id.layout_right)
        mSpeedRecyclerView = findViewById(R.id.rv_right)
        mEpisodeRecyclerView = findViewById(R.id.rv_right)
        mShareImageView = findViewById(R.id.iv_share)
        mNextImageView = findViewById(R.id.iv_next)
        mEpisodeTextView = findViewById(R.id.tv_episode)
        mSettingImageView = findViewById(R.id.iv_setting)
        mSettingContainer = findViewById(R.id.layout_setting)
        mReverseRadioGroup = findViewById(R.id.rg_reverse)
        mBottomProgressCheckBox = findViewById(R.id.cb_bottom_progress)
        mBottomProgress = super.mBottomProgressBar

        mRightContainer?.gone()
        mSettingContainer?.gone()
        mSpeedTextView?.setOnClickListener {
            mRightContainer?.let {
                val adapter = SpeedAdapter(
                    listOf(
                        SpeedBean("speed", "", "0.5"),
                        SpeedBean("speed", "", "0.75"),
                        SpeedBean("speed", "", "1"),
                        SpeedBean("speed", "", "1.25"),
                        SpeedBean("speed", "", "1.5"),
                        SpeedBean("speed", "", "2")
                    )
                )
                mSpeedRecyclerView?.layoutManager = LinearLayoutManager(context)
                mSpeedRecyclerView?.adapter = adapter
                adapter.notifyDataSetChanged()
            }
            showRightContainer()
        }
        mEpisodeTextView?.setOnClickListener {
            mRightContainer?.let {
                mEpisodeRecyclerView?.layoutManager = LinearLayoutManager(context)
                mEpisodeRecyclerView?.adapter = mEpisodeAdapter
                mEpisodeAdapter?.notifyDataSetChanged()
                mEpisodeRecyclerView?.scrollToPosition(mEpisodeAdapter?.currentIndex ?: 0)
            }
            showRightContainer()
        }
        mSettingImageView?.setOnClickListener { showSettingContainer() }
        mReverseValue = mReverseRadioGroup?.getChildAt(0)?.id
        mReverseRadioGroup?.children?.forEach {
            (it as RadioButton).apply {
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) return@setOnCheckedChangeListener
                    mReverseValue = id
                    when (id) {
                        R.id.rb_no_reverse -> resolveTransform(NO_REVERSE)
                        R.id.rb_horizontal_reverse -> resolveTransform(HORIZONTAL_REVERSE)
                        R.id.rb_vertical_reverse -> resolveTransform(VERTICAL_REVERSE)
                    }
                }
            }
        }
        mBottomProgressCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mBottomProgressBar = mBottomProgress
                mBottomProgressBar.visible()
            } else {
                mBottomProgressBar.gone()
                mBottomProgressBar = null
            }
            mBottomProgressCheckBoxValue = isChecked
        }
        mBottomProgressCheckBox?.isChecked = mBottomProgressBar != null

        //重置视频比例
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()

        mMoreScaleTextView?.text = mScaleStrings[mScaleIndex].first

        //切换视频比例
        mMoreScaleTextView?.setOnClickListener(OnClickListener {
            startDismissControlViewTimer()      //重新开始ui消失时间计时
            if (!mHadPlay) {
                return@OnClickListener
            }
            mScaleIndex = (mScaleIndex + 1) % mScaleStrings.size
            resolveTypeUI()
        })

        mClingImageView?.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, DlnaActivity::class.java)
                    .putExtra("url", mUrl)
                    .putExtra("title", mTitle)
            )
            mOriginUrl
        }
    }

    private fun showSettingContainer() {
        mSettingContainer?.let {
            hideAllWidget()
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
            if (mReverseValue == null) mReverseValue = mReverseRadioGroup?.getChildAt(0)?.id
            mReverseValue?.let { id -> findViewById<RadioButton>(id).isChecked = true }
            mBottomProgressCheckBox?.isChecked = mBottomProgressCheckBoxValue
        }
    }

    private fun showRightContainer() {
        mRightContainer?.let {
            hideAllWidget()
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

    override fun hideAllWidget() {
        super.hideAllWidget()
        setViewShowState(mRightContainer, INVISIBLE)
        setViewShowState(mSettingContainer, INVISIBLE)
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
        mSettingContainer?.let {
            // 如果显示，则隐藏
            if (it.visibility == View.VISIBLE) {
                it.gone()
                // 因为设置界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
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
        player.mSpeedTextView?.text = mSpeedTextView?.text
        player.mFullscreenButton.visibility = mFullscreenButton.visibility
        player.mEpisodeTextViewVisibility = mEpisodeTextViewVisibility
        player.mEpisodeTextView?.visibility = mEpisodeTextViewVisibility
        player.mEpisodeAdapter = mEpisodeAdapter
        player.mTextureViewTransform = mTextureViewTransform
        player.mReverseValue = mReverseValue
        player.mBottomProgressCheckBoxValue = mBottomProgressCheckBoxValue
        if (player.mBottomProgressBar != null) player.mBottomProgress = player.mBottomProgressBar
        if (!player.mBottomProgressCheckBoxValue) player.mBottomProgressBar = null
        player.resolveTypeUI()
        return player
    }

    /**
     * 退出全屏时将对应处理参数逻辑返回给非播放器
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
            mFullscreenButton.visibility = player.mFullscreenButton.visibility
            mSpeedTextView?.text = player.mSpeedTextView?.text
            mEpisodeTextViewVisibility = player.mEpisodeTextViewVisibility
            mEpisodeTextView?.visibility = mEpisodeTextViewVisibility
            mEpisodeAdapter = player.mEpisodeAdapter
            mTextureViewTransform = player.mTextureViewTransform
            mReverseValue = player.mReverseValue
            mBottomProgressCheckBoxValue = player.mBottomProgressCheckBoxValue
            if (mBottomProgressBar != null) mBottomProgress = mBottomProgressBar
            if (!mBottomProgressCheckBoxValue) mBottomProgressBar = null
            resolveTypeUI()
        }
    }

    fun setShowType(index: Int) {
        if (!mHadPlay || index !in mScaleStrings.indices) {
            return
        }
        mScaleIndex = index
        resolveTypeUI()
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

    /**
     * 需要在尺寸发生变化的时候重新处理
     */
    override fun onSurfaceSizeChanged(
        surface: Surface?,
        width: Int,
        height: Int
    ) {
        super.onSurfaceSizeChanged(surface, width, height)
        resolveTransform(mTextureViewTransform)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
//        resolveRotateUI()
        resolveTransform(mTextureViewTransform)
    }

    /**
     * 处理镜像旋转
     */
    private fun resolveTransform(transformSize: Int) {
        if (mTextureView == null) return
        val transform = Matrix()
        when (transformSize) {
            NO_REVERSE -> {  // 正常
                transform.setScale(1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            HORIZONTAL_REVERSE -> {  // 左右镜像
                transform.setScale(-1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            VERTICAL_REVERSE -> {  // 上下镜像
                transform.setScale(1f, -1f, 0f, mTextureView.height / 2.toFloat())
            }
            else -> return
        }
        mTextureViewTransform = transformSize
        mTextureView.setTransform(transform)
        mTextureView.invalidate()
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
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageResource(R.drawable.ic_refresh_white_24)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24)
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

    override fun changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear()
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

    override fun changeUiToPlayingClear() {
        super.changeUiToPlayingClear()
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
    }

    override fun changeUiToPauseClear() {
        super.changeUiToPauseClear()
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
    }

    override fun changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear()
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        mBottomContainer.visibility = View.GONE
    }

    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
    }

    override fun changeUiToClear() {
        super.changeUiToClear()
    }

    override fun changeUiToError() {
        super.changeUiToError()
    }

    fun setEpisodeButtonOnClickListener(listener: OnClickListener) {
        mEpisodeButtonOnClickListener = listener
    }

    fun setEpisodeAdapter(adapter: EpisodeRecyclerViewAdapter) {
        mEpisodeAdapter = adapter
    }

    fun getShareButton() = mShareImageView

    fun getEpisodeButton() = mEpisodeTextView

    fun getDownloadButton() = mDownloadButton

    fun getBottomContainer() = mBottomContainer

    fun getClingButton() = mClingImageView

    fun getNextButton() = mNextImageView

    fun getRightContainer() = mRightContainer

    fun setEpisodeButtonVisibility(visibility: Int) {
        mEpisodeTextView?.visibility = visibility
        mEpisodeTextViewVisibility = visibility
    }

    fun enableDismissControlViewTimer(start: Boolean) {
        if (start) super.startDismissControlViewTimer()
        else super.cancelDismissControlViewTimer()
    }

    class SpeedBean(
        override var type: String,
        override var actionUrl: String,
        var title: String
    ) : BaseBean, Serializable

    inner class SpeedAdapter(val list: List<SpeedBean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return RightRecyclerViewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_list_item_1, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = list[position]

            when (holder) {
                is RightRecyclerViewViewHolder -> {
                    if (item.type == "speed") {
                        if (item.title.toFloat() == speed) {
                            holder.tvTitle.setTextColor(context.getResColor(R.color.unchanged_main_color_2))
                        }
                        holder.tvTitle.text = item.title
                        holder.itemView.setOnClickListener {
                            if (item.title == "1") {
                                mSpeedTextView?.text = App.context.getString(R.string.play_speed)
                            } else {
                                mSpeedTextView?.text = item.title + "X"
                            }
                            setSpeed(item.title.toFloat(), true)
                            mRightContainer?.gone()
                            //因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                            startDismissControlViewTimer()
                        }
                    }
                }
            }
        }

        override fun getItemCount(): Int = list.size
    }

    abstract class EpisodeRecyclerViewAdapter(
        private val activity: Activity,
        private val dataList: List<AnimeEpisodeDataBean>,
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        abstract val currentIndex: Int

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return RightRecyclerViewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player_list_item_1, parent, false)
            )
        }

        override fun getItemCount(): Int = dataList.size
    }

    class RightRecyclerViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view as TextView
    }
}