package cn.jingzhuan.lib.chart3.data

import android.graphics.Rect
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import java.util.Collections
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-05
 * created by lei
 */
open class ChartData<V : AbstractValue, T : AbstractDataSet<V>> {

    private var chartData: MutableList<T> = Collections.synchronizedList(ArrayList())

    var leftMin = Float.MAX_VALUE

    var leftMax = -Float.MAX_VALUE

    var rightMin = Float.MAX_VALUE

    var rightMax = -Float.MAX_VALUE

    var entryCount = 0

    var leftAxis: AxisY? = null

    var rightAxis: AxisY? = null

    val dataSets: MutableList<T>
        get() {
            return chartData
        }

    fun add(e: T?): Boolean {
        if (e == null) return false
        synchronized(this) {
            return dataSets.add(e)
        }
    }

    fun remove(e: T?): Boolean {
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

    fun setMinMax() {
        if (leftAxis != null && leftMin != Float.MAX_VALUE) {
            leftAxis?.yMin = leftMin
            leftAxis?.yMax = leftMax
        }
        if (rightAxis != null && rightMin != Float.MAX_VALUE) {
            rightAxis?.yMin = rightMin
            rightAxis?.yMax = rightMax
        }
    }

    @JvmOverloads
    fun calcMaxMin(
        viewport: Viewport?,
        content: Rect?,
        lMax: Float = -Float.MAX_VALUE,
        lMin: Float = Float.MAX_VALUE,
        rMax: Float = -Float.MAX_VALUE,
        rMin: Float = Float.MAX_VALUE
    ) {
        leftMin = lMin
        leftMax = lMax
        rightMin = rMin
        rightMax = rMax
        entryCount = 0
        if (dataSets.isNotEmpty()) {
            synchronized(this) {
                for (t in dataSets) {
                    if (!t.dataSetEnable || t !is ScatterDataSet) continue
                    if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
                        t.calcMinMax(viewport!!, content!!, leftMax, leftMin)
                    }
                    if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
                        t.calcMinMax(viewport!!, content!!, rightMax, rightMin)
                    }
                    if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_LEFT) {
                        leftMax = max(leftMax, t.viewportYMax)
                        leftMin = min(leftMin, t.viewportYMin)
                    }
                    if (t.axisDependency == AxisY.DEPENDENCY_BOTH || t.axisDependency == AxisY.DEPENDENCY_RIGHT) {
                        rightMax = max(rightMax, t.viewportYMax)
                        rightMin = min(rightMin, t.viewportYMin)
                    }
                    if (t.getEntryCount() > entryCount) {
                        entryCount = t.getEntryCount()
                    }
                }
            }
            setMinMax()
        }
    }

    fun setChart(chart: AbstractChartView<V, T>) {
        leftAxis = chart.axisLeftRenderer.axis as AxisY
        rightAxis = chart.axisRightRenderer.axis as AxisY
    }
}
