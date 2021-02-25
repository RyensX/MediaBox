package com.skyd.imomoe.util.dlna.dms;

import android.content.Context;

import androidx.annotation.NonNull;

import org.fourthline.cling.support.model.item.Item;

import java.util.List;

interface IMediaContentDao {
    @NonNull
    List<Item> getImageItems(@NonNull Context context);

    @NonNull
    List<Item> getAudioItems(@NonNull Context context);

    @NonNull
    List<Item> getVideoItems(@NonNull Context context);
}
