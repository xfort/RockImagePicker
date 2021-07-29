package org.xfort.rock.imagepicker.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import org.xfort.rock.imagepicker.R

/**
 ** Created by ZhangHuaXin on 2021/5/10.
 **/
class RatioImageView : AppCompatImageView, Checkable {
    val CHECKED_COLOR = resources.getColor(R.color.translucent_black)
    private var checked = false
    var ratioWH = 1F
        set(value) {
            field = value
            requestLayout()
        }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
    }

    override fun setChecked(checked: Boolean) {
        if (this.checked != checked) {
            this.checked = checked
            postInvalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, (widthSize / ratioWH).toInt())

    }


    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        if (checked) {
            canvas?.drawColor(CHECKED_COLOR)

            Log.d("RatioImageView", "onMeasure_" + ratioWH + "_" + width + "_" + height)

        }
    }

    override fun isChecked(): Boolean {
        return checked
    }

    override fun toggle() {
        isChecked = !checked
    }


}