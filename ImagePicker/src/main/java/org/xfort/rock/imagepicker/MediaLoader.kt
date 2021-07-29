package org.xfort.rock.imagepicker

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.loader.content.CursorLoader

/**
 ** Created by ZhangHuaXin on 2021/5/7.
 *  读取 媒体文件列表
 **/
class MediaLoader(
    val life: Lifecycle,
    context: Context,
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?
) : CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder), LifecycleObserver {
    companion object {
        /**
         * 图片、视频类别
         */
        val PROJECTION_MEDIA = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.ORIENTATION
        )

        fun newInstance(context: Context, life: Lifecycle): MediaLoader {
            var contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            var selection =
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + MediaStore.Files.FileColumns.SIZE + ">0 And " + MediaStore.Files.FileColumns.MIME_TYPE + "!=?"
            var selectionsArgs =
                arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), "image/gif")
            var sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
            return MediaLoader(
                life,
                context,
                contentUri,
                PROJECTION_MEDIA,
                selection,
                selectionsArgs,
                sortOrder
            )
        }
    }


    init {
        life.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
//        cancelLoadInBackground()
    }
}