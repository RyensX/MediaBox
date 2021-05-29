package com.skyd.imomoe.config

object UnknownActionUrl {
    val actionMap: HashMap<String, Action> = HashMap()

    interface Action {
        fun action()
    }
}