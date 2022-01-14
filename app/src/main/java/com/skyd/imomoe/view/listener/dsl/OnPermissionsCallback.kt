package com.skyd.imomoe.view.listener.dsl

import com.hjq.permissions.XXPermissions

fun XXPermissions.requestPermissions(init: OnPermissionsCallback.() -> Unit) {
    val listener = OnPermissionsCallback()
    listener.init()
    this.request(listener)
}

fun XXPermissions.requestSinglePermission(init: OnSinglePermissionCallback.() -> Unit) {
    val listener = OnSinglePermissionCallback()
    listener.init()
    this.request(listener)
}

private typealias Granted = (permissions: MutableList<String>?, all: Boolean) -> Unit
private typealias Denied = (permissions: MutableList<String>?, never: Boolean) -> Unit

class OnPermissionsCallback : com.hjq.permissions.OnPermissionCallback {
    private var granted: Granted? = null
    private var denied: Denied? = null

    fun onGranted(granted: Granted?) {
        this.granted = granted
    }

    fun onDenied(denied: Denied?) {
        this.denied = denied
    }

    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
        granted?.invoke(permissions, all)
    }

    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
        denied?.invoke(permissions, never)
    }
}

private typealias SingleGranted = () -> Unit
private typealias SingleDenied = (never: Boolean) -> Unit

class OnSinglePermissionCallback : com.hjq.permissions.OnPermissionCallback {
    private var singleGranted: SingleGranted? = null
    private var singleDenied: SingleDenied? = null

    fun onGranted(singleGranted: SingleGranted?) {
        this.singleGranted = singleGranted
    }

    fun onDenied(singleDenied: SingleDenied?) {
        this.singleDenied = singleDenied
    }

    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
        singleGranted?.invoke()
    }

    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
        singleDenied?.invoke(never)
    }
}
