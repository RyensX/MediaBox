package com.su.mediabox.view.component.player

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.util.AttributeSet
import com.su.mediabox.util.logD
import android.view.*
import android.view.View.OnClickListener
import android.widget.*
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.su.mediabox.App
import com.su.mediabox.Pref
import com.su.mediabox.R
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ItemPlayEpisodeBinding
import com.su.mediabox.databinding.ItemPlayerSpeedBinding
import com.su.mediabox.pluginapi.data.EpisodeData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.saveData
import com.su.mediabox.util.*
import com.su.mediabox.util.Util.getResDrawable
import com.su.mediabox.util.Util.getScreenBrightness
import com.su.mediabox.util.Util.openVideoByExternalPlayer
import com.su.mediabox.view.activity.VideoMediaPlayActivity
import com.su.mediabox.viewmodel.VideoMediaPlayViewModel
import com.su.mediabox.view.activity.DlnaActivity
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.component.ZoomView
import com.su.mediabox.view.component.textview.TypefaceTextView
import com.su.mediabox.view.listener.dsl.setOnSeekBarChangeListener
import kotlinx.coroutines.*
import java.io.File
import kotlin.math.abs
import kotlin.properties.Delegates

//TODO ????????????????????????????????????
open class VideoMediaPlayer : StandardGSYVideoPlayer {
    companion object {
        val mScaleStrings = listOf(
            "????????????" to GSYVideoType.SCREEN_TYPE_DEFAULT,
            "16:9" to GSYVideoType.SCREEN_TYPE_16_9,
            "4:3" to GSYVideoType.SCREEN_TYPE_4_3,
            "??????" to GSYVideoType.SCREEN_TYPE_FULL,
            "????????????" to GSYVideoType.SCREEN_MATCH_FULL
        )

        const val NO_REVERSE = 0
        const val HORIZONTAL_REVERSE = 1
        const val VERTICAL_REVERSE = 2

        // ??????????????????Alpha
        const val NIGHT_SCREEN_MAX_ALPHA: Int = 0xAA

        val playPositionMemoryStoreCoroutineScope by lazy(LazyThreadSafetyMode.NONE) {
            CoroutineScope(Dispatchers.Default)
        }

    }

    /**
     * ?????????????????????????????????5????????????????????????
     */
    var playPositionMemoryTimeLimit = 5000L

    var playPositionMemoryStore: PlayPositionMemoryDataStore? = null
    private var playPositionViewJob: Job? = null

    var playOperatingProxy: PlayOperatingProxy? = null

    // ???????????????
    private var preSeekPlayPosition: Long? = null

    // ????????????????????????
    private var doublePointerZoomingMoving = false

    var ivDownloadButton: ImageView? = null
        private set

    private var initFirstLoad = true

    //???????????????????????????
    private var mScaleIndex = 0

    //4:3  16:9???
    private var tvMoreScale: TextView? = null

    //????????????
    private var tvSpeed: TextView? = null
    private var rvSpeed: RecyclerView? = null

    //??????
    private var mPlaySpeed = 1f

    //??????
    private var tvEpisode: TextView? = null
    private var rvEpisode: RecyclerView? = null
    private var videoName = ""

    //????????????
    var ivCling: ImageView? = null
        private set

    //????????????
    var ivShare: ImageView? = null
        private set

    //????????????
    var ivMore: ImageView? = null
        private set

    val mBottomContainer: ViewGroup? = mBottomContainer

    //???????????????
    var ivNextEpisode: ImageView? = null
        private set

    // ???????????????
    private var vgPlayPosition: ViewGroup? = null

    // ????????????
    private var tvPlayPosition: TextView? = null

    // ??????????????????ImageView
    private var ivClosePlayPositionTip: ImageView? = null

    // ??????
    protected var vgSettingContainer: ViewGroup? = null
    private var ivSetting: ImageView? = null

    // ??????RadioGroup
    private var rgReverse: RadioGroup? = null
    private var mReverseValue: Int? = null
    private var mTextureViewTransform: Int =
        NO_REVERSE

    // ???????????????CheckBox
    private var cbBottomProgress: SwitchCompat? = null
    private var playBottomProgress: ProgressBar? = null

    //???????????????
    private var pbBottomProgress: ProgressBar? = null

    // ?????????????????????
    private var tvOpenByExternalPlayer: TextView? = null

    // ?????????CheckBox
    private var cbMediaCodec: CheckBox? = null

    // ???????????????
    var vgRightContainer: ViewGroup? = null
        private set

    // ?????????????????????tv
    private var tvTouchDownHighSpeed: TextView? = null
    private var mLongPressing: Boolean = false

    //??????????????????
    private var playErrorRetry: Button? = null

    //????????????
    private var loadingHint: TextView? = null

    // ????????????
    private var tvRestoreScreen: TextView? = null

    // ??????
    private var tvDlna: TextView? = null

    // ?????????????????????????????????
    private var mDoublePointerZoomMoved: Boolean = false

    // ?????????????????????????????????
    private var vgBiggerSurface: ViewGroup? = null

    // ??????????????????
    private var mUiCleared: Boolean = true

    // ??????????????????
    private var tcSystemTime: TextClock? = null

    // top??????
    private var viewTopContainerShadow: View? = null

    // ????????????View
    private var viewNightScreen: View? = null

    // ????????????seekbar
    private var sbNightScreen: SeekBar? = null

    // ????????????SeekBar???
    private var mNightScreenSeekBarProgress: Int = 0

    // ??????????????????????????????????????????????????????
    protected open var mStatusBarOffset: Int = 50.dp

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = R.layout.layout_video_media_play

    @SuppressLint("ClickableViewAccessibility")
    override fun init(context: Context?) {
        super.init(context)

        tvMoreScale = findViewById(R.id.tv_more_scale)
        tvSpeed = findViewById(R.id.tv_speed)
        vgRightContainer = findViewById(R.id.layout_right)
        rvSpeed = findViewById(R.id.rv_speed)
        rvEpisode = findViewById(R.id.rv_episode)
        ivNextEpisode = findViewById(R.id.iv_next)
        ivSetting = findViewById(R.id.iv_setting)
        vgSettingContainer = findViewById(R.id.layout_setting)
        rgReverse = findViewById(R.id.rg_reverse)
        cbBottomProgress = findViewById(R.id.cb_bottom_progress)
        pbBottomProgress = findViewById(R.id.progress)
        playBottomProgress = findViewById(R.id.play_bottom_progressbar)
        tvOpenByExternalPlayer = findViewById(R.id.tv_open_by_external_player)
        tvRestoreScreen = findViewById(R.id.tv_restore_screen)
        tvTouchDownHighSpeed = findViewById(R.id.tv_touch_down_high_speed)
        vgBiggerSurface = findViewById(R.id.bigger_surface)
        tcSystemTime = findViewById(R.id.tc_system_time)
        viewTopContainerShadow = findViewById(R.id.view_top_container_shadow)
        viewNightScreen = findViewById(R.id.view_player_night_screen)
        sbNightScreen = findViewById(R.id.sb_player_night_screen)
        tvDlna = findViewById(R.id.tv_dlna)
        vgPlayPosition = findViewById(R.id.ll_play_position_view)
        tvPlayPosition = findViewById(R.id.tv_play_position_time)
        ivClosePlayPositionTip = findViewById(R.id.iv_close_play_position_tip)
        playErrorRetry = findViewById(R.id.play_error_retry)
        loadingHint = findViewById(R.id.loading_hint)

        vgRightContainer?.gone()
        vgSettingContainer?.gone()
        tvTouchDownHighSpeed?.gone()
        vgPlayPosition?.gone()

        vgBiggerSurface?.setOnClickListener(this)
        vgBiggerSurface?.setOnTouchListener(this)

        ivClosePlayPositionTip?.setOnClickListener {
            playPositionViewJob?.cancel()
            vgPlayPosition?.gone(true, 200L)
        }
        vgPlayPosition?.setOnClickListener {
            preSeekPlayPosition?.also { if (it > 0L) seekTo(it) }
            vgPlayPosition?.gone(true, 200L)
        }

        tvRestoreScreen?.setOnClickListener {
            mTextureViewContainer?.run {
                if (this is ZoomView) restore()
                else {
                    translationX = 0f
                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                    rotation = 0f
                }
                mDoublePointerZoomMoved = false
                it.gone()
            }
        }

        ivSetting?.setOnClickListener { showSettingContainer() }
        mReverseValue = rgReverse?.getChildAt(0)?.id
        rgReverse?.children?.forEach {
            (it as RadioButton).apply {
                setOnCheckedChangeListener { _, isChecked ->
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

        //???????????????????????????
        Pref.isShowPlayerBottomProgressBar.value.also {
            cbBottomProgress?.isChecked = it
            updateBottomProgressBar(it)
        }
        cbBottomProgress?.setOnCheckedChangeListener { _, isChecked ->
            updateBottomProgressBar(isChecked)
        }

        //??????????????????
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()

        tvMoreScale?.text = mScaleStrings[mScaleIndex].first

        //??????????????????
        tvMoreScale?.setOnClickListener(OnClickListener {
            startDismissControlViewTimer()      //????????????ui??????????????????
            if (!mHadPlay) {
                return@OnClickListener
            }
            mScaleIndex = (mScaleIndex + 1) % mScaleStrings.size
            resolveTypeUI()
        })

        ivCling?.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, DlnaActivity::class.java)
                    .putExtra("url", mUrl)
                    .putExtra("title", mTitle)
            )
            mOriginUrl
        }

        tvOpenByExternalPlayer?.setOnClickListener {
            if (!openVideoByExternalPlayer(mContext, mUrl))
                mContext.getString(R.string.matched_app_not_found).showToast()
        }

        sbNightScreen?.setOnSeekBarChangeListener {
            onProgressChanged { seekBar, progress, _ ->
                seekBar ?: return@onProgressChanged
                mNightScreenSeekBarProgress = progress
                viewNightScreen?.setBackgroundColor((NIGHT_SCREEN_MAX_ALPHA * progress / seekBar.max) shl 24)
            }
        }

        //??????
        tvDlna?.setOnClickListener {
            val url = getUrl()
            if (url == null) {
                mContext.getString(R.string.please_wait_video_loaded).showToast()
                return@setOnClickListener
            }
            startActivity(
                mContext, Intent(mContext, DlnaActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("title", getTitle()), null
            )
        }

        //????????????
        tvSpeed?.apply {
            rvSpeed
                ?.linear()
                ?.initTypeList(DataViewMapList().registerDataViewMap<Float, PlaySpeedViewHolder>()) {
                    vHCreateDSL<PlaySpeedViewHolder> {
                        //????????????
                        setOnClickListener(itemView) { pos ->
                            val adapter = bindingTypeAdapter
                            adapter.getData<Float>(pos)?.also { speed ->
                                setSpeed(speed, true)
                                text = if (speed != 1F) "${speed}X"
                                else App.context.getString(R.string.play_speed)

                                vgRightContainer?.gone()
                                tvTouchDownHighSpeed?.gone()
                                startDismissControlViewTimer()

                                //??????????????????
                                adapter.notifyItemChanged(pos)
                                //??????????????????
                                adapter.getTag<Int>()?.also {
                                    adapter.notifyItemChanged(it)
                                }

                                //tag?????????????????????pos
                                adapter.setTag(pos)
                            }
                        }
                        //????????????
                        setOnLongClickListener(itemView) { pos ->
                            getData<Float>(pos)?.also {
                                if (speed != it) {
                                    //??????????????????
                                    bindingTypeAdapter.setTag(
                                        speed,
                                        Const.ViewComponent.PLAY_SPEED_TAG
                                    )
                                    setSpeed(it, true)
                                    showSpeed(it)
                                    "????????????${it}X??????".showToast()
                                }
                            }
                            true
                        }
                        setOnTouchListener(itemView) { event, _ ->
                            if (event.actionMasked == MotionEvent.ACTION_CANCEL) {
                                //???????????????????????????
                                bindingTypeAdapter.getTag<Float>(Const.ViewComponent.PLAY_SPEED_TAG)
                                    ?.also {
                                        if (speed != it) {
                                            setSpeed(it, true)
                                            tvTouchDownHighSpeed?.gone()
                                            "??????${it}X??????".showToast()
                                        }
                                    }
                            }
                            false
                        }
                    }
                }
            setOnClickListener(this@VideoMediaPlayer)
        }

        //??????
        playErrorRetry?.setOnClickListener(this)
    }

    /**
     * ?????????????????????????????????
     */
    private fun initEpisodeList() {
        if (tvEpisode != null)
            return
        tvEpisode = findViewById(R.id.tv_episode)
        //????????????
        tvEpisode?.apply {
            //???????????????????????????VM???????????????
            if (VideoMediaPlayActivity.playList == null || playOperatingProxy == null) {
                gone()
                return@apply
            }
            visible()
            rvEpisode
                ?.grid(if (VideoMediaPlayActivity.playList!!.size > 8) 4 else 1)
                ?.initTypeList(DataViewMapList().registerDataViewMap<EpisodeData, PlayerEpisodeViewHolder>()) {
                    vHCreateDSL<PlayerEpisodeViewHolder> {
                        setOnClickListener(itemView) { pos ->
                            val adapter = bindingTypeAdapter
                            adapter.getData<EpisodeData>(pos)?.also { episodeData ->
                                //??????????????????
                                adapter.getTag<Int>()?.also {
                                    adapter.notifyItemChanged(it)
                                }
                                //??????????????????
                                adapter.notifyItemChanged(pos)
                                //??????????????????pos
                                adapter.setTag(pos)
                                //????????????
                                gsyVideoManager.pause()
                                //????????????
                                playOperatingProxy?.playVideoMedia(episodeData.url)
                                //TODO ??????????????????????????????Tag???????????????
                            }
                        }
                    }
                }
            setOnClickListener(this@VideoMediaPlayer)
            //?????????
            ivNextEpisode?.apply {
                setOnClickListener {
                    playNextEpisode()
                }
            }
        }
    }

    fun playVideo(
        playUrl: String,
        episodeName: String,
        videoName: String = this.videoName,
        cacheWithPlay: Boolean = false
    ) {
        initEpisodeList()
        logD("????????????", "videName=$videoName episodeName=$episodeName \nurl=$playUrl")
        if (this.videoName.isBlank())
        //????????????????????????????????????????????????????????????????????????????????????????????????
            playPositionMemoryStoreCoroutineScope.launch {
                //??????????????????
                VideoMediaPlayActivity.playList?.forEachIndexed { index, episodeData ->
                    if (episodeData.url.isNotBlank() && episodeData.url == playOperatingProxy?.currentPlayEpisodeUrl) {
                        logD("??????????????????", index.toString())
                        rvEpisode?.typeAdapter()?.setTag(index)
                        return@forEachIndexed
                    }
                }
            }
        this.videoName = videoName

        setUp(playUrl, cacheWithPlay, String.format("%s %s", videoName, episodeName))
        startPlayLogic()
    }

    /**
     * ???????????????
     * @return ???????????????????????????
     */
    fun playNextEpisode(): Boolean {
        val episodeAdapter = rvEpisode?.typeAdapter()
        episodeAdapter?.getTag<Int>()?.also { pos ->
            if (pos < (VideoMediaPlayActivity.playList?.size ?: pos) - 1)
                VideoMediaPlayActivity.playList?.getOrNull(pos + 1)?.url?.also {
                    episodeAdapter.apply {
                        //??????????????????
                        notifyItemChanged(pos)
                        //??????????????????
                        notifyItemChanged(pos + 1)
                        //????????????
                        episodeAdapter.setTag(pos + 1)
                    }
                    playOperatingProxy?.playVideoMedia(it)
                    return true
                }
            else {
                "??????????????????".showToast()
                return false
            }
        }
        return false
    }

    override fun onError(what: Int, extra: Int) {
        super.onError(what, extra)
        logD("????????????", "url=$mOriginUrl")
        context.getString(R.string.play_error).showToast(Toast.LENGTH_LONG)
    }

    class PlayerEpisodeViewHolder private constructor(private val binding: ItemPlayEpisodeBinding) :
        TypeViewHolder<EpisodeData>(binding.root) {

        constructor(parent: ViewGroup) : this(
            ItemPlayEpisodeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBind(data: EpisodeData) {
            binding.vcPlayEpisodeName.apply {
                setTextColor(if (bindingAdapterPosition == bindingTypeAdapter.getTag<Int>()) Const.Player.SELECT_ITEM_COLOR else Const.Player.UNSELECT_ITEM_COLOR)
            }.text = data.name
        }
    }

    class PlaySpeedViewHolder private constructor(private val binding: ItemPlayerSpeedBinding) :
        TypeViewHolder<Float>(binding.root) {

        constructor(parent: ViewGroup) : this(
            ItemPlayerSpeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        override fun onBind(data: Float) {
            binding.itemPlayerSpeedName.apply {
                setTextColor(if (bindingAdapterPosition == bindingTypeAdapter.getTag<Int>()) Const.Player.SELECT_ITEM_COLOR else Const.Player.UNSELECT_ITEM_COLOR)
            }.text = "${data}X"
        }
    }

    fun getUrl(): String? = mUrl

    fun getTitle(): String = mTitle

    private fun showSettingContainer() {
        mLockScreen?.gone()
        vgSettingContainer?.let {
            hideAllWidget()
            it.translationX = 150f.dp
            it.visible()
            val animator = ObjectAnimator.ofFloat(
                it, "translationX", 170f.dp, 0f
            )
            animator.duration = 300
            animator.start()
            //??????xx????????????????????????
            cancelDismissControlViewTimer()
            if (mReverseValue == null) mReverseValue = rgReverse?.getChildAt(0)?.id
            mReverseValue?.let { id -> findViewById<RadioButton>(id).isChecked = true }

//            mMediaCodecCheckBox?.isChecked = GSYVideoType.isMediaCodec()
//            mMediaCodecCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
//                if (isChecked) GSYVideoType.enableMediaCodec()
//                else GSYVideoType.disableMediaCodec()
//                startPlayLogic()
//            }
        }
    }

    private fun showSpeed(speed: Float) {
        tvTouchDownHighSpeed?.apply {
            text =
                mContext.getString(R.string.touch_down_high_speed, speed.toString())
            visible()
        }
    }

    fun setTopContainer(top: ViewGroup?) {
        mTopContainer = top
        viewTopContainerShadow = if (top == null) {
            viewTopContainerShadow?.visible()
            null
        } else {
            findViewById(R.id.view_top_container_shadow)
        }
        restartTimerTask()
    }

    private fun showRightContainer() {
        mLockScreen?.gone()
        vgRightContainer?.let {
            hideAllWidget()
            it.translationX = 150f.dp
            it.visible()
            val animator = ObjectAnimator.ofFloat(it, "translationX", 170f.dp, 0f)
            animator.duration = 300
            animator.start()
            //??????xx????????????????????????
            cancelDismissControlViewTimer()
        }
    }

    override fun hideAllWidget() {
        super.hideAllWidget()
//        setViewShowState(vgRightContainer, INVISIBLE)
//        setViewShowState(vgSettingContainer, INVISIBLE)
        setViewShowState(tvRestoreScreen, View.GONE)
        setViewShowState(viewTopContainerShadow, View.INVISIBLE)
    }

    override fun onClickUiToggle(e: MotionEvent?) {
        vgRightContainer?.let {
            //?????????????????????????????????
            if (it.visibility == View.VISIBLE) {
                it.gone()
                //????????????????????????????????????xx????????????????????????????????????xx????????????????????????
                startDismissControlViewTimer()
                return
            }
        }
        vgSettingContainer?.let {
            // ????????????????????????
            if (it.visibility == View.VISIBLE) {
                it.gone()
                // ????????????????????????????????????xx????????????????????????????????????xx????????????????????????
                startDismissControlViewTimer()
                return
            }
        }
        super.onClickUiToggle(e)
        setRestoreScreenTextViewVisibility()
    }

    /**
     * ?????????????????????????????????????????????????????????
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
        ) as VideoMediaPlayer
        player.mScaleIndex = mScaleIndex
        player.tvSpeed?.text = tvSpeed?.text
        player.mFullscreenButton.visibility = mFullscreenButton.visibility
        player.mTextureViewTransform = mTextureViewTransform
        player.mReverseValue = mReverseValue
        player.mPlaySpeed = mPlaySpeed
        player.sbNightScreen?.progress = mNightScreenSeekBarProgress
        touchSurfaceUp()
        player.setRestoreScreenTextViewVisibility()
        player.resolveTypeUI()
        return player
    }

    private fun setRestoreScreenTextViewVisibility() {
        if (mUiCleared) {
            tvRestoreScreen?.gone()
        } else {
            if (mDoublePointerZoomMoved) tvRestoreScreen?.visible()
            else tvRestoreScreen?.gone()
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????
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
            val player = gsyVideoPlayer as VideoMediaPlayer
            mScaleIndex = player.mScaleIndex
            mFullscreenButton.visibility = player.mFullscreenButton.visibility
            tvSpeed?.text = player.tvSpeed?.text
            mTextureViewTransform = player.mTextureViewTransform
            mReverseValue = player.mReverseValue
            mPlaySpeed = player.mPlaySpeed
            mNightScreenSeekBarProgress = player.sbNightScreen?.progress ?: 0
            player.touchSurfaceUp()
            setRestoreScreenTextViewVisibility()
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
     * ??????/???????????????????????????
     * ?????????GSYVideoType.setShowType????????????????????????????????????APP???
     */
    @SuppressLint("SetTextI18n")
    private fun resolveTypeUI() {
        if (!mHadPlay) {
            return
        }
        tvMoreScale?.text = mScaleStrings[mScaleIndex].first
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()
        setSpeed(mPlaySpeed, true)
        tvTouchDownHighSpeed?.gone()
        mLongPressing = false
    }

    override fun setSpeed(speed: Float, soundTouch: Boolean) {
        super.setSpeed(speed, soundTouch)
        onSpeedChanged(speed)
    }

    /**
     * ?????????????????????????????????
     */
    protected open fun onSpeedChanged(speed: Float) {

    }

    /**
     * ????????????????????????????????????????????????
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
     * ??????????????????
     */
    private fun resolveTransform(transformSize: Int) {
        if (mTextureView == null) return
        val transform = Matrix()
        when (transformSize) {
            NO_REVERSE -> {  // ??????
                transform.setScale(1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            HORIZONTAL_REVERSE -> {  // ????????????
                transform.setScale(-1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            VERTICAL_REVERSE -> {  // ????????????
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
                it.isFocused = true
            }
        }
        return result
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_pause_white_24))
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_play_white_24))
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_refresh_white_24))
                }
                else -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_play_white_24))
                }
            }
        } else {
            super.updateStartImage()
        }
    }

    private fun updateBottomProgressBar(isChecked: Boolean) {
        playBottomProgress?.isVisible = isChecked
        Pref.isShowPlayerBottomProgressBar.saveData(isChecked)
        mBottomProgressBar = if (isChecked) playBottomProgress else null
    }

    override fun onBrightnessSlide(percent: Float) {
        val activity = mContext as Activity
        val lpa = activity.window.attributes
        val mBrightnessData = lpa.screenBrightness
        if (mBrightnessData <= 0.00f) {
            getScreenBrightness(activity)?.div(255.0f)?.let {
                lpa.screenBrightness = it
                activity.window.attributes = lpa
            }
        }
        super.onBrightnessSlide(percent)
    }

    override fun onVideoSizeChanged() {
        super.onVideoSizeChanged()
        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onVideoSizeChanged()
        }
    }

    //??????
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        viewTopContainerShadow?.visible()
        initFirstLoad = true
        mUiCleared = false
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        viewTopContainerShadow?.visible()
        mUiCleared = false
    }

    override fun changeUiToClear() {
        super.changeUiToClear()
        viewTopContainerShadow?.invisible()
        mUiCleared = true

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()
    }

    //?????????
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        viewTopContainerShadow?.visible()
        mUiCleared = false

        if (VideoMediaPlayActivity.playList != null)
            ivNextEpisode?.gone()

        playErrorRetry?.gone()
        loadingHint?.visible()
    }

    //?????????
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        viewTopContainerShadow?.visible()
//        if (initFirstLoad) {
//            mBottomContainer.gone()
//            mStartButton.gone()
//        }
        initFirstLoad = false
        mUiCleared = false

        if (VideoMediaPlayActivity.playList != null)
            ivNextEpisode?.visible()

        ivSetting?.visible()
        loadingHint?.gone()
    }

    //??????????????????
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        viewTopContainerShadow?.visible()
        mBottomContainer?.gone()
        tvTouchDownHighSpeed?.gone()
        mUiCleared = false

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()
    }

    override fun changeUiToError() {
        super.changeUiToError()
        viewTopContainerShadow?.invisible()

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()

        mLockScreen?.gone()
        //???????????????????????????????????????
        mTopContainer?.visible()
        playErrorRetry?.visible()
        loadingHint?.gone()
        ivSetting?.gone()
    }

    override fun changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        viewTopContainerShadow?.visible()
        loadingHint?.visible()
    }

    override fun onVideoPause() {
        super.onVideoPause()
        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onVideoPause()
        }
    }

    override fun onVideoResume(seek: Boolean) {
//        super.onVideoResume(seek)
        mPauseBeforePrepared = false
        if (mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE) {
            try {
                clickStartIcon()
                mVideoAllCallBack.let {
                    if (it is MyVideoAllCallBack) it.onVideoResume()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    public override fun clickStartIcon() {
        super.clickStartIcon()

        // ????????????????????????????????????
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            onVideoResume()
        }
    }

    override fun onClick(v: View) {
        if (currentState != GSYVideoView.CURRENT_STATE_ERROR)
            super.onClick(v)

        when (v.id) {
            // bigger_surface???????????????surface_container??????????????????
            R.id.bigger_surface -> {
                vgSettingContainer?.gone()
                vgRightContainer?.gone()
                rvEpisode?.gone()
                if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
                    if (mVideoAllCallBack != null) {
                        Debuger.printfLog("onClickStartError")
                        mVideoAllCallBack.onClickStartError(mOriginUrl, mTitle, this)
                    }
                    prepareVideo()
                } else {
                    if (mVideoAllCallBack != null && isCurrentMediaListener) {
                        if (mIfCurrentIsFullscreen) {
                            Debuger.printfLog("onClickBlankFullscreen")
                            mVideoAllCallBack.onClickBlankFullscreen(mOriginUrl, mTitle, this)
                        } else {
                            Debuger.printfLog("onClickBlank")
                            mVideoAllCallBack.onClickBlank(mOriginUrl, mTitle, this)
                        }
                    }
                    startDismissControlViewTimer()
                }
            }
            R.id.thumb -> {
                vgSettingContainer?.gone()
                vgRightContainer?.gone()
                rvEpisode?.gone()
            }
            R.id.back -> (context as Activity).finish()
            //??????
            R.id.play_error_retry -> prepareVideo()
            //??????????????????
            R.id.tv_speed -> {
                rvEpisode?.gone()
                rvSpeed?.visible()
                showRightContainer()
                val adapter = rvSpeed?.typeAdapter()
                if (adapter?.currentList.isNullOrEmpty()) {
                    adapter?.setTag(2)
                    adapter?.submitList(listOf(0.5F, 0.75F, 1F, 1.25F, 1.5F, 2F, 4F, 8F))
                }
                adapter?.getTag<Int>()?.also {
                    rvSpeed?.smartScrollToPosition(it)
                }
            }
            //??????????????????
            R.id.tv_episode -> {
                rvSpeed?.gone()
                rvEpisode?.apply {
                    val adapter = typeAdapter()
                    isVisible = true
                    showRightContainer()
                    adapter.submitList(VideoMediaPlayActivity.playList) {
                        //??????
                        adapter.getTag<Int>()?.also {
                            smartScrollToPosition(it)
                        }
                    }
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    //TODO ??????????????????????????????
    override fun touchDoubleUp(e: MotionEvent?) {
        // ????????????????????????
        val oldUiVisibilityState = mBottomContainer?.visibility ?: VISIBLE

        // ????????????
        super.touchDoubleUp(e)

        // ????????????????????????????????????
        if (mCurrentState == CURRENT_STATE_PLAYING) {       // ???????????????????????????
            //?????????Ui???????????????????????????????????????Ui?????????????????????????????????????????????Ui??????????????????
            if (oldUiVisibilityState == VISIBLE) changeUiToPlayingShow()
            else changeUiToPlayingClear()
//            cancelDismissControlViewTimer()
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {  // ???????????????????????????
            //?????????Ui???????????????????????????????????????Ui?????????????????????????????????????????????Ui??????????????????
            if (oldUiVisibilityState == VISIBLE) changeUiToPauseShow()
            else changeUiToPauseClear()
//            cancelDismissControlViewTimer()
        }
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        // ?????????????????????
        if (absDeltaY > mThreshold && absDeltaY > absDeltaX && mDownY <= mStatusBarOffset) {
            cancelProgressTimer()
            return
        }
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        // ---??????????????????
        if (event.pointerCount == 1) {
            if (event.action == MotionEvent.ACTION_UP) {
                // ???????????????????????????????????????
                if (mLongPressing) {
                    mLongPressing = false
                    setSpeed(mPlaySpeed, true)
                    tvTouchDownHighSpeed?.gone()
                    return false
                }
            }
        }
        // ---??????????????????
        // ???????????????????????????????????????
        if (!mIfCurrentIsFullscreen) return super.onTouch(v, event)
        if (v?.id == R.id.surface_container) {
            if (event.pointerCount > 1 && event.actionMasked == MotionEvent.ACTION_MOVE) {
                // ?????????surface_container???????????????????????????1??????return false??????
                // ??????super??????????????????????????????????????????????????????
                doublePointerZoomingMoving = true
                mDoublePointerZoomMoved = true
                if (!mUiCleared) tvRestoreScreen?.visible()
                // ?????????bigger_surface???????????????surface_container??????????????????
                return false
            }
        }
        // ???????????????????????????????????????super?????????
        if (doublePointerZoomingMoving) {
            tvRestoreScreen?.visible()
            // ?????????????????????????????????????????????
            if (event.action == MotionEvent.ACTION_UP) {
                doublePointerZoomingMoving = false
            }
            return false
        }
        return if (v?.id == R.id.bigger_surface || v?.id == R.id.surface_container) {
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchSurfaceDown(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = x - mDownX
                    val deltaY = y - mDownY
                    val absDeltaX = abs(deltaX)
                    val absDeltaY = abs(deltaY)
                    if (mIfCurrentIsFullscreen && mIsTouchWigetFull
                        || mIsTouchWiget && !mIfCurrentIsFullscreen
                    ) {
                        if (!mChangePosition && !mChangeVolume && !mBrightness) {
                            touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
                        }
                    }
                    touchSurfaceMove(deltaX, deltaY, y)
                }
                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    touchSurfaceUp()
                    Debuger.printfLog(
                        this.hashCode()
                            .toString() + "------------------------------ surface_container ACTION_UP"
                    )
                    startProgressTimer()
                    //?????????????????????????????????????????????????????????
                    if (mHideKey && mShowVKey) return true
                }
            }
            gestureDetector.onTouchEvent(event)
            return false
        } else {
            super.onTouch(v, event)
        }
    }

    override fun onBackFullscreen() {
        if (!mFullAnimEnd) {
            return
        }
        mIfCurrentIsFullscreen = false
        var delay = 0
        if (mOrientationUtils != null) {
            val orientationUtils = mOrientationUtils
            delay = if (orientationUtils is AnimeOrientationUtils)
                orientationUtils.backToProtVideo2()
            else
                orientationUtils.backToProtVideo()
            mOrientationUtils.isEnable = false
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener()
                mOrientationUtils = null
            }
        }

        if (!mShowFullAnimation) {
            delay = 0
        }

        val vp = CommonUtil.scanForActivity(context)
            .findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val oldF = vp.findViewById<View>(fullId)
        if (oldF != null) {
            //??????fix bug#265?????????????????????????????????????????????
            val gsyVideoPlayer = oldF as GSYVideoPlayer
            gsyVideoPlayer.isIfCurrentIsFullscreen = false
        }

        mInnerHandler.postDelayed({ backToNormal() }, delay.toLong())
    }

    private val PLAY_POS_TAG = "????????????"

    /**
     * ????????????????????????????????????
     */
    override fun onPrepared() {
        playPositionViewJob?.cancel()
        playPositionMemoryStore?.apply {
            playPositionMemoryStoreCoroutineScope.launch {
                logD(PLAY_POS_TAG, "?????????????????????$mOriginUrl")
                getPlayPosition(mOriginUrl)?.also {
                    logD(PLAY_POS_TAG, "??????????????????$it")
                    preSeekPlayPosition = it
                    if (it > 0L) {
                        //TODO ??????????????????
                        val isAutoSeek = true
                        if (isAutoSeek) {
                            logD(PLAY_POS_TAG, "???????????????$it")
                            seekOnStart = it
                            context.getString(R.string.play_auto_seek).showToast(Toast.LENGTH_LONG)
                        } else
                            playPositionViewJob = launch(Dispatchers.Main) {
                                tvPlayPosition?.text = positionFormat(it)
                                vgPlayPosition?.visible()
                                //??????5???
                                delay(5000)
                                vgPlayPosition?.gone(true, 200L)
                            }
                    }
                }
            }
        }
        super.onPrepared()
    }

    override fun startAfterPrepared() {
        super.startAfterPrepared()
        logD(PLAY_POS_TAG, "??????????????????????????????pos=${gsyVideoManager.currentPosition} url=$mOriginUrl")
        //????????????????????????
        preSeekPlayPosition?.also {
            if (gsyVideoManager.currentPosition < it) {
                logD(PLAY_POS_TAG, "??????????????????????????????????????????pos=$it url=$mOriginUrl")
                gsyVideoManager.seekTo(it)
            }
        }
    }

    /**
     * 1.???????????????????????????
     */
    override fun onDetachedFromWindow() {
        storePlayPosition()
        playOperatingProxy = null
        super.onDetachedFromWindow()
    }

    /**
     * 2.???????????????????????????
     */
    override fun setUp(
        url: String?,
        cacheWithPlay: Boolean,
        cachePath: File?,
        title: String?,
        changeState: Boolean
    ): Boolean {
        if (url != mOriginUrl) {
            vgPlayPosition?.gone()
            storePlayPosition()
        }

        return super.setUp(url, cacheWithPlay, cachePath, title, changeState)
    }

    /**
     * 1.???????????????????????????
     * 2.???????????????????????????
     *
     * @param position ??????0????????????????????????????????????????????????0??????????????????
     *
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private fun storePlayPosition(position: Long = gsyVideoManager.currentPosition) {
        when (currentState) {
            CURRENT_STATE_PREPAREING, CURRENT_STATE_PLAYING_BUFFERING_START, CURRENT_STATE_ERROR -> return
        }
        val url = mOriginUrl
        val duration = gsyVideoManager.duration
        // ????????????????????????????????? ??? ??????????????????????????????????????????????????????????????????????????????????????????
        if (position < 0 ||
            (position > playPositionMemoryTimeLimit && duration > 0 && abs(position - duration) > 2000L)
        ) {
            logD(PLAY_POS_TAG, "???????????????pos=$position target=$url")
            playPositionMemoryStore?.apply {
                playPositionMemoryStoreCoroutineScope.launch {
                    putPlayPosition(url, position)
                }
            }
        } else
            logD(PLAY_POS_TAG, "?????????????????????pos=$position target=$url")
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        // ????????????
        storePlayPosition(-1L)
    }

    fun enableDismissControlViewTimer(start: Boolean) {
        if (start) super.startDismissControlViewTimer()
        else super.cancelDismissControlViewTimer()
    }

    override fun lockTouchLogic() {
        super.lockTouchLogic()
        mLockScreen.setImageResource(if (mLockCurScreen) R.drawable.ic_outline_lock_24 else R.drawable.ic_outline_lock_open_24)
    }

    interface PlayPositionMemoryDataStore {

        suspend fun getPlayPosition(url: String): Long?

        /**
         * @param position ???????????????????????????GSYVideoViewBridge::currentPosition??????
         */
        @WorkerThread
        suspend fun putPlayPosition(url: String, position: Long)

        @WorkerThread
        suspend fun deletePlayPosition(url: String)

        fun positionFormat(position: Long): String
    }

    interface PlayOperatingProxy {
        val currentPlayEpisodeUrl: String

        /**
         * ????????????????????????player??????
         */
        fun playVideoMedia(episodeUrl: String)
        suspend fun putDanmaku(danmaku: String): Boolean
    }
}