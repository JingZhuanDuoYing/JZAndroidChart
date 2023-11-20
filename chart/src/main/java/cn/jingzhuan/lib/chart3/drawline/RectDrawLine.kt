package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
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
            val startY = chartView.getScaleY(startPoint.value, lMax, lMin)

            val endX = endPoint.x
            val endY = chartView.getScaleY(endPoint.value, lMax, lMin)

            drawShape(canvas, dataSet, startX, startY, endX, endY)
            return
        }

        val startIndex = baseDataSet.values.indexOfFirst { it.time == startPoint.time }
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        if (!dataSet.isSelect || dataSet.isActionUp) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        val endIndex = baseDataSet.values.indexOfFirst { it.time == endPoint.time }
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
        // 画矩形
        val rect = RectF(startX, startY, endX, endY)
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = getLineSizePx(dataSet.lineSize)
        setDashPathEffect(dataSet.dash)
        canvas.drawRect(rect, linePaint)
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        dataSet.selectRegion = Region(rect.left.toInt(), rect.top.toInt(), rect.right.toInt(), rect.bottom.toInt())

        // 画矩形四条边的背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = dataSet.pointOuterR * 2f
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawRect(rect, linePaint)
        }
    }
}
