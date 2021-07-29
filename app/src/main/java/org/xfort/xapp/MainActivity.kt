package org.xfort.xapp

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.imagePicker.setOnClickListener {
            imagePicker.start()
        }
        val imgWidth = (resources.getDimension(R.dimen.meta) * 90).toInt()

        imagePicker = RockMedia(this) { result ->
            viewBinding.imagesInfo.text = ""
            var ids = IntArray(result.size)
            result.forEachIndexed { index, it ->
                viewBinding.imagesInfo.append("${it.id}_${it.name}_${it.contentUri?.toString()}\n\n")
                val itemImg = ImageView(this)
                itemImg.scaleType = ImageView.ScaleType.CENTER_CROP
                itemImg.id = ViewCompat.generateViewId()
                viewBinding.root.addView(itemImg, imgWidth, imgWidth)
                itemImg.setImageURI(it.contentUri)
                ids[index] = itemImg.id
            }
            viewBinding.images.referencedIds = ids
        }
        imagePicker.maxCount = 9

    }
}