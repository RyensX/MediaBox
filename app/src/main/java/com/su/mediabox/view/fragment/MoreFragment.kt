package com.su.mediabox.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.R
import com.su.mediabox.bean.MoreBean
import com.su.mediabox.pluginapi.Constant.ViewHolderTypeString
import com.su.mediabox.databinding.FragmentMoreBinding
import com.su.mediabox.view.activity.AboutActivity
import com.su.mediabox.view.activity.HistoryActivity
import com.su.mediabox.view.activity.SettingActivity
import com.su.mediabox.view.activity.SkinActivity
import com.su.mediabox.view.adapter.MoreAdapter
import com.su.mediabox.pluginapi.Constant.ActionUrl
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl

class MoreFragment : BaseFragment<FragmentMoreBinding>() {
    private val list: MutableList<MoreBean> = ArrayList()
    private val adapter: MoreAdapter = MoreAdapter(this, list)

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMoreBinding =
        FragmentMoreBinding.inflate(inflater, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list.add(
            MoreBean(
                ViewHolderTypeString.MORE_1,
                buildRouteActionUrl(
                    ActionUrl.ANIME_LAUNCH_ACTIVITY,
                    HistoryActivity::class.qualifiedName!!
                ),
                getString(R.string.watch_history),
                R.drawable.ic_history_white_24
            )
        )
        list.add(
            MoreBean(
                ViewHolderTypeString.MORE_1,
                ActionUrl.ANIME_SKIP_BY_WEBSITE,
                getString(R.string.skip_by_website),
                R.drawable.ic_insert_link_white_24
            )
        )

        mBinding.run {
            rvMoreFragment.layoutManager = GridLayoutManager(activity, 2)
            rvMoreFragment.adapter = adapter
        }
    }

    companion object {
        const val TAG = "MoreFragment"
    }
}
