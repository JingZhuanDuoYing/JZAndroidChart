package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.max
import kotlin.math.min

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

    override fun drawTypeShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        val startPoint = dataSet.startDrawValue
        val endPoint = dataSet.endDrawValue
        if (startPoint == null || endPoint == null) return

        if (startPoint.value > lMax && endPoint.value > lMax) return
        if (startPoint.value < lMin && endPoint.value < lMin) return

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
        // 画矩形
        val rect = RectF(startX, startY, endX, endY)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = getLineSizePx(dataSet.lineSize)
        setDashPathEffect(dataSet.dash)
        canvas.drawRect(rect, linePaint)
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        val radius = (dataSet.pointOuterR * 2f)

        val left = min(rect.left.toInt() , rect.right.toInt())
        val right = max(rect.left.toInt() , rect.right.toInt())
        val top = min(rect.top.toInt() , rect.bottom.toInt())
        val bottom = max(rect.top.toInt() , rect.bottom.toInt())

        val leftRegion = Region(left - radius.toInt(), top - radius.toInt(), left + radius.toInt(), bottom + radius.toInt())
        val topRegion = Region(left - radius.toInt(), top - radius.toInt(), right + radius.toInt(), top + radius.toInt())
        val rightRegion = Region(right - radius.toInt(), top - radius.toInt(), right + radius.toInt(), bottom + radius.toInt())
        val bottomRegion = Region(left - radius.toInt(), bottom - radius.toInt(), right + radius.toInt(), bottom + radius.toInt())
//        dataSet.selectRegion = Region(left - radius.toInt(), top - radius.toInt(), right + radius.toInt(), bottom + radius.toInt())
        dataSet.selectRegions.clear()
        dataSet.selectRegions.add(leftRegion)
        dataSet.selectRegions.add(topRegion)
        dataSet.selectRegions.add(rightRegion)
        dataSet.selectRegions.add(bottomRegion)

        // 画矩形四条边的背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = radius
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawRect(rect, linePaint)
        }
    }
}
