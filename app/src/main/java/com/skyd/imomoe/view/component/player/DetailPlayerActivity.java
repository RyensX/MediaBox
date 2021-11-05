package com.skyd.imomoe.view.component.player;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.skyd.imomoe.view.activity.BaseActivity;
import com.skyd.skin.core.SkinBaseActivity;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.utils.OrientationOption;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import org.jetbrains.annotations.NotNull;

import static com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PAUSE;

/**
 * 详情模式播放页面基础类
 */
public abstract class DetailPlayerActivity<T extends GSYBaseVideoPlayer, VB extends ViewBinding> extends BaseActivity<VB> implements MyVideoAllCallBack {

    protected boolean isPlay;

    // 是否是在onPause方法里自动暂停的
    protected boolean isPause;

    protected AnimeOrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 选择普通模式
     */
    public void initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new AnimeOrientationUtils(this, getGSYVideoPlayer(), getOrientationOption());
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        if (getGSYVideoPlayer().getFullscreenButton() != null) {
            getGSYVideoPlayer().getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFull();
                    clickForFullScreen();
                }
            });
        }
        // 退出全屏监听，避免平板退出全屏后变成竖屏
        getGSYVideoPlayer().setBackFromFullScreenListener(view -> {
            onBackPressed();
        });
    }

    /**
     * 选择builder模式
     */
    public void initVideoBuilderMode() {
        initVideo();
        getGSYVideoOptionBuilder().
                setVideoAllCallBack(this)
                .build(getGSYVideoPlayer());
    }

    public void showFull() {
        if (orientationUtils.getIsLand() != 1) {
            //直接横屏
            orientationUtils.resolveByClick();
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusBar
        getGSYVideoPlayer().startWindowFullscreen(DetailPlayerActivity.this, hideActionBarWhenFull(), hideStatusBarWhenFull());

    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo2();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (getGSYVideoPlayer().getCurrentPlayer().getCurrentState() != CURRENT_STATE_PAUSE) {
            getGSYVideoPlayer().getCurrentPlayer().onVideoPause();
            if (orientationUtils != null) {
                orientationUtils.setIsPause(true);
            }
            isPause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            getGSYVideoPlayer().getCurrentPlayer().onVideoResume();
            if (orientationUtils != null) {
                orientationUtils.setIsPause(false);
            }
            isPause = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getGSYVideoPlayer().getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            getGSYVideoPlayer().onConfigurationChanged(this, newConfig, orientationUtils, hideActionBarWhenFull(), hideStatusBarWhenFull());
        }
    }

    @Override
    public void onStartPrepared(String url, Object... objects) {
        videoPlayStatusChanged(true);
        needShowToolbar(true);
    }

    @Override
    public void onPrepared(String url, Object... objects) {

        if (orientationUtils == null) {
            throw new NullPointerException("initVideo() or initVideoBuilderMode() first");
        }
        //开始播放了才能旋转和全屏
        orientationUtils.setEnable(getDetailOrientationRotateAuto() && !isAutoFullWithSize());
        isPlay = true;
        isPause = false;
        videoPlayStatusChanged(true);
    }

    @Override
    public void onClickStartIcon(String url, Object... objects) {

    }

    @Override
    public void onClickStartError(String url, Object... objects) {

    }

    @Override
    public void onClickStop(String url, Object... objects) {
        videoPlayStatusChanged(false);
    }

    @Override
    public void onClickStopFullscreen(String url, Object... objects) {
        videoPlayStatusChanged(false);
    }

    @Override
    public void onClickResume(String url, Object... objects) {
        videoPlayStatusChanged(true);
    }

    @Override
    public void onClickResumeFullscreen(String url, Object... objects) {
        videoPlayStatusChanged(true);
    }

    @Override
    public void onClickSeekbar(String url, Object... objects) {

    }

    @Override
    public void onClickSeekbarFullscreen(String url, Object... objects) {

    }

    @Override
    public void onAutoComplete(String url, Object... objects) {
        videoPlayStatusChanged(false);
        needShowToolbar(true);
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {

    }

    @Override
    public void onQuitFullscreen(String url, Object... objects) {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    @Override
    public void onQuitSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onEnterSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekVolume(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekPosition(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekLight(String url, Object... objects) {

    }

    @Override
    public void onPlayError(String url, Object... objects) {
        videoPlayStatusChanged(false);
        needShowToolbar(true);
    }

    @Override
    public void onClickStartThumb(String url, Object... objects) {

    }

    @Override
    public void onClickBlank(String url, Object... objects) {

    }

    @Override
    public void onClickBlankFullscreen(String url, Object... objects) {

    }

    @Override
    public void onComplete(String url, Object... objects) {
        videoPlayStatusChanged(false);
        needShowToolbar(true);
    }

    public boolean hideActionBarWhenFull() {
        return true;
    }

    public boolean hideStatusBarWhenFull() {
        return true;
    }

    /**
     * 可配置旋转 OrientationUtils
     */
    public OrientationOption getOrientationOption() {
        return null;
    }

    /**
     * 播放控件
     */
    public abstract T getGSYVideoPlayer();

    /**
     * 配置播放器
     */
    public abstract GSYVideoOptionBuilder getGSYVideoOptionBuilder();

    /**
     * 点击了全屏
     */
    public abstract void clickForFullScreen();

    /**
     * 是否启动旋转横屏，true表示启动
     */
    public abstract boolean getDetailOrientationRotateAuto();

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    public boolean isAutoFullWithSize() {
        return false;
    }

    @Override
    public void onVideoPause() {
        videoPlayStatusChanged(false);
        needShowToolbar(true);
    }

    @Override
    public void onVideoResume() {
        videoPlayStatusChanged(true);
        needShowToolbar(false);
    }

    /**
     * 视频播放状态变化
     *
     * @param playing false：未在播放（包括播放失败暂停等等）；true：正在播放（包括正在准备加载、缓冲等等）
     */
    protected void videoPlayStatusChanged(boolean playing) {

    }

    /**
     * 是否需要必须显示工具栏
     *
     * @param show false：不需要显示；true：需要显示
     */
    protected void needShowToolbar(boolean show) {

    }
}
