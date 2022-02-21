package com.su.mediabox.view.activity

import android.os.Bundle
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.databinding.ActivityAnimeDownloadBinding
import com.su.mediabox.util.gone
import com.su.mediabox.util.showToast
import com.su.mediabox.util.requestManageExternalStorage
import com.su.mediabox.util.visible
import com.su.mediabox.view.adapter.AnimeDownloadAdapter
import com.su.mediabox.viewmodel.AnimeDownloadViewModel

class AnimeDownloadActivity : BasePluginActivity<ActivityAnimeDownloadBinding>() {

    private val viewModel by viewModels<AnimeDownloadViewModel>()
    private lateinit var adapter: AnimeDownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.mode = intent.getIntExtra("mode", 0)
        viewModel.actionBarTitle =
            intent.getStringExtra("actionBarTitle") ?: getString(R.string.download_anime)
        viewModel.directoryName = intent.getStringExtra("directoryName") ?: ""

        adapter = AnimeDownloadAdapter(this, viewModel.animeCoverList)

        mBinding.run {
            atbAnimeDownloadActivityToolbar.titleText = viewModel.actionBarTitle
            atbAnimeDownloadActivityToolbar.setBackButtonClickListener { finish() }
            atbAnimeDownloadActivityToolbar.setButtonClickListener(0) {
                MaterialDialog(this@AnimeDownloadActivity).show {
                    title(res = R.string.attention)
                    message(
                        text = "由于新版Android存储机制变更，因此新缓存的动漫将存储在App的私有路径，" +
                                "以前缓存的动漫依旧能够观看，其后面将有“旧”字样。新缓存的动漫与以前缓存的互不影响。" +
                                "\n\n注意：新缓存的动漫将在App被卸载或数据被清除后丢失。"
                    )
                    positiveButton { dismiss() }
                }
            }

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

        requestManageExternalStorage {
            onGranted {
                if (viewModel.mode == 0) viewModel.getAnimeCover()
                else if (viewModel.mode == 1) {
                    mBinding.layoutAnimeDownloadLoading.layoutCircleProgressTextTip1.visible()
                    viewModel.getAnimeCoverEpisode(viewModel.directoryName)
                }
            }
            onDenied {
                "无存储权限，无法播放本地缓存视频".showToast(Toast.LENGTH_LONG)
                finish()
            }
        }
    }

    override fun getBinding(): ActivityAnimeDownloadBinding =
        ActivityAnimeDownloadBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub? = mBinding.layoutAnimeDownloadNoDownload
}
