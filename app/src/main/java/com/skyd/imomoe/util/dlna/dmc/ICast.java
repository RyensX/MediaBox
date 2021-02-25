package com.skyd.imomoe.util.dlna.dmc;

import androidx.annotation.NonNull;

public interface ICast {

    @NonNull
    String getId();

    @NonNull
    String getUri();

    String getName();

    interface ICastVideo extends ICast {

        /**
         * @return video duration, ms
         */
        long getDurationMillSeconds();

        long getSize();

        long getBitrate();
    }

    interface ICastAudio extends ICast {
        /**
         * @return audio duration, ms
         */
        long getDurationMillSeconds();

        long getSize();
    }

    interface ICastImage extends ICast {
        long getSize();
    }

}
