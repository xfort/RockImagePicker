package org.xfort.xapp

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import org.xfort.rock.imagepicker.RockMedia
import org.xfort.xapp.databinding.ActivityMainBinding

/**
 ** Created by ZhangHuaXin on 2021/7/29.
 **/
class MainActivity : AppCompatActivity() {
    val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    lateinit var imagePicker: RockMedia
    var checkedImgs = mutableListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.imagePicker.setOnClickListener {
            imagePicker.maxCount = 9
            imagePicker.checkedUris = checkedImgs
            imagePicker.start(1, null)
        }
        val imgWidth = (resources.getDimension(R.dimen.meta) * 90).toInt()

        imagePicker = RockMedia(this) { data ->
            var result = data.medias
            if (result.isNullOrEmpty()) {
                return@RockMedia
            }
            viewBinding.imagesInfo.text = ""
            var ids = IntArray(result.size)
            checkedImgs.clear()
            viewBinding.images.referencedIds?.forEach {
                viewBinding.root.removeView(viewBinding.root.findViewById(it))
            }
            result.forEachIndexed { index, it ->
                viewBinding.imagesInfo.append("${it.id}_${it.name}_${it.contentUri?.toString()}\n\n")
                val itemImg = ImageView(this)
                itemImg.scaleType = ImageView.ScaleType.CENTER_CROP
                itemImg.id = ViewCompat.generateViewId()
                viewBinding.root.addView(itemImg, imgWidth, imgWidth)
                itemImg.setImageURI(it.contentUri)
                ids[index] = itemImg.id
                it.contentUri?.let {
                    checkedImgs.add(it)
                }
            }
            viewBinding.images.referencedIds = ids
        }


    }
}