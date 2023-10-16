package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-16
 * 画矩形
 */
class RectDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

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
        } else if (startX == -1f && endX != -1f) {
            startX = endX - dataSet.distanceX
            startY = endY - dataSet.distanceY
        }

        val rect = RectF(startX, startY, endX, endY)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dataSet.lineSize
        canvas.drawRect(rect, linePaint)

        linePaint.style = Paint.Style.FILL
        linePaint.alpha = if(dataSet.isSelect) 10 else 50
        canvas.drawRect(rect, linePaint)
    }
}
