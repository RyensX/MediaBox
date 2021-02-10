package com.skyd.imomoe.view.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.skyd.imomoe.util.Util.toBitmap


class RoundCornersImageView : AppCompatImageView {
    private var cornerSize = 15         //圆角大小
    private val paint: Paint = Paint()
    private val rectSrc = Rect()
    private val rectDest = Rect()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setCornerSize(size: Int) {
        cornerSize = size
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        val drawable = drawable
        if (null != drawable) {
            val bitmap = drawable.toBitmap()
            val b = getRoundBitmap(bitmap, cornerSize)
            rectSrc.set(0, 0, b.width, b.height)
            rectDest.set(0, 0, width, height)
            paint.reset()
            canvas.drawBitmap(b, rectSrc, rectDest, paint)
        } else {
            super.onDraw(canvas)
        }
    }

    /**
     * @param bitmap
     * @param roundPx
     * @return Bitmap
     */
    private fun getRoundBitmap(bitmap: Bitmap, roundPx: Int): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx.toFloat(), roundPx.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}