package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import cn.jingzhuan.lib.chart.AxisAutoValues
import cn.jingzhuan.lib.chart.component.Axis
import cn.jingzhuan.lib.chart.component.AxisX
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
class AxisRenderer<V : AbstractValue, T : AbstractDataSet<V>>(
    chart: AbstractChartView<V, T>?,
    val axis: Axis
) : AbstractRenderer<V, T>(chart!!) {

    private lateinit var axisPaint: Paint

    private lateinit var gridPaint: Paint

    private val labelBuffer = CharArray(100)

    init {
        initPaints()
    }

    private fun initPaints() {
        axisPaint = Paint()
        axisPaint.style = Paint.Style.STROKE

        gridPaint = Paint()
        gridPaint.style = Paint.Style.STROKE

        labelTextPaint.textSize = axis.labelTextSize
        labelTextPaint.color = axis.labelTextColor

        axis.labelWidth = labelTextPaint.measureText("0000").toInt()
        if (axis.labelTextSize > 0) {
            axis.labelHeight = abs(labelTextPaint.fontMetrics.top).toInt()
        }
    }

    /**
     * 画坐标轴
     */
    override fun renderer(canvas: Canvas) {
        if (axis.labels == null) {
            if (axis is AxisX) {
                computeAxisStopsX(viewport.left, viewport.right, axis, null)
            } else if (axis is AxisY) {
                computeAxisStopsY(axis)
            }
        }
        // Draws lib container
        drawAxisLine(canvas)
    }

    private fun drawAxisLine(canvas: Canvas) {
        var startX = 0f
        var startY = 0f
        var stopX = 0f
        var stopY = 0f
        val halfThickness = axis.axisThickness * .5f
        when (axis.axisPosition) {
            AxisX.TOP, AxisX.TOP_INSIDE -> {
                startX = contentRect.left.toFloat()
                startY = contentRect.top + halfThickness
                stopX = contentRect.right.toFloat()
                stopY = startY
            }

            AxisX.BOTTOM, AxisX.BOTTOM_INSIDE -> {
                startX = contentRect.left.toFloat()
                startY = contentRect.bottom - halfThickness
                stopX = contentRect.right.toFloat()
                stopY = startY
            }

            AxisY.LEFT_INSIDE, AxisY.LEFT_OUTSIDE -> {
                startX = contentRect.left + halfThickness
                startY = contentRect.top.toFloat()
                stopX = startX
                stopY = contentRect.bottom.toFloat()
            }

            AxisY.RIGHT_INSIDE, AxisY.RIGHT_OUTSIDE -> {
                startX = contentRect.right - halfThickness
                startY = contentRect.top.toFloat()
                stopX = startX
                stopY = contentRect.bottom.toFloat()
            }
        }
        // Draw axis line
        if (axis.isEnable) {
            axisPaint.strokeWidth = axis.axisThickness
            axisPaint.color = axis.axisColor
            canvas.drawLine(startX, startY, stopX, stopY, axisPaint)
        }
    }

    private fun computeAxisStopsX(
        start: Float,
        stop: Float,
        axis: AxisX,
        autoValues: AxisAutoValues?
    ) {
        val range = (stop - start).toDouble()
        if (axis.gridCount == 0 || range <= 0) {
            return
        }
        val count = axis.gridCount + 1
        val rawInterval = range / count
        val interval = roundToOneSignificantFigure(rawInterval).toDouble()
        val first = ceil(start / interval) * interval
        axis.mLabelEntries = FloatArray(count + 1)
        var f: Double = first
        var i = 0
        while (i < count + 1) {
            axis.mLabelEntries[i] = f.toFloat()
            f += interval
            ++i
        }
    }

    /**
     * Rounds the given number to the given number of significant digits. Based on an answer on
     * [Stack Overflow](http://stackoverflow.com/questions/202302).
     */
    private fun roundToOneSignificantFigure(num: Double): Float {
        val d = ceil(log10(if (num < 0) -num else num).toFloat().toDouble()).toFloat()
        val power = 1 - d.toInt()
        val magnitude = 10.0.pow(power.toDouble()).toFloat()
        val shifted = (num * magnitude).roundToInt()
        return shifted / magnitude
    }

    private fun computeAxisStopsY(axis: AxisY) {
        val min = axis.yMin.toDouble()
        val max = axis.yMax.toDouble()
        val count = axis.gridCount + 1
        val interval = (max - min) / count
        axis.mLabelEntries = FloatArray(count + 1)
        if (min < Float.MAX_VALUE && max > -Float.MAX_VALUE && min <= max) {
            var f = min
            var j = 0
            while (j < count + 1) {
                axis.mLabelEntries[j] = f.toFloat()
                f += interval
                j++
            }
        }
    }

    /**
     * 画坐标轴文本
     */
    fun drawAxisLabels(canvas: Canvas) {
        if (!axis.isLabelEnable) return
        if (axis.labels == null || axis.labels.isEmpty()) return
        val labels = axis.labels
        labelTextPaint.color = axis.labelTextColor
        labelTextPaint.textSize = axis.labelTextSize
        var x = 0f
        var y = 0f
        when (axis.axisPosition) {
            AxisX.TOP, AxisX.TOP_INSIDE -> {
                x = contentRect.left.toFloat()
                y = contentRect.top.toFloat()
            }

            AxisX.BOTTOM, AxisX.BOTTOM_INSIDE -> {
                x = contentRect.left.toFloat()
                y = (contentRect.top + contentRect.height()).toFloat()
            }

            AxisY.LEFT_INSIDE, AxisY.LEFT_OUTSIDE -> {
                x = contentRect.left.toFloat()
                y = contentRect.bottom.toFloat()
            }

            AxisY.RIGHT_INSIDE, AxisY.RIGHT_OUTSIDE -> {
                x = contentRect.right.toFloat()
                y = contentRect.bottom.toFloat()
            }
        }
        var labelOffset: Int
        var labelLength: Int
        if (axis is AxisX) {
            // X轴
            val width = contentRect.width() / labels.size.toFloat()
            for (i in labels.indices) {
                val labelCharArray = labels[i].toCharArray()
                labelLength = labelCharArray.size
                System.arraycopy(
                    labelCharArray,
                    0,
                    labelBuffer,
                    labelBuffer.size - labelLength,
                    labelLength
                )
                labelOffset = labelBuffer.size - labelLength
                labelTextPaint.textAlign = Paint.Align.CENTER
                val colorSetter = axis.getLabelColorSetter()
                if (colorSetter != null) {
                    labelTextPaint.color = colorSetter.getColorByIndex(i)
                }
                canvas.drawText(
                    labelBuffer, labelOffset, labelLength,
                    width * 0.5f + getDrawX(i / labels.size.toFloat()),
                    y + labelTextPaint.textSize, labelTextPaint
                )
            }
        } else {
            // Y轴
            val height = contentRect.height() / (axis.labels.size - 1f)
            var separation = 0f
            for (i in axis.labels.indices) {
                val labelCharArray = axis.labels[i].toCharArray()
                labelLength = labelCharArray.size
                System.arraycopy(
                    labelCharArray,
                    0,
                    labelBuffer,
                    labelBuffer.size - labelLength,
                    labelLength
                )
                labelOffset = labelBuffer.size - labelLength
                when (axis.axisPosition) {
                    AxisY.LEFT_OUTSIDE, AxisY.RIGHT_INSIDE -> {
                        labelTextPaint.textAlign = Paint.Align.RIGHT
                        separation = -axis.labelSeparation.toFloat()
                    }

                    AxisY.LEFT_INSIDE, AxisY.RIGHT_OUTSIDE -> {
                        labelTextPaint.textAlign = Paint.Align.LEFT
                        separation = axis.labelSeparation.toFloat()
                    }
                }
                val textHeightOffset = (labelTextPaint.descent() + labelTextPaint.ascent()) / 2
                val colorSetter = axis.labelColorSetter
                if (colorSetter != null) {
                    labelTextPaint.color = colorSetter.getColorByIndex(i)
                }
                canvas.drawText(
                    labelBuffer, labelOffset, labelLength,
                    x + separation,
                    y - i * height - textHeightOffset,
                    labelTextPaint
                )
            }
        }
    }

    /**
     * 画网格线
     */
    fun drawGridLines(canvas: Canvas) {
        val count = axis.gridCount + 1
        gridPaint.strokeWidth = axis.gridThickness
        gridPaint.color = axis.gridColor
        if (axis.dashedGridIntervals != null && axis.dashedGridPhase > 0) {
            gridPaint.pathEffect = DashPathEffect(axis.dashedGridIntervals, axis.dashedGridPhase)
        }
        if (axis is AxisX) {
            val width = contentRect.width() / count.toFloat()
            for (i in 1 until count) {
                if (axis.getGirdLineColorSetter() != null) {
                    gridPaint.color =
                        axis.getGirdLineColorSetter().getColorByIndex(axis.getGridColor(), i)
                }
                canvas.drawLine(
                    contentRect.left + i * width,
                    contentRect.top.toFloat(),
                    contentRect.left + i * width,
                    contentRect.bottom.toFloat(),
                    gridPaint
                )
            }
        }
        if (axis is AxisY) {
            val height = contentRect.height() / count.toFloat()
            for (i in 1 until count) {
                if (axis.getGirdLineColorSetter() != null) {
                    gridPaint.color = axis.getGirdLineColorSetter().getColorByIndex(axis.getGridColor(), i)
                }
                canvas.drawLine(
                    contentRect.left.toFloat(),
                    contentRect.top + i * height,
                    contentRect.right.toFloat(),
                    contentRect.top + i * height,
                    gridPaint
                )
            }
        }
    }
}
