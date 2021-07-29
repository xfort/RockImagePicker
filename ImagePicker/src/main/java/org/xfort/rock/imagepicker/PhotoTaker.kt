package org.xfort.rock.imagepicker

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 ** Created by ZhangHuaXin on 2021/5/13.
 *  调用系统相机拍照
 **/
class PhotoTaker {
    var currentPhotoUri: Uri? = null

    fun startCapture(context: FragmentActivity) {
        currentPhotoUri = null
        val intent = newIntent(context.contentResolver)
        context.startActivityForResult(intent, REQUEST_CAPTURE)
    }

    fun startCapture(fragment: Fragment) {
        currentPhotoUri = null
        val intent = newIntent(fragment.requireActivity().contentResolver)
        fragment.startActivityForResult(intent, REQUEST_CAPTURE)
    }

    private fun newIntent(cr: ContentResolver): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentPhotoUri = MediaStoreUtils.createImageUri(cr)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return intent
    }

    fun onActivityResult(resCode: Int, data: Intent?) {
        if (resCode != Activity.RESULT_OK) {
            currentPhotoUri = null
        }
    }

    companion object {
        const val REQUEST_CAPTURE = 1
    }
}