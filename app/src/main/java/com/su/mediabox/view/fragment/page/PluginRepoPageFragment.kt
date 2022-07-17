package com.su.mediabox.view.fragment.page

import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.su.mediabox.R
import com.su.mediabox.model.PreviewPluginInfo
import com.su.mediabox.config.Const
import com.su.mediabox.databinding.PagePluginRepoBinding
import com.su.mediabox.net.RetrofitManager
import com.su.mediabox.net.service.PluginService
import com.su.mediabox.plugin.PluginManager
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.data.SimpleTextData
import com.su.mediabox.pluginapi.util.UIUtil.dp
import com.su.mediabox.util.Util
import com.su.mediabox.util.getFirstItemDecorationBy
import com.su.mediabox.util.logD
import com.su.mediabox.util.showToast
import com.su.mediabox.view.adapter.type.*
import com.su.mediabox.view.fragment.BaseViewBindingFragment
import com.su.mediabox.view.viewcomponents.inner.PreviewPluginInfoViewHolder
import com.su.mediabox.view.viewcomponents.SimpleTextViewHolder
import com.su.mediabox.viewmodel.PageLoadViewModel

class PluginRepoPageFragment : BaseViewBindingFragment<PagePluginRepoBinding>(),
    PageLoadViewModel.LoadData {

    private val emptyView by lazy(LazyThreadSafetyMode.NONE) {
        SimpleTextData(requireContext().getString(R.string.plugin_repo_load_error)).apply {
            val padding = 8.dp
            paddingLeft = padding
            paddingTop = padding
            paddingRight = padding
            paddingBottom = padding
            spanSize = Constant.DEFAULT_SPAN_COUNT
        }
    }

    private val api = RetrofitManager.get().create(PluginService::class.java)

    override fun buildViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        PagePluginRepoBinding.inflate(inflater)

    override fun pagerInit() {
        setHasOptionsMenu(true)

        mBinding.customDataList.linear().initTypeList(
            DataViewMapList()
                .registerDataViewMap<PreviewPluginInfo, PreviewPluginInfoViewHolder>()
                .registerDataViewMap<SimpleTextData, SimpleTextViewHolder>()
        ) {
            emptyData = emptyView
        }

        pageLoadViewModel.loadDataFun = this

        pageLoadViewModel.loadState.observe(this) {
            when (it) {
                is PageLoadViewModel.LoadState.FAILED -> loadFailed(it.throwable)
                is PageLoadViewModel.LoadState.SUCCESS -> loadSuccess(it)
                is PageLoadViewModel.LoadState.LOADING -> loading()
            }
        }

        mBinding.customDataSwipe.apply {
            //刷新
            setOnRefreshListener {
                if (pageLoadViewModel.loadState.value !is PageLoadViewModel.LoadState.LOADING)
                    pageLoadViewModel.reLoadData()
            }
            //载入更多
            setOnLoadMoreListener {
                if (pageLoadViewModel.loadState.value is PageLoadViewModel.LoadState.SUCCESS)
                    pageLoadViewModel.loadData()
            }
        }

        PluginManager.pluginLiveData.observe(this) {
            if (pageLoadViewModel.loadState.value !is PageLoadViewModel.LoadState.LOADING)
                pageLoadViewModel.reLoadData()
        }
    }


    private val pageLoadViewModel by viewModels<PageLoadViewModel>()

    private fun loadSuccess(loadState: PageLoadViewModel.LoadState.SUCCESS) {
        mBinding.customDataList.apply {
            val dy = getFirstItemDecorationBy<DynamicGridItemDecoration>()
            if (loadState.data.isNullOrEmpty()) {
                if (dy == null || layoutManager == null)
                    dynamicGrid()
            } else {
                if (dy != null)
                    removeItemDecoration(dy)
                if (layoutManager == null)
                    linear()
            }
            typeAdapter().submitList(loadState.data) {
                if (!loadState.data.isNullOrEmpty() && loadState.isLoadEmptyData) {
                    getString(R.string.no_more_info).showToast()
                }
                postDelayed({
                    mBinding.customDataSwipe.closeHeaderOrFooter()
                }, 100)
            }
        }
    }

    private fun loadFailed(throwable: Throwable?) {
        mBinding.customDataSwipe.closeHeaderOrFooter()
        throwable?.apply {
            printStackTrace()
            message?.let { "加载错误：$it" }?.showToast(Toast.LENGTH_LONG)
        } ?: "加载错误".showToast()
    }

    private fun loading() {
        mBinding.customDataSwipe.autoRefresh()
    }

    //TODO 待重新实现进而分离出Activity
    //混合本地插件信息分类为 已安装/可更新/可下载
    override suspend fun load(page: Int): List<Any>? {
        //因类别分析需要，所以不再分页加载插件信息
        if (page > 1)
            return null
        val repoData =
            Util.withoutExceptionGet(true) { api.fetchRepositoryPluginPreviewInfo() } ?: return null
        val localPluginData = PluginManager.pluginLiveData.value ?: return null
        val combineData = mutableListOf<Any>()

        val installed = mutableListOf<Any>()
        val updatable = mutableListOf<Any>()
        val downloadable = mutableListOf<Any>()

        for (pp in repoData) {
            localPluginData.find { it.packageName == pp.packageName }?.also {
                //已安装
                if (//debug的无视版本
                    it.isExternalPlugin
                    || (pp.version == it.version && pp.apiVersion == it.apiVersion)
                ) {
                    pp.state = Const.Plugin.PLUGIN_STATE_OPEN
                    pp.mergeLocalData(it)
                    installed.add(pp)
                }
                //可更新
                else {
                    pp.state = Const.Plugin.PLUGIN_STATE_UPDATABLE
                    updatable.add(pp)
                }
            } ?: run {
                //可下载
                pp.state = Const.Plugin.PLUGIN_STATE_DOWNLOADABLE
                downloadable.add(pp)
            }
        }

        combineData.apply {
            if (updatable.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_updatable, updatable.size
                        )
                    )
                )
                addAll(updatable)
            }
            if (installed.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_installed, installed.size
                        )
                    )
                )
                addAll(installed)
            }
            if (downloadable.isNotEmpty()) {
                add(
                    getPluginCategoryText(
                        getString(
                            R.string.plugin_repo_title_template_downloadable, downloadable.size
                        )
                    )
                )
                addAll(downloadable)
            }
        }

        return combineData
    }

    private fun getPluginCategoryText(text: String) = SimpleTextData(text).apply {
        paddingLeft = 14.dp
        paddingTop = 16.dp
        paddingBottom = 6.dp
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.page_plugin_repo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_plugin_repo_help ->
                MaterialDialog(requireContext()).show {
                    title(res = R.string.menu_plugin_repo_help)
                    message(res = R.string.menu_plugin_repo_help_hint)
                    positiveButton(res = R.string.menu_plugin_repo_office) { Util.openBrowser(Const.Common.GITHUB_PLUGIN_REPO_OFFICE_URL) }
                    negativeButton(res = R.string.cancel) { dismiss() }
                }
            R.id.menu_plugin_repo -> Util.openBrowser(Const.Common.GITHUB_PLUGIN_REPO_URL)
        }
        return super.onOptionsItemSelected(item)
    }
}