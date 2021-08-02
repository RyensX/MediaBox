package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.ClassifyDataBean
import com.skyd.imomoe.databinding.ActivityClassifyBinding
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.adapter.BaseRvAdapter
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.ClassifyViewModel


class ClassifyActivity : BaseActivity<ActivityClassifyBinding>() {
    private lateinit var viewModel: ClassifyViewModel
    private var lastRefreshTime: Long = System.currentTimeMillis()
    private lateinit var spinnerAdapter: ArrayAdapter<ClassifyBean>
    private lateinit var classifyTabAdapter: ClassifyTabAdapter
    private val classifyTabList: MutableList<ClassifyDataBean> = ArrayList()
    private lateinit var classifyAdapter: SearchAdapter
    private var classifyTabTitle: String = ""       //如 地区
    private var classifyTitle: String = ""          //如 大陆
    private var currentPartUrl: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ClassifyViewModel::class.java)

        currentPartUrl = intent.getStringExtra("partUrl") ?: ""
        classifyTabTitle = intent.getStringExtra("classifyTabTitle") ?: ""
        classifyTitle = intent.getStringExtra("classifyTitle") ?: ""

        mBinding.llClassifyActivityToolbar.run {
            ivToolbar1Back.setOnClickListener { finish() }
            tvToolbar1Title.text = getString(R.string.anime_classify)
            tvToolbar1Title.isFocused = true
        }

        spinnerAdapter = ArrayAdapter(this, R.layout.item_spinner_item_1)
        classifyTabAdapter = ClassifyTabAdapter(this, classifyTabList)
        classifyAdapter = SearchAdapter(this, viewModel.classifyList)

        mBinding.run {
            srlClassifyActivity.setOnRefreshListener {
                //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    if (viewModel.mldClassifyTabList.value == true)
                        viewModel.getClassifyData(currentPartUrl)
                    else viewModel.getClassifyTabData()
                } else {
                    srlClassifyActivity.finishRefresh()
                }
            }
            srlClassifyActivity.setOnLoadMoreListener {
                viewModel.pageNumberBean?.let {
                    viewModel.getClassifyData(it.actionUrl, isRefresh = false)
                    return@setOnLoadMoreListener
                }
                mBinding.srlClassifyActivity.finishLoadMore()
                getString(R.string.no_more_info).showToast()
            }

            rvClassifyActivityTab.layoutManager =
                GridLayoutManager(this@ClassifyActivity, 2, GridLayoutManager.HORIZONTAL, false)
            rvClassifyActivityTab.setHasFixedSize(true)
            rvClassifyActivityTab.adapter = classifyTabAdapter

            rvClassifyActivity.layoutManager = LinearLayoutManager(this@ClassifyActivity)
            rvClassifyActivity.setHasFixedSize(true)
            rvClassifyActivity.adapter = classifyAdapter

            spinnerClassifyActivity.adapter = spinnerAdapter
            spinnerClassifyActivity.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    pos: Int, id: Long
                ) {
                    if (view is TextView) view.setTextColor(getResColor(R.color.foreground_main_color_2_skin))
                    classifyTabList.clear()
                    classifyTabList.addAll(viewModel.classifyTabList[pos].classifyDataList)
                    classifyTabAdapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        viewModel.mldClassifyTabList.observe(this, Observer {
            if (it) {
                spinnerAdapter.clear()
                spinnerAdapter.addAll(viewModel.classifyTabList)
                spinnerAdapter.notifyDataSetChanged()

                //自动选中第一个
                if (currentPartUrl.isEmpty() && viewModel.classifyTabList.size > 0 &&
                    viewModel.classifyTabList[0].classifyDataList.size > 0
                ) {
                    val firstItem = viewModel.classifyTabList[0].classifyDataList[0]
                    currentPartUrl = firstItem.actionUrl
                    classifyTabTitle = viewModel.classifyTabList[0].toString()
                    classifyTitle = firstItem.title
                    tabSelected(currentPartUrl)
                } else {
                    viewModel.classifyTabList.forEachIndexed { index, classifyBean ->
                        classifyBean.classifyDataList.forEach { item ->
                            if (item.actionUrl == currentPartUrl) {
                                mBinding.spinnerClassifyActivity.setSelection(index, true)
                                classifyTabTitle = classifyBean.name
                                classifyTitle = item.title
                                tabSelected(currentPartUrl)
                            }
                        }
                    }
                }
            } else {
                mBinding.srlClassifyActivity.finishRefresh()
            }
        })

        viewModel.mldClassifyList.observe(this, Observer {
            mBinding.srlClassifyActivity.closeHeaderOrFooter()
            viewModel.isRequesting = false
            if (it == 0) {
                mBinding.llClassifyActivityToolbar.tvToolbar1Title.text =
                    if (classifyTabTitle.isEmpty()) "${getString(R.string.anime_classify)}  $classifyTitle"
                    else "${getString(R.string.anime_classify)}  $classifyTabTitle：$classifyTitle"
                classifyAdapter.notifyDataSetChanged()
            } else if (it == 1) {
                val pair = viewModel.newPageIndex
                if (pair != null) {
                    classifyAdapter.notifyItemRangeInserted(pair.first, pair.second)
                } else classifyAdapter.notifyDataSetChanged()
            } else if (it == -1) {
                classifyAdapter.notifyDataSetChanged()
            }
        })

        viewModel.getClassifyTabData()

        if (currentPartUrl.isNotEmpty()) {
            tabSelected(currentPartUrl)
        }
    }

    override fun getBinding(): ActivityClassifyBinding =
        ActivityClassifyBinding.inflate(layoutInflater)

    private fun tabSelected(partUrl: String) {
        currentPartUrl = partUrl
        mBinding.srlClassifyActivity.autoRefresh()
    }

    class ClassifyTabAdapter(
        val activity: ClassifyActivity,
        private val dataList: List<ClassifyDataBean>
    ) : BaseRvAdapter(dataList) {

        class ClassifyTab1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView = view.findViewById<TextView>(R.id.text_view_1)
        }

        override fun getItemViewType(position: Int): Int = 0

        fun getItem(position: Int): ClassifyDataBean {
            return dataList[position]
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            return ClassifyTab1ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_text_view_1, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            when (holder) {
                is ClassifyTab1ViewHolder -> {
                    holder.textView.text = item.title
                    holder.itemView.setOnClickListener {
                        activity.classifyTabTitle = activity.spinnerAdapter.getItem(
                            activity.mBinding.spinnerClassifyActivity.selectedItemPosition
                        ).toString()
                        activity.classifyTitle = item.title
                        activity.tabSelected(item.actionUrl)
                    }
                }
                else -> {
                    holder.itemView.visibility = View.GONE
                    (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
                }
            }
        }
    }
}
