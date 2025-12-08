package cn.jingzhuan.lib.chart3.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.ColorUtils
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet

/**
 * @since 2023-09-13
 * @author lei
 */
class TreeDraw(
    private val contentRect: Rect,
    private val renderPaint: Paint,
    private val chartAnimator: ChartAnimator
) : IDraw<TreeDataSet> {

    private var focusIndex: Int = -1

    fun setFocusIndex(index: Int) {
        this.focusIndex = index
    }

    override fun drawDataSet(
        canvas: Canvas,
        chartData: ChartData<TreeDataSet>,
        dataSet: TreeDataSet,
        viewport: Viewport,
    ) {
        if (dataSet.isVisible && dataSet.values.size > focusIndex) {
            drawTree(
                canvas, dataSet,
                chartData.leftMax, chartData.leftMin,
                chartData.rightMax, chartData.rightMin
            )
        }
    }

    private fun drawTree(
        canvas: Canvas,
        dataSet: TreeDataSet,
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

        renderPaint.strokeWidth = dataSet.strokeThickness
        renderPaint.style = Paint.Style.FILL

        // 只在图中间绘制指定的 TreeValue
        val treeValue = dataSet.getEntryForIndex(focusIndex) ?: return

        if (!treeValue.isEnable) return
        val leafCount = treeValue.leafCount
        if (leafCount < 1) return

        val zeroX: Float = contentRect.width() / 2f
        val maxLeafSpace: Float = contentRect.width() / 4f
        val maxLeafValue = treeValue.maxLeafValue

        for (i in 0 until leafCount) {
            val leaf = treeValue.leafs?.get(i)
            if (leaf == null || leaf.high.isNaN()) continue

            val leftValue = leaf.leftValue
            val rightValue = leaf.rightValue
            val high = leaf.high * chartAnimator.phaseY

            val y: Float = calcHeight(high, max, min)

            treeValue.setCoordinate(contentRect.width() / 2f, y)

            val left = zeroX - leftValue.toFloat() / maxLeafValue.toFloat() * maxLeafSpace
            val positiveColor =
                ColorUtils.setAlphaComponent(dataSet.positiveColor, dataSet.colorAlpha)
            renderPaint.color = positiveColor
            canvas.drawLine(left, y, zeroX, y, renderPaint)

            val right = zeroX + rightValue.toFloat() / maxLeafValue.toFloat() * maxLeafSpace
            val negativeColor =
                ColorUtils.setAlphaComponent(dataSet.negativeColor, dataSet.colorAlpha)
            renderPaint.color = negativeColor
            canvas.drawLine(zeroX, y, right, y, renderPaint)
        }

    }

    private fun calcHeight(value: Double, max: Double, min: Double): Float {
        return if (max.compareTo(min) == 0) 0f else ((max - value) / (max - min) * contentRect.height()).toFloat()
    }

}