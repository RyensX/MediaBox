package com.skyd.imomoe.view.component.widget.everydayanime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.google.gson.Gson
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.EverydayAnimeWidgetModel
import com.skyd.imomoe.model.interfaces.IEverydayAnimeWidgetModel
import com.skyd.imomoe.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EverydayAnimeService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return EverydayAnimeRemoteViewsFactory(this.applicationContext, intent)
    }
}

internal class EverydayAnimeRemoteViewsFactory(
    private val mContext: Context,
    intent: Intent
) : RemoteViewsFactory {
    private val model = DataSourceManager.create(IEverydayAnimeWidgetModel::class.java)
        ?: EverydayAnimeWidgetModel()
    private val mWidgetItems: MutableList<AnimeCoverBean> = ArrayList()

    override fun onCreate() {

    }

    override fun onDestroy() {
        mWidgetItems.clear()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val item = mWidgetItems[position]
        val rv = RemoteViews(mContext.packageName, R.layout.item_anime_cover_10)
        rv.setTextViewText(R.id.tv_anime_cover_10_title, item.title)
        rv.setTextViewText(R.id.tv_anime_cover_10_episode, item.episodeClickable?.title)

        val extras = Bundle()
        // 传Serializable的对象获取为null，原因未知，只能传转成json之后的了
        extras.putString(EverydayAnimeWidgetProvider.ITEM, Gson().toJson(item))
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.item_anime_cover_10, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        val list = getEverydayAnimeData()
        if (list.size != 7) return
        mWidgetItems.clear()
        mWidgetItems.addAll(
            list[Util.getRealDayOfWeek(
                Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK)
            ) - 1].toMutableList()
        )
    }

    private fun getEverydayAnimeData(): MutableList<List<AnimeCoverBean>> {
        return model.getEverydayAnimeData()
    }
}