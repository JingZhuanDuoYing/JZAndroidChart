package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.ScatterTextDataSet
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SCATTER_TEXT_ALIGN_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SCATTER_TEXT_HORIZONTAL_LEFT
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SCATTER_TEXT_HORIZONTAL_RIGHT
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-11
 * @author lei 画标签指示文本
 */

class ScatterTextDraw(
    private val contentRect: Rect,
    private val renderPaint: Paint,
    private val textPaint: Paint,
    private val chartAnimator: ChartAnimator,
) : IDraw<ScatterTextDataSet> {

    init {
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<ScatterTextDataSet>,
        dataSet: ScatterTextDataSet,
        viewport: Viewport,
    ) {
        drawScatterText(
            canvas, dataSet, chartData.dataSets, viewport,
            chartData.leftMax, chartData.leftMin,
            chartData.rightMax, chartData.rightMin
        )
    }

    private fun drawScatterText(
        canvas: Canvas,
        dataSet: ScatterTextDataSet,
        dataSets: List<ScatterTextDataSet>,
        viewport: Viewport,
        lMax: Float,
        lMin: Float,
        rMax: Float,
        rMin: Float,
    ) {

        val min: Float
        val max: Float

        when (dataSet.axisDependency) {
            AxisY.DEPENDENCY_RIGHT -> {
                min = rMin
                max = rMax
            }

            else -> {
                min = lMin
                max = lMax
            }
        }

        renderPaint.strokeWidth = dataSet.lineThickness.toFloat()
        renderPaint.color = dataSet.lineColor
        renderPaint.textSize = dataSet.textSize.toFloat()

        textPaint.color = dataSet.textColor
        textPaint.textSize = dataSet.textSize.toFloat()

        val text = dataSet.text ?: ""
        val bgColor = dataSet.textBgColor
        val lineColor = dataSet.lineColor
        val frameColor = dataSet.frameColor
        val axisDependency = dataSet.axisDependency
        val dashHeight = dataSet.lineDashHeight
        val textPadding = dataSet.textPadding
        var dashLength: Int

        val textBound = Rect()
        renderPaint.getTextBounds(text, 0, text.length, textBound)

        val textRectHeight = textBound.height()
        val textRectWidth = textBound.width()

        val valueCount = dataSet.getEntryCount()
        val visibleRange = dataSet.getVisibleRange(viewport)
        val scale = 1f / viewport.width()
        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - viewport.left * contentRect.width() * scale

        val valuePhaseCount = floor((valueCount * chartAnimator.phaseX).toDouble()).toInt()

        val pointWidth = contentRect.width() / max(visibleRange, dataSet.minValueCount.toFloat())

        val dataSetIndex = dataSets.indexOf(dataSet)

        val startIndexOffset = 0

        val dataSize = dataSet.values.size
        var leftIndex = (dataSize * viewport.left).roundToInt()
        leftIndex = max(leftIndex, 0)
        var rightIndex = (dataSize * viewport.right).roundToInt()
        rightIndex = min(rightIndex, dataSize)

        var i = leftIndex

        while (i < rightIndex) {
            val value = dataSet.getEntryForIndex(i)
            if (value == null || !value.isVisible || value.high.isNaN() || value.low.isNaN()) {
                i++
                continue
            }

            val offset: Int = calOffset(dataSets, dataSetIndex, i, textPadding)

            dashLength = dashHeight + offset


            val xPosition = startX + step * (i + startIndexOffset)
            val yHighPosition = (max - value.high.toFloat()) / (max - min) * contentRect.height()
            val yLowPosition = (max - value.low.toFloat()) / (max - min) * contentRect.height()
            var anchor = yHighPosition


            val centerX = xPosition + pointWidth * 0.5f
            value.setCoordinate(centerX, anchor)

            var bottom = anchor - dashLength
            var top: Float = bottom - textRectHeight - textPadding * 2
            var pathEnd = bottom

            // 如果此时顶部位置小于安全空间 向下
            if (top < dataSet.safeSpace) {
                anchor = yLowPosition
                bottom = anchor + dashLength + textRectHeight
                top = bottom - textRectHeight - textPadding * 2
                pathEnd = top
            }

            val roundRect = RectF()
            var right : Float
            var left : Float

            when (dataSet.horizontalAlignment) {
                SCATTER_TEXT_HORIZONTAL_LEFT -> {
                    right = centerX + textPadding
                    left = centerX - textRectWidth - textPadding
                }

                SCATTER_TEXT_HORIZONTAL_RIGHT -> {
                    right = centerX + textRectWidth + textPadding
                    left = centerX - textPadding
                }

                else -> {
                    right = centerX + textRectWidth * 0.5f + textPadding
                    left = centerX - textRectWidth * 0.5f - textPadding
                }
            }

            if (axisDependency == AxisY.DEPENDENCY_BOTH) {
                val maxRight = contentRect.right - renderPaint.strokeWidth
                if (right > maxRight && centerX < contentRect.right) {
                    right = maxRight
                    left = maxRight - textRectWidth - textPadding * 2
                }
                val minLeft = renderPaint.strokeWidth
                if (left < minLeft && centerX > 0f) {
                    left = minLeft
                    right = left + textRectWidth + textPadding * 2
                }
            }

            if (dataSet.align == SCATTER_TEXT_ALIGN_BOTTOM) {
                anchor = yLowPosition
                bottom = anchor + dashLength + textRectHeight
                top = bottom - textRectHeight - textPadding * 2
                pathEnd = top

                // 如果此时底部位置大于(mContentRect.height() - safeSpace) 向上
                if (bottom > contentRect.height() - dataSet.safeSpace) {
                    anchor = yHighPosition
                    bottom = anchor - dashLength
                    top = bottom - textRectHeight - textPadding * 2
                    pathEnd = bottom
                }
            }

            // 左边界
            if (centerX < textRectWidth * 0.5f + textPadding && centerX > 0 && dataSet.isCircle) {
                top = anchor + textRectHeight * 0.5f + textPadding
                bottom = anchor - textRectHeight * 0.5f - textPadding
                right = centerX + dashLength + textRectWidth + textPadding * 2
                left = right - textRectWidth - textPadding * 2
                if (bottom < dataSet.safeSpace.toFloat()) {
                    bottom = dataSet.safeSpace.toFloat()
                    top = bottom + textRectHeight + textPadding * 2
                }
                if (top > contentRect.height() - dataSet.safeSpace) {
                    top = (contentRect.height() - dataSet.safeSpace).toFloat()
                    bottom = top - textRectHeight - textPadding * 2
                }
            }

            // 右边界
            if (centerX > contentRect.right - (textRectWidth * 0.5f + textPadding) && centerX < contentRect.right && dataSet.isCircle) {
                top = anchor + textRectHeight * 0.5f + textPadding
                bottom = anchor - textRectHeight * 0.5f - textPadding
                left = centerX - dashLength - textRectWidth - textPadding * 2
                right = left + textRectWidth + textPadding * 2
                if (bottom < dataSet.safeSpace.toFloat()) {
                    bottom = dataSet.safeSpace.toFloat()
                    top = bottom + textRectHeight + textPadding * 2
                }
                if (top > contentRect.height() - dataSet.safeSpace) {
                    top = (contentRect.height() - dataSet.safeSpace).toFloat()
                    bottom = top - textRectHeight - textPadding * 2
                }
            }

            roundRect.set(left, top, right, bottom)

            val radius = if (dataSet.isCircle) textRectWidth.toFloat() else 2f

            renderPaint.pathEffect = null
            renderPaint.style = Paint.Style.FILL
            renderPaint.isAntiAlias = true
            renderPaint.color = bgColor
            canvas.drawRoundRect(roundRect, radius, radius, renderPaint)

            renderPaint.style = Paint.Style.STROKE
            renderPaint.isAntiAlias = true
            renderPaint.color = frameColor
            canvas.drawRoundRect(roundRect, radius, radius, renderPaint)

            val fontMetrics = textPaint.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline = roundRect.centerY() + distance
            canvas.drawText(text, roundRect.centerX(), baseline, textPaint)

            val path = Path()
            if (centerX < textRectWidth * 0.5f + textPadding && centerX > 0 && dataSet.isCircle) {
                // 左边界
                if (bottom == dataSet.safeSpace.toFloat()) {
                    // 上边界
                    path.moveTo(centerX, anchor)
                    path.lineTo(
                        centerX + dashLength,
                        anchor + (textRectHeight + textPadding * 2) * 0.5f
                    )
                } else if (top == (contentRect.height() - dataSet.safeSpace).toFloat()) {
                    // 下边界
                    path.moveTo(centerX, anchor)
                    path.lineTo(
                        centerX + dashLength,
                        anchor - (textRectHeight + textPadding * 2) * 0.5f
                    )
                } else {
                    path.moveTo(centerX, anchor)
                    path.lineTo(centerX + dashLength, anchor)
                }
            } else if (centerX > contentRect.right - (textRectWidth * 0.5f + textPadding) && centerX < contentRect.right && dataSet.isCircle) {
                // 右边界
                if (bottom == dataSet.safeSpace.toFloat()) {
                    path.moveTo(centerX, anchor)
                    path.lineTo(
                        centerX - dashLength,
                        anchor + (textRectHeight + textPadding * 2) * 0.5f
                    )
                } else if (top == (contentRect.height() - dataSet.safeSpace).toFloat()) {
                    // 下边界
                    path.moveTo(centerX, anchor)
                    path.lineTo(
                        centerX - dashLength,
                        anchor - (textRectHeight + textPadding * 2) * 0.5f
                    )
                } else {
                    path.moveTo(centerX, anchor)
                    path.lineTo(centerX - dashLength, anchor)
                }
            } else {
                path.moveTo(centerX, anchor)
                path.lineTo(centerX, pathEnd)
            }

            renderPaint.color = lineColor
            renderPaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f, 5f, 5f), 0f)
            canvas.drawPath(path, renderPaint)

            i++
        }
        renderPaint.pathEffect = null
    }

    private fun calOffset(
        dataSets: List<ScatterTextDataSet>,
        dataSetIndex: Int,
        i: Int,
        textPadding: Int,
    ): Int {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        val textBound = Rect()
        var offset = 0
        var invalidTime = 0
        for (j in 0 until dataSetIndex) {
            val dataSet = dataSets[j]

            if (dataSet.align != dataSets[dataSetIndex].align) {
                invalidTime++
                continue
            }

            val value = dataSet.getEntryForIndex(i)
            if (value == null || !value.isVisible || value.high.isNaN() || value.low.isNaN()) {
                invalidTime++
                continue
            }

            val text = dataSet.text ?: ""
            paint.textSize = dataSet.textSize.toFloat()
            paint.getTextBounds(text, 0, text.length, textBound)
            val textHeight = textBound.height()
            offset += textHeight + textPadding * 2
        }
        if (offset > 0) {
            offset += textPadding * (dataSetIndex - invalidTime)
        }
        return offset
    }

}