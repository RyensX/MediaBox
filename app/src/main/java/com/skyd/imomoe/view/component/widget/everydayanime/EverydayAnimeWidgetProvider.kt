package com.skyd.imomoe.view.component.widget.everydayanime

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.google.gson.Gson
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.RouteProcessor
import com.skyd.imomoe.util.Util.getWeekday
import com.skyd.imomoe.util.showToast
import java.util.*


class EverydayAnimeWidgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == VIEW_CLICK_ACTION) {
            val item = Gson().fromJson(intent.getStringExtra(ITEM), AnimeCoverBean::class.java)
            if (item.episodeClickable?.actionUrl.equals(item.actionUrl))
                startPlayActivity(context, item.episodeClickable?.actionUrl)
            else startPlayActivity(context, item.episodeClickable?.actionUrl + item.actionUrl)
        } else if (intent.action == REFRESH_ACTION) {
            val mgr: AppWidgetManager = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, EverydayAnimeWidgetProvider::class.java)
            val rv = RemoteViews(context.packageName, R.layout.widget_everyday_anime)
            rv.setTextViewText(
                R.id.tv_widget_everyday_anime_title, getWeekday(
                    Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK)
                )
            )
            mgr.updateAppWidget(mgr.getAppWidgetIds(cn), rv)
            mgr.notifyAppWidgetViewDataChanged(
                mgr.getAppWidgetIds(cn),
                R.id.lv_widget_everyday_anime
            )
            App.context.getString(R.string.update_widget).showToast()
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // update each of the widgets with the remote adapter
        for (i in appWidgetIds.indices) {
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, EverydayAnimeService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val rv = RemoteViews(context.packageName, R.layout.widget_everyday_anime)
            rv.setTextViewText(
                R.id.tv_widget_everyday_anime_title, getWeekday(
                    Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK)
                )
            )
            rv.setRemoteAdapter(R.id.lv_widget_everyday_anime, intent)
            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
//            rv.setEmptyView(R.id.stack_view, R.id.empty_view)
            Intent(context, EverydayAnimeWidgetProvider::class.java).apply {
                action = REFRESH_ACTION
                rv.setOnClickPendingIntent(
                    R.id.iv_widget_everyday_anime_refresh,
                    PendingIntent.getBroadcast(
                        context, 0, this,
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT
                        else PendingIntent.FLAG_MUTABLE
                    )
                )
            }
            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            val viewClickIntent = Intent(context, EverydayAnimeWidgetProvider::class.java)
            viewClickIntent.action = VIEW_CLICK_ACTION
            viewClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, viewClickIntent,
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT
                else PendingIntent.FLAG_MUTABLE
            )
            rv.setPendingIntentTemplate(R.id.lv_widget_everyday_anime, toastPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv)
            appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetIds,
                R.id.lv_widget_everyday_anime
            )
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun startPlayActivity(context: Context, actionUrl: String?) {
        actionUrl ?: return
        (DataSourceManager.getRouterProcessor() ?: RouteProcessor()).process(context, actionUrl)
    }

    companion object {
        const val VIEW_CLICK_ACTION = "com.skyd.imomoe.widget.VIEW_CLICK_ACTION"
        const val REFRESH_ACTION = "com.skyd.imomoe.widget.REFRESH_ACTION"
        const val ITEM = "com.skyd.imomoe.widget.ITEM"
    }
}