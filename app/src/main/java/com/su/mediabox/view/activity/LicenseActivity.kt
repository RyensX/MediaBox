package com.su.mediabox.view.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.su.mediabox.R
import com.su.mediabox.bean.License
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.ActivityLicenseBinding
import com.su.mediabox.databinding.ViewComponentLicenseBinding
import com.su.mediabox.pluginapi.action.WebBrowserAction
import com.su.mediabox.util.Util
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.viewBind
import com.su.mediabox.view.adapter.type.*

class LicenseActivity : BaseActivity() {

    private val mBinding by viewBind(ActivityLicenseBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            atbLicenseActivity.setBackButtonClickListener { finish() }
            rvLicenseActivity.linear()
                .initTypeList(DataViewMapList().registerDataViewMap<License, LicenseViewHolder>()) {
                    submitList(Const.Common.licenses)
                }
        }
    }

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
                        Util.openBrowser(url)
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
