package com.skyd.imomoe.view.component.player

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SendDanmuBean
import com.skyd.imomoe.bean.SendDanmuResultBean
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmuService
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.html.SnifferVideo.AC
import com.skyd.imomoe.util.html.SnifferVideo.KEY
import com.skyd.imomoe.util.html.SnifferVideo.REFEREER_URL
import com.skyd.imomoe.util.html.SnifferVideo.VIDEO_ID
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.component.player.AnimeDanmakuLoaderFactory.Companion.TAG_ANIME
import com.skyd.imomoe.view.component.player.AnimeDanmakuLoaderFactory.Companion.create
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.loader.IllegalDataException
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import kotlin.math.abs

/**
 * 注意：这只是一个例子，演示如何集合弹幕，需要完善如弹出输入弹幕等的，可以自行完善。
 * 注意：b站的弹幕so只有v5 v7 x86、没有64，所以记得配置上ndk过滤。
 */
class DanmakuVideoPlayer : AnimeVideoPlayer {
    private lateinit var mDanmuUrl: String
    private var mParser: BaseDanmakuParser? = null //解析器对象
    private lateinit var danmakuView: IDanmakuView          //弹幕view
    private var danmakuContext: DanmakuContext? = null
    var danmakuStartSeekPosition: Long = -1

    // 由于seek后弹幕时间不精确，因此标记是否需要校准
    private var needCorrectSeekPosition = false

    // 是否在显示弹幕
    var mDanmaKuShow = true

    // 请求弹幕相关参数
    var mDanmuParamMap = HashMap<String, String>()
        private set

    // 弹幕输入文本框
    private var mDanmuInputEditText: EditText? = null

    // 弹幕开关
    private var mShowDanmuImageView: ImageView? = null

