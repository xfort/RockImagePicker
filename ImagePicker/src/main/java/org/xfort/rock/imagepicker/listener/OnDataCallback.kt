package org.xfort.xrock.listener

/**
 ** Created by ZhangHuaXin on 2021/5/17.
 * 数据回调，可用于fragment数据回传
 **/
interface OnDataCallback<T> {
    fun onDataCallback(data: T?)
}