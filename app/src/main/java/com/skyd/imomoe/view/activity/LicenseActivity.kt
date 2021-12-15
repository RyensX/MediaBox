package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.LicenseBean
import com.skyd.imomoe.config.Const.ActionUrl
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.databinding.ActivityLicenseBinding
import com.skyd.imomoe.view.adapter.LicenseAdapter


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

        mBinding.run {
            atbLicenseActivity.setBackButtonClickListener { finish() }
            rvLicenseActivity.layoutManager = LinearLayoutManager(this@LicenseActivity)
            rvLicenseActivity.adapter = adapter
        }
    }

    override fun getBinding(): ActivityLicenseBinding =
        ActivityLicenseBinding.inflate(layoutInflater)
}