    private var mDanmuController: ViewGroup? = null

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)
        danmakuView = findViewById(R.id.danmaku_view)
        mShowDanmuImageView = findViewById(R.id.iv_show_danmu)
        mDanmuInputEditText = findViewById(R.id.et_input_danmu)
        mDanmuController = findViewById(R.id.cl_danmu_controller)
        mDanmuInputEditText?.gone()
        mShowDanmuImageView?.gone()
        // 设置高度是0
        hideBottomDanmuController()

        mDanmuInputEditText?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    val text = v.text.toString()
                    if (text.isBlank()) {
                        mContext.resources.getString(R.string.please_input_danmu_text).showToast()
                        return false
                    }
                    mDanmuInputEditText?.setText("")
                    SendDanmuBean(
                        "DIYgod", "rgb(255, 255, 255)",
                        mDanmuParamMap[VIDEO_ID] ?: "",
                        mDanmuParamMap[REFEREER_URL] ?: "",
                        "27.5px", text, currentPlayer.currentPositionWhenPlaying / 1000.0,
                        "right"
                    ).let {
                        sendDanmaku(mDanmuParamMap[AC] ?: "", mDanmuParamMap[KEY] ?: "", it)
                    }
                    return true

                }
                return true
            }
        })

        mDanmuUrl = ""
        initDanmaku()

        mShowDanmuImageView?.setOnClickListener {
            startDismissControlViewTimer()
            mDanmaKuShow = !mDanmaKuShow
            resolveDanmakuShow()
        }
    }

    override fun onPrepared() {
        super.onPrepared()
        onPrepareDanmaku(this)
    }

    override fun onVideoPause() {
        super.onVideoPause()
        danmakuOnPause()
    }

    override fun onVideoResume(seek: Boolean) {
        super.onVideoResume(seek)
        danmakuOnResume()
    }

    override fun clickStartIcon() {
        super.clickStartIcon()
        if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
            danmakuOnResume()
        } else if (mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE) {
            danmakuOnPause()
        }
    }

    override fun onCompletion() {
        releaseDanmaku(this)
    }

    override fun onSeekComplete() {
        super.onSeekComplete()
        val time = mProgressBar.progress / 100.0 * duration
        // 如果已经初始化过的，直接seek到对于位置
        Log.e("---", "$time ${mProgressBar.progress} $duration")
//        Log.e("---", "$mCurrentPosition ${mProgressBar.progress} $duration")
        if (mHadPlay && danmakuView.isPrepared) {
            resolveDanmakuSeek(this, time.toLong())
            needCorrectSeekPosition = true
        } else if (mHadPlay && !danmakuView.isPrepared) {
            // 如果没有初始化过的，记录位置等待
            danmakuStartSeekPosition = time.toLong()
        }
    }

    override fun cloneParams(from: GSYBaseVideoPlayer, to: GSYBaseVideoPlayer) {
        (to as DanmakuVideoPlayer).mDanmuUrl = (from as DanmakuVideoPlayer).mDanmuUrl
        super.cloneParams(from, to)
    }

    /**
     * 处理播放器在全屏切换时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val player =
            super.startWindowFullscreen(context, actionBar, statusBar) as DanmakuVideoPlayer
        // 设置弹幕信息Map
        player.mDanmuParamMap.clear()
        player.mDanmuParamMap.putAll(mDanmuParamMap)
        // 对弹幕设置偏移记录
        player.danmakuStartSeekPosition = currentPositionWhenPlaying.toLong()
        player.mDanmaKuShow = mDanmaKuShow
        player.mShowDanmuImageView?.visibility = mShowDanmuImageView?.visibility ?: View.GONE
        player.mDanmuInputEditText?.visibility = mDanmuInputEditText?.visibility ?: View.GONE
        onPrepareDanmaku(player)
        return player
    }

    /**
     * 处理播放器在退出全屏时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        gsyVideoPlayer?.let {
            val player = it as DanmakuVideoPlayer
            // 设置弹幕信息Map
            mDanmuParamMap.clear()
            mDanmuParamMap.putAll(player.mDanmuParamMap)
            mDanmaKuShow = player.mDanmaKuShow
            mShowDanmuImageView?.visibility = player.mShowDanmuImageView?.visibility ?: View.GONE
            mDanmuInputEditText?.visibility = player.mDanmuInputEditText?.visibility ?: View.GONE
            if (player.mDanmuInputEditText?.visibility == View.VISIBLE) showBottomDanmuController()
            else hideBottomDanmuController()

            if (player.danmakuView.isPrepared) {
                resolveDanmakuSeek(this, player.currentPositionWhenPlaying.toLong())
                resolveDanmakuShow()
                releaseDanmaku(player)
            }
        }
    }

    protected fun danmakuOnPause() {
        if (danmakuView.isPrepared) {
            danmakuView.pause()
        }
    }

    protected fun danmakuOnResume() {
        if (danmakuView.isPrepared && danmakuView.isPaused) {
            danmakuView.resume()
        }
    }

    fun setDanmaKuUrl(url: String, paramMap: HashMap<String, String>? = null) {
        if (paramMap != null) {
            mDanmuParamMap.clear()
            mDanmuParamMap.putAll(paramMap)
        }
        mDanmuUrl = url
        if (!danmakuView.isPrepared) {
            onPrepareDanmaku(currentPlayer as DanmakuVideoPlayer)
        }
    }

    private fun initDanmaku() {
        // 设置最大显示行数
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5 // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true
        val danamakuAdapter = DanamakuAdapter(danmakuView)
        danmakuContext = DanmakuContext.create()
        danmakuContext?.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
            ?.setDuplicateMergingEnabled(false)?.setScrollSpeedFactor(1.2f)?.setScaleTextSize(1.2f)
            ?.setCacheStuffer(SpannedCacheStuffer(), danamakuAdapter) // 图文混排使用SpannedCacheStuffer
            ?.setMaximumLines(maxLinesPair)
            ?.preventOverlapping(overlappingEnablePair)
        //todo 这是为了demo效果，实际上需要去掉这个，外部传输文件进来
        danmakuView.setCallback(object : DrawHandler.Callback {
            override fun updateTimer(timer: DanmakuTimer) {
                if (abs(currentPlayer.speed - 1f) >= 0.001f) timer.update(
                    currentPlayer.currentPositionWhenPlaying.toLong()
                )/* else if (needCorrectSeekPosition) {
                    currentPlayer.currentPositionWhenPlaying.toLong()
                    needCorrectSeekPosition = false
                }*/
            }

            override fun drawingFinished() {}
            override fun danmakuShown(danmaku: BaseDanmaku) {}
            override fun prepared() {
                if (danmakuStartSeekPosition != -1L) {
                    resolveDanmakuSeek(this@DanmakuVideoPlayer, danmakuStartSeekPosition)
                    danmakuStartSeekPosition = -1
                }
                resolveDanmakuShow()
            }
        })
        danmakuView.enableDanmakuDrawingCache(true)
    }

    /**
     * 弹幕的显示与关闭
     */
    private fun resolveDanmakuShow() {
        post {
            if (mDanmaKuShow) {
                if (!danmakuView.isShown) danmakuView.show()
                mShowDanmuImageView?.isSelected = true
            } else {
                if (danmakuView.isShown) danmakuView.hide()
                mShowDanmuImageView?.isSelected = false
            }
        }
    }

    /**
     * 开始播放弹幕
     */
    private fun onPrepareDanmaku(gsyVideoPlayer: DanmakuVideoPlayer) {
        if (mDanmuUrl.isBlank()) return
        mDanmuInputEditText?.visible()
        mShowDanmuImageView?.visible()
        showBottomDanmuController()
        // 使用的弹幕url进行显示，要网络请求，因此在io线程执行（与danmakuView.prepare需要顺序执行）
        GlobalScope.launch(Dispatchers.IO) {
            gsyVideoPlayer.mParser = createParser(gsyVideoPlayer.mDanmuUrl)
            gsyVideoPlayer.danmakuView.let { danmakuView ->
                if (!danmakuView.isPrepared && gsyVideoPlayer.mParser != null) {
                    danmakuView.prepare(gsyVideoPlayer.mParser, gsyVideoPlayer.danmakuContext)
                }
            }
        }

        // 若不加下面的if，则切换横竖屏后不管是否暂停，弹幕都会自动播放
        if (gsyVideoPlayer.currentState == CURRENT_STATE_PLAYING) gsyVideoPlayer.danmakuView.resume()
        else gsyVideoPlayer.danmakuView.pause()
    }

    /**
     * 弹幕偏移
     */
    private fun resolveDanmakuSeek(gsyVideoPlayer: DanmakuVideoPlayer, time: Long) {
        gsyVideoPlayer.danmakuView.let { danmakuView ->
            if (mHadPlay && danmakuView.isPrepared) danmakuView.seekTo(time)
        }
    }

    /**
     * 创建解析器对象，解析弹幕url
     *
     * @param url
     * @return
     */
    private fun createParser(url: String): BaseDanmakuParser {
        if (url.isBlank()) {
            return object : BaseDanmakuParser() {
                override fun parse(): Danmakus {
                    return Danmakus()
                }
            }
        }
        val loader = create(TAG_ANIME)
        try {
            if (loader is AnimeDanmakuLoader) loader.load(URL(url))
            else loader?.load(url)
        } catch (e: IllegalDataException) {
            e.printStackTrace()
        }
        return AnimeDanmakuParser().apply {
            load(loader?.dataSource)
        }
    }

    /**
     * 释放弹幕控件
     */
    private fun releaseDanmaku(danmakuVideoPlayer: DanmakuVideoPlayer?) {
        danmakuVideoPlayer?.danmakuView?.let { danmakuView ->
            Debuger.printfError("release Danmaku!")
            danmakuView.release()
        }
    }

    /**
     * 显示非全屏模式下播放器下方弹幕控制部分
     */
    private fun showBottomDanmuController() {
        mDanmuController?.let { danmuController ->
            if (danmuController.layoutParams.height == 0) {
                danmuController.layoutParams = danmuController.layoutParams.apply {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                post {
                    layoutParams = layoutParams.apply { height += danmuController.height }
                }
            }
        }
    }

    /**
     * 隐藏非全屏模式下播放器下方弹幕控制部分
     */
    private fun hideBottomDanmuController() {
        mDanmuController?.let { danmuController ->
            if (danmuController.layoutParams.height != 0) {
                post {
                    layoutParams = layoutParams.apply {
                        if (danmuController.height > 0)
                            height -= danmuController.height
                    }
                }
                danmuController.layoutParams = danmuController.layoutParams.apply {
                    height = 0
                }
            }
        }
    }

    /**
     * 发送弹幕
     * @param isLive 是否是直播弹幕
     */
    private fun sendDanmaku(
        ac: String,
        key: String,
        sendDanmuBean: SendDanmuBean,
        isLive: Boolean = false
    ) {
        val danmaku = danmakuContext?.mDanmakuFactory?.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        danmaku ?: return
        danmaku.text = sendDanmuBean.text
        danmaku.isLive = isLive
        danmaku.time = (sendDanmuBean.time * 1000).toLong()
        danmaku.textSize = 0.7f * sendDanmuBean.size.replace("px", "").toFloat() *
                (mParser?.displayer?.density ?: 1 - 0.6f)
        val color = AnimeDanmakuParser.getColor(sendDanmuBean.color)
        danmaku.textColor = color
        danmaku.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
        danmaku.underlineColor = Color.GREEN
        danmakuView.addDanmaku(danmaku)

        val request = RetrofitManager.instance.create(DanmuService::class.java) ?: return
        val json = Gson().toJson(sendDanmuBean)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        request.sendDanmu(ac, key, json).enqueue(object : Callback<SendDanmuResultBean> {
            override fun onFailure(call: Call<SendDanmuResultBean>, t: Throwable) {
                mContext.getString(R.string.send_danmu_failed, t.message).showToast()
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<SendDanmuResultBean>,
                response: Response<SendDanmuResultBean>
            ) {
                response.body()?.message?.showToast()
            }
        })
    }
}