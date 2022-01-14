package com.skyd.imomoe.util.dlna.dms

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore.*
import org.fourthline.cling.support.model.PersonWithRole
import org.fourthline.cling.support.model.Res
import org.fourthline.cling.support.model.item.ImageItem
import org.fourthline.cling.support.model.item.Item
import org.fourthline.cling.support.model.item.Movie
import org.fourthline.cling.support.model.item.MusicTrack
import java.io.File
import java.util.*

internal class MediaContentDao(private val mBaseUrl: String) : IMediaContentDao {
    @SuppressLint("Range")
    override fun getImageItems(context: Context): List<Item> {
        val items: MutableList<Item> = ArrayList()
        context.contentResolver.query(
            Images.Media.EXTERNAL_CONTENT_URI,
            CONTENT_IMAGE_COLUMNS,
            null,
            null,
            null
        ).use { cursor ->
            if (cursor == null) return items
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[0])).toString()
                val title = cursor.getString(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[1]))
                val creator = cursor.getString(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[2]))
                val data = cursor.getString(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[3]))
                val mimeType = cursor.getString(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[4]))
                val size = cursor.getLong(cursor.getColumnIndex(CONTENT_IMAGE_COLUMNS[5]))
                val url = mBaseUrl + File.separator + data
                val res = Res(mimeType, size, "", null, url)
                val imageItem = ImageItem(id, MediaItem.IMAGE_ID, title, creator, res)
                items.add(imageItem)
            }
        }
        return items
    }

    @SuppressLint("Range")
    override fun getAudioItems(context: Context): List<Item> {
        val items: MutableList<Item> = ArrayList()
        context.contentResolver.query(
            Audio.Media.EXTERNAL_CONTENT_URI,
            CONTENT_AUDIO_COLUMNS,
            null,
            null,
            null
        ).use { cursor ->
            if (cursor == null) return items
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[0])).toString()
                val title = cursor.getString(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[1]))
                val creator = cursor.getString(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[2]))
                val data = cursor.getString(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[3]))
                val mimeType = cursor.getString(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[4]))
                val size = cursor.getLong(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[5]))
                val album = cursor.getString(cursor.getColumnIndex(CONTENT_AUDIO_COLUMNS[7]))
                val url = mBaseUrl + File.separator + data
                val res = Res(mimeType, size, "", null, url)
                val musicTrack = MusicTrack(
                    id,
                    MediaItem.AUDIO_ID,
                    title,
                    creator,
                    album,
                    PersonWithRole(creator),
                    res
                )
                items.add(musicTrack)
            }
        }
        return items
    }

    @SuppressLint("Range")
    override fun getVideoItems(context: Context): List<Item> {
        val items: MutableList<Item> = ArrayList()
        context.contentResolver.query(
            Video.Media.EXTERNAL_CONTENT_URI,
            CONTENT_VIDEO_COLUMNS,
            null,
            null,
            null
        ).use { cur ->
            if (cur == null) return items
            cur.moveToFirst()
            while (cur.moveToNext()) {
                val id = cur.getInt(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[0])).toString()
                val title = cur.getString(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[1]))
                val creator = cur.getString(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[2]))
                val data = cur.getString(cur.getColumnIndexOrThrow(CONTENT_VIDEO_COLUMNS[3]))
                val mimeType = cur.getString(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[4]))
                val size = cur.getLong(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[5]))
                val resolution = cur.getString(cur.getColumnIndex(CONTENT_VIDEO_COLUMNS[7]))
                val url = mBaseUrl + File.separator + data
                val res = Res(mimeType, size, "", null, url)
                res.resolution = resolution
                val movie = Movie(id, MediaItem.VIDEO_ID, title, creator, res)
                items.add(movie)
            }
        }
        return items
    }

    companion object {
        private val CONTENT_IMAGE_COLUMNS = arrayOf(
            MediaColumns._ID,
            MediaColumns.TITLE,
            MediaColumns.ARTIST,
            MediaColumns.DATA,
            MediaColumns.MIME_TYPE,
            MediaColumns.SIZE
        )
        private val CONTENT_AUDIO_COLUMNS = arrayOf(
            MediaColumns._ID,
            MediaColumns.TITLE,
            MediaColumns.ARTIST,
            MediaColumns.DATA,
            MediaColumns.MIME_TYPE,
            MediaColumns.SIZE,
            MediaColumns.ALBUM
        )
        private val CONTENT_VIDEO_COLUMNS = arrayOf(
            MediaColumns._ID,
            MediaColumns.TITLE,
            MediaColumns.ARTIST,
            MediaColumns.DATA,
            MediaColumns.MIME_TYPE,
            MediaColumns.SIZE,
            MediaColumns.RESOLUTION
        )
    }

}