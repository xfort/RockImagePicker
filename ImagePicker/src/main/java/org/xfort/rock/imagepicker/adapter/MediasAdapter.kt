package org.xfort.rock.imagepicker.adapter

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.collection.ArrayMap
import androidx.core.database.getStringOrNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import org.xfort.rock.imagepicker.*
import org.xfort.xrock.listener.OnItemViewClickListener


/**
 ** Created by ZhangHuaXin on 2021/5/8.
 **/
class MediasAdapter(life: Lifecycle) : RecyclerView.Adapter<MediaVH>(),
    OnItemViewClickListener<String>, LifecycleObserver {
    init {
        life.addObserver(this)
    }

    lateinit var inflater: LayoutInflater
    private var cursors: Cursor? = null
    private var idColumIndex: Int = -1
    private var columnsIndex = IntArray(MediaLoader.PROJECTION_MEDIA.size)

    /**
     * 缓存由 cursor 转化而来的 MediaItem
     */
    private var mediaItemsArray = ArrayMap<String, MediaItem>()

    /**
     * 选中的图片
     */
    var checkedMedias = mutableSetOf<MediaItem>()
    var checkMaxCount = 1
    var mediaSelectListener: OnMediaSelectListener? = null
    lateinit var glide: RequestManager

    lateinit var onItemViewClick: OnItemViewClickListener<Uri>

    init {
        setHasStableIds(true)
    }

    fun swapCursor(newCursor: Cursor?) {
        if (cursors == newCursor) {
            return
        }
        mediaItemsArray.clear()
        if (newCursor == null || newCursor.isClosed) {
            val count = itemCount
            if (cursors != null) {
                cursors = null
            }
            notifyItemRangeRemoved(0, count)
        } else {
            idColumIndex = newCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

            MediaLoader.PROJECTION_MEDIA.forEachIndexed { index, s ->
                columnsIndex[index] = newCursor.getColumnIndex(s)
            }
            cursors = if (idColumIndex >= 0) {
                newCursor
            } else {
                null
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        cursors?.moveToPosition(position)
            ?.let { if (it) return cursors?.getLong(idColumIndex) ?: RecyclerView.NO_ID }
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        cursors?.let { if (!it.isClosed) return it.count }
        return 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaVH {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        val mediaVH = MediaVH(inflater.inflate(R.layout.item_media, parent, false))
        mediaVH.itemViewClick = this
        mediaVH.glide = glide
        return mediaVH
    }

    override fun onBindViewHolder(holder: MediaVH, position: Int) {
        var itemUri = UriUtils.contentUri(
            cursors?.getLong(idColumIndex) ?: -1,
            cursors?.getStringOrNull(columnsIndex[1]) ?: ""
        )
        val uriKey = itemUri.toString()
        var mediaItem = mediaItemsArray[uriKey]
        if (mediaItem == null) {
            mediaItem = MediaItem.parseCursor(cursors, columnsIndex)
            mediaItemsArray[uriKey] = mediaItem
        }
        mediaItem?.appAdapterPosition = position
        holder.bindData(mediaItem)

        var checkIndex = -1
        mediaItem?.let {
            checkedMedias.forEachIndexed { index, item ->
                if (item.contentUri == it.contentUri) {
                    checkIndex = index
                    if (item.appAdapterPosition < 0) {
                        //来自刚刚拍照 or 初始选中的图片
                        item.appAdapterPosition = position
                        item.mimeType = it.mimeType
                        item.name = it.name

                    }
                    return@forEachIndexed
                }
            }
        }
        holder.setCheckedIndex(checkIndex)
    }

    override fun onBindViewHolder(
        holder: MediaVH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (!payloads.isNullOrEmpty()) {
            var obj = payloads[0]
            if ((obj is MediaItem)) {
                var checkIndex = checkedMedias.indexOf(obj)
                holder.setCheckedIndex(checkIndex)
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifeDestroy() {
        cursors?.close()
        mediaItemsArray.clear()
        checkedMedias.clear()
        mediaSelectListener = null
    }

    override fun onItemViewClick(v: View?, position: Int, data: String?) {
        when (v?.id) {
            R.id.index -> onCheckChange(v, position, data ?: "")
            R.id.image -> onItemViewClick.onItemViewClick(v, position, Uri.parse(data))
        }
    }

    private fun onCheckChange(v: View, position: Int, data: String) {
        if (data.isNotBlank()) {
            var itemMedia = mediaItemsArray[data]
            if (itemMedia != null) {
                var checked = (v as CheckedTextView).isChecked
                if (!checked) {
                    //当前是未选中状态
                    if (checkedMedias.size >= checkMaxCount) {
                        //已选满
                        Toast.makeText(
                            v.context,
                            v.resources.getString(
                                R.string.checked_max_count_x,
                                checkMaxCount.toString()
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
                updateCheckStatus(position, !checked, itemMedia)
            }
        }
    }

    /**
     * 刷新选中状态
     */
    fun updateCheckStatus(position: Int, isChecked: Boolean, itemMedia: MediaItem) {
        if (isChecked) {
            if (checkedMedias.add(itemMedia) && (position in 0 until itemCount)) {
                notifyItemChanged(position, itemMedia)
                mediaSelectListener?.onSelecteChange(itemMedia)
            } else {
                notifyItemChanged(position, itemMedia)
            }
        } else {
            if (checkedMedias.remove(itemMedia) && (position in 0 until itemCount)) {
                notifyItemChanged(position, itemMedia)
                checkedMedias.forEach {
                    notifyItemChanged(it.appAdapterPosition, it)
                }
                mediaSelectListener?.onSelecteChange(itemMedia)
            } else {
                notifyItemChanged(position, itemMedia)
            }
        }
    }

    /**
     * 设置默认选中的图片
     */
    fun setDefaultCheck(checkedUri: Uri) {
        if (checkedMedias.size >= checkMaxCount) {
            return
        }
        var item = mediaItemsArray[checkedUri.toString()]
        if (item != null && item.appAdapterPosition >= 0) {
            updateCheckStatus(item.appAdapterPosition, true, item)
        } else {
            item = MediaItem(
                ContentUris.parseId(checkedUri),
                "image/*",
                1,
                0,
                0,
                0,
                ""
            )
            item.contentUri = checkedUri
            if (checkedMedias.add(item)) {
                mediaSelectListener?.onSelecteChange(item)
            }
        }
    }
}