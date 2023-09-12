package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import androidx.collection.ArrayMap
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.value.ScatterValue
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_CENTER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_TOP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_TOP
import java.lang.Float.isNaN
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-11
 * @author lei 画图标
 */

class ScatterDraw(var contentRect: Rect) : IDraw<ScatterDataSet> {

    private val topHeights = ArrayMap<String, Float>()

    private val bottomHeights = ArrayMap<String, Float>()

    private val parentTopHeights = ArrayMap<String, Float>()

    private val parentBottomHeights = ArrayMap<String, Float>()

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<ScatterDataSet>,
        dataSet: ScatterDataSet,
        viewport: Viewport,
    ) {
        clearTemporaryData()
        drawScatter(
            canvas, dataSet, viewport,
            chartData.leftMax, chartData.leftMin,
            chartData.rightMax, chartData.rightMin
        )
    }

    private fun drawScatter(
        canvas: Canvas,
        dataSet: ScatterDataSet,
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

        val visibleRange = dataSet.getVisibleRange(viewport)

        val width =
            (contentRect.width() - dataSet.startXOffset - dataSet.endXOffset) / visibleRange + 1

        var shape = dataSet.shape

        var shapeWidth = shape?.intrinsicWidth?.toFloat() ?: 0f
        var shapeHeight = shape?.intrinsicHeight?.toFloat() ?: 0f
        if (dataSet.isAutoWidth) {
            shapeWidth = max(width * 0.8f, dataSet.shapeMinWidth)
            if (!isNaN(dataSet.shapeMaxWidth)) {
                shapeWidth = min(shapeWidth, dataSet.shapeMaxWidth)
            }
            shapeHeight =
                if (shape == null) 0f else shapeWidth * shapeHeight / shape.intrinsicWidth.toFloat()

        }

        val yOffset = if (dataSet.shapeAlign == SHAPE_ALIGN_CENTER) shapeHeight * 0.5f else 0f

        val valueCount = dataSet.getEntryCount()

        var i = 0
        while (i < valueCount && i < dataSet.values.size) {
            val value: ScatterValue? = dataSet.getEntryForIndex(i)
            if (!value!!.isVisible) {
                i++
                continue
            }

            val startIndexOffset = 0

            val drawDeltaX = (i + startIndexOffset) / valueCount.toFloat()

            val drawX =
                contentRect.left + contentRect.width() * (drawDeltaX - viewport.left) / viewport.width()

            val xPosition: Float = dataSet.startXOffset + width * 0.5f + drawX - shapeWidth * 0.5f
            var yPosition: Float
            val heightIndexKey = i.toString()
            if (dataSet.shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {

                var offset: Float = shapeHeight
                if (!isNaN(value.value)) {
                    val lastOffset = parentBottomHeights[heightIndexKey] ?: 0f
                    offset += lastOffset
                    parentBottomHeights[heightIndexKey] = offset
                }
                yPosition = contentRect.height() - offset
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_PARENT_TOP) {

                val offset = parentTopHeights[heightIndexKey] ?: 0f
                yPosition = contentRect.top + offset
                if (!isNaN(value.value)) {
                    parentTopHeights[heightIndexKey] = offset + shapeHeight
                }
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_BOTTOM) {

                var offset: Float = shapeHeight
                if (!isNaN(value.value)) {
                    val lastOffset = bottomHeights[heightIndexKey] ?: 0f
                    offset += lastOffset
                    bottomHeights[heightIndexKey] = offset
                }
                yPosition = (max - value.value) / (max - min) * contentRect.height() - offset
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_TOP) {

                val offset = topHeights[heightIndexKey] ?: 0f
                yPosition = (max - value.value) / (max - min) * contentRect.height() + offset
                if (!isNaN(value.value)) {
                    topHeights[heightIndexKey] = offset + shapeHeight
                }
            } else {
                yPosition = (max - value.value) / (max - min) * contentRect.height() - yOffset
            }

            if (value.shape != null) {
                shape = value.shape
            }

            value.setCoordinate(xPosition, yPosition)
            val x: Int = (xPosition + dataSet.drawOffsetX).toInt()
            val y: Int = (yPosition + dataSet.drawOffsetY).toInt()
            if (value.color != Color.TRANSPARENT) {
                shape?.setColorFilter(value.color, PorterDuff.Mode.SRC_OVER)
            }
            shape?.setBounds(
                x,
                y, (x + shapeWidth).toInt(), (y + shapeHeight).toInt()
            )
            val saveId = canvas.save()
            shape?.draw(canvas)
            canvas.restoreToCount(saveId)
            if (dataSet.textValueRenderers != null) {
                for (textValueRenderer in dataSet.textValueRenderers!!) {
                    textValueRenderer.render(
                        canvas, i,
                        x + shapeWidth * 0.5f, y + shapeHeight * 0.5f
                    )
                }
            }
            i++
        }

    }

    private fun clearTemporaryData() {
        topHeights.clear()
        bottomHeights.clear()
        parentTopHeights.clear()
        parentBottomHeights.clear()
    }
}