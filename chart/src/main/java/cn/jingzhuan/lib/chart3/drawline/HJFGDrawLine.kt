package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-18
 * 画黄金分割
 */
class HJFGDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

    private val ratioArray = floatArrayOf(0.236f, 0.382f, 0.5f, 0.618f, 0.809f, 1.618f, 2.618f)

    private val sb = StringBuilder()

    override fun onDraw(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        super.onDraw(canvas, dataSet, baseDataSet, lMax, lMin)
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

        // 画第一条线
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = dataSet.lineSize
        linePaint.alpha = 255
        canvas.drawLine(0f, startY, width, startY, linePaint)

        // 画第一条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = 10
            canvas.drawLine(0f, startY, width, startY, linePaint)
        }

        // 画第二条线
        linePaint.style = Paint.Style.FILL
        linePaint.strokeWidth = dataSet.lineSize
        linePaint.alpha = 255
        canvas.drawLine(0f, endY, width, endY, linePaint)

        // 画第二条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = 10
            canvas.drawLine(0f, endY, width, endY, linePaint)
        }

        // 画黄金分割虚线 固定比例 7条虚线
        linePaint.strokeWidth = 3f
        linePaint.alpha = 255
        linePaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f, 5f, 5f), 0f)

        ratioArray.toList().forEach {
            // 画虚线
            val y = startY + (endY - startY) * it
            canvas.drawLine(0f, y, width, y, linePaint)

            // 画文本
            val ratio = "${String.format("%.1f", it * 100)}%"
            sb.append(ratio)
            val value = String.format("%.${chartView.decimalDigitsNumber}f", lMax - y / height * (lMax - lMin))
            sb.append("  $value")
            val text = sb.toString()
            canvas.drawText(text, 15f, y - 5f, textPaint)
            sb.clear()

        }
        linePaint.pathEffect = null

    }
}
