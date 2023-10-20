package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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

        val startIndex = baseDataSet.values.indexOfFirst { it.time == startPoint.time }
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)
        if (!dataSet.isSelect) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        val endIndex = baseDataSet.values.indexOfFirst { it.time == endPoint.time }
        val endX = getEntryX(endIndex, baseDataSet) ?: return
        val endY = chartView.getScaleY(endPoint.value, lMax, lMin)
        if (!dataSet.isSelect) {
            endPoint.apply { dataIndex = endIndex; x = endX; y = endY }
        }

        // 画线段
        canvas.drawLine(startX, startY, endX, endY, linePaint)

        // 当前起点与终点的夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

        // 画选中背景
        val path = updatePath(dataSet, angle.toFloat(), startX, startY, endX, endY)
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawPath(path, linePaint)
        }

        // 画箭头
        drawArrow(canvas, dataSet, angle.toFloat(), endX, endY)
    }

    /**
     * 画箭头
     */
    private fun drawArrow(canvas: Canvas, dataSet: DrawLineDataSet, angle: Float, endX: Float, endY: Float) {
        canvas.save()
        // 箭头长度
        val arrowHeight = dataSet.pointOuterR * 1.8f

        val x1 = endX - dataSet.pointOuterR
        val y1 = endY + arrowHeight

        val x2 = endX + dataSet.pointOuterR
        val y2 = endY + arrowHeight

        canvas.rotate(angle + 90f, endX, endY)

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
