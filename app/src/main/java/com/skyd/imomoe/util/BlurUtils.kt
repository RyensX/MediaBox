package com.skyd.imomoe.util

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.skyd.imomoe.App
import kotlin.math.roundToInt


object BlurUtils {
    /**
     * 图片缩放比例
     */
    private const val SCALE_DEGREE = 0.2f

    /**
     * 最大模糊度（在0.0到25.0之间）
     */
    private const val BLUR_RADIUS = 25f

    fun blur(bitmap: Bitmap): Bitmap {
        //计算图片缩小的长宽
        val width = (bitmap.width * SCALE_DEGREE).roundToInt()
        val height = (bitmap.height * SCALE_DEGREE).roundToInt()

        //将缩小后的图片作为预渲染的图片
        val inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        //创建一张渲染后的输入图片
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        //创建RenderScript内核对象
        val renderScript = RenderScript.create(App.context)
        //创建一个模糊效果的RenderScript的工具对象
        val scriptIntrinsicBlur =
            ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        /**
         * 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
         * 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
         */
        val inputAllocation = Allocation.createFromBitmap(renderScript, inputBitmap)
        val outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap)

        //设置渲染的模糊程度，25f是最大模糊度
        scriptIntrinsicBlur.setRadius(BLUR_RADIUS)
        //设置ScriptIntrinsicBlur对象的输入内存
        scriptIntrinsicBlur.setInput(inputAllocation)
        //将ScriptIntrinsicBlur输出数据保存到输出内存中
        scriptIntrinsicBlur.forEach(outputAllocation)

        //将数据填充到Allocation中
        outputAllocation.copyTo(outputBitmap)
        return outputBitmap
    }
}