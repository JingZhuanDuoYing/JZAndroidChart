package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import cn.jingzhuan.lib.chart.utils.FloatUtils

/**
 * @since 2023-09-11
 * @author lei 画最大最小值
 */
class MaxMinArrowDraw(textColor: Int, textSize: Int) {
    private val arrowLeft = "←"

    private val arrowRight = "→"

    private val mLabelBuffer = CharArray(20)

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

        // 画最大值
        val maxLength = FloatUtils.formatFloatValue(mLabelBuffer, maxValue, decimalDigitsNumber)

        val maxText = String(mLabelBuffer, mLabelBuffer.size - maxLength, maxLength)

        if (maxX < contentWidth shr 1) {
            mPaint.textAlign = Align.LEFT
            val text = "$arrowLeft$maxText"
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                maxX,
                maxY + mTextBounds.height() * 0.5f,
                mPaint
            )
        } else {
            mPaint.textAlign = Align.RIGHT
            val text = "$maxValue$arrowRight"
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                maxX,
                maxY + mTextBounds.height() * 0.5f,
                mPaint
            )
        }

        // 画最小值
        val minLength = FloatUtils.formatFloatValue(mLabelBuffer, minValue, decimalDigitsNumber);

        val minText = String(mLabelBuffer, mLabelBuffer.size - minLength, minLength)

        if (minX < contentWidth shr 1) {
            mPaint.textAlign = Align.LEFT
            val text = "$arrowLeft$minText"
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                minX,
                minY + mTextBounds.height() * 0.5f,
                mPaint
            )
        } else {
            mPaint.textAlign = Align.RIGHT
            val text = "$minValue$arrowRight"
            mPaint.getTextBounds(text, 0, text.length, mTextBounds)
            canvas.drawText(
                text,
                minX,
                minY + mTextBounds.height() * 0.5f,
                mPaint
            )
        }

    }

}