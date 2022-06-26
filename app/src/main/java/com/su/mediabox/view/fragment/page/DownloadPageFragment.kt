package com.su.mediabox.view.fragment.page

import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.databinding.PageDownloadBinding
import com.su.mediabox.view.fragment.BaseViewBindingFragment

class DownloadPageFragment : BaseViewBindingFragment<PageDownloadBinding>() {

    override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        PageDownloadBinding.inflate(inflater)

    override fun pagerInit() {

    }

}