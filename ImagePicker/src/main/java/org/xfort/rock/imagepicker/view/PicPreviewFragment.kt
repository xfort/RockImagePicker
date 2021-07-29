package org.xfort.rock.imagepicker.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.chrisbanes.photoview.PhotoView
import org.xfort.rock.imagepicker.R

/**
 ** Created by ZhangHuaXin on 2021/5/17.
 * 大图预览
 **/
class PicPreviewFragment : DialogFragment() {
    private lateinit var closeView: View
    private lateinit var photoView: PhotoView

    companion object {
        fun newInstance(imgUri: Uri): PicPreviewFragment {
            val frag = PicPreviewFragment()
            frag.arguments = bundleOf("img" to imgUri)
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pic_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeView = view.findViewById(R.id.close)
        photoView = view.findViewById(R.id.photoview)
        closeView.setOnClickListener { dismiss() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val imgUri = arguments?.getParcelable<Uri>("img")
        imgUri?.let {
            photoView.setImageURI(it)
        }
    }
}