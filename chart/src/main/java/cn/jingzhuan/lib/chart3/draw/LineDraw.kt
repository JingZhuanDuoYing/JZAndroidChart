package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart.data.PartLineData
import cn.jingzhuan.lib.chart2.TimeUtil
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.value.LineValue
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-10
 * @author lei 画线
 */
class LineDraw(
    private val contentRect: Rect,
    private val renderPaint: Paint,
    private val chartAnimator: ChartAnimator,
    private val isLineChart: Boolean = false
) : IDraw<LineDataSet> {

    private var textPaint: Paint

    private var pointPaint: Paint

    private var pointLinePath: Path

    private var shaderPaths: MutableList<Path>

    private var shaderPathColors: MutableList<Shader?>

    private var linePaths: MutableList<Path> = ArrayList()

    private var shaderPath: Path? = null

    private var partLineList: MutableList<PartLineData>

    private var isHighLight = false

    private var maxVisibleCount = 0

    init {
        shaderPath = Path()
        shaderPaths = ArrayList()
        shaderPathColors = ArrayList()
        partLineList = ArrayList()

        pointPaint = Paint()
        pointPaint.style = Paint.Style.FILL
        pointLinePath = Path()

        textPaint = Paint()
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 8f
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.textSize = 25f
    }

    fun setHighLightState(isHighLight: Boolean) {
        this.isHighLight = isHighLight
    }

    fun setMaxVisibleCount(count: Int) {
        this.maxVisibleCount = count
    }

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<LineDataSet>,
        dataSet: LineDataSet,
        viewport: Viewport,
    ) {
        if (dataSet.isVisible) {
            drawLine(
                canvas, dataSet, viewport,
                chartData.leftMax, chartData.leftMin,
                chartData.rightMax, chartData.rightMin
            )
        }
    }

    private fun drawLine(
        canvas: Canvas,
        lineDataSet: LineDataSet,
        viewport: Viewport,
        lMax: Float,
        lMin: Float,
        rMax: Float,
        rMin: Float,
    ) {
        val min: Float
        val max: Float

        when (lineDataSet.axisDependency) {
            AxisY.DEPENDENCY_RIGHT -> {
                min = rMin
                max = rMax
            }

            else -> {
                min = lMin
                max = lMax
            }
        }

        val lineThickness: Int = lineDataSet.lineThickness

        renderPaint.style = Paint.Style.STROKE
        renderPaint.strokeWidth = lineThickness.toFloat()
        renderPaint.color = lineDataSet.color

        shaderPath?.reset()

        shaderPaths.clear()
        shaderPathColors.clear()

        linePaths.clear()
        partLineList.clear()

        val valueCount = lineDataSet.getEntryCount()

        val visibleRange = lineDataSet.getVisibleRange(viewport)
        val width = if (visibleRange > 0) contentRect.width() / visibleRange else 0f

        val scale = 1.0f / viewport.width()

        val step = if (valueCount > 1 && isLineChart) {
            (contentRect.width() * scale / (valueCount - 1))
        } else {
            contentRect.width() * scale / valueCount
        }

        val startX =
            contentRect.left + (if (isLineChart) 0f else step * 0.5f) - viewport.left * contentRect.width() * scale

//        val valuePhaseCount = floor((valueCount * chartAnimator.phaseX).toDouble()).toInt()

        var linePath = Path()

        val startIndexOffset = 0

        val dataSize = lineDataSet.values.size
        var leftIndex = (dataSize * viewport.left).roundToInt()
        leftIndex = max(leftIndex, 0)
        var rightIndex = (dataSize * viewport.right).roundToInt()
        rightIndex = min(rightIndex, dataSize)

        val lastValue = lineDataSet.values.lastOrNull()
        if (lastValue == null || lastValue.isValueNaN) {
            for (i in (rightIndex - 1) downTo (leftIndex + 1)) {
                val lineEntry = lineDataSet.getEntryForIndex(i)
                if (lineEntry != null && !lineEntry.value.isNaN()) {
                    rightIndex = i + 1
                    break
                }
            }
        }

        // 画带状线
        if (lineDataSet.isDrawBand) {
            try {
                // 这里有潜在的崩溃问题，先catch，后续重写
                rightIndex = lineDataSet.values.take(rightIndex).indexOfLast {
                    !it.value.isNaN()
                } + 1
                leftIndex = max(rightIndex - maxVisibleCount, 0)
                drawBand(canvas, lineDataSet, startX, step, startIndexOffset, max, min, leftIndex, rightIndex)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        // 画单值水平线
        if (lineDataSet.isHorizontalLine) {
            drawHorizontalLine(canvas, lineDataSet, linePath, max, min)
            return
        }

        // 画点状线
        if (lineDataSet.isPointLine) {
            drawPointLine(
                canvas,
                lineDataSet,
                visibleRange,
                startX,
                step,
                startIndexOffset,
                max,
                min,
                leftIndex,
                rightIndex
            )
            return
        }

        var i = leftIndex
        val leftX = startX + step * (leftIndex + startIndexOffset)

        val headValue: LineValue? = lineDataSet.headPoint
        if (headValue != null && !headValue.isValueNaN) {
            // 垂直方向绘制范围收缩至能容下线条的宽度
            val headYPosition: Float =
                (max - headValue.value.toFloat()) / (max - min) * (contentRect.height() - 2 * lineThickness) + lineThickness * 0.5f
            linePath.moveTo(startX, headYPosition)
            var firstValue: LineValue? = null
            var firstXPosition = startX

            while (i < rightIndex) {
                val value: LineValue? = lineDataSet.getEntryForIndex(i)
                if (value != null && !value.isValueNaN) {
                    firstValue = value
                    firstXPosition = startX + step * (i + startIndexOffset)
                    break
                }
                i++
            }

            if (firstValue != null) {
                val firstYPosition =
                    (max - firstValue.value.toFloat()) / (max - min) * (contentRect.height() - 2 * lineThickness) + lineThickness * 0.5f
                linePath.lineTo(firstXPosition, firstYPosition)
            }
        }

        var isFirst = true
        var prevValue: LineValue? = null

        val shaderSplit =
            !lineDataSet.shaderBaseValue.isNaN() && lineDataSet.shaderBaseValue < max && lineDataSet.shaderBaseValue > min

        var lastIndex = 0
        if (chartAnimator.phaseX > 0) {
            lastIndex =
                (floor((rightIndex * chartAnimator.phaseX).toDouble()) - 1).toInt()
        }

        if (lastIndex >= valueCount) lastIndex = valueCount - 1

        var preBaseX = Float.NaN

        while (i < rightIndex) {
            val value: LineValue? = lineDataSet.getEntryForIndex(i)
            if (value == null || value.isValueNaN) {
                i++
                continue
            }
            val xPosition = startX + step * (i + startIndexOffset)
            val yPosition: Float =
                (max - value.value.toFloat()) / (max - min) * (contentRect.height() - 2 * lineThickness) + lineThickness * 0.5f
            value.setCoordinate(xPosition, yPosition)

            //分段线条
            if (i > 1 && lineDataSet.isPartLine) {
                val lastValue: LineValue? = lineDataSet.getEntryForIndex(i - 1)
                if (lastValue != null) {
                    if (value.isPathEnd) {
                        linePath.lineTo(xPosition, yPosition)
                        partLineList.add(PartLineData(linePath, lastValue.pathColor))
                        linePath = Path()
                        isFirst = true
                    } else {
                        val split = value.pathColor != lastValue.pathColor
                        if (split) {
                            partLineList.add(PartLineData(linePath, lastValue.pathColor))
                            linePath = Path()
                            linePath.moveTo(lastValue.x, lastValue.y)
                        }
                    }
                    if (i == rightIndex - 1) {
                        partLineList.add(PartLineData(linePath, value.pathColor))
                    }
                }
            }

            //普通线条
            if (isFirst) {
                if (!value.isPathEnd) {
                    isFirst = false
                    linePath.moveTo(xPosition, yPosition)
                }
            } else {
                linePath.lineTo(xPosition, yPosition)
                if (value.isPathEnd) {
                    linePaths.add(linePath)
                    linePath = Path()
                    isFirst = true
                }
            }

            //阴影
            if (shaderSplit) {
                val baseValue: Float = lineDataSet.shaderBaseValue.toFloat()
                val baseValueY: Float = contentRect.height() / (max - min) * (max - baseValue)
                if (prevValue == null) {
                    preBaseX = value.x
                    shaderPath?.moveTo(preBaseX, yPosition)
                } else if (prevValue.value > lineDataSet.shaderBaseValue) {
                    if (value.value <= lineDataSet.shaderBaseValue) {
                        // 跨越颜色区域
                        val nextBaseX: Float = getBaseX(prevValue, value, baseValueY)
                        shaderPath?.lineTo(nextBaseX, baseValueY)
                        shaderPath?.lineTo(preBaseX, baseValueY)
                        shaderPath?.close()
                        shaderPaths.add(Path(shaderPath))
                        shaderPathColors.add(lineDataSet.shaderTop)
                        shaderPath?.reset()
                        shaderPath?.moveTo(nextBaseX, baseValueY)
                        shaderPath?.lineTo(xPosition, yPosition)
                        preBaseX = nextBaseX
                    } else {
                        shaderPath?.lineTo(xPosition, yPosition) // 当前值坐标
                    }
                } else if (value.value > lineDataSet.shaderBaseValue) {
                    val nextBaseX: Float = getBaseX(prevValue, value, baseValueY)
                    shaderPath?.lineTo(nextBaseX, baseValueY)
                    shaderPath?.lineTo(preBaseX, baseValueY)
                    shaderPath?.close()
                    shaderPaths.add(Path(shaderPath))
                    shaderPathColors.add(lineDataSet.shaderBottom)
                    shaderPath?.reset()
                    shaderPath?.moveTo(nextBaseX, baseValueY)
                    shaderPath?.lineTo(xPosition, yPosition)
                    preBaseX = nextBaseX
                } else {
                    // 当前值坐标
                    shaderPath?.lineTo(xPosition, yPosition)
                }
                prevValue = value
                if (lastIndex == i) {
                    shaderPath?.lineTo(xPosition, baseValueY)
                    shaderPath?.lineTo(preBaseX, baseValueY)
                    shaderPath?.close()
                    shaderPaths.add(Path(shaderPath))
                    if (prevValue.value > baseValue) {
                        shaderPathColors.add(lineDataSet.shaderTop)
                    } else {
                        shaderPathColors.add(lineDataSet.shaderBottom)
                    }
                    shaderPath?.reset()
                }
            }
            i++
        }

        if (!isFirst) {
            linePaths.add(linePath)
        }

        if (!shaderSplit) {
            // 不区分颜色分段
            // draw shader area
            if (i > 0 && lineDataSet.shader != null && lineDataSet.values.size > 0) {
                renderPaint.style = Paint.Style.FILL
                if (shaderPath == null) {
                    shaderPath = Path(linePath)
                } else {
                    shaderPath?.set(linePath)
                }
                val lineValue: LineValue? = lineDataSet.getEntryForIndex(i - 1)
                if (lineValue != null && shaderPath != null) {
                    shaderPath?.lineTo(lineValue.x, contentRect.bottom.toFloat())
                    shaderPath?.lineTo(leftX + startIndexOffset * width, contentRect.bottom.toFloat())
                    shaderPath?.lineTo(
                        leftX + startIndexOffset * width,
                        lineDataSet.values[0].y
                    )
                    shaderPath?.close()
                    renderPaint.shader = lineDataSet.shader
                    canvas.drawPath(shaderPath!!, renderPaint)
                    renderPaint.shader = null
                    renderPaint.style = Paint.Style.STROKE
                }
            }
        } else {
            renderPaint.style = Paint.Style.FILL
            i = 0
            while (i < shaderPaths.size) {
                val path = shaderPaths[i]
                val shader = shaderPathColors[i]!!
                renderPaint.shader = shader
                canvas.drawPath(path, renderPaint)
                renderPaint.shader = null
                i++
            }
            renderPaint.style = Paint.Style.STROKE
        }

        if (lineDataSet.isLineVisible) {
            if (lineDataSet.isPartLine) {
                for (partLineData in partLineList) {
                    renderPaint.color = partLineData.color
                    canvas.drawPath(partLineData.path, renderPaint)
                }
            } else {
                for (path in linePaths) {
                    canvas.drawPath(path, renderPaint)
                }
            }
        }


    }

    /**
     * 画点状线
     */
    private fun drawPointLine(
        canvas: Canvas,
        lineDataSet: LineDataSet,
        visibleRange: Float,
        startX: Float,
        step: Float,
        startIndexOffset: Int,
        max: Float,
        min: Float,
        leftIndex: Int,
        rightIndex: Int
    ) {
        val interval: Float = lineDataSet.interval

        if (interval != 0f) {
            renderPaint.pathEffect = DashPathEffect(
                floatArrayOf(interval, interval), lineDataSet.phase
            )
        }
        renderPaint.isAntiAlias = true

        pointPaint.color = lineDataSet.color
        pointPaint.isAntiAlias = true

        pointLinePath.reset()

        val candleWidth =
            contentRect.width() / max(visibleRange, lineDataSet.minValueCount.toFloat())
        var isFirst = true

        var i = leftIndex
        while (i < rightIndex) {
            val point = lineDataSet.getEntryForIndex(i)
            if (point == null || point.isValueNaN) {
                i++
                continue
            }
            val xPosition = startX + step * (i + startIndexOffset)
            var yPosition = (max - point.value.toFloat()) / (max - min) * contentRect.height()
            val candlestickCenterX = xPosition + candleWidth * 0.5f
            point.setCoordinate(candlestickCenterX, yPosition)
            if (yPosition + lineDataSet.radius > contentRect.bottom) {
                yPosition -= lineDataSet.radius
            }
            if (yPosition - lineDataSet.radius < contentRect.top) yPosition += lineDataSet.radius
            if (isFirst) {
                isFirst = false
                pointLinePath.moveTo(candlestickCenterX, yPosition)
            } else {
                pointLinePath.lineTo(candlestickCenterX, yPosition)
            }
            if (point.isDrawCircle) {
                canvas.drawCircle(
                    candlestickCenterX,
                    yPosition,
                    lineDataSet.radius,
                    pointPaint
                )
            }
            i++
        }
        if (lineDataSet.isLineVisible) {
            canvas.drawPath(pointLinePath, renderPaint)
        }
        renderPaint.pathEffect = null
    }

    /**
     * 画单值水平线
     */
    private fun drawHorizontalLine(
        canvas: Canvas,
        lineDataSet: LineDataSet,
        linePath: Path,
        max: Float,
        min: Float,
    ) {

        renderPaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f, 5f, 5f), 0f)
        renderPaint.color = lineDataSet.color

        val value = lineDataSet.getEntryForIndex(lineDataSet.values.size - 1) ?: return

        val text: String = if (isHighLight) value.value.toString() else lineDataSet.tag ?: ""
        val textBound = Rect()
        renderPaint.getTextBounds(text, 0, text.length, textBound)
        val padding = 10

        val left = if (lineDataSet.horizontalLeft) textBound.width() + padding * 2 else 0
        val yPosition: Float = (max - value.value.toFloat()) / (max - min) * contentRect.height()
        linePath.moveTo(left.toFloat(), yPosition)
        linePath.lineTo(contentRect.width().toFloat(), yPosition)
        canvas.drawPath(linePath, renderPaint)
        linePath.close()

        textPaint.color = Color.WHITE

        renderPaint.pathEffect = null
        renderPaint.style = Paint.Style.FILL
        renderPaint.strokeWidth = 2f
        renderPaint.textSize = 25f

        val textRect = Rect()
        textRect.left = 0
        textRect.top = (yPosition - textBound.height()).toInt()
        textRect.right = textBound.width() + padding * 2
        textRect.bottom = (yPosition + textBound.height()).toInt()

        if (!lineDataSet.horizontalLeft && TimeUtil.isInTime()) {
            textRect.left = contentRect.width() - textBound.width() - padding * 2
            textRect.right = contentRect.width()
        }
        if (textRect.top < 0 && yPosition >= 0) {
            val height = textRect.height()
            textRect.top = 0
            textRect.bottom = height
        }
        if (textRect.bottom > contentRect.height() && yPosition <= contentRect.height()) {
            val height = textRect.height()
            textRect.bottom = contentRect.height()
            textRect.top = contentRect.height() - height
        }
        if (!lineDataSet.horizontalLeft && value.value in min..max) {
            canvas.drawRect(textRect, renderPaint)
        }
        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = textRect.centerY() + distance
        if (value.value in min..max) {
            if (lineDataSet.horizontalLeft) {
                textPaint.color = lineDataSet.color
            }
            canvas.drawText(text, textRect.centerX().toFloat(), baseline, textPaint)
        }
    }

    /**
     * 绘制带状线
     */
    private fun drawBand(
        canvas: Canvas,
        lineDataSet: LineDataSet,
        startX: Float,
        step: Float,
        startIndexOffset: Int,
        max: Float,
        min: Float,
        leftIndex: Int,
        rightIndex: Int
    ) {
        var cache1 = Path()
        var cache2 = Path()
        //当数据1 > 数据2 将cache1连接控件底部形成不规则封闭图形 cache2连接控件顶部 取交集部分绘制
        var isCloseToBottom: Boolean
        var preStatus = false
        var newPath = true
        var pathStartX = 0f
        val paths = ArrayList<PartLineData>()

        var i = leftIndex
        while (i < rightIndex) {
            val point = lineDataSet.getEntryForIndex(i)
            if (point == null || point.isValueNaN) {
                i++
                continue
            }

            isCloseToBottom = point.value > point.secondValue
            //
            val xPosition = startX + step * (i + startIndexOffset)
            val yPosition: Float = (max - point.value.toFloat()) / (max - min) * contentRect.height()
            val secondYPosition: Float =
                (max - point.secondValue.toFloat()) / (max - min) * contentRect.height()

            point.setCoordinate(xPosition, yPosition)
            point.secondY = secondYPosition
            if (newPath) {
                cache1.moveTo(xPosition, yPosition)
                cache2.moveTo(xPosition, secondYPosition)
                newPath = false
                pathStartX = xPosition
            } else {
                cache1.lineTo(xPosition, yPosition)
                cache2.lineTo(xPosition, secondYPosition)
            }
            if (i > 0 && i == rightIndex - 1) { //结尾部分

                //找到上一个点 做为起点
                val prePoint = lineDataSet.getEntryForIndex(i - 1) ?: return

                val special = prePoint.value > prePoint.secondValue
                if (special != isCloseToBottom) {
                    if (special) {
                        cache1.lineTo(xPosition, contentRect.height().toFloat())
                        cache1.lineTo(pathStartX, contentRect.height().toFloat())
                        cache2.lineTo(xPosition, 0f)
                        cache2.lineTo(pathStartX, 0f)
                    } else {
                        cache2.lineTo(xPosition, contentRect.height().toFloat())
                        cache2.lineTo(pathStartX, contentRect.height().toFloat())
                        cache1.lineTo(xPosition, 0f)
                        cache1.lineTo(pathStartX, 0f)
                    }
                    cache1.close()
                    cache2.close()
                    cache1.op(cache2, Path.Op.INTERSECT)
                    paths.add(PartLineData(cache1, prePoint.pathColor))
                    cache1 = Path()
                    cache2 = Path()
                    pathStartX = prePoint.x

                    //相交代表 本来数据比较高的线变成低 低的线变成高
                    cache1.moveTo(prePoint.x, prePoint.y)
                    cache1.lineTo(xPosition, yPosition)
                    cache2.moveTo(prePoint.x, prePoint.secondY)
                    cache2.lineTo(xPosition, secondYPosition)
                }
                if (isCloseToBottom) {
                    cache1.lineTo(xPosition, contentRect.height().toFloat())
                    cache1.lineTo(pathStartX, contentRect.height().toFloat())
                    cache2.lineTo(xPosition, 0f)
                    cache2.lineTo(pathStartX, 0f)
                } else {
                    cache2.lineTo(xPosition, contentRect.height().toFloat())
                    cache2.lineTo(pathStartX, contentRect.height().toFloat())
                    cache1.lineTo(xPosition, 0f)
                    cache1.lineTo(pathStartX, 0f)
                }
                cache1.close()
                cache2.close()
                cache1.op(cache2, Path.Op.INTERSECT)
                paths.add(PartLineData(cache1, point.pathColor))
            } else {
                if (preStatus != isCloseToBottom && i > 0) {
                    //相交  两个值 高的连接底部 低的连接顶部

                    if (isCloseToBottom) {
                        cache2.lineTo(xPosition, contentRect.height().toFloat())
                        cache2.lineTo(pathStartX, contentRect.height().toFloat())
                        cache1.lineTo(xPosition, 0f)
                        cache1.lineTo(pathStartX, 0f)
                    } else {
                        cache1.lineTo(xPosition, contentRect.height().toFloat())
                        cache1.lineTo(pathStartX, contentRect.height().toFloat())
                        cache2.lineTo(xPosition, 0f)
                        cache2.lineTo(pathStartX, 0f)
                    }
                    cache1.close()
                    cache2.close()
                    cache1.op(cache2, Path.Op.INTERSECT)


                    //找到上一个点 做为起点
                    val prePoint = lineDataSet.getEntryForIndex(i - 1) ?: return
                    paths.add(PartLineData(cache1, prePoint.pathColor))
                    cache1 = Path()
                    cache2 = Path()
                    pathStartX = prePoint.x

                    //相交代表 本来数据比较高的线变成低 低的线变成高
                    cache1.moveTo(prePoint.x, prePoint.y)
                    cache1.lineTo(xPosition, yPosition)
                    cache2.moveTo(prePoint.x, prePoint.secondY)
                    cache2.lineTo(xPosition, secondYPosition)
                }
            }
            preStatus = isCloseToBottom
            i++
        }
        if (paths.isNotEmpty()) {
            for (pathData in paths) {
                renderPaint.color = pathData.color
                renderPaint.style = Paint.Style.FILL
                canvas.drawPath(pathData.path, renderPaint)
            }
        }
    }

    private fun getBaseX(p1: LineValue, p2: LineValue, baseY: Float): Float {
        val x1 = p1.x
        val x2 = p2.x
        val y1 = abs(p1.y - baseY)
        val y2 = abs(p2.y - baseY)
        return (y1 * x2 + x1 * y2) / (y2 + y1)
    }

}