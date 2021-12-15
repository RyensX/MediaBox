package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityFavoriteBinding
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.FavoriteAdapter
import com.skyd.imomoe.viewmodel.FavoriteViewModel

class FavoriteActivity : BaseActivity<ActivityFavoriteBinding>() {
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        adapter = FavoriteAdapter(this, viewModel.favoriteList)

        mBinding.run {
            atbFavoriteActivity.setBackButtonClickListener { finish() }

            srlFavoriteActivity.setColorSchemeColors(
                this@FavoriteActivity.getResColor(R.color.main_color_skin)
            )
            srlFavoriteActivity.setOnRefreshListener { viewModel.getFavoriteData() }
            rvFavoriteActivity.layoutManager = GridLayoutManager(this@FavoriteActivity, 3)
            rvFavoriteActivity.adapter = adapter
            rvFavoriteActivity.addItemDecoration(AnimeEpisodeItemDecoration())
        }

        viewModel.mldFavoriteList.observe(this, Observer {
            mBinding.srlFavoriteActivity.isRefreshing = false
            if (it) {
                if (viewModel.favoriteList.isEmpty()) showLoadFailedTip(
                    getString(R.string.no_favorite),
                    null
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun getBinding(): ActivityFavoriteBinding =
        ActivityFavoriteBinding.inflate(layoutInflater)

    override fun onResume() {
        super.onResume()

        mBinding.srlFavoriteActivity.isRefreshing = true
        viewModel.getFavoriteData()
    }

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutFavoriteActivityNoFavorite
}

