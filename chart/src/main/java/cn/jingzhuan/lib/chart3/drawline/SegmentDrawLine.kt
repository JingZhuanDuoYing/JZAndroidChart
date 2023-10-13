package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.abs

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
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return

        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        val startValue = startPoint.value
        val endValue = endPoint.value
        if (startValue > lMax && endValue > lMax) return
        if (startValue < lMin && endValue < lMin) return

//        val key = dataSet.lineKey ?: return
        val startX = visibleValues.find { it.time == startPoint.time }?.x ?: -1f
        var endX = visibleValues.find { it.time == endPoint.time }?.x ?: -1f

        Log.d("onDraw", "startX=  $startX  endX= $endX")
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        val endY = chartView.getScaleY(endPoint.value, lMax, lMin)

        if (startX != -1f && endX != -1f) {
            // 两点的之前的x，y 赋值
            dataSet.distanceX = abs(endX - startX)
            dataSet.distanceY = abs(endY - startY)
        }

        // 当前两点均不在可视区域时 因为是线段 不再绘制
        if (startX == -1f && endX == -1f) return

        // 起点在屏幕内，终点滑出右侧边界，需计算右边界的点
        if (startX != -1f && endX == -1f) {
            // 根据平行截割定理 得(rightY - startY) / (endY - startY) = (rightX - startX) / (endX - startX)
            val rightX = chartView.contentRect.right.toFloat()
            val rightY = (rightX - startX) / dataSet.distanceX * dataSet.distanceY + startY
            Log.d(
                "onDraw", "width= + ${dataSet.distanceX}  height= ${dataSet.distanceY} startY= $startY + rightY= $rightY + endY=  $endY")
            canvas.drawLine(startX, startY, rightX, rightY, linePaint)
        } else if (startX == -1f) {
            // 根据平行截割定理 得(endY - startY) / (leftY - startY) = (endX - startX) / (endX - leftX)
            val leftX = chartView.contentRect.left.toFloat()
            val leftY = dataSet.distanceY / dataSet.distanceX / (endX - leftX) * dataSet.distanceY + startY
            Log.d(
                "onDraw", "width= + ${dataSet.distanceX}  height= ${dataSet.distanceY}, $startY + leftX= $leftX + leftY=  $leftY")
            canvas.drawLine(leftX, leftY, endX, endY, linePaint)
        } else {
            canvas.drawLine(startX, startY, endX, endY, linePaint)
        }
    }

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        // 当前形状是线段 先画线段 再画背景
        val startValue = dataSet.startDrawValue
        val endValue = dataSet.endDrawValue
        if (startValue == null || endValue == null) return

        val startX = baseDataSet.values.find { it.time == startValue.time }?.x ?: return
        val startY = chartView.getScaleY(startValue.value, lMax, lMin)

        val endX = baseDataSet.values.find { it.time == endValue.time }?.x ?: return
        val endY = chartView.getScaleY(endValue.value, lMax, lMin)

        canvas.drawLine(startX, startY, endX, endY, linePaint)

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
