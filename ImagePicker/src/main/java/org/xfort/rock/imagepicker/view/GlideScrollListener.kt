package org.xfort.rock.imagepicker.view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager

/**
 ** Created by ZhangHuaXin on 2021/5/14.
 * 列表飞速滚动时暂停加载图片，停止、手指滑动状态时自动加载图片
 **/
class GlideScrollListener(private val glide: RequestManager) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_SETTLING -> glide.pauseRequests()
            else -> glide.resumeRequests()
        }
    }
}