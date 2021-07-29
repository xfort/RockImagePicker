package org.xfort.xrock.listener

import android.view.View

/**
 ** Created by ZhangHuaXin on 2021/5/12.
 * 列表的ViewHolder内的view点击监听
 **/
interface OnItemViewClickListener<T> {
    fun onItemViewClick(v: View?, position: Int, data: T?)
}