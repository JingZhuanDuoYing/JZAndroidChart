package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Region
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.utils.NumberUtils

/**
 * @since 2023-10-18
 * 画黄金分割
 */
class HJFGDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {

    private val ratioArray = floatArrayOf(0.236f, 0.382f, 0.5f, 0.618f, 0.809f, 1.618f, 2.618f)

    private val sb = StringBuilder()

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
//            val startX = startPoint.x
            val startY = chartView.getScaleY(startPoint.value.toFloat(), lMax, lMin)

//            val endX = endPoint.x
            val endY = chartView.getScaleY(endPoint.value.toFloat(), lMax, lMin)

            drawShape(canvas, dataSet, startY, endY, lMax, lMin)
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
        drawShape(canvas, dataSet, startY, endY, lMax, lMin)
    }

    private fun drawShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        startY: Float,
        endY: Float,
        lMax: Float,
        lMin: Float
    ) {
        val width = chartView.contentRect.width().toFloat()
        val height = chartView.contentRect.height().toFloat()

        // 直径
        val diam = dataSet.pointOuterR * 2f

        // 黄金分割不适配虚线
        linePaint.pathEffect = null

        // 画第一条线
        linePaint.style = Paint.Style.FILL
        // 黄金分割固定线宽
        linePaint.strokeWidth = getLineSizePx(2f)
        linePaint.alpha = 255
        canvas.drawLine(0f, startY, width, startY, linePaint)

        // 画第一条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = diam
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawLine(0f, startY, width, startY, linePaint)
        }

        // 画文本
        sb.append("base")
        val value1 = NumberUtils.keepPrecision((lMax - startY / height * (lMax - lMin)).toString(), chartView.decimalDigitsNumber)
        sb.append("  $value1")
        val text1 = sb.toString()
        canvas.drawText(text1, 15f, startY - 5f, textPaint)
        sb.clear()

        // 画第二条线
        linePaint.style = Paint.Style.FILL
        // 黄金分割固定线宽
        linePaint.strokeWidth = getLineSizePx(2f)
        linePaint.alpha = 255
        canvas.drawLine(0f, endY, width, endY, linePaint)

        // 画第二条线选中背景
        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.strokeWidth = diam
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawLine(0f, endY, width, endY, linePaint)
        }

        // 画文本
        sb.append("100%")
        val value2 = NumberUtils.keepPrecision((lMax - endY / height * (lMax - lMin)).toString(), chartView.decimalDigitsNumber)
        sb.append("  $value2")
        val text2 = sb.toString()
        canvas.drawText(text2, 15f, endY - 5f, textPaint)
        sb.clear()

        // 画黄金分割虚线 固定比例 7条虚线
        linePaint.strokeWidth = 3f
        linePaint.alpha = 255
        linePaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f, 5f, 5f), 0f)

        ratioArray.toList().forEach {
            // 画虚线
            val y = startY + (endY - startY) * it
            canvas.drawLine(0f, y, width, y, linePaint)

            // 画文本
            val ratio = "${NumberUtils.keepPrecision((it * 100).toString(), 1)}%"
            sb.append(ratio)
            val value = NumberUtils.keepPrecision((lMax - y / height * (lMax - lMin)).toString(), chartView.decimalDigitsNumber)
            sb.append("  $value")
            val text = sb.toString()
            canvas.drawText(text, 15f, y - 5f, textPaint)
            sb.clear()

        }
        linePaint.pathEffect = null

        if (startY < endY) {
            dataSet.selectRegion = Region(0, (startY - diam).toInt(), chartView.contentRect.width(), (endY + diam).toInt())
        } else {
            dataSet.selectRegion = Region(0, (endY - diam).toInt(), chartView.contentRect.width(), (startY + diam).toInt())
        }
    }
}
