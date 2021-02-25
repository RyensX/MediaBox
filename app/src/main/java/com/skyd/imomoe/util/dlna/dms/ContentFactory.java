package com.skyd.imomoe.util.dlna.dms;

import android.content.Context;

import androidx.annotation.NonNull;

import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.List;

public class ContentFactory {
    private static ContentFactory sInstance;

    public static void initInstance(Context context, String baseUrl) {
        if (sInstance == null || sInstance.checkBaseUrl(baseUrl)) {
            sInstance = new ContentFactory(context.getApplicationContext(), baseUrl);
        }
    }

    public static ContentFactory getInstance() {
        return sInstance;
    }

    private final Context context;
    private final String baseUrl;

    private ContentFactory(Context context, String baseUrl) {
        this.context = context.getApplicationContext();
        this.baseUrl = baseUrl;
    }

    boolean checkBaseUrl(String baseUrl) {
        return this.baseUrl != null && !this.baseUrl.equals(baseUrl);
    }

    @NonNull
    public Container getContent(String containerId) {
        Container result = new Container();
        result.setChildCount(0);
        if (MediaItem.ROOT_ID.equals(containerId)) {
            // 定义音频资源
            Container audioContainer = new Container();
            audioContainer.setId(MediaItem.AUDIO_ID);
            audioContainer.setParentID(MediaItem.ROOT_ID);
            audioContainer.setClazz(MediaItem.AUDIO_CLASS);
            audioContainer.setTitle("Audios");

            result.addContainer(audioContainer);
            result.setChildCount(result.getChildCount() + 1);

            // 定义图片资源
            Container imageContainer = new Container();
            imageContainer.setId(MediaItem.IMAGE_ID);
            imageContainer.setParentID(MediaItem.ROOT_ID);
            imageContainer.setClazz(MediaItem.IMAGE_CLASS);
            imageContainer.setTitle("Images");

            result.addContainer(imageContainer);
            result.setChildCount(result.getChildCount() + 1);

            // 定义视频资源
            Container videoContainer = new Container();
            videoContainer.setId(MediaItem.VIDEO_ID);
            videoContainer.setParentID(MediaItem.ROOT_ID);
            videoContainer.setClazz(MediaItem.VIDEO_CLASS);
            videoContainer.setTitle("Videos");

            result.addContainer(videoContainer);
            result.setChildCount(result.getChildCount() + 1);
        } else if (MediaItem.IMAGE_ID.equals(containerId)) {
            MediaContentDao contentDao = new MediaContentDao(baseUrl);
            //Get image items
            List<Item> items = contentDao.getImageItems(context);
            for (Item item : items) {
                result.addItem(item);
                result.setChildCount(result.getChildCount() + 1);
            }
        } else if (MediaItem.AUDIO_ID.equals(containerId)) {
            MediaContentDao contentDao = new MediaContentDao(baseUrl);
            //Get audio items
            List<Item> items = contentDao.getAudioItems(context);
            for (Item item : items) {
                result.addItem(item);
                result.setChildCount(result.getChildCount() + 1);
            }
        } else if (MediaItem.VIDEO_ID.equals(containerId)) {
            MediaContentDao contentDao = new MediaContentDao(baseUrl);
            //Get video items
            List<Item> items = contentDao.getVideoItems(context);
            for (Item item : items) {
                result.addItem(item);
                result.setChildCount(result.getChildCount() + 1);
            }
        }
        return result;
    }
}
