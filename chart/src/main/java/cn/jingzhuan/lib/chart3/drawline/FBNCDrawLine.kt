package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-18
 * 画斐波那挈线
 */
class FBNCDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

    private val sb = StringBuilder()

    private val mTextBounds = Rect()

    private var textHeight = 0

    private var textTopPadding = 8f

    private var textBottomPadding = 4f

    override fun onDraw(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float,
    ) {
        super.onDraw(canvas, dataSet, baseDataSet, lMax, lMin)

        val text = "1"
        textPaint.getTextBounds(text, 0, text.length, mTextBounds)
        textHeight = mTextBounds.height()

        if (dataSet.isSelect) return
        drawTypeShape(canvas, dataSet, baseDataSet, lMax, lMin)
    }

    override fun drawTypeShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        if (startPoint.value > lMax && endPoint.value > lMax) return
        if (startPoint.value < lMin && endPoint.value < lMin) return

        // 没有吸附并且没有抬起时平顺滑动
        if (!chartView.isDrawLineAdsorb && !dataSet.isActionUp) {
            val startX = startPoint.x
            val startIndex = getIndexInTime(dataSet, baseDataSet, startPoint.time)

            val endX = endPoint.x
            val endIndex = getIndexInTime(dataSet, baseDataSet, endPoint.time)

            drawShape(canvas, dataSet, startX, endX, startIndex, endIndex, baseDataSet)
            return
        }

        val startIndex = getIndexInTime(dataSet, baseDataSet, startPoint.time)
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        if (!dataSet.isSelect || dataSet.isActionUp) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        val endIndex = getIndexInTime(dataSet, baseDataSet, endPoint.time)
        val endX = getEntryX(endIndex, baseDataSet) ?: return
        val endY = chartView.getScaleY(endPoint.value, lMax, lMin)
        if (!dataSet.isSelect || dataSet.isActionUp) {
            endPoint.apply { dataIndex = endIndex; x = endX; y = endY }
        }

        drawShape(canvas, dataSet, startX, endX, startIndex, endIndex, baseDataSet)
    }

    private fun drawShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        startX: Float,
        endX: Float,
        startIndex: Int,
        endIndex: Int,
        baseDataSet: AbstractDataSet<*>,
    ) {
        val height = chartView.contentRect.height().toFloat()

        val bottomY = height - textHeight - textTopPadding

        // 斐波那挈不适配虚线
        linePaint.pathEffect = null

        // 画第一条线
        linePaint.style = Paint.Style.FILL
        // 斐波那挈固定线宽
        linePaint.strokeWidth = getLineSizePx(2f)
        linePaint.alpha = 255
        canvas.drawLine(startX, 0f, startX, bottomY, linePaint)

        // 画第一条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawLine(startX, 0f, startX, bottomY, linePaint)
        }

        // 画第一条线文本
        var firstDrawIndex = 1
        sb.append("$firstDrawIndex")
        canvas.drawText(sb.toString(), startX - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
        sb.clear()

        // 画第二条线
        linePaint.style = Paint.Style.FILL
        // 斐波那挈固定线宽
        linePaint.strokeWidth = getLineSizePx(2f)
        linePaint.alpha = 255
        canvas.drawLine(endX, 0f, endX, bottomY, linePaint)

        // 画第二条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawLine(endX, 0f, endX, bottomY, linePaint)
        }

        // 画第二条线文本
        var secondDrawIndex = endIndex - startIndex + 1
        firstDrawIndex = secondDrawIndex
        sb.append("$secondDrawIndex")
        canvas.drawText(sb.toString(), endX - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
        sb.clear()

        // 画第三条线
        val thirdDrawIndex = (endIndex - startIndex) * 2 + 1
        val thirdX = getEntryX(startIndex + (endIndex - startIndex) * 2, baseDataSet) ?: -1f
        linePaint.style = Paint.Style.FILL
        // 斐波那挈固定线宽
        linePaint.strokeWidth = getLineSizePx(2f) * 0.5f
        linePaint.alpha = 255
        canvas.drawLine(thirdX, 0f, thirdX, bottomY, linePaint)
        secondDrawIndex = thirdDrawIndex

        // 画第三条线文本
        sb.append("$thirdDrawIndex")
        canvas.drawText(sb.toString(), thirdX - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
        sb.clear()

        // 画剩余等比数列
        val lastIndex = baseDataSet.values.size - 1
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = getLineSizePx(2f) * 0.5f
        linePaint.alpha = 255

        // 基准线的差
        val diff = endIndex - startIndex - 1
        var i = startIndex + secondDrawIndex * 2

        while (i < lastIndex) {
            val currentIndex = firstDrawIndex + secondDrawIndex + diff
            if (currentIndex + startIndex > lastIndex) break
            val x = getEntryX(startIndex + currentIndex - 1, baseDataSet) ?: -1f
            canvas.drawLine(x, 0f, x, bottomY, linePaint)

            sb.append("$currentIndex")
            canvas.drawText(sb.toString(), x - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
            sb.clear()

            firstDrawIndex = secondDrawIndex
            secondDrawIndex = currentIndex
            i = secondDrawIndex
        }
    }
}
