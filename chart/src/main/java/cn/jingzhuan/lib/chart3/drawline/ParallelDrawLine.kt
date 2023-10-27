package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * @since 2023-10-17
 * 画平行线
 */
class ParallelDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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

    override fun drawTypeShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        // 当前形状是平行 先画第一条直线及背景 再画第二条直线及背景 起点在图表在边界，终点在图表右边界
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        val thirdPoint = dataSet.thirdDrawValue
        if (startPoint == null || endPoint == null) return

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

        // 当前起点与终点的夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

        // 根据cos(angel) = (chartView.right - min(startX, endX)) / 半径 (与边界交叉点的直线距离)
        val rightRadius = (width - startX) / cos(angle * Math.PI / 180).toFloat()
        val leftRadius = (startX - 0f) / cos(angle * Math.PI / 180).toFloat()

        // val radius = chartView.right - min(startX, endX)
        // x1 = x0 + r * cos(a * PI /180 )
        // y1 = y0 + r * sin(a * PI /180 )
        val x1 = startX + rightRadius * cos(angle * Math.PI / 180).toFloat()
        val y1 = startY + rightRadius * sin(angle * Math.PI / 180).toFloat()

        val x2 = startX - leftRadius * cos(angle * Math.PI / 180).toFloat()
        val y2 = startY - leftRadius * sin(angle * Math.PI / 180).toFloat()

        // 画第一条线
        linePaint.strokeWidth = getLineSizePx(dataSet.lineSize)
        linePaint.alpha = 255
        setDashPathEffect(dataSet.dash)
        canvas.drawLine(x1, y1, x2, y2, linePaint)
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        val path = updatePath(dataSet, angle.toFloat(), x1, y1, x2, y2)

        // 画选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawPath(path, linePaint)
        }

        // 画第二条线
        if (thirdPoint != null) {
            val thirdIndex = baseDataSet.values.indexOfFirst { it.time == thirdPoint.time }
            val thirdX = getEntryX(thirdIndex, baseDataSet) ?: return
            val thirdY = chartView.getScaleY(thirdPoint.value, lMax, lMin)

            val sRightRadius = (width - thirdX) / cos(angle * Math.PI / 180).toFloat()
            val sLeftRadius = (thirdX - 0f) / cos(angle * Math.PI / 180).toFloat()
            val x3 = thirdX + sRightRadius * cos(angle * Math.PI / 180).toFloat()
            val y3 = thirdY + sRightRadius * sin(angle * Math.PI / 180).toFloat()

            val x4 = thirdX - sLeftRadius * cos(angle * Math.PI / 180).toFloat()
            val y4 = thirdY - sLeftRadius * sin(angle * Math.PI / 180).toFloat()

            // 画第二条线
            linePaint.strokeWidth = getLineSizePx(dataSet.lineSize)
            linePaint.alpha = 255
            setDashPathEffect(dataSet.dash)
            canvas.drawLine(x3, y3, x4, y4, linePaint)
            if (linePaint.pathEffect != null) linePaint.pathEffect = null

            val parallelPath = updatePath(dataSet, angle.toFloat(), x3, y3, x4, y4, true)
            if (dataSet.isSelect) {
                linePaint.style = Paint.Style.FILL
                linePaint.alpha = dataSet.selectAlpha
                canvas.drawPath(parallelPath, linePaint)
            }
        }
    }
}
