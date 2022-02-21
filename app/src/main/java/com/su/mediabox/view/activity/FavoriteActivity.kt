package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityFavoriteBinding
import com.su.mediabox.util.Util.getResColor
import com.su.mediabox.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.su.mediabox.view.adapter.FavoriteAdapter
import com.su.mediabox.viewmodel.FavoriteViewModel

class FavoriteActivity : BasePluginActivity<ActivityFavoriteBinding>() {
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        adapter = FavoriteAdapter(this, viewModel.favoriteList)

        mBinding.run {
            atbFavoriteActivity.setBackButtonClickListener { finish() }

            srlFavoriteActivity.setColorSchemeColors(
                this@FavoriteActivity.getResColor(R.color.unchanged_main_color_2_skin)
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

