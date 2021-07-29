package org.xfort.rock.imagepicker

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore

/**
 ** Created by ZhangHuaXin on 2021/5/10.
 **/

object UriUtils {

    fun contentUri(id: Long, mimeType: String): Uri {
        var contentUri = when {
            mimeType.startsWith("image") -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            mimeType.startsWith("video") -> {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
            else -> {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            }
        }
        contentUri = ContentUris.withAppendedId(contentUri, id)
        return contentUri
    }
}