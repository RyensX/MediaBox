package com.skyd.skin.core.attrs

import android.view.View

abstract class SkinAttr : Cloneable {

    // the integer value of the "id" attribute
    var attrResourceRefId = -1

    abstract fun applySkin(view: View)

    abstract fun tag(): String
}