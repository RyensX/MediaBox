package com.su.mediabox

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> Fragment.lifecycleCollect(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    collector: FlowCollector<T>
) {
    lifecycleScope.launch {
        lifecycle.whenStateAtLeast(state) {
            viewLifecycleOwner.repeatOnLifecycle(state) {
                flow.collect(collector)
            }
        }
    }
}

fun <T> ComponentActivity.lifecycleCollect(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    collector: FlowCollector<T>
) {
    lifecycleScope.launch {
        lifecycle.whenStateAtLeast(state) {
            repeatOnLifecycle(state) {
                flow.collect(collector)
            }
        }
    }
}