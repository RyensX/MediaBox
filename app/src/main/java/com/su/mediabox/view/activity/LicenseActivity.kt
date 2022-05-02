package com.su.mediabox.view.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.bean.License
import com.su.mediabox.databinding.ActivityLicenseBinding
import com.su.mediabox.databinding.ViewComponentLicenseBinding
import com.su.mediabox.pluginapi.v2.action.WebBrowserAction
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*

class LicenseActivity : BaseActivity<ActivityLicenseBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mutableListOf<License>().apply {
            add(License("", "名称", "许可证", true))
            add(
                License(
                    "https://github.com/SkyD666/Imomoe",
                    "Imomoe",
                    "GPL-3.0 License"
                )
            )
            add(
                License(
                    "https://github.com/jhy/jsoup",
                    "jsoup",
                    "MIT License"
                )
            )
            add(
                License(
                    "https://github.com/coil-kt/coil",
                    "coil",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/CarGuo/GSYVideoPlayer",
                    "GSYVideoPlayer",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/square/okhttp",
                    "okhttp",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/square/retrofit",
                    "retrofit2",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/getActivity/XXPermissions",
                    "XXPermissions",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/Kotlin/kotlinx.coroutines",
                    "kotlinx.coroutines",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/afollestad/material-dialogs",
                    "material-dialogs",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/lingochamp/FileDownloader",
                    "FileDownloader",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/4thline/cling",
                    "cling",
                    "LGPL License"
                )
            )
            add(
                License(
                    "https://github.com/eclipse/jetty.project",
                    "jetty.project",
                    "EPL-2.0, Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/NanoHttpd/nanohttpd",
                    "nanohttpd",
                    "BSD-3-Clause License"
                )
            )
            add(
                License(
                    "https://github.com/greenrobot/EventBus",
                    "EventBus",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/scwang90/SmartRefreshLayout",
                    "SmartRefreshLayout",
                    "Apache-2.0 License"
                )
            )
            add(
                License(
                    "https://github.com/KwaiAppTeam/AkDanmaku",
                    "AkDanmaku",
                    "MIT License"
                )
            )
            add(
                License(
                    "https://github.com/JakeWharton/DiskLruCache",
                    "DiskLruCache",
                    "Apache-2.0 License"
                )
            )

            mBinding.run {
                atbLicenseActivity.setBackButtonClickListener { finish() }
                rvLicenseActivity.linear()
                    .initTypeList(DataViewMapList().registerDataViewMap<License, LicenseViewHolder>()) {
                        submitList(this@apply)
                    }
            }
        }

    }

    override fun getBinding(): ActivityLicenseBinding =
        ActivityLicenseBinding.inflate(layoutInflater)

    class LicenseViewHolder private constructor(private val binding: ViewComponentLicenseBinding) :
        TypeViewHolder<License>(binding.root) {

        private var tmpData: License? = null

        private val styleColor by lazy(LazyThreadSafetyMode.NONE) { itemView.resources.getColor(R.color.main_color_2_skin) }

        constructor(parent: ViewGroup) : this(
            ViewComponentLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) {
            setOnClickListener(binding.root) {
                tmpData?.apply {
                    if (!isHead) {
                        WebBrowserAction.obtain(url).go(itemView.context)
                    }
                }
            }
        }

        override fun onBind(data: License) {
            tmpData = data
            binding.apply {
                val textColor = if (!data.isHead) styleColor else Color.WHITE
                vcLicenseName.apply {
                    setTextColor(textColor)
                }.text = data.title
                vcLicenseLicense.apply {
                    setTextColor(textColor)
                }.text = data.license
                root.setBackgroundColor(if (data.isHead) styleColor else Color.WHITE)
            }
        }
    }
}
