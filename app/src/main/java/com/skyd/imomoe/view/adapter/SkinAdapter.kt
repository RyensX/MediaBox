package com.skyd.imomoe.view.adapter

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinBean
import com.skyd.imomoe.util.SkinCover1ViewHolder
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.gone
import com.skyd.imomoe.util.visible
import com.skyd.imomoe.view.activity.SkinActivity
import com.skyd.skin.SkinManager

class SkinAdapter(
    val activity: SkinActivity,
    private val dataList: List<SkinBean>
) : BaseRvAdapter(dataList) {
    private var selectedItem: SkinCover1ViewHolder? = null
    private var selectedItemPosition: Int = -1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = dataList[position]

        when (holder) {
            is SkinCover1ViewHolder -> {
                if (item.using) {
                    holder.ivSkinCover1Selected.visible()
                    selectedItem = holder
                    selectedItemPosition = position
                } else holder.ivSkinCover1Selected.gone()
                holder.tvSkinCover1Title.text = item.title
                item.cover.let { cover ->
                    if (cover is Int) {
                        holder.ivSkinCover1Cover.setImageDrawable(ColorDrawable(cover))
                    } else if (cover is String) {
                        holder.ivSkinCover1Cover.loadImage(cover)
                    }
                }
                holder.itemView.setOnClickListener {
                    if (item.using) return@setOnClickListener
                    if (item.skinSuffix == SkinManager.KEY_SKIN_DARK_SUFFIX && item.skinPath == "")
                        SkinManager.setDarkMode(SkinManager.DARK_MODE_YES)
                    else SkinManager.notifyListener(item.skinPath, item.skinSuffix)
                    holder.ivSkinCover1Selected.visible()
                    dataList[selectedItemPosition].using = false
                    selectedItem?.ivSkinCover1Selected?.gone()
                    selectedItem = holder
                    selectedItemPosition = position
                }
            }
            else -> {
                holder.itemView.visibility = View.GONE
                (App.context.resources.getString(R.string.unknown_view_holder) + position).showToast()
            }
        }
    }
}