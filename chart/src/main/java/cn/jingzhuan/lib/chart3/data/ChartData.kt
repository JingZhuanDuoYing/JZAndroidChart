package cn.jingzhuan.lib.chart3.data

import android.graphics.Rect
import android.util.ArrayMap
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.renderer.AxisRenderer
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_LEFT
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_RIGHT
import java.util.Collections
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

    val dataSets: MutableList<T>
        get() {
            return chartData
        }

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

        var overlayRatio: Float? = null

        if (dataSets.isNotEmpty()) {
            synchronized(this) {
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
                    if (!t.isEnable || t is ScatterDataSet) continue

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
                        leftMax = max(leftMax, t.viewportYMax)
                        leftMin = min(leftMin, t.viewportYMin)
                    }
                    if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
                        rightMax = max(rightMax, t.viewportYMax)
                        rightMin = min(rightMin, t.viewportYMin)
                    }
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

    fun setChart(chart: AbstractChartView<T>) {
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
