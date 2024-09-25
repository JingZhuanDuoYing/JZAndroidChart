package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.Pair
import androidx.core.util.containsValue
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.value.CandlestickValue
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_NONE
import cn.jingzhuan.lib.chart3.utils.NumberUtils
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-06
 * @author lei 画K线
 */
class CandlestickDraw(
    private val contentRect: Rect,
    private val renderPaint: Paint,
    var textPaint: Paint = Paint(),
    var decimalDigitsNumber: Int = 2,
    var showScaleMin: Boolean = false
) : IDraw<CandlestickDataSet> {

    private val upperShadowBuffers = FloatArray(4)

    private val lowerShadowBuffers = FloatArray(4)

    private val bodyBuffers = FloatArray(4)

    private var linePaths: MutableList<Path> = ArrayList()


    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<CandlestickDataSet>,
        dataSet: CandlestickDataSet,
        viewport: Viewport,
    ) {
        if (dataSet.isVisible) {
            if (showScaleMin) {
                drawCandlestickMinLine(
                    canvas, dataSet, viewport,
                    chartData.leftMax, chartData.leftMin,
                    chartData.rightMax, chartData.rightMin
                )
            } else {
                drawCandlestick(
                    canvas, dataSet, viewport,
                    chartData.leftMax, chartData.leftMin,
                    chartData.rightMax, chartData.rightMin
                )
            }
        }
    }

    private fun drawCandlestickMinLine(
        canvas: Canvas,
        dataSet: CandlestickDataSet,
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

        val lineThickness: Int = dataSet.lineThickness

        renderPaint.style = Paint.Style.STROKE
        renderPaint.strokeWidth = lineThickness.toFloat()
        renderPaint.color = dataSet.lineColor

        linePaths.clear()

        val valueCount = dataSet.getEntryCount()

        val scale = 1.0f / viewport.width()

        val step = contentRect.width() * scale / valueCount
//        contentRect.left - viewport.left * contentRect.width() * scale
        val startX =
            contentRect.left + (step * 0.5f) - viewport.left * contentRect.width() * scale

        val linePath = Path()

        val startIndexOffset = 0

        val dataSize = dataSet.values.size
        var leftIndex = (dataSize * viewport.left).roundToInt()
        leftIndex = max(leftIndex, 0)
        var rightIndex = (dataSize * viewport.right).roundToInt()
        rightIndex = min(rightIndex, dataSize)

        var i = leftIndex
        var isFirst = true

        while (i < rightIndex) {
            val value: CandlestickValue? = dataSet.getEntryForIndex(i)
            val close = value?.close
            if (close == null || close.isNaN()) {
                i++
                continue
            }
            val xPosition = startX + step * (i + startIndexOffset)
            val yPosition: Float =
                (max - close) / (max - min) * (contentRect.height() - 2 * lineThickness) + lineThickness * 0.5f
            value.setCoordinate(xPosition, yPosition)

            if (isFirst) {
                isFirst = false
                linePath.moveTo(xPosition, yPosition)
            } else {
                linePath.lineTo(xPosition, yPosition)
            }

            linePath.lineTo(xPosition, yPosition)
            i++
        }

        if (!isFirst) {
            linePaths.add(linePath)
        }

        for (path in linePaths) {
            canvas.drawPath(path, renderPaint)
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

//        val visibleValues = candlestickDataSet.getVisiblePoints(viewport)
//        if (visibleValues.isNullOrEmpty()) return

        val visibleRange = candlestickDataSet.getVisibleRange(viewport)

        val scale = 1.0f / viewport.width()
        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - viewport.left * contentRect.width() * scale
        var candleWidth = candlestickDataSet.candleWidth

        if (candlestickDataSet.isAutoWidth) {
            candleWidth = (contentRect.width() / max(visibleRange, candlestickDataSet.minValueCount.toFloat()))
        }

        val widthPercent = candlestickDataSet.candleWidthPercent

        val gapArray = ArrayList<Pair<Float, Float>>()

        val dataSize = candlestickDataSet.values.size
        var leftIndex = (dataSize * viewport.left).roundToInt()
        leftIndex = max(leftIndex, 0)
        var rightIndex = (dataSize * viewport.right).roundToInt()
        rightIndex = min(rightIndex, dataSize)

        var i = leftIndex
        while (i < rightIndex) {
            val candlestick = candlestickDataSet.getEntryForIndex(i)
            if (candlestick == null || !candlestick.isVisible) {
                i++
                continue
            }
//            if (!visibleValues.contains(candlestick)) {
//                i++
//                continue
//            }

            val xPosition = startX + step * i
            val candlestickCenterX = xPosition + candleWidth * 0.5

            // 画缺口
            if (candlestickDataSet.enableGap) {
                val lowGap = candlestickDataSet.lowGaps[i, null]
                if (lowGap != null) gapArray.add(lowGap)
                val highGap = candlestickDataSet.highGaps[i, null]
                if (highGap != null) gapArray.add(highGap)
            }

            // 画背景
            if (candlestick.fillBackgroundColor != COLOR_NONE) {
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
                var top = min(bodyBuffers[1], bodyBuffers[3])
                var bottom = max(bodyBuffers[1], bodyBuffers[3])

                if ((bottom - top).absoluteValue <= candlestickDataSet.strokeThickness) {
                    renderPaint.style = Paint.Style.FILL
                    val center = (bottom + top).times(0.5f)
                    top = center - candlestickDataSet.strokeThickness.times(0.5f)
                    bottom = center + candlestickDataSet.strokeThickness.times(0.5f)
                }
                canvas.drawRect(
                    bodyBuffers[0],
                    top,
                    bodyBuffers[2],
                    bottom,
                    renderPaint
                )
            }

            canvas.drawLines(upperShadowBuffers, renderPaint)
            canvas.drawLines(lowerShadowBuffers, renderPaint)
            i++
        }

        if (gapArray.isNotEmpty()) {
            renderPaint.color = candlestickDataSet.gapColor
            renderPaint.style = Paint.Style.FILL
            var gaps = gapArray.toList()
            val size = gapArray.size
            val maxSize = candlestickDataSet.gapMaxSize
            if (size > maxSize) {
                gaps = gapArray.subList(size - maxSize, size)
            }
            gaps.forEach { value ->
                if (candlestickDataSet.lowGaps.containsValue(value)) {
                    val index = candlestickDataSet.lowGaps.indexOfValue(value)
                    val lowIndex = candlestickDataSet.lowGaps.keyAt(index)
                    val xPosition = startX + step * lowIndex
                    val delX = xPosition + candleWidth * 0.5f
                    drawGap(canvas, value, delX, max, min, candleWidth, 0)
                }

                if (candlestickDataSet.highGaps.containsValue(value)) {
                    val index = candlestickDataSet.highGaps.indexOfValue(value)
                    val highIndex = candlestickDataSet.highGaps.keyAt(index)
                    val xPosition = startX + step * highIndex
                    val delX = xPosition + candleWidth * 0.5f
                    drawGap(canvas, value, delX, max, min, candleWidth, 1)
                }

            }

        }

    }

    private fun drawGap(
        canvas: Canvas,
        gap: Pair<Float, Float>?,
        startX: Float,
        max: Float,
        min: Float,
        candleWidth: Float,
        type: Int
    ) {
        if (gap != null) {
            val y1: Float = (max - gap.first) / (max - min) * contentRect.height()
            val y2: Float = (max - gap.second) / (max - min) * contentRect.height()

            canvas.drawRect(
                startX,
                min(y1, y2),
                contentRect.right.toFloat(),
                max(y1, y2),
                renderPaint
            )

            val firstText = NumberUtils.keepPrecision("${gap.first}", decimalDigitsNumber)
            val secondText = NumberUtils.keepPrecision("${gap.second}", decimalDigitsNumber)

            val text = "$firstText-$secondText"
            val rect = Rect()
            textPaint.getTextBounds(text, 0, text.length, rect)
            val textHeight = rect.height()
            val baseline = if (type == 0) y1 - 3f else y1 + textHeight + 3f

            val textWidth = textPaint.measureText(text)

            val totalWidth = contentRect.right.toFloat() - startX - candleWidth * 0.5f

            if (textWidth > totalWidth) {
                val singleWidth = textWidth / text.length.toFloat()
                val leaveSpace = textWidth - totalWidth
                var leaveIndex = text.length - (leaveSpace / singleWidth).toInt()
                leaveIndex = max(0, leaveIndex - 3)
                val newText = text.removeRange(leaveIndex, text.length).plus("...")
                canvas.drawText(newText, startX + candleWidth * 0.5f, baseline, textPaint)
            } else {
                canvas.drawText(text, startX + candleWidth * 0.5f, baseline, textPaint)
            }

        }
    }

}