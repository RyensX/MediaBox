package com.skyd.imomoe.util

import android.app.Activity
import androidx.fragment.app.Fragment
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.skyd.imomoe.view.listener.dsl.OnSinglePermissionCallback
import com.skyd.imomoe.view.listener.dsl.requestSinglePermission

fun Activity.requestManageExternalStorage(init: OnSinglePermissionCallback.() -> Unit) {
    XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .requestSinglePermission(init)
}

fun Fragment.requestManageExternalStorage(init: OnSinglePermissionCallback.() -> Unit) {
    XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .requestSinglePermission(init)
}
