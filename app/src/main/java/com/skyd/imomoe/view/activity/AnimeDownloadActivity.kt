package com.skyd.imomoe.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.util.Util.gone
import com.skyd.imomoe.util.Util.visible
import com.skyd.imomoe.view.adapter.AnimeDownloadAdapter
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel
import kotlinx.android.synthetic.main.activity_anime_download.*
import kotlinx.android.synthetic.main.layout_circle_progress_text_tip_1.*
import kotlinx.android.synthetic.main.layout_image_text_tip_1.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*

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

        tv_toolbar_1_title.text = actionBarTitle

        viewModel = ViewModelProvider(this).get(AnimeDownloadViewModel::class.java)
        adapter = AnimeDownloadAdapter(this, viewModel.animeCoverList)

        iv_toolbar_1_back.setOnClickListener { finish() }

        rv_anime_download_activity.layoutManager = LinearLayoutManager(this)
        rv_anime_download_activity.adapter = adapter

        layout_anime_download_loading.inflate()
        tv_circle_progress_text_tip_1.text =
            getString(R.string.compute_md5_read_database)

        viewModel.mldAnimeCoverList.observe(this, {
            if (it) {
                layout_circle_progress_text_tip_1.gone()
                if (viewModel.animeCoverList.size == 0) {
                    if (layout_anime_download_no_download != null) {
                        layout_anime_download_no_download.inflate()
                        tv_image_text_tip_1.text = getString(R.string.no_download_video)
                    } else {
                        layout_image_text_tip_1.visible()
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })

        if (mode == 0) viewModel.getAnimeCover()
        else if (mode == 1) {
            layout_circle_progress_text_tip_1.visible()
            viewModel.getAnimeCoverEpisode(directoryName)
        }
    }
}