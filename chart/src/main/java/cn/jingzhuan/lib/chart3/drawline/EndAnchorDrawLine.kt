package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

        // 画线段
        setDashPathEffect(dataSet.dash)

        val diffW = dataSet.pointOuterR * cos((angle) * Math.PI / 180).toFloat()

        val diffH = dataSet.pointOuterR * sin((angle) * Math.PI / 180).toFloat()

        val rEndX = endX - diffW
        val rEndY = if (endY == 0f) 0f else endY - diffH

        canvas.drawLine(startX, startY, rEndX, rEndY, linePaint)
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

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
        val maxWidth = dataSet.pointOuterR * 1.8f
        val minWidth = dataSet.pointOuterR * 0.75f
        // 以正常显示的40跟为基准算比例
        val percent = 40f / chartView.currentVisibleEntryCount
        var normalWidth = dataSet.pointOuterR * percent
        if (normalWidth > maxWidth) normalWidth = maxWidth
        if (normalWidth < minWidth) normalWidth = minWidth
//        val arrowHeight = dataSet.pointOuterR * 1.8f
        val arrowWidth = normalWidth
        val arrowHeight = normalWidth * 1.8f

        val x1 = endX - arrowWidth
        val y1 = endY + arrowHeight

        val x2 = endX + arrowWidth
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
