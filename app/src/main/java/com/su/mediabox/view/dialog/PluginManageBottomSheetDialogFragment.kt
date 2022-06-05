package com.su.mediabox.view.dialog

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.su.mediabox.R
import com.su.mediabox.databinding.DialogPluginManageBottomSheetBinding
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.showToast
import com.su.mediabox.util.unsafeLazy
import com.su.mediabox.view.adapter.type.dynamicGrid
import com.su.mediabox.view.adapter.type.initTypeList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PluginManageBottomSheetDialogFragment private constructor() : BottomSheetDialogFragment(),
    PopupMenu.OnMenuItemClickListener {

    companion object {

        private const val PLUGIN_PACKAGE_NAME = "ppn"

        fun create(packageName: String) =
            PluginManageBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(PLUGIN_PACKAGE_NAME, packageName)
                }
            }
    }

    private lateinit var binding: DialogPluginManageBottomSheetBinding
    private val pluginInfo by unsafeLazy {
        arguments?.getString(PLUGIN_PACKAGE_NAME)?.let { PluginManager.queryPluginInfo(it) }
    }
    private val pluginMenu by unsafeLazy {
        PopupMenu(
            requireContext(),
            binding.pmMenu
        ).also {
            it.inflate(R.menu.plugin_menage_menu)
            it.setOnMenuItemClickListener(this)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPluginManageBottomSheetBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        pluginInfo?.also { pi ->
            binding.apply {
                pmIcon.setImageDrawable(pi.icon)
                pmName.text = pi.name
                //菜单
                pmMenu.setOnClickListener {
                    pluginMenu.show()
                }
                //信息列表
                pmInfoList.dynamicGrid().initTypeList {
                    lifecycleScope.launchWhenResumed {
                        submitList(withContext(Dispatchers.Default) {
                            val infos = mutableListOf<SimpleTextData>()
                            infos.addAll(
                                buildInfoPair(
                                    getString(R.string.plugin_manage_media_package_label),
                                    pi.packageName
                                )
                            )
                            infos.addAll(
                                buildInfoPair(
                                    getString(R.string.plugin_manage_media_version_label),
                                    pi.version
                                )
                            )
                            infos.addAll(buildInfoPair("API", "${pi.apiVersion}"))
                            infos
                        })
                    }
                }
            }
        } ?: dismiss()
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, javaClass.simpleName)
    }

    fun show(fragmentActivity: FragmentActivity) {
        show(fragmentActivity.supportFragmentManager)
    }

    private fun buildInfoPair(type: String, data: String, color: Int = Color.BLACK) =
        listOf(SimpleTextData(type).apply {
            spanSize = 2
            fontColor = color
            fontStyle = Typeface.BOLD
            paddingLeft = 8.dp
            paddingTop = 8.dp
            paddingRight = 8.dp
            paddingBottom = 8.dp
        }, SimpleTextData(data).apply {
            spanSize = 6
            gravity = Gravity.RIGHT
            fontColor = color
            paddingLeft = 8.dp
            paddingTop = 8.dp
            paddingRight = 8.dp
            paddingBottom = 8.dp
        })

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_pm_uninstall -> if (pluginInfo?.isExternalPlugin == true) "外部插件无法卸载".showToast() else
                pluginInfo?.run {
                    PluginManager.uninstallPlugin(this, requireActivity()) {
                        dismiss()
                    }
                }
        }
        return true
    }
}