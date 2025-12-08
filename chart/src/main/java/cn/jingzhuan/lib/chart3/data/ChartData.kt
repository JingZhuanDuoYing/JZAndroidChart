package cn.jingzhuan.lib.chart3.data

import android.graphics.Rect
import android.util.ArrayMap
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.renderer.AxisRenderer
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_TOP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_TOP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_LEFT
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_RIGHT
import java.util.Collections
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-05
 * created by lei
 */
open class ChartData<T : AbstractDataSet<*>> {

    private var chartData: MutableList<T> = Collections.synchronizedList(ArrayList())

    var leftMin = Float.MAX_VALUE

    var leftMax = -Float.MAX_VALUE

    var rightMin = Float.MAX_VALUE

    var rightMax = -Float.MAX_VALUE

    private var axisRenderers: ArrayMap<Int, AxisRenderer<T>>? = null

    private var offsetsMapper: MutableMap<String, MutableMap<String, Float>> = mutableMapOf()

    val dataSets: MutableList<T>
        get() {
            return chartData
        }

    private lateinit var abstractChart: AbstractChartView<T>

    open fun add(e: T?): Boolean {
        if (e == null) return false
        synchronized(this) {
            return dataSets.add(e)
        }
    }

    open fun remove(e: T?): Boolean {
        synchronized(this) {
            return e != null && dataSets.remove(e)
        }
    }

    fun clear() {
        synchronized(this) { dataSets.clear() }
        leftMin = Float.MAX_VALUE
        leftMax = -Float.MAX_VALUE
        rightMin = Float.MAX_VALUE
        rightMax = -Float.MAX_VALUE
    }

    private fun setAxisMinMax() {
        if (axisRenderers != null) {
            val leftAxis = axisRenderers?.get(TYPE_AXIS_LEFT)?.axis as AxisY
            leftAxis.apply {
                yMin = leftMin
                yMax = leftMax
            }

            val rightAxis = axisRenderers?.get(TYPE_AXIS_RIGHT)?.axis as AxisY
            rightAxis.apply {
                yMin = rightMin
                yMax = rightMax
            }
        }
    }

    open fun calcMaxMin(viewport: Viewport, content: Rect, offsetPercent: Float) {
        calcMaxMin(viewport, content, offsetPercent, -Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE)
    }

    open fun calcMaxMin(viewport: Viewport, content: Rect, offsetPercent: Float, lMax: Float, lMin: Float, rMax: Float, rMin: Float) {
        leftMin = lMin
        leftMax = lMax
        rightMin = rMin
        rightMax = rMax

        if (dataSets.isNotEmpty()) {
            synchronized(this) {
                val basisDataSet = dataSets.find { it is CandlestickDataSet && it.isBasis }
                if (abstractChart.showMinLine() && basisDataSet != null) {
                    calcMinMaxDataSet(basisDataSet, viewport, content, null)
                } else {
                    val (normalList, scatterList) = dataSets.partition { it !is ScatterDataSet }
                    calcMinMaxInner(normalList, viewport, content)
                    calcMinMaxScatters(scatterList, viewport, content)
                }

                if (offsetPercent != 0f) {
                    val rangeLeft = leftMax - leftMin
                    leftMin -= rangeLeft * offsetPercent
                    leftMax += rangeLeft * offsetPercent

                    val rangeRight = rightMax - rightMin
                    rightMin -= rangeRight * offsetPercent
                    rightMax += rangeRight * offsetPercent
                }

            }
        }
        setAxisMinMax()
    }

    private fun calcMinMaxScatters(dataSets: List<T>, viewport: Viewport, content: Rect) {
        if (dataSets.isNotEmpty() && abs(leftMin) != Float.MAX_VALUE && abs(leftMax) != Float.MAX_VALUE
            || abs(rightMin) != Float.MAX_VALUE && abs(rightMax) != Float.MAX_VALUE) {
            this.offsetsMapper.clear()
            val originYLeftMax = leftMax
            val originYLeftMin = leftMin
            val originYRightMax = rightMax
            val originYRightMin = rightMin

            val scatters = dataSets.filter { it is ScatterDataSet && it.isEnable
                    && (it.shapeAlign == SHAPE_ALIGN_TOP || it.shapeAlign == SHAPE_ALIGN_BOTTOM) }
            for (t in scatters) {
                if (t !is ScatterDataSet) continue
                calcMinMaxScatterDataSet(t, viewport, content, originYLeftMax, originYLeftMin, originYRightMax, originYRightMin, offsetsMapper)
            }

            val parentsScatters = dataSets.filter { it is ScatterDataSet && it.isEnable
                    && (it.shapeAlign == SHAPE_ALIGN_PARENT_TOP || it.shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) }
            for (t in parentsScatters) {
                if (t !is ScatterDataSet) continue
                calcMinMaxScatterDataSet(t, viewport, content, leftMax, leftMin, rightMax, rightMin, offsetsMapper)
            }
        }
    }

    private fun calcMinMaxInner(dataSets: List<T>, viewport: Viewport, content: Rect) {
        var overlayRatio: Float? = null
        for (t in dataSets) {
            if (t.overlayKline) {
                val dataSet = getTouchDataSet()
                if (dataSet != null) {
                    overlayRatio = t.calcOverlayRatio(viewport, dataSet)
                    if (overlayRatio != null) break
                }
            }
        }
        for (t in dataSets) {
            if (!t.isEnable) continue
            calcMinMaxDataSet(t, viewport, content, overlayRatio)
        }
    }

    private fun calcMinMaxScatterDataSet(
        t: ScatterDataSet,
        viewport: Viewport,
        content: Rect,
        originYLeftMax: Float,
        originYLeftMin: Float,
        originYRightMax: Float,
        originYRightMin: Float,
        offsetsMapper: MutableMap<String, MutableMap<String, Float>>
    ) {
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
            t.calcMinMaxInner(viewport, content, leftMax, leftMin, originYLeftMax, originYLeftMin, offsetsMapper)
        }
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
            t.calcMinMaxInner(viewport, content, rightMax, rightMin, originYRightMax, originYRightMin, offsetsMapper)
        }
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
            leftMax = max(leftMax, t.maxVisibleY)
            leftMin = min(leftMin, t.minVisibleY)
        }
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
            rightMax = max(rightMax, t.maxVisibleY)
            rightMin = min(rightMin, t.minVisibleY)
        }
    }

    private fun calcMinMaxDataSet(t: T, viewport: Viewport, content: Rect, overlayRatio: Float?) {
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
            if (t.overlayKline) {
                t.calcOverlayMinMax(viewport, overlayRatio)
            } else {
                t.calcMinMax(viewport, content, leftMax, leftMin)
            }
        }
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
            if (t.overlayKline) {
                t.calcOverlayMinMax(viewport, overlayRatio)
            } else {
                t.calcMinMax(viewport, content, rightMax, rightMin)
            }
        }

        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
            leftMax = max(leftMax, t.maxVisibleY)
            leftMin = min(leftMin, t.minVisibleY)
        }
        if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
            rightMax = max(rightMax, t.maxVisibleY)
            rightMin = min(rightMin, t.minVisibleY)
        }
    }

    fun setChart(chart: AbstractChartView<T>) {
        abstractChart = chart
        axisRenderers = chart.axisRenderers
    }

    open fun getTouchDataSet(): T?{
        return null
    }

    open fun getFlagDataSet(): T?{
        return null
    }

    open fun getTouchEntryCount(): Int{
        return 0
    }
}
