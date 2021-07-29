package org.xfort.rock.imagepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.xfort.rock.imagepicker.MediaItem
import org.xfort.rock.imagepicker.R
import org.xfort.xrock.listener.OnItemViewClickListener

/**
 ** Created by ZhangHuaXin on 2021/5/12.
 **/
class ThumnailAdapter : RecyclerView.Adapter<MediaThumnailVH>() {

    val dataList = mutableListOf<MediaItem>()
    var itemViewClick: OnItemViewClickListener<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemId(position: Int): Long {
        return dataList[position].contentUri.toString().hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaThumnailVH {
        val vh = MediaThumnailVH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_thumnail, parent, false)
        )
        vh.itemViewClick = itemViewClick
        return vh
    }

    override fun onBindViewHolder(holder: MediaThumnailVH, position: Int) {
        holder.bind(dataList[position])
    }
}