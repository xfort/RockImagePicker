package org.xfort.rock.imagepicker.adapter

import android.net.Uri
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import org.xfort.rock.imagepicker.MediaItem
import org.xfort.rock.imagepicker.R
import org.xfort.rock.imagepicker.view.RatioImageView
import org.xfort.xrock.listener.OnItemViewClickListener

/**
 ** Created by ZhangHuaXin on 2021/5/8.
 **/
class MediaVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    lateinit var glide: RequestManager
    var imgView: RatioImageView = itemView.findViewById(R.id.image)
    var selectedIndexTV: CheckedTextView = itemView.findViewById(R.id.index)
    var itemViewClick: OnItemViewClickListener<String>? = null

    init {
        selectedIndexTV.setOnClickListener(this)
        imgView.setOnClickListener(this)
    }

    var itemUri: Uri? = null
    fun bindData(item: MediaItem?) {
        itemUri = null
        if (item == null) {
            imgView.setImageDrawable(null)
        } else {
            itemUri = item.contentUri

            var imgRatio = if (item.width > 0 && item.height > 0) {
                item.width.toFloat() / item.height
            } else {
                1F
            }

            if (imgRatio > 1.5f) {
                imgRatio = 1.5f
                imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else if (imgRatio < 0.3F) {
                imgRatio = 0.3f
                imgView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                imgView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
            imgView.ratioWH = imgRatio
            imgView.post {
                glide.load(item.contentUri).into(imgView)
                    .waitForLayout()
            }
        }
    }

    fun setCheckedIndex(checkedIndex: Int) {
        if (checkedIndex >= 0) {
            selectedIndexTV.isChecked = true
            selectedIndexTV.text = (checkedIndex + 1).toString()
        } else {
            selectedIndexTV.isChecked = false
            selectedIndexTV.text = ""
        }
        imgView.isChecked = selectedIndexTV.isChecked
    }

    override fun onClick(v: View?) {
//        when (v) {
//            selectedIndexTV -> toggle(v)
//            imgView -> goPreview()
//        }
        itemViewClick?.onItemViewClick(v, adapterPosition, itemUri?.toString() ?: "")

    }

    fun toggle(v: View) {
//        selectedIndexTV.toggle()
//
//        if (!selectedIndexTV.isChecked) {
//            setCheckedIndex(-1)
//        }
        itemViewClick?.onItemViewClick(v, adapterPosition, itemUri?.toString() ?: "")
    }


    /**
     * 预览大图
     */
    fun goPreview() {

    }
}