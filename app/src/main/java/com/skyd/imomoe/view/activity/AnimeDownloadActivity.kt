package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityAnimeDownloadBinding
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.AnimeDownloadAdapter
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel

class AnimeDownloadActivity : BaseActivity<ActivityAnimeDownloadBinding>() {
    private var mode = 0        //0是默认的，是番剧；1是番剧每一集
    private var actionBarTitle = ""
    private var directoryName = ""
    private lateinit var viewModel: AnimeDownloadViewModel
    private lateinit var adapter: AnimeDownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mode = intent.getIntExtra("mode", 0)
        actionBarTitle =
            intent.getStringExtra("actionBarTitle") ?: getString(R.string.download_anime)
        directoryName =
            intent.getStringExtra("directoryName") ?: ""

        viewModel = ViewModelProvider(this).get(AnimeDownloadViewModel::class.java)
        adapter = AnimeDownloadAdapter(this, viewModel.animeCoverList)

        mBinding.run {
            llAnimeDownloadActivityToolbar.tvToolbar1Title.text = actionBarTitle
            llAnimeDownloadActivityToolbar.ivToolbar1Back.setOnClickListener { finish() }

            rvAnimeDownloadActivity.layoutManager = LinearLayoutManager(this@AnimeDownloadActivity)
            rvAnimeDownloadActivity.adapter = adapter

            layoutAnimeDownloadLoading.tvCircleProgressTextTip1.text =
                getString(R.string.read_download_data_file)
        }

        viewModel.mldAnimeCoverList.observe(this, Observer {
            if (it) {
                mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.gone()
                if (viewModel.animeCoverList.size == 0) {
                    showLoadFailedTip(getString(R.string.no_download_video), null)
                }
                adapter.notifyDataSetChanged()
            }
        })

        if (mode == 0) viewModel.getAnimeCover()
        else if (mode == 1) {
            mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.visible()
            viewModel.getAnimeCoverEpisode(directoryName)
        }
    }

    override fun getBinding(): ActivityAnimeDownloadBinding =
        ActivityAnimeDownloadBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutAnimeDownloadNoDownload
}
