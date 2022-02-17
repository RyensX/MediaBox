package com.su.mediabox.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.su.mediabox.bean.LicenseBean
import com.su.mediabox.pluginapi.Constant.ActionUrl
import com.su.mediabox.pluginapi.Constant.ViewHolderTypeString
import com.su.mediabox.databinding.ActivityLicenseBinding
import com.su.mediabox.view.adapter.LicenseAdapter


class LicenseActivity : BaseActivity<ActivityLicenseBinding>() {
    private val list: MutableList<LicenseBean> = ArrayList()
    private val adapter: LicenseAdapter = LicenseAdapter(this, list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        list.add(LicenseBean(ViewHolderTypeString.LICENSE_HEADER_1, "", "", "", ""))
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/SkyD666/Imomoe",
                "Imomoe",
                "GPL-3.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/jhy/jsoup",
                "jsoup",
                "MIT License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/coil-kt/coil",
                "coil",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/CarGuo/GSYVideoPlayer",
                "GSYVideoPlayer",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/square/okhttp",
                "okhttp",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/square/retrofit",
                "retrofit",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/getActivity/XXPermissions",
                "XXPermissions",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/Kotlin/kotlinx.coroutines",
                "kotlinx.coroutines",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/afollestad/material-dialogs",
                "material-dialogs",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/lingochamp/FileDownloader",
                "FileDownloader",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/4thline/cling",
                "cling",
                "LGPL License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/eclipse/jetty.project",
                "jetty.project",
                "EPL-2.0, Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/NanoHttpd/nanohttpd",
                "nanohttpd",
                "BSD-3-Clause License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/greenrobot/EventBus",
                "EventBus",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/scwang90/SmartRefreshLayout",
                "SmartRefreshLayout",
                "Apache-2.0 License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/KwaiAppTeam/AkDanmaku",
                "AkDanmaku",
                "MIT License"
            )
        )
        list.add(
            LicenseBean(
                ViewHolderTypeString.LICENSE_1,
                ActionUrl.ANIME_BROWSER,
                "https://github.com/JakeWharton/DiskLruCache",
                "DiskLruCache",
                "Apache-2.0 License"
            )
        )

        mBinding.run {
            atbLicenseActivity.setBackButtonClickListener { finish() }
            rvLicenseActivity.layoutManager = LinearLayoutManager(this@LicenseActivity)
            rvLicenseActivity.adapter = adapter
        }
    }

    override fun getBinding(): ActivityLicenseBinding =
        ActivityLicenseBinding.inflate(layoutInflater)
}