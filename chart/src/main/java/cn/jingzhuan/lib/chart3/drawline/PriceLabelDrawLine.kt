package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.utils.NumberUtils
import kotlin.math.atan2
import kotlin.math.roundToInt

/**
 * @since 2023-10-16
 * 价格标注
 */
class PriceLabelDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {
    private val sb = StringBuilder()

    private val mTextBounds = Rect()

    private val padding by lazy {
        chart.resources.getDimensionPixelSize(R.dimen.jz_chart_highlight_text_padding)
    }

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
        lMin: Float,
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

            drawShape(canvas, dataSet, startX, startY, endX, endY, startPoint.value)
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

        drawShape(canvas, dataSet, startX, startY, endX, endY, startPoint.value)
    }

    private fun drawShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        textValue: Float
    ) {
        // 画线段 固定虚线"5,3,5,4"
        setDashPathEffect("5,3,5,4")
        // 线的厚度也固定1
//        linePaint.strokeWidth = getLineSizePx(1f)
        canvas.drawLine(startX, startY, endX, endY, linePaint)
        if (linePaint.pathEffect != null) linePaint.pathEffect = null

        // 画起点价格 位置显示在第二个点
        val text = NumberUtils.keepPrecision(textValue.toString(), chartView.decimalDigitsNumber)
        sb.append(text)

        val textWidth = textPaint.measureText(text).roundToInt() + padding * 2
        textPaint.getTextBounds(text, 0, text.length, mTextBounds)
        val textHeight = mTextBounds.height()

        val left = if (startX <= endX) endX else endX - textWidth
        val right = if (startX <= endX) endX + textWidth else endX
        val bottom = endY + textHeight + padding * 2
        val rect = RectF(left, endY, right, bottom)
        linePaint.style = Paint.Style.STROKE
        canvas.drawRect(rect, linePaint)

        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = rect.centerY() + distance

        canvas.drawText(sb.toString(), left + padding, baseline, textPaint)
        sb.clear()

        // 当前起点与终点的夹角
        val angle = atan2(endY - startY,  endX - startX) * 180 / Math.PI

        // 画选中背景
        val path = updatePath(dataSet, angle.toFloat(), startX, startY, endX, endY)

        dataSet.selectRegions.clear()
        dataSet.selectRegions.add(dataSet.selectRegion)
        val rectRegion = Region(rect.left.toInt(), rect.top.toInt(), rect.right.toInt(), rect.bottom.toInt())
        dataSet.selectRegions.add(rectRegion)

        if (dataSet.isSelect) {
            linePaint.style = Paint.Style.FILL
            linePaint.alpha = dataSet.selectAlpha
            canvas.drawPath(path, linePaint)
        }
    }
}