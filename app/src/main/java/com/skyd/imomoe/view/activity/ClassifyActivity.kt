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
import com.skyd.imomoe.util.Util.showToast
import com.skyd.imomoe.view.adapter.SearchAdapter
import com.skyd.imomoe.viewmodel.ClassifyViewModel
import kotlinx.android.synthetic.main.activity_classify.*
import kotlinx.android.synthetic.main.layout_toolbar_1.*


class ClassifyActivity : BaseActivity() {
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
        setContentView(R.layout.activity_classify)

        viewModel = ViewModelProvider(this).get(ClassifyViewModel::class.java)

        currentPartUrl = intent.getStringExtra("partUrl") ?: ""
        classifyTabTitle = intent.getStringExtra("classifyTabTitle") ?: ""
        classifyTitle = intent.getStringExtra("classifyTitle") ?: ""

        iv_toolbar_1_back.setOnClickListener { finish() }
        tv_toolbar_1_title.text = getString(R.string.anime_classify)
        tv_toolbar_1_title.isFocused = true

        spinnerAdapter = ArrayAdapter(this, R.layout.item_spinner_item_1)
        classifyTabAdapter = ClassifyTabAdapter(this, classifyTabList)
        classifyAdapter = SearchAdapter(this, viewModel.classifyList)

        srl_classify_activity.setColorSchemeResources(R.color.main_color)
        srl_classify_activity.setOnRefreshListener {
            //避免刷新间隔太短
            if (System.currentTimeMillis() - lastRefreshTime > 500) {
                lastRefreshTime = System.currentTimeMillis()
                viewModel.getClassifyData(currentPartUrl)
            } else {
                srl_classify_activity.isRefreshing = false
            }
        }

        val layoutManager = GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false)
        rv_classify_activity_tab.layoutManager = layoutManager
        rv_classify_activity_tab.setHasFixedSize(true)
        rv_classify_activity_tab.adapter = classifyTabAdapter

        val layoutManager2 = LinearLayoutManager(this)
        rv_classify_activity.layoutManager = layoutManager2
        rv_classify_activity.setHasFixedSize(true)
        rv_classify_activity.adapter = classifyAdapter

        spinner_classify_activity.adapter = spinnerAdapter
        spinner_classify_activity.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                pos: Int, id: Long
            ) {
                classifyTabList.clear()
                classifyTabList.addAll(viewModel.classifyTabList[pos].classifyDataList)
                classifyTabAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewModel.mldClassifyTabList.observe(this, Observer {
            spinnerAdapter.clear()
            spinnerAdapter.addAll(it)
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
            }
        })

        viewModel.mldClassifyList.observe(this, Observer {
            classifyAdapter.notifyDataSetChanged()
            srl_classify_activity.isRefreshing = false
            tv_toolbar_1_title.text =
                if (classifyTabTitle.isEmpty()) "${getString(R.string.anime_classify)}  $classifyTitle"
                else "${getString(R.string.anime_classify)}  $classifyTabTitle：$classifyTitle"
        })

        viewModel.getClassifyTabData()

        if (currentPartUrl.isNotEmpty()) {
            tabSelected(currentPartUrl)
        }
    }

    private fun tabSelected(partUrl: String) {
        currentPartUrl = partUrl
        srl_classify_activity.isRefreshing = true
        viewModel.getClassifyData(partUrl)
    }

    class ClassifyTabAdapter(
        val activity: ClassifyActivity,
        private val dataList: List<ClassifyDataBean>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        class ClassifyTab1ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView = view.findViewById<TextView>(R.id.text_view_1)
        }

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

        override fun getItemCount(): Int = dataList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = dataList[position]

            when (holder) {
                is ClassifyTab1ViewHolder -> {
                    holder.textView.text = item.title
                    holder.itemView.setOnClickListener {
                        activity.classifyTabTitle = activity.spinnerAdapter.getItem(
                            activity.spinner_classify_activity.selectedItemPosition
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