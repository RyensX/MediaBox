package com.skyd.imomoe.util

import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.bean.ResponseDataType

/**
 * 根据GetDataEnum状态自动更新
 * @param type 刷新/加载更多/加载失败
 * @param deltaDataSet 新数据集
 * @param dataSet 构造adapter时传入的list
 */
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.smartNotifyDataSetChanged(
    type: ResponseDataType,
    deltaDataSet: MutableList<T>,
    dataSet: MutableList<T>
) {
    when (type) {
        ResponseDataType.REFRESH -> {
            val count = dataSet.size
            dataSet.clear()
            notifyItemRangeRemoved(0, count)
            dataSet.addAll(deltaDataSet)
            notifyItemRangeInserted(0, deltaDataSet.size)
        }
        ResponseDataType.LOAD_MORE -> {
            val index = dataSet.size
            dataSet.addAll(deltaDataSet)
            notifyItemRangeInserted(index, deltaDataSet.size)
        }
        ResponseDataType.FAILED -> {
            val count = dataSet.size
            dataSet.clear()
            notifyItemRangeRemoved(0, count)
        }
    }
}
