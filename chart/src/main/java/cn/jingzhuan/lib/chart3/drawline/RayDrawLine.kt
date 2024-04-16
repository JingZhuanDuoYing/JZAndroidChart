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
 * @since 2023-10-16
 * 画射线
 */
class RayDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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
        // 当前形状是射线 先画射线 再画背景
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        if (startPoint.value > lMax && endPoint.value > lMax) return
        if (startPoint.value < lMin && endPoint.value < lMin) return

        // 没有吸附并且没有抬起时平顺滑动
        if (!chartView.isDrawLineAdsorb && !dataSet.isActionUp) {
            val startX = startPoint.x
            val startY = chartView.getScaleY(startPoint.value, lMax, lMin)

            val endX = endPoint.x
            val endY = chartView.getScaleY(endPoint.value, lMax, lMin)

            drawShape(canvas, dataSet, startX, startY, endX, endY)
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
        // 当前起点与终点的夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

        // 画射线
        setDashPathEffect(dataSet.dash)
        val path = if (startX == endX) {
            val secondY = if (endY >= startY) chartView.contentRect.height().toFloat() else 0f
            canvas.drawLine(startX, startY, endX, secondY, linePaint)
            updatePath(dataSet, angle.toFloat(), startX, startY, endX, secondY)
        } else {
            // 根据cos(angel) = (chartView.right - min(startX, endX)) / 半径 (与边界交叉点的直线距离)
            val rightRadius = (chartView.contentRect.width().toFloat() - startX) / cos(angle * Math.PI / 180).toFloat()
            val leftRadius = (startX - 0f) / cos(angle * Math.PI / 180).toFloat()
            val radius = if (startX > endX) -leftRadius else rightRadius
            val x1 = startX + radius * cos(angle * Math.PI / 180).toFloat()
            val y1 = startY + radius * sin(angle * Math.PI / 180).toFloat()

            canvas.drawLine(startX, startY, x1, y1, linePaint)
            updatePath(dataSet, angle.toFloat(), startX, startY, x1, y1)
        }
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        // 画选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawPath(path, linePaint)
        }
    }
}
