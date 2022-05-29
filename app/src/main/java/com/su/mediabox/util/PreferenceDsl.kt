package com.su.mediabox.util

import androidx.annotation.StringRes
import androidx.preference.*

inline fun PreferenceFragmentCompat.preferenceScreen(screenDsl: PreferenceScreen.() -> Unit): PreferenceScreen =
    preferenceManager.createPreferenceScreen(requireContext()).apply { screenDsl() }

fun Preference.titleRes(@StringRes id: Int, vararg args: Any) {
    title = context.getString(id, *args)
}

fun Preference.keyRes(@StringRes id: Int, vararg args: Any) {
    key = context.getString(id, *args)
}

fun Preference.summaryRes(@StringRes id: Int, vararg args: Any) {
    summary = context.getString(id, *args)
}

inline fun <T : Preference> PreferenceGroup.dsl(preference: T, dslBlock: T.() -> Unit) =
    preference.apply {
        //如key之类的配置需要先设置再add，否则无法生效
        dslBlock()
        addPreference(preference)
    }

inline fun PreferenceGroup.preferenceCategory(prefDsl: PreferenceCategory.() -> Unit) =
    PreferenceCategory(context).apply {
        //必须先add，否侧错误
        this@preferenceCategory.addPreference(this)
        prefDsl()
    }

inline fun PreferenceGroup.switchPreference(
    prefDsl: SwitchPreferenceCompat.() -> Unit
) = dsl(SwitchPreferenceCompat(context), prefDsl)

inline fun PreferenceGroup.checkPreference(
    prefDsl: CheckBoxPreference.() -> Unit
) = dsl(CheckBoxPreference(context), prefDsl)

inline fun PreferenceGroup.preference(
    prefDsl: Preference.() -> Unit
) = dsl(Preference(context), prefDsl)