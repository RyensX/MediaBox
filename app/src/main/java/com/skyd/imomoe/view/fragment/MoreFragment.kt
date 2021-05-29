package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.MoreBean
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_LAUNCH_ACTIVITY
import com.skyd.imomoe.config.Const.ActionUrl.Companion.ANIME_SKIP_BY_WEBSITE
import com.skyd.imomoe.databinding.FragmentMoreBinding
import com.skyd.imomoe.view.activity.AboutActivity
import com.skyd.imomoe.view.activity.HistoryActivity
import com.skyd.imomoe.view.activity.SettingActivity
import com.skyd.imomoe.view.adapter.MoreAdapter

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
                "$ANIME_LAUNCH_ACTIVITY/${HistoryActivity::class.qualifiedName}",
                getString(R.string.watch_history),
                R.drawable.ic_history_white_24
            )
        )
        list.add(
            MoreBean(
                ViewHolderTypeString.MORE_1,
                ANIME_SKIP_BY_WEBSITE,
                getString(R.string.skip_by_website),
                R.drawable.ic_insert_link_white_24
            )
        )
        list.add(
            MoreBean(
                ViewHolderTypeString.MORE_1,
                "$ANIME_LAUNCH_ACTIVITY/${SettingActivity::class.qualifiedName}",
                getString(R.string.setting),
                R.drawable.ic_settings_white_24
            )
        )
        list.add(
            MoreBean(
                ViewHolderTypeString.MORE_1,
                "$ANIME_LAUNCH_ACTIVITY/${AboutActivity::class.qualifiedName}",
                getString(R.string.about),
                R.drawable.ic_info_white_24
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
