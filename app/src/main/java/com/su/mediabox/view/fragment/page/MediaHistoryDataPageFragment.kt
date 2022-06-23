package com.su.mediabox.view.fragment.page

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.App
import com.su.mediabox.R
import com.su.mediabox.bean.MediaHistory
import com.su.mediabox.database.getAppDataBase
import com.su.mediabox.databinding.ItemMediaHistoryBinding
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.appCoroutineScope
import com.su.mediabox.util.coil.CoilUtil.loadImage
import com.su.mediabox.util.displayOnlyIfHasData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.util.setOnLongClickListener
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseFragment
import com.su.mediabox.viewmodel.MediaDataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MediaHistoryDataPageFragment : BaseFragment() {

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
                DataViewMapList().registerDataViewMap<MediaHistory, HistoryViewHolder>(),
                HistoryDiff
            ) {
                viewModel.history.observe(this@MediaHistoryDataPageFragment) {
                    submitList(it) {
                        //TODO
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.page_media_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_media_history_clear ->
                lifecycleScope.launch(Dispatchers.Main) {
                    MaterialDialog(requireContext()).show {
                        title(res = R.string.media_data_page_history_clear_title)
                        message(
                            text = getString(R.string.media_data_page_history_clea_desc,
                                withContext(Dispatchers.IO) {
                                    getAppDataBase().historyDao().getHistoryCount()
                                })
                        )
                        positiveButton(res = R.string.ok) {
                            appCoroutineScope.launch(Dispatchers.IO) {
                                getAppDataBase().historyDao().deleteAllHistory()
                            }
                        }
                        negativeButton(res = R.string.cancel) { dismiss() }
                    }
                }
        }
        return super.onOptionsItemSelected(item)
    }

    class HistoryViewHolder private constructor(private val binding: ItemMediaHistoryBinding) :
        TypeViewHolder<MediaHistory>(binding.root) {

        companion object {
            private val lastTimeFormat =
                java.text.SimpleDateFormat(
                    App.context.getString(R.string.media_data_history_last_time_format),
                    Locale.CHINA
                )
        }

        private var dataMedia: MediaHistory? = null

        constructor(parent: ViewGroup) : this(
            ItemMediaHistoryBinding.inflate(
                LayoutInflater.from(App.context),
                parent, false
            )
        ) {
            setOnClickListener(binding.root) {
                dataMedia?.apply {
                    DetailAction.obtain(mediaUrl).go(bindingContext)
                }
            }
            setOnLongClickListener(binding.root) {
                dataMedia?.apply {
                    PopupMenu(bindingContext, binding.vcVideoLinearItemLastTime).apply {
                        menu.add(R.string.delete)
                        setOnMenuItemClickListener {
                            appCoroutineScope.launch {
                                getAppDataBase().historyDao().deleteHistory(mediaUrl)
                            }
                            true
                        }
                        show()
                    }
                }
                true
            }
        }

        override fun onBind(data: MediaHistory) {
            super.onBind(data)
            this.dataMedia = data
            binding.apply {
                vcVideoLinearItemCover.displayOnlyIfHasData(data.cover) { loadImage(it) }
                vcVideoLinearItemName.displayOnlyIfHasData(data.mediaTitle) { text = it }
                vcVideoLinearItemEpisodeTitle.displayOnlyIfHasData(data.lastEpisodeTitle) {
                    text = it
                }
                vcVideoLinearItemLastTime.displayOnlyIfHasData(lastTimeFormat.format(data.lastViewTime)) {
                    text = it
                }

            }
        }

    }

    object HistoryDiff : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(
            oldItem: Any,
            newItem: Any
        ) =
            oldItem is MediaHistory && newItem is MediaHistory && oldItem.mediaUrl == newItem.mediaUrl

        override fun areContentsTheSame(
            oldItem: Any,
            newItem: Any
        ) = oldItem is MediaHistory && newItem is MediaHistory && oldItem.cover == newItem.cover &&
                oldItem.mediaTitle == newItem.mediaTitle &&
                oldItem.lastEpisodeTitle == newItem.lastEpisodeTitle &&
                oldItem.lastEpisodeUrl == newItem.lastEpisodeUrl
    }

}