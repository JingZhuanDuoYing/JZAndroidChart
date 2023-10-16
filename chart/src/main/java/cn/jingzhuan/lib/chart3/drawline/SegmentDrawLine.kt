package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-09
 * 画线段
 */
class SegmentDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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

        var startX = visibleValues.find { it.time == startPoint.time }?.x ?: -1f
        var endX = visibleValues.find { it.time == endPoint.time }?.x ?: -1f

        Log.d("onDraw", "startX=  $startX  endX= $endX")
        var startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        var endY = chartView.getScaleY(endPoint.value, lMax, lMin)

        // 当前两点均不在可视区域时 因为是线段 不再绘制
        if (startX == -1f && endX == -1f) return

        // 起点在屏幕内，终点滑出右侧边界，需计算右边界的点
        if (startX != -1f && endX == -1f) {
            endX = startX + dataSet.distanceX
            endY = startY + dataSet.distanceY
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        } else if (startX == -1f && endX != -1f) {
            startX = endX - dataSet.distanceX
            startY = endY - dataSet.distanceY
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        } else {
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        }
    }

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return

        // 当前形状是线段 先画线段 再画背景
        val startValue = dataSet.startDrawValue
        val endValue = dataSet.endDrawValue
        if (startValue == null || endValue == null) return

        var startX = visibleValues.find { it.time == startValue.time }?.x ?: -1f
        var startY = chartView.getScaleY(startValue.value, lMax, lMin)

        var endX = visibleValues.find { it.time == endValue.time }?.x ?: -1f
        var endY = chartView.getScaleY(endValue.value, lMax, lMin)

        if (startX != -1f && endX == -1f) {
            endX = startX + dataSet.distanceX
            endY = startY + dataSet.distanceY
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        } else if (startX == -1f && endX != -1f) {
            startX = endX - dataSet.distanceX
            startY = endY - dataSet.distanceY
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        } else {
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        }

        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dataSet.pointRadiusOut * 2f
        linePaint.alpha = 10
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        path.close()
        canvas.drawPath(path, linePaint)
    }
}
