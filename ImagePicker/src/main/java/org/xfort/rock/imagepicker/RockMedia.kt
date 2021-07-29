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

    constructor(fragment: Fragment, resCallback: ActivityResultCallback<List<MediaItem>>) {
        launcher = fragment.registerForActivityResult(
            MediaResultContract(maxCount, checkedUris), resCallback
        )
    }

    constructor(
        activity: FragmentActivity, resCallback: ActivityResultCallback<List<MediaItem>>
    ) {
        launcher = activity.registerForActivityResult(
            MediaResultContract(maxCount, checkedUris), resCallback
        )
    }

    fun start() {
        try {

            launcher.launch(bundleOf("max" to maxCount,"checkedUris" to checkedUris))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun createMediasFragment(
        dataCallback: OnDataCallback<List<MediaItem>>
    ): MediaListFragment {
        val frag = MediaListFragment.newInstance(checkedUris, maxCount)
        frag.onDataCallback = dataCallback
        return frag
    }

    class MediaResultContract(val maxCount: Int, val checkedUri: List<Uri>?) :
        ActivityResultContract<Bundle, List<MediaItem>>() {
        override fun createIntent(context: Context, input: Bundle?): Intent {
            return Intent(ACTION_PICK_MEDIA).apply { //                addCategory(CATEGORY_MEDIA)
                setDataAndType(Uri.parse("imagepicker://rock.xfort.org"), "image/picker")
                putExtra("max", maxCount)
                checkedUri?.let {
                    putParcelableArrayListExtra("checkedUris", ArrayList(it))
                }
                input?.let {
                    putExtras(input)
                }
            }
        }

        override fun parseResult(resultCode: Int, data: Intent?): List<MediaItem> {
            return if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableArrayListExtra<MediaItem>("data")?.toList() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }
}