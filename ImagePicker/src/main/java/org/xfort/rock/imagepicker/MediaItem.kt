package org.xfort.rock.imagepicker

import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

/**
 ** Created by ZhangHuaXin on 2021/5/10.
 *
 **/
data class MediaItem(
    val id: Long,
    var mimeType: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val time: Long,
    var name: String,
) : Parcelable {
    var contentUri: Uri? = when {
        mimeType.startsWith("image") -> {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        mimeType.startsWith("video") -> {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        else -> {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }
    }
    var appAdapterPosition: Int = -1

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString() ?: ""
    ) {
//        contentUri = parcel.readParcelable(Uri::class.java.classLoader)
    }

    init {
        contentUri = UriUtils.contentUri(id, mimeType)
    }


    override fun hashCode(): Int {
        return (contentUri.toString() + id.toString()).hashCode()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.let {
            it.writeLong(id)
            it.writeString(mimeType)
            it.writeLong(size)
            it.writeInt(width)
            it.writeInt(height)
            it.writeLong(time)
            it.writeString(name)
        }
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<MediaItem> {
        override fun createFromParcel(parcel: Parcel): MediaItem {
            return MediaItem(parcel)
        }

        override fun newArray(size: Int): Array<MediaItem?> {
            return arrayOfNulls(size)
        }

        fun parseCursor(cursor: Cursor?, columnsIndex: IntArray): MediaItem? {
            cursor?.let {
                var origin = it.getIntOrNull(columnsIndex[7])
                var width = it.getIntOrNull(columnsIndex[3]) ?: 0
                var height = it.getIntOrNull(columnsIndex[4]) ?: 0
                if (width > 0 && height > 0) {
                    when (origin) {
                        90, 270 -> {
                            width = width.xor(height)
                            height = width.xor(height)
                            width = width.xor(height)
                        }
                    }
                }

                var item = MediaItem(
                    it.getLongOrNull(columnsIndex[0]) ?: -1,
                    it.getStringOrNull(columnsIndex[1]) ?: "",
                    it.getLongOrNull(columnsIndex[2]) ?: -1,
                    width,
                    height,
                    it.getLongOrNull(columnsIndex[5]) ?: -1,
                    it.getStringOrNull(columnsIndex[6]) ?: "",
                )
                if (item.mimeType.contains("gif")) {
                    Log.d("MediaItem", item.toString() + "_" + origin)
                }
                return item
            }
            return null
        }
    }
}
