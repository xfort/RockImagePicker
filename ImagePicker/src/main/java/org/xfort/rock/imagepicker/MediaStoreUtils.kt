package org.xfort.rock.imagepicker

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.*

/**
 ** Created by ZhangHuaXin on 2021/5/13.
 **/
object MediaStoreUtils {

    fun hasCamera(context: Context): Boolean {
        val pm: PackageManager = context.applicationContext.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun createImageUri(contentResolver: ContentResolver): Uri? {
        val fileTab = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val newImgValue = ContentValues()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = String.format("pic_%s.jpg", timeStamp)
        newImgValue.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
        return contentResolver.insert(fileTab, newImgValue)
    }
}