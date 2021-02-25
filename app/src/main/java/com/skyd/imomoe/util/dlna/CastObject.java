package com.skyd.imomoe.util.dlna;

import androidx.annotation.NonNull;

import com.skyd.imomoe.util.dlna.dmc.ICast;

public class CastObject {
    private CastObject() {
    }

    public static ICast newInstance(String url, String id, String name) {
        if (url.endsWith(".mp4")) {
            return CastVideo.newInstance(url, id, name);
        } else if (url.endsWith(".mp3")) {
            return CastAudio.newInstance(url, id, name);
        } else if (url.endsWith(".jpg")) {
            return CastImage.newInstance(url, id, name);
        } else {
            return null;
        }
    }

    /**
     *
     */
    public static class CastAudio implements ICast.ICastVideo {
        public static CastAudio newInstance(String url, String id, String name) {
            return new CastAudio(url, id, name);
        }

        public final String url;

        public final String id;

        public final String name;

        private long duration;

        public CastAudio(String url, String id, String name) {
            this.url = url;
            this.id = id;
            this.name = name;
        }

        /**
         * @param duration the total time of video (ms)
         */
        public CastAudio setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        @NonNull
        @Override
        public String getId() {
            return id;
        }

        @NonNull
        @Override
        public String getUri() {
            return url;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getDurationMillSeconds() {
            return duration;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public long getBitrate() {
            return 0;
        }
    }

    /**
     *
     */
    public static class CastImage implements ICast.ICastVideo {
        public static CastImage newInstance(String url, String id, String name) {
            return new CastImage(url, id, name);
        }

        public final String url;

        public final String id;

        public final String name;

        public CastImage(String url, String id, String name) {
            this.url = url;
            this.id = id;
            this.name = name;
        }

        @NonNull
        @Override
        public String getId() {
            return id;
        }

        @NonNull
        @Override
        public String getUri() {
            return url;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getDurationMillSeconds() {
            return -1L;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public long getBitrate() {
            return 0;
        }
    }

    /**
     *
     */
    public static class CastVideo implements ICast.ICastVideo {
        public static CastVideo newInstance(String url, String id, String name) {
            return new CastVideo(url, id, name);
        }

        public final String url;

        public final String id;

        public final String name;

        private long duration;

        public CastVideo(String url, String id, String name) {
            this.url = url;
            this.id = id;
            this.name = name;
        }

        /**
         * @param duration the total time of video (ms)
         */
        public CastVideo setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        @NonNull
        @Override
        public String getId() {
            return id;
        }

        @NonNull
        @Override
        public String getUri() {
            return url;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getDurationMillSeconds() {
            return duration;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public long getBitrate() {
            return 0;
        }
    }
}
