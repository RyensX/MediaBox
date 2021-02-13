package com.skyd.imomoe.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.AnimeDownloadAdapter
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel
import kotlinx.android.synthetic.main.activity_anime_download.*

class AnimeDownloadActivity : AppCompatActivity() {
    private var mode = 0        //0是默认的，是番剧；1是番剧每一集
    private var actionBarTitle = ""
    private var directoryName = ""
    private lateinit var viewModel: AnimeDownloadViewModel
    private lateinit var adapter: AnimeDownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime_download)

        mode = intent.getIntExtra("mode", 0)
        actionBarTitle =
            intent.getStringExtra("actionBarTitle") ?: getString(R.string.download_anime)
        directoryName =
            intent.getStringExtra("directoryName") ?: ""

        tv_anime_download_activity_toolbar_title.text = actionBarTitle

        viewModel = ViewModelProvider(this).get(AnimeDownloadViewModel::class.java)
        adapter = AnimeDownloadAdapter(this, viewModel.animeCoverList)

        iv_anime_download_activity_back.setOnClickListener { finish() }

        rv_anime_download_activity.layoutManager = LinearLayoutManager(this)
        rv_anime_download_activity.adapter = adapter

        viewModel.mldAnimeCoverList.observe(this, {
            if (it) {
                ll_anime_download_loading.gone()
                ll_anime_download_no_download.visibility =
                    if (viewModel.animeCoverList.size == 0) View.VISIBLE
                    else View.GONE
                adapter.notifyDataSetChanged()
            }
        })

        if (mode == 0) viewModel.getAnimeCover()
        else if (mode == 1) {
            ll_anime_download_loading.visible()
            viewModel.getAnimeCoverEpisode(directoryName)
        }
    }
}