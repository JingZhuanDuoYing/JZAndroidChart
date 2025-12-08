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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-11
 * @author lei 画图标
 */

class ScatterDraw(
    private val contentRect: Rect
) : IDraw<ScatterDataSet> {

    private val topHeights = ArrayMap<String, Float>()

    private val bottomHeights = ArrayMap<String, Float>()

    private val parentTopHeights = ArrayMap<String, Float>()

    private val parentBottomHeights = ArrayMap<String, Float>()

    private var leftBoundCountMap = ArrayMap<String, Int>()
    private var rightBoundCountMap = ArrayMap<String, Int>()

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<ScatterDataSet>,
        viewport: Viewport,
    ) {
        clearTemporaryData()
        super.drawDataSet(canvas, chartData, viewport)
    }

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<ScatterDataSet>,
        dataSet: ScatterDataSet,
        viewport: Viewport,
    ) {
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
        lMax: Double,
        lMin: Double,
        rMax: Double,
        rMin: Double,
    ) {

        val min: Double
        val max: Double

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
            if (!dataSet.shapeMaxWidth.isNaN()) {
                shapeWidth = min(shapeWidth, dataSet.shapeMaxWidth)
            }
            shapeHeight =
                if (shape == null) 0f else shapeWidth * shapeHeight / shape.intrinsicWidth.toFloat()

        }

        // 指示器图标
        val pointShapeWidth = dataSet.autoTurnPointShape?.intrinsicWidth?.toFloat() ?: 0f
        val pointShapeHeight = dataSet.autoTurnPointShape?.intrinsicHeight?.toFloat() ?: 0f

        val yOffset = if (dataSet.shapeAlign == SHAPE_ALIGN_CENTER) shapeHeight * 0.5f else 0f

        val valueCount = dataSet.getEntryCount()

        val dataSize = dataSet.values.size
        var leftIndex = (dataSize * viewport.left).roundToInt()
        leftIndex = max(leftIndex, 0)
        var rightIndex = (dataSize * viewport.right).roundToInt()
        rightIndex = min(rightIndex, dataSize)

        var i = leftIndex

        while (i < rightIndex) {
            val value: ScatterValue? = dataSet.getEntryForIndex(i)
            if (!value!!.isVisible) {
                i++
                continue
            }

            val startIndexOffset = 0

            val drawDeltaX = (i + startIndexOffset) / valueCount.toFloat()

            val drawX = contentRect.left + contentRect.width() * (drawDeltaX - viewport.left) / viewport.width()

            val xPosition = dataSet.startXOffset + width * 0.5f + drawX - shapeWidth * 0.5f
            var yPosition: Float
            val heightIndexKey = i.toString()

            var shouldTurn = false

            if (dataSet.shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {

                var offset = shapeHeight
                if (!value.value.isNaN()) {
                    val lastOffset = parentBottomHeights.getOrDefault(heightIndexKey, 0f)
                    offset += lastOffset
                    parentBottomHeights[heightIndexKey] = offset
                }
                yPosition = contentRect.height() - offset
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_PARENT_TOP) {

                val offset = parentTopHeights.getOrDefault(heightIndexKey, 0f)
                yPosition = contentRect.top + offset
                if (!value.value.isNaN()) {
                    parentTopHeights[heightIndexKey] = offset + shapeHeight
                }
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_BOTTOM) {

                var offset = shapeHeight
                if (!value.value.isNaN()) {
                    val lastOffset = bottomHeights.getOrDefault(heightIndexKey, 0f)
                    offset += lastOffset
                    bottomHeights[heightIndexKey] = offset
                }
                yPosition = ((max - value.value) / (max - min) * contentRect.height() - offset).toFloat()
                if (dataSet.isAutoTurn) {
                    if (yPosition < 0f) {
                        shouldTurn = true
                        yPosition = ((max - value.value) / (max - min) * contentRect.height() + offset - shapeHeight).toFloat()
                    }
                }
            } else if (dataSet.shapeAlign == SHAPE_ALIGN_TOP) {

                val offset = topHeights.getOrDefault(heightIndexKey, 0f)
                yPosition = ((max - value.value) / (max - min) * contentRect.height() + offset).toFloat()
                if (!value.value.isNaN()) {
                    topHeights[heightIndexKey] = offset + shapeHeight
                }
                if (dataSet.isAutoTurn) {
                    if (yPosition > (contentRect.height() - shapeHeight - pointShapeHeight)) {
                        shouldTurn = true
                        yPosition = ((max - value.value) / (max - min) * contentRect.height() - offset - shapeHeight).toFloat()
                    }
                }
            } else {
                yPosition = ((max - value.value) / (max - min) * contentRect.height() - yOffset).toFloat()
            }

            if (value.shape != null) {
                shape = value.shape
            }

            value.setCoordinate(xPosition, yPosition)
            val x = (xPosition + dataSet.drawOffsetX).toInt()
            val y = (yPosition + dataSet.drawOffsetY).toInt()
            if (value.color != Color.TRANSPARENT) {
                shape?.setColorFilter(value.color, PorterDuff.Mode.SRC_OVER)
            }

            if (dataSet.isAutoTurn && drawX < contentRect.left + (shape?.intrinsicWidth ?: 0) * 0.5f) { // 左边界
                val turnY = ((max - value.value) / (max - min) * contentRect.height() - yOffset + dataSet.drawOffsetY).toInt()
                // 如果设置了 先画指示器 并且需要旋转90度
                val leftBoundCount = leftBoundCountMap.getOrDefault(heightIndexKey, 0)
                val pointLeft = drawX.toInt() + dataSet.autoTurnShapePadding * leftBoundCount + shapeWidth.toInt() * leftBoundCount
                val pointShape = dataSet.autoTurnPointShape
                if (pointShape != null && leftBoundCount == 0) {
                    var pointTop = turnY - pointShapeHeight.toInt()
                    var pointBottom = turnY
                    if (pointBottom >= contentRect.height()) {
                        pointBottom = contentRect.height() - pointShapeWidth.toInt()
                        pointTop = pointBottom - pointShapeHeight.toInt()
                    }
                    pointShape.setBounds(pointLeft, pointTop, pointLeft + pointShapeWidth.toInt(), pointBottom)
                    val pointSaveCount = canvas.save()
                    canvas.rotate(90f, drawX, pointBottom.toFloat())
                    pointShape.draw(canvas)
                    canvas.restoreToCount(pointSaveCount)
                }

                // 再画图标
                val halfShapeHeight = shapeHeight * 0.5f
                if (turnY < halfShapeHeight) {// 画在顶部
                    shape?.setBounds(
                        pointLeft + pointShapeHeight.toInt(),
                        0,
                        pointLeft + pointShapeHeight.toInt() + shapeWidth.toInt(),
                        shapeHeight.toInt()
                    )
                } else if (turnY > contentRect.height() - halfShapeHeight) {// 画在底部
                    shape?.setBounds(
                        pointLeft + pointShapeHeight.toInt(),
                        contentRect.height() - shapeHeight.toInt(),
                        pointLeft + pointShapeHeight.toInt() + shapeWidth.toInt(),
                        contentRect.height()
                    )
                } else {// 居中正常画
                    shape?.setBounds(
                        pointLeft + pointShapeHeight.toInt(),
                        turnY - halfShapeHeight.toInt(),
                        pointLeft + pointShapeHeight.toInt() + shapeWidth.toInt(),
                        turnY + halfShapeHeight.toInt(),
                    )
                }
                val shapeSaveCount = canvas.save()
                shape?.draw(canvas)
                canvas.restoreToCount(shapeSaveCount)
                leftBoundCountMap[heightIndexKey] = leftBoundCount + 1
            } else if (dataSet.isAutoTurn && drawX > contentRect.right - (shape?.intrinsicWidth ?: 0) * 0.5f) { // 右边界
                val turnY = ((max - value.value) / (max - min) * contentRect.height() - yOffset + dataSet.drawOffsetY).toInt()
                // 如果设置了 先画指示器 并且需要旋转90度
                val rightBoundCount = rightBoundCountMap.getOrDefault(heightIndexKey, 0)
                val pointLeft = drawX.toInt() - dataSet.autoTurnShapePadding * rightBoundCount - shapeWidth.toInt() * rightBoundCount
                val pointShape = dataSet.autoTurnPointShape
                if (pointShape != null && rightBoundCount == 0) {
                    var pointTop = turnY - pointShapeHeight.toInt()
                    var pointBottom = turnY

                    if (pointBottom <= 0) {
                        pointBottom = pointShapeWidth.toInt()
                        pointTop = pointBottom - pointShapeHeight.toInt()
                    } else if (pointBottom >= contentRect.height()) {
                        pointBottom = contentRect.height()
                        pointTop = pointBottom - pointShapeHeight.toInt()
                    }

                    pointShape.setBounds(pointLeft, pointTop, pointLeft + pointShapeWidth.toInt(), pointBottom)
                    val pointSaveCount = canvas.save()
                    canvas.rotate(-90f, drawX, pointBottom.toFloat())
                    pointShape.draw(canvas)
                    canvas.restoreToCount(pointSaveCount)
                }

                // 再画图标
                val halfShapeHeight = shapeHeight * 0.5f
                if (turnY < halfShapeHeight) {// 画在顶部
                    shape?.setBounds(
                        pointLeft - pointShapeHeight.toInt() - shapeWidth.toInt(),
                        0,
                        pointLeft - pointShapeHeight.toInt(),
                        shapeHeight.toInt()
                    )
                } else if (turnY > contentRect.height() - halfShapeHeight) {// 画在底部
                    shape?.setBounds(
                        pointLeft - pointShapeHeight.toInt() - shapeWidth.toInt(),
                        contentRect.height() - shapeHeight.toInt(),
                        pointLeft - pointShapeHeight.toInt(),
                        contentRect.height()
                    )
                } else {// 居中正常画
                    shape?.setBounds(
                        pointLeft - pointShapeHeight.toInt() - shapeWidth.toInt(),
                        turnY - halfShapeHeight.toInt(),
                        pointLeft - pointShapeHeight.toInt(),
                        turnY + halfShapeHeight.toInt(),
                    )
                }
                val shapeSaveCount = canvas.save()
                shape?.draw(canvas)
                canvas.restoreToCount(shapeSaveCount)

                rightBoundCountMap[heightIndexKey] = rightBoundCount + 1
            } else {
                if (dataSet.isAutoTurn && dataSet.shapeAlign == SHAPE_ALIGN_TOP) { //顶部对齐
                    // 先画指示器 如果设置了
                    val pointShape = dataSet.autoTurnPointShape
                    val showPointShape = topHeights.getOrDefault(heightIndexKey, 0f) == shapeHeight
                    if (pointShape != null && showPointShape) {
                        val centerX = x + shapeWidth * 0.5f
                        val left =  (centerX - pointShapeWidth * 0.5f).toInt()
                        val right = (centerX + pointShapeWidth * 0.5f).toInt()
                        var top = y
                        if (shouldTurn) { // 如果需要转向 顶部对齐的改成底部对齐
                            top = y - pointShapeHeight.toInt() + shapeHeight.toInt()
                        }
                        pointShape.setBounds(left, top, right, top + pointShapeHeight.toInt())
                        val pointSaveCount = canvas.save()
                        pointShape.draw(canvas)
                        canvas.restoreToCount(pointSaveCount)
                    }

                    // 画图标
                    val shapeSplitSpace =  if (!showPointShape) dataSet.autoTurnShapePadding else 0
                    var shapeTop = y + pointShapeHeight.toInt() + shapeSplitSpace
                    if (shouldTurn) { // 如果需要转向
                        shapeTop = y - pointShapeHeight.toInt() - shapeSplitSpace
                    }
                    shape?.setBounds(x, shapeTop, (x + shapeWidth).toInt(), (shapeTop + shapeHeight).toInt())
                    val shapeSaveCount = canvas.save()
                    shape?.draw(canvas)
                    canvas.restoreToCount(shapeSaveCount)
                } else if (dataSet.isAutoTurn && dataSet.shapeAlign == SHAPE_ALIGN_BOTTOM) { //底部对齐
                    // 先画指示器 如果设置了
                    val pointShape = dataSet.autoTurnPointShape
                    val showPointShape = bottomHeights.getOrDefault(heightIndexKey, 0f) == shapeHeight
                    if (pointShape != null && showPointShape) {
                        val centerX = x + shapeWidth * 0.5f
                        val left =  (centerX - pointShapeWidth * 0.5f).toInt()
                        val right = (centerX + pointShapeWidth * 0.5f).toInt()
                        var top = y + shapeHeight.toInt() - pointShapeHeight.toInt()
                        if (shouldTurn) { // 如果需要转向 底部对齐的改成顶部对齐
                            top = y
                        }
                        pointShape.setBounds(left, top, right, top + pointShapeHeight.toInt())
                        val pointSaveCount = canvas.save()
                        pointShape.draw(canvas)
                        canvas.restoreToCount(pointSaveCount)
                    }

                    // 画图标
                    val shapeSplitSpace =  if (!showPointShape) dataSet.autoTurnShapePadding else 0
                    var shapeTop = y + shapeHeight.toInt() - pointShapeHeight.toInt() - shapeHeight.toInt() - shapeSplitSpace
                    if (shouldTurn) { // 如果需要转向
                        shapeTop = y + pointShapeHeight.toInt() + shapeSplitSpace
                    }
                    shape?.setBounds(x, shapeTop, (x + shapeWidth).toInt(), (shapeTop + shapeHeight).toInt())
                    val shapeSaveCount = canvas.save()
                    shape?.draw(canvas)
                    canvas.restoreToCount(shapeSaveCount)
                } else {
                    shape?.setBounds(
                        x,
                        y, (x + shapeWidth).toInt(), (y + shapeHeight).toInt()
                    )
                    val saveId = canvas.save()
                    shape?.draw(canvas)
                    canvas.restoreToCount(saveId)
                }
            }

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
        leftBoundCountMap.clear()
        rightBoundCountMap.clear()
    }
}