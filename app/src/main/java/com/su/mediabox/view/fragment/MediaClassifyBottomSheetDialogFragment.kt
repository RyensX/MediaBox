package com.su.mediabox.view.fragment

import android.app.Dialog
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.su.mediabox.databinding.BottomSheetMediaClassifyBinding
import com.su.mediabox.databinding.ItemClassifyCategoryBinding
import com.su.mediabox.databinding.ItemClassifyItemBinding
import com.su.mediabox.databinding.ViewComponentTextBinding
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.ClassifyAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.ClassifyItemData
import com.su.mediabox.pluginapi.v2.been.GridItemData
import com.su.mediabox.pluginapi.v2.been.TextData
import com.su.mediabox.util.setOnClickListener
import com.su.mediabox.view.adapter.type.*

class MediaClassifyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetMediaClassifyBinding
    var data: List<GridItemData>? = null
    var currentClassifyAction: ClassifyAction? = null

    //载入分类项数据
    var loadClassify: ((ClassifyAction) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            binding = BottomSheetMediaClassifyBinding.inflate(layoutInflater)
            init()
            setContentView(binding.root)
        }
    }

    fun show(fragmentManager: FragmentManager) {
        show(fragmentManager, javaClass.simpleName)
    }

    override fun onStart() {
        super.onStart()
        //通过tag标记当前分类
        binding.bsClassifyList.typeAdapter().setTag(currentClassifyAction)

        data?.also {
            binding.bsClassifyList.submitList(it)
            return
        }
    }

    private fun init() {
        binding.bsClassifyList.grid(6)
            .apply {
                (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int) =
                            data?.get(position)?.spanSize ?: GridItemData.DEFAULT_SPAN_SIZE
                    }
            }
            .initTypeList(
                DataViewMapList()
                    .registerDataViewMap<ClassifyCategoryData, ClassifyCategoryViewHolder>()
                    .registerDataViewMap<ClassifyItemData, ClassifyItemViewHolder>()
            ) {
                addViewHolderClickListener<ClassifyItemViewHolder> { pos ->
                    bindingTypeAdapter.getData<ClassifyItemData>(pos)?.action?.also {
                        if (it is ClassifyAction)
                            loadClassify?.invoke(it)
                    }
                }
            }
    }

    class ClassifyCategoryData(val category: String) : GridItemData(6)

    class ClassifyCategoryViewHolder private constructor(private val binding: ItemClassifyCategoryBinding) :
        TypeViewHolder<ClassifyCategoryData>(binding.root) {

        constructor(parent: ViewGroup) : this(
            ItemClassifyCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBind(data: ClassifyCategoryData) {
            binding.root.text = data.category
        }
    }

    class ClassifyItemViewHolder private constructor(private val binding: ItemClassifyItemBinding) :
        TypeViewHolder<ClassifyItemData>(binding.root) {

        constructor(parent: ViewGroup) : this(
            ItemClassifyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBind(data: ClassifyItemData) {
            //TODO 选中高亮（tag标记了当前分类）
            val action = data.action
            if (action is ClassifyAction)
                binding.root.text = action.classify
        }
    }
}