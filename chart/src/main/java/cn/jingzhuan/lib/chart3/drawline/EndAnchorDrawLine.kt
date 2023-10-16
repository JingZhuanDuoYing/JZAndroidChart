package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.atan2

/**
 * @since 2023-10-16
 * 画终点箭头线段
 */
class EndAnchorDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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
        // 画箭头
        drawArrow(canvas, dataSet, startX, startY, endX, endY)
    }

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return

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

        // 画箭头
        drawArrow(canvas, dataSet, startX, startY, endX, endY)
    }

    private fun drawArrow(canvas: Canvas, dataSet: DrawLineDataSet, startX: Float, startY: Float, endX: Float, endY: Float) {
        // 画箭头

        canvas.save()
        // 箭头长度
        val arrowHeight = dataSet.pointRadiusOut * 1.8f
        // 当前夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

        val x1 = endX - dataSet.pointRadiusOut
        val y1 = endY + arrowHeight

        val x2 = endX + dataSet.pointRadiusOut
        val y2 = endY + arrowHeight

        canvas.rotate(angle.toFloat() + 90f, endX, endY)

        linePaint.style = Paint.Style.FILL
        linePaint.alpha = 255
        linePaint.strokeWidth = 1f
        val trianglePath = Path()
        trianglePath.moveTo(endX, endY)
        trianglePath.lineTo(x1, y1)
        trianglePath.lineTo(x2, y2)
        trianglePath.close()
        canvas.drawPath(trianglePath, linePaint)

        canvas.restore()
    }
}
