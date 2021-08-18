package org.xfort.rock.imagepicker

import android.os.Bundle

/**
 ** Created by ZhangHuaXin on 2021/8/18.
 * 选择图片的结果
 **/
data class MediaResult(
    val requestCode: Int, val resultCode: Int, val medias: List<MediaItem>?, val data: Bundle?
) {}