package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.atan2
import kotlin.math.cos
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
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return

        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        val startValue = startPoint.value
        val endValue = endPoint.value
        if (startValue > lMax && endValue > lMax) return
        if (startValue < lMin && endValue < lMin) return

        if (dataSet.leftCrossValue == -1f && dataSet.rightCrossValue == -1f) {
            val startX = visibleValues.find { it.time == startPoint.time }?.x ?: return
            val startY = chartView.getScaleY(startValue, lMax, lMin)

            val endX = visibleValues.find { it.time == endPoint.time }?.x ?: return
            val endY = chartView.getScaleY(endValue, lMax, lMin)

            // 算出夹角
            val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

            val rightRadius = (chartView.width - startX) / cos(angle * Math.PI / 180).toFloat()
            val leftRadius = (startX - 0f) / cos(angle * Math.PI / 180).toFloat()
            // x1 = x0 + r * cos(a * PI /180 )
            // y1 = y0 + r * sin(a * PI /180 )
            val x1 = startX + rightRadius * cos(angle * Math.PI / 180).toFloat()
            val y1 = startY + rightRadius * sin(angle * Math.PI / 180).toFloat()

            val x2 = startX - leftRadius * cos(angle * Math.PI / 180).toFloat()
            val y2 = startY - leftRadius * sin(angle * Math.PI / 180).toFloat()
            dataSet.leftCrossValue = lMax - y2 / chartView.contentRect.height() * (lMax - lMin)
            dataSet.rightCrossValue = lMax - y1 / chartView.contentRect.height() * (lMax - lMin)

            canvas.drawLine(x1, y1, x2, y2, linePaint)
        } else {
            val startY = chartView.getScaleY(dataSet.leftCrossValue, lMax, lMin)
            val endY = chartView.getScaleY(dataSet.rightCrossValue, lMax, lMin)
            canvas.drawLine(0f, startY, chartView.width.toFloat(), endY, linePaint)
        }
    }

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        // 当前形状是直线 先画直线 再画背景 起点在图表在边界，终点在图表右边界
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        val startX = baseDataSet.values.find { it.time == startPoint.time }?.x ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)

        val endX = baseDataSet.values.find { it.time == endPoint.time }?.x ?: return
        val endY = chartView.getScaleY(endPoint.value, lMax, lMin)

        // 算出夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI
        Log.d("drawTypeShape角度", "angle=$angle")

        // 根据cos(angel) = (chartView.right - min(startX, endX)) / 半径 (与边界交叉点的直线距离)
        val rightRadius = (chartView.width - startX) / cos(angle * Math.PI / 180).toFloat()
        val leftRadius = (startX - 0f) / cos(angle * Math.PI / 180).toFloat()

//        val radius = chartView.right - min(startX, endX)
        // x1 = x0 + r * cos(a * PI /180 )
        // y1 = y0 + r * sin(a * PI /180 )
        val x1 = startX + rightRadius * cos(angle * Math.PI / 180).toFloat()
        val y1 = startY + rightRadius * sin(angle * Math.PI / 180).toFloat()

        val x2 = startX - leftRadius * cos(angle * Math.PI / 180).toFloat()
        val y2 = startY - leftRadius * sin(angle * Math.PI / 180).toFloat()
        dataSet.leftCrossValue = lMax - y2 / chartView.contentRect.height() * (lMax - lMin)
        dataSet.rightCrossValue = lMax - y1 / chartView.contentRect.height() * (lMax - lMin)

        canvas.drawLine(x1, y1, x2, y2, linePaint)

        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dataSet.pointRadiusOut * 2f
        linePaint.alpha = 10
        val path = Path()
        path.moveTo(x1, y1)
        path.lineTo(startX, startY)
        path.lineTo(endX, endY)
        path.lineTo(x2, y2)
        path.close()
        canvas.drawPath(path, linePaint)
    }
}
