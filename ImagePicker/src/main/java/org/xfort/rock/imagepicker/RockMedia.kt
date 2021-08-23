package org.xfort.rock.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.xfort.rock.imagepicker.view.MediaActivity
import org.xfort.rock.imagepicker.view.MediaListFragment
import org.xfort.xrock.listener.OnDataCallback

/**
 ** Created by ZhangHuaXin on 2021/5/25.
 *
 * 相册图片、视频选取
 **/
class RockMedia {
    companion object {
        private const val ACTION_PICK_MEDIA = "org.xfort.intent.action.PICK_MEDIA"
        private const val CATEGORY_MEDIA = "org.xfort.intent.category.MEDIA"
    }

    var maxCount = 6
    var checkedUris: List<Uri>? = null
    var activityResultContract: MediaResultContract? = null

    /**
     * FileColumns.MEDIA_TYPE_**
     * 想要选取的类别，图片？视频？默认图片
     */
    var chooseMediaType: Set<Int>? = null
    lateinit var launcher: ActivityResultLauncher<Bundle>

    constructor(fragment: Fragment, resCallback: ActivityResultCallback<MediaResult>) {
        launcher = fragment.registerForActivityResult(
            MediaResultContract(), resCallback
        )
    }

    constructor(
        activity: FragmentActivity, resCallback: ActivityResultCallback<MediaResult>
    ) {
        launcher = activity.registerForActivityResult(
            MediaResultContract(), resCallback
        )
    }

    /**
     * 开始选取图片
     */
    fun start(requestCode: Int, args: Bundle?) {
        try {
            var dataArgs = args
            if (dataArgs == null) {
                dataArgs = bundleOf("max" to maxCount, "checkedUris" to checkedUris)
            } else {
                dataArgs.apply {
                    putInt("max", maxCount)
                    checkedUris?.let {
                        putParcelableArray("checkedUris", it.toTypedArray())
                    }
                }
            }
            dataArgs.putInt("requestCode", requestCode)
            launcher.launch(dataArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 相册图片的fragment
     */
    fun createMediasFragment(
        dataCallback: OnDataCallback<List<MediaItem>>
    ): MediaListFragment {
        val frag = MediaListFragment.newInstance(checkedUris, maxCount)
        frag.onDataCallback = dataCallback
        return frag
    }

    /**
     * 图片选择结果
     */
    class MediaResultContract() : ActivityResultContract<Bundle, MediaResult>() {

        var requeestCode = 0
        override fun createIntent(context: Context, input: Bundle?): Intent {
            requeestCode = input?.getInt("requestCode") ?: 0
            return Intent(
                context,
                MediaActivity::class.java
            ).apply { //                addCategory(CATEGORY_MEDIA)
                setDataAndType(Uri.parse("imagepicker://rock.xfort.org"), "image/picker")
                input?.let {
                    putExtras(input)
                }
            }
        }

        override fun parseResult(resultCode: Int, data: Intent?): MediaResult {
            var medias = if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableArrayListExtra<MediaItem>("data")?.toList()
            } else {
                null
            }
            return MediaResult(requeestCode, resultCode, medias, null)
        }
    }
}