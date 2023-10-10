package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

/**
 * @since 2023-10-09
 * 画线段
 */
class SegmentDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {
    private val widthSets: MutableMap<String, Float>
    private val heightSets: MutableMap<String, Float>

    private val linePath = Path()

    init {
        widthSets = ConcurrentHashMap()
        heightSets = ConcurrentHashMap()
    }

    override fun onDraw(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        super.onDraw(canvas, dataSet, baseDataSet, lMax, lMin)
        val values = dataSet.values
        if (values.size != 2) return
        val startValue = values[0]
        val endValue = values[1]
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return
        val key = dataSet.lineKey ?: return
        val linePath = Path()
        var startX = -1f
        var endX = -1f
        for (baseValue in visibleValues) {
            if (baseValue.time == startValue.time) {
                startX = baseValue.x
            }
            if (baseValue.time == endValue.time) {
                endX = baseValue.x
            }
        }
        val startY = getScaleY(startValue.value)
        val endY = getScaleY(endValue.value)
        if (startX != -1f && endX != -1f) {
            val width = abs(endX - startX)
            val height = abs(endY - startY)
            widthSets[key] = width
            heightSets[key] = height
        }
        if (startX == -1f && endX == -1f) return
        if (endX == -1f) {
            // 根据平行截割定理 得(rightY - startY) / (endY - startY) = (rightX - startX) / (endX - startX)
            var width = 0f
            var height = 0f
            if (widthSets.containsKey(key)) {
                width = widthSets[key]!!
            }
            if (heightSets.containsKey(key)) {
                height = heightSets[key]!!
            }
            val rightX = chartView.contentRect.right.toFloat()
            val rightY = (rightX - startX) / width * height + startY
            Log.d(
                "onDraw", "width=" + width + "height=" + height +
                        "startY=" + startY + "rightY=" + rightY + "endY=" + endY
            )
            linePath.moveTo(startX, startY)
            linePath.lineTo(rightX, rightY)
        } else {
            linePath.moveTo(startX, startY)
            linePath.lineTo(endX, endY)
        }
        linePaint.style = Paint.Style.STROKE
        canvas.drawPath(linePath, linePaint)
    }

    override fun drawTypeShape(canvas: Canvas, dataSet: DrawLineDataSet) {
        // 当前形状是线段 先画线段 再画背景
        if (dataSet.pointStart == null || dataSet.pointEnd == null) return
        linePaint.style = Paint.Style.STROKE

        linePath.moveTo(dataSet.pointStart!!.x, dataSet.pointStart!!.y)
        linePath.lineTo(dataSet.pointEnd!!.x, dataSet.pointEnd!!.y)
        canvas.drawPath(linePath, linePaint)

        linePath.close()

        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dataSet.pointRadiusIn * 2f
        linePaint.alpha = 30
        linePath.moveTo(dataSet.pointStart!!.x, dataSet.pointStart!!.y)
        linePath.lineTo(dataSet.pointEnd!!.x, dataSet.pointEnd!!.y)

        canvas.drawPath(linePath, linePaint)



        linePath.close()
    }
}
