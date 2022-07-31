package com.su.mediabox.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

inline fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) =
    beginTransaction().run {
        block()
        commit()
    }

inline fun AppCompatActivity.transaction(block: FragmentTransaction.() -> Unit) =
    supportFragmentManager.transaction(block)

inline fun Fragment.transaction(block: FragmentTransaction.() -> Unit) =
    childFragmentManager.transaction(block)