package com.su.mediabox.view.fragment.page

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.database.entity.MediaUpdateRecord
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.database.getOfflineDatabase
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.appCoroutineScope
import com.su.mediabox.util.logD
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.view.viewcomponents.inner.MediaUpdateRecordViewHolder
import com.su.mediabox.viewmodel.MediaDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaUpdateDataPageFragment : BaseFragment() {

    private lateinit var dataList: RecyclerView
    private val viewModel by activityViewModels<MediaDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = RecyclerView(inflater.context).also { dataList = it }

    override fun pagerInit() {
        setHasOptionsMenu(true)

        dataList
            .grid(1)
            .apply {
                addItemDecoration(DynamicGridItemDecoration(10.dp))
            }
            .initTypeList(
                DataViewMapList().registerDataViewMap<MediaUpdateRecord, MediaUpdateRecordViewHolder>()
            ) {
                viewModel.update.asLiveData().observe(this@MediaUpdateDataPageFragment) {
                    logD("媒体更新记录", "数量:${it.size}")
                    submitList(it)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.page_media_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_media_update_all_confirmed ->
                lifecycleScope.launch(Dispatchers.IO) {
                    getOfflineDatabase().mediaUpdateDao().confirmedAll()
                }
            R.id.menu_media_update_clear ->
                lifecycleScope.launch(Dispatchers.Main) {
                    MaterialDialog(requireContext()).show {
                        title(res = R.string.media_data_page_clear_title)
                        message(
                            text = getString(R.string.media_data_page_clear_desc,
                                withContext(Dispatchers.IO) {
                                    getOfflineDatabase().mediaUpdateDao()
                                        .getMediaUpdateRecordCount()
                                })
                        )
                        positiveButton(res = R.string.ok) {
                            appCoroutineScope.launch(Dispatchers.IO) {
                                getOfflineDatabase().mediaUpdateDao().deleteAll()
                            }
                        }
                        negativeButton(res = R.string.cancel) { dismiss() }
                    }
                }
        }
        return super.onOptionsItemSelected(item)
    }

}