package org.xfort.rock.imagepicker.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 ** Created by ZhangHuaXin on 2021/5/11.
 * RecyclerView 的 ItemDecoration
 **/
class DividerItemDecoration : ItemDecoration() {
    companion object {
        const val LINEAR = 1
        const val GRID = 2
        const val VERTICAL = 1
        const val HORIZONTAL = 2
    }

    private val mBounds = Rect()
    var layoutType = LINEAR
    var layoutOrientation = VERTICAL
    var gridSpanCount = 1
    var itemSpaceSize = Pair(0, 0)

    /**
     * 列表头部
     */
    var headerSpaceSize = Pair(0, 0)

    /**
     * 列表底部
     */
    var footerSpaceSize = Pair(0, 0)
    var dividerDrawable: Drawable? = null

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || dividerDrawable == null) {
            return
        }
        when (layoutOrientation) {
            VERTICAL -> drawVertical(c, parent, state)
        }
    }

   private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(childView, this.mBounds)
            val bottom: Int = this.mBounds.bottom+itemSpaceSize.second
            var top: Int = bottom - itemSpaceSize.second
            //if (this.dividerDrawable?.intrinsicHeight?:0 > 0) {
            //    top = bottom - dividerDrawable?.intrinsicHeight!!
            //}
            dividerDrawable?.setBounds(left, top, right, bottom)
            dividerDrawable?.draw(canvas)
        }
        canvas.restore()
    }


    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        when (layoutType) {
            LINEAR -> linearItemOffset(outRect, view, parent, state)
            GRID -> gridItemOffset(outRect, view, parent, state)
        }
    }

    fun linearItemOffset(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        var adapterPosition = parent.getChildAdapterPosition(view)
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        when (adapterPosition) {
            0 -> {
                if (layoutOrientation == HORIZONTAL) {
                    left = headerSpaceSize.first
                } else {
                    top = headerSpaceSize.second
                }
            }
            state.itemCount - 1 -> {
                if (layoutOrientation == HORIZONTAL) {
                    left = itemSpaceSize.first
                    right = footerSpaceSize.first
                } else {
                    top = itemSpaceSize.second
                    bottom = footerSpaceSize.second
                }
            }
            else -> {
                if (layoutOrientation == HORIZONTAL) {
                    left = itemSpaceSize.first
                } else {
                    top = itemSpaceSize.second
                }
            }
        }
        outRect.set(left, top, right, bottom)
    }


    fun gridItemOffset(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        val lp = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams)

        if (lp.isFullSpan) {
        } else {
            var lineIndex = lp.spanIndex
            if (lineIndex == 0) {
                right = itemSpaceSize.first / 2
            } else if (lineIndex == gridSpanCount - 1) {
                left = itemSpaceSize.first / 2
            } else {
                left = itemSpaceSize.first / 2
                right = itemSpaceSize.first / 2
            }
        }
        var adapterPosition = parent.getChildAdapterPosition(view)
        if (adapterPosition < gridSpanCount) {
            top = headerSpaceSize.second
        } else if (adapterPosition >= state.itemCount - gridSpanCount) {
            bottom = headerSpaceSize.second
        } else {
            top = itemSpaceSize.second
        }
        outRect.set(left, top, right, bottom)
    }
}