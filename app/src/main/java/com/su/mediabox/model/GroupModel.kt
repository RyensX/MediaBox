package com.su.mediabox.model

interface GroupModel<T> {
    val childData: List<T>?
    var isExpand: Boolean
}