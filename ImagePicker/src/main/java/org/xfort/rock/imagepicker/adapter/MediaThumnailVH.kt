package org.xfort.rock.imagepicker.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import org.xfort.rock.imagepicker.MediaItem
import org.xfort.rock.imagepicker.R
import org.xfort.xrock.listener.OnItemViewClickListener

/**
 ** Created by ZhangHuaXin on 2021/5/12.
 **/
class MediaThumnailVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val imgIV: ImageView = itemView.findViewById(R.id.thumnail)
    var glide: RequestManager = Glide.with(itemView)
    var itemViewClick: OnItemViewClickListener<String>? = null

    init {
        imgIV.setOnClickListener(this)
    }

    fun bind(item: MediaItem) {
        glide.load(item.contentUri).into(imgIV)
    }

    override fun onClick(v: View?) {
        itemViewClick?.onItemViewClick(v, adapterPosition, "")
    }
}