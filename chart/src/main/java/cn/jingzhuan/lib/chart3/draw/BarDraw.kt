package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart.utils.FloatUtils
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import java.lang.Float.isNaN
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @since 2023-09-12
 * @author lei 画柱子
 */
class BarDraw(
    private val contentRect: Rect,
    private val renderPaint: Paint,
    private val textPaint: Paint,
    private val chartAnimator: ChartAnimator,
) : IDraw<BarDataSet> {

    private val labelBuffer = CharArray(100)

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<BarDataSet>,
        dataSet: BarDataSet,
        viewport: Viewport,
    ) {
        if (dataSet.isVisible) {
            drawBar(
                canvas, dataSet, viewport,
                chartData.leftMax, chartData.leftMin,
                chartData.rightMax, chartData.rightMin
            )
        }
    }

    private fun drawBar(
        canvas: Canvas,
        dataSet: BarDataSet,
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

        val strokeThickness = dataSet.strokeThickness

        renderPaint.strokeWidth = strokeThickness
        renderPaint.style = Paint.Style.FILL

        textPaint.color = dataSet.valueColor
        textPaint.textSize = dataSet.valueTextSize

        val valueCount = dataSet.getEntryCount()

        var width = dataSet.barWidth
        val visibleRange = dataSet.getVisibleRange(viewport)

        if (dataSet.isAutoBarWidth && visibleRange > 0) {
            width = contentRect.width() / visibleRange
            if (dataSet.minBarWidth > 0.0f && width < dataSet.minBarWidth) {
                width = dataSet.minBarWidth
            }
        }
        val percent: Float = dataSet.barWidthPercent

        val scale = 1f / viewport.width()
        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - viewport.left * contentRect.width() * scale


        var i = 0
        while (i < valueCount && i < dataSet.values.size) {
            val barValue = dataSet.getEntryForIndex(i)
            if (barValue == null || !barValue.isEnable) {
                i++
                continue
            }

            val floatValues = barValue.values
            if (floatValues == null || floatValues.isEmpty() || isNaN(floatValues.first())) {
                i++
                continue
            }

            if (barValue.color != -2) {
                renderPaint.color = barValue.color
            } else {
                renderPaint.color = dataSet.color
            }

            val startIndexOffset = 0

            val x = startX + step * (i + startIndexOffset)
            var top: Float
            var bottom: Float = calcHeight(0f, max, min)

            if (barValue.valueCount > 0) {
                val value = floatValues[0] * chartAnimator.phaseY
                var style = barValue.paintStyle
                top = calcHeight(value, max, min)

                if (top == 0f && style == Paint.Style.STROKE) {
                    top += strokeThickness
                }

                if (width < strokeThickness * 2 && style == Paint.Style.STROKE) {
                    style = Paint.Style.FILL
                }

                if (barValue.valueCount > 1) bottom = calcHeight(floatValues[1], max, min)

                val roundBottom = bottom.roundToInt()
                if (roundBottom == contentRect.height()) {
                    bottom = contentRect.height() - strokeThickness
                    if (style == Paint.Style.FILL) {
                        bottom += strokeThickness / 2
                    }
                }

                barValue.setCoordinate(x + width * 0.5f, top)

                renderPaint.style = style

                val left = x + width * (1 - percent) * 0.5f
                val right = left + width * percent

                if (abs(top - bottom) < 0.0001f) {
                    canvas.drawLine(left, top, right, bottom, renderPaint)
                } else {
                    val gradientColors = barValue.gradientColors
                    if (gradientColors != null && gradientColors.size > 1) {
                        val centerX = (left + right) * 0.5f
                        renderPaint.shader = LinearGradient(
                            centerX, top, centerX, bottom,
                            gradientColors[0],
                            gradientColors[1], Shader.TileMode.MIRROR
                        )
                    }
                    canvas.drawRect(left, top, right, bottom, renderPaint)
                }

                var labelLength: Int
                var labelOffset: Int
                if (dataSet.isDrawValueEnable) {
                    val valueFormatter = dataSet.valueFormatter
                    if (valueFormatter == null) {
                        labelLength = FloatUtils.formatFloatValue(labelBuffer, value, 2)
                    } else {
                        val labelCharArray = valueFormatter.format(floatValues[0], i).toCharArray()
                        labelLength = labelCharArray.size
                        System.arraycopy(
                            labelCharArray,
                            0,
                            labelBuffer,
                            labelBuffer.size - labelLength,
                            labelLength
                        )
                    }
                    labelOffset = labelBuffer.size - labelLength
                    textPaint.textAlign = Paint.Align.CENTER
                    canvas.drawText(
                        labelBuffer, labelOffset, labelLength,
                        x + width * 0.5f,
                        top - 10, textPaint
                    )
                }
            }
            renderPaint.shader = null
            i++
        }

    }

    private fun calcHeight(value: Float, max: Float, min: Float): Float {
        return if (max.compareTo(min) == 0) 0f else (max - value) / (max - min) * contentRect.height()
    }

}