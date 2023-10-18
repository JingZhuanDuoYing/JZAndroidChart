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

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        if (startPoint.value > lMax && endPoint.value > lMax) return
        if (startPoint.value < lMin && endPoint.value < lMin) return

        val startIndex = baseDataSet.values.indexOfFirst { it.time == startPoint.time }
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        if (!dataSet.isSelect) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        val endIndex = baseDataSet.values.indexOfFirst { it.time == endPoint.time }
        val endX = getEntryX(endIndex, baseDataSet) ?: return
        val endY = chartView.getScaleY(endPoint.value, lMax, lMin)
        if (!dataSet.isSelect) {
            endPoint.apply { dataIndex = endIndex; x = endX; y = endY }
        }

        val width = chartView.contentRect.width().toFloat()
        val height = chartView.contentRect.height().toFloat()

        val bottomY = height - textHeight - textTopPadding

        // 画第一条线
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = dataSet.lineSize
        linePaint.alpha = 255
        canvas.drawLine(startX, 0f, startX, bottomY, linePaint)

        // 画第一条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = 10
            canvas.drawLine(startX, 0f, startX, bottomY, linePaint)
        }

        // 画第一条线文本
        sb.append("1")
        canvas.drawText(sb.toString(), startX - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
        sb.clear()

        // 画第二条线
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = dataSet.lineSize
        linePaint.alpha = 255
        canvas.drawLine(endX, 0f, endX, bottomY, linePaint)

        // 画第二条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = 10
            canvas.drawLine(endX, 0f, endX, bottomY, linePaint)
        }

        // 画第二条线文本
        sb.append("2")
        canvas.drawText(sb.toString(), endX - textPaint.measureText(sb.toString()) * 0.5f, height - textBottomPadding, textPaint)
        sb.clear()

        // 画剩余等比数列
//        val lastIndex = baseDataSet.values.size - 1
//        linePaint.style = Paint.Style.FILL
//        linePaint.strokeWidth = dataSet.lineSize
//        linePaint.alpha = 255
//
//        var firstIndex = 0
//        var secondIndex = endIndex - startIndex + 1
//
//        for (i in endIndex until lastIndex) {
//            val currentIndex = firstIndex + secondIndex + 1
//            val x = chartView.getEntryX(startIndex + currentIndex)
//            canvas.drawLine(x, 0f, x, bottomY, linePaint)
//            firstIndex = secondIndex
//            secondIndex = currentIndex
//        }


    }
}
