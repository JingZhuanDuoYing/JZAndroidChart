package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * @since 2023-10-13
 * 画直线
 */
class StraightDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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
        // 当前形状是直线 先画直线 再画背景 起点在图表左边界，终点在图表右边界
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        // 没有吸附并且没有抬起时平顺滑动
        if (!chartView.isDrawLineAdsorb && !dataSet.isActionUp) {
            val startX = startPoint.x
            val startY = chartView.getScaleY(startPoint.value.toFloat(), lMax, lMin)

            val endX = endPoint.x
            val endY = chartView.getScaleY(endPoint.value.toFloat(), lMax, lMin)

            drawShape(canvas, dataSet, startX, startY, endX, endY)
            return
        }

        val startIndex = getIndexInTime(dataSet, baseDataSet, startPoint.time)
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value.toFloat(), lMax, lMin)
        if (!dataSet.isSelect || dataSet.isActionUp) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        val endIndex = getIndexInTime(dataSet, baseDataSet, endPoint.time)
        val endX = getEntryX(endIndex, baseDataSet) ?: return
        val endY = chartView.getScaleY(endPoint.value.toFloat(), lMax, lMin)
        if (!dataSet.isSelect || dataSet.isActionUp) {
            endPoint.apply { dataIndex = endIndex; x = endX; y = endY }
        }

        drawShape(canvas, dataSet, startX, startY, endX, endY)

    }

    private fun drawShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {
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

        // 画线段
        setDashPathEffect(dataSet.dash)
        if (startX == endX) {
            canvas.drawLine(startX, 0f, endX, chartView.contentRect.height().toFloat(), linePaint)
        } else {
            canvas.drawLine(x1, y1, x2, y2, linePaint)
        }
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        val path = if (startX == endX) {
            updatePath(dataSet, angle.toFloat(), startX, 0f, endX, chartView.contentRect.height().toFloat())
        } else {
            updatePath(dataSet, angle.toFloat(), x1, y1, x2, y2)
        }

        // 画选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawPath(path, linePaint)
        }
    }
}
