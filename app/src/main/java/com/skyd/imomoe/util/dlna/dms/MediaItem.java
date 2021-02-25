package com.skyd.imomoe.util.dlna.dms;

import org.fourthline.cling.support.model.DIDLObject;

public interface MediaItem {
    String ROOT_ID = "0";
    String AUDIO_ID = "10";
    String VIDEO_ID = "20";
    String IMAGE_ID = "30";

    DIDLObject.Class AUDIO_CLASS = new DIDLObject.Class("object.container.audio");
    DIDLObject.Class IMAGE_CLASS = new DIDLObject.Class("object.item.imageItem");
    DIDLObject.Class VIDEO_CLASS = new DIDLObject.Class("object.container.video");
}
