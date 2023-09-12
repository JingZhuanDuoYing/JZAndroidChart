package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import cn.jingzhuan.lib.chart3.utils.NumberUtils

/**
 * @since 2023-09-11
 * @author lei 画最大最小值
 */
class MaxMinArrowDraw(textColor: Int, textSize: Int) {
    private val arrowLeft = "←"

    private val arrowRight = "→"

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mTextBounds = Rect()

    init {
        mPaint.color = textColor
        mPaint.textSize = textSize.toFloat()
    }

    fun drawMaxMin(
        canvas: Canvas,
        contentWidth: Int,
        maxX: Float,
        minX: Float,
        maxY: Float,
        minY: Float,
        maxValue: Float,
        minValue: Float,
        decimalDigitsNumber: Int,
    ) {
        val sb = StringBuilder()
        // 画最大值
        val maxText = NumberUtils.keepPrecision("$maxValue", decimalDigitsNumber)

        if (maxX < contentWidth shr 1) {
            mPaint.textAlign = Align.LEFT
            sb.append(arrowLeft)
            sb.append(maxText)
            val text = sb.toString()
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                maxX,
                maxY + mTextBounds.height() * 0.5f,
                mPaint
            )
            sb.clear()
        } else {
            mPaint.textAlign = Align.RIGHT
            sb.append(maxText)
            sb.append(arrowRight)
            val text = sb.toString()
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                maxX,
                maxY + mTextBounds.height() * 0.5f,
                mPaint
            )
            sb.clear()
        }

        // 画最小值
        val minText = NumberUtils.keepPrecision("$minValue", decimalDigitsNumber)

        if (minX < contentWidth shr 1) {
            mPaint.textAlign = Align.LEFT
            sb.append(arrowLeft)
            sb.append(minText)
            val text = sb.toString()
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                minX,
                minY + mTextBounds.height() * 0.5f,
                mPaint
            )
            sb.clear()
        } else {
            mPaint.textAlign = Align.RIGHT
            sb.append(minText)
            sb.append(arrowRight)
            val text = sb.toString()
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                minX,
                minY + mTextBounds.height() * 0.5f,
                mPaint
            )
            sb.clear()
        }

    }

}