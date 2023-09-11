package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_NONE
import kotlin.math.max

/**
 * @since 2023-09-06
 * @author lei 画K线
 */
class CandlestickDraw(var contentRect: Rect, private var renderPaint: Paint) : IDraw<CandlestickDataSet> {

    private val upperShadowBuffers = FloatArray(4)

    private val lowerShadowBuffers = FloatArray(4)

    private val bodyBuffers = FloatArray(4)


    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<CandlestickDataSet>,
        dataSet: CandlestickDataSet,
        viewport: Viewport,
    ) {
        if (dataSet.isVisible) {
            drawCandlestick(
                canvas, dataSet, viewport,
                chartData.leftMax, chartData.leftMin,
                chartData.rightMax, chartData.rightMin
            )
        }
    }

    private fun drawCandlestick(
        canvas: Canvas,
        candlestickDataSet: CandlestickDataSet,
        viewport: Viewport,
        lMax: Float,
        lMin: Float,
        rMax: Float,
        rMin: Float,
    ) {
        val min: Float
        val max: Float

        when (candlestickDataSet.axisDependency) {
            AxisY.DEPENDENCY_RIGHT -> {
                min = rMin
                max = rMax
            }

            else -> {
                min = lMin
                max = lMax
            }
        }

        renderPaint.strokeWidth = candlestickDataSet.strokeThickness
        renderPaint.color = candlestickDataSet.color

        val valueCount = candlestickDataSet.getEntryCount()

        val visibleValues = candlestickDataSet.getVisiblePoints(viewport)
        if (visibleValues.isNullOrEmpty()) return

        val visibleRange = candlestickDataSet.getVisibleRange(viewport)

        val scale = 1.0f / viewport.width()
        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - viewport.left * contentRect.width() * scale
        var candleWidth = candlestickDataSet.candleWidth

        if (candlestickDataSet.isAutoWidth) {
            candleWidth = (contentRect.width() / max(visibleRange, candlestickDataSet.minValueCount.toFloat()))
        }

        val widthPercent = candlestickDataSet.candleWidthPercent

        var i = 0
        while (i < valueCount && i < candlestickDataSet.values.size) {
            val candlestick = candlestickDataSet.getEntryForIndex(i)
            if (!candlestick!!.isVisible) {
                i++
                continue
            }
            if (!visibleValues.contains(candlestick)) {
                i++
                continue
            }

            val xPosition = startX + step * i
            val candlestickCenterX = xPosition + candleWidth * 0.5

            if (candlestickDataSet.enableGap) {

                renderPaint.color = candlestickDataSet.gapColor
                renderPaint.style = Paint.Style.FILL

                if (candlestickDataSet.lowGaps.size() > 0) {
                    // 缺口
                    val gap = candlestickDataSet.lowGaps[i, null]

                    if (gap != null) {
                        val y1: Float = (max - gap.first) / (max - min) * contentRect.height()
                        val y2: Float = (max - gap.second) / (max - min) * contentRect.height()

                        canvas.drawRect(
                            xPosition,
                            y1,
                            contentRect.right.toFloat(),
                            y2,
                            renderPaint
                        )
                    }
                }
                if (candlestickDataSet.highGaps.size() > 0) {
                    // 缺口
                    val gap = candlestickDataSet.highGaps[i, null]

                    if (gap != null) {
                        val y1: Float = (max - gap.first) / (max - min) * contentRect.height()
                        val y2: Float = (max - gap.second) / (max - min) * contentRect.height()

                        canvas.drawRect(
                            candlestickCenterX.toFloat(),
                            y1,
                            contentRect.right.toFloat(),
                            y2,
                            renderPaint
                        )
                    }
                }
            }

            if (candlestick.fillBackgroundColor != COLOR_NONE) { // 画背景
                renderPaint.color = candlestick.fillBackgroundColor
                renderPaint.style = Paint.Style.FILL
                canvas.drawRect(
                    xPosition,
                    0f,
                    (xPosition + candleWidth),
                    canvas.height.toFloat(),
                    renderPaint
                )
            }

            val highY: Float = (max - candlestick.high) / (max - min) * contentRect.height()
            val lowY: Float = (max - candlestick.low) / (max - min) * contentRect.height()
            val openY: Float = (max - candlestick.open) / (max - min) * contentRect.height()
            val closeY: Float = (max - candlestick.close) / (max - min) * contentRect.height()

            bodyBuffers[0] = (xPosition + (1 - widthPercent) * 0.5 * candleWidth).toFloat()
            bodyBuffers[1] = closeY
            bodyBuffers[2] = (bodyBuffers[0] + candleWidth * widthPercent)
            bodyBuffers[3] = openY

            upperShadowBuffers[0] = candlestickCenterX.toFloat()
            upperShadowBuffers[2] = candlestickCenterX.toFloat()
            lowerShadowBuffers[0] = candlestickCenterX.toFloat()
            lowerShadowBuffers[2] = candlestickCenterX.toFloat()
            candlestick.setCoordinate(candlestickCenterX.toFloat(), closeY)

            if (candlestick.open.compareTo(candlestick.close) > 0) {
                // 阴线
                upperShadowBuffers[1] = highY
                upperShadowBuffers[3] = openY
                lowerShadowBuffers[1] = lowY
                lowerShadowBuffers[3] = closeY

                if (candlestick.color == COLOR_NONE) {
                    renderPaint.color = candlestickDataSet.decreasingColor
                } else {
                    renderPaint.color = candlestick.color
                }

                if (candlestick.paintStyle != null) {
                    renderPaint.style = candlestick.paintStyle
                } else {
                    renderPaint.style = candlestickDataSet.decreasingPaintStyle
                }
            } else if (candlestick.open.compareTo(candlestick.close) < 0) {
                // 阳线
                upperShadowBuffers[1] = highY
                upperShadowBuffers[3] = closeY
                lowerShadowBuffers[1] = lowY
                lowerShadowBuffers[3] = openY

                if (candlestick.color == COLOR_NONE) {
                    renderPaint.color = candlestickDataSet.increasingColor
                } else {
                    renderPaint.color = candlestick.color
                }

                if (candlestick.paintStyle != null) {
                    renderPaint.style = candlestick.paintStyle
                } else {
                    renderPaint.style = candlestickDataSet.increasingPaintStyle
                }
            } else {
                upperShadowBuffers[1] = highY
                upperShadowBuffers[3] = openY
                lowerShadowBuffers[1] = lowY
                lowerShadowBuffers[3] = upperShadowBuffers[3]

                if (candlestick.color == COLOR_NONE) {
                    renderPaint.color = candlestickDataSet.neutralColor
                } else {
                    renderPaint.color = candlestick.color
                }
            }

            if (i > 0) {
                val previousValue = candlestickDataSet.getEntryForIndex(i - 1)
                val isLimitUp20 =
                    candlestick.close.compareTo(previousValue!!.close * 1.2f - 0.01f) > 0
                            && candlestick.close == candlestick.high

                val isLimitUp = candlestick.close.compareTo(previousValue.close * 1.1f - 0.01f) > 0
                        && candlestick.close == candlestick.high

                if (candlestickDataSet.limitUpColor != Color.TRANSPARENT) {
                    if (isLimitUp20) {
                        renderPaint.color = candlestickDataSet.limitUpColor20
                    } else if (isLimitUp) {
                        renderPaint.color = candlestickDataSet.limitUpColor
                    }
                }

                if (candlestickDataSet.limitUpPaintStyle != null) {
                    if (isLimitUp || isLimitUp20) {
                        renderPaint.style = candlestickDataSet.limitUpPaintStyle
                    }
                }
            }

            if (bodyBuffers[1] == bodyBuffers[3]) {
                canvas.drawLine(
                    bodyBuffers[0],
                    bodyBuffers[1],
                    bodyBuffers[2],
                    bodyBuffers[3],
                    renderPaint
                )
            } else {
                canvas.drawRect(
                    bodyBuffers[0],
                    bodyBuffers[1],
                    bodyBuffers[2],
                    bodyBuffers[3],
                    renderPaint
                )
            }

            canvas.drawLines(upperShadowBuffers, renderPaint)
            canvas.drawLines(lowerShadowBuffers, renderPaint)
            i++
        }

    }

}