package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.BarValue
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
open class BarDataSet @JvmOverloads constructor(
    barValues: List<BarValue>,
    @AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH
) : AbstractDataSet<BarValue>(barValues, axisDependency) {

    var barWidth = 20f

    var minBarWidth = 0.0f

    var isAutoBarWidth = false

    var strokeThickness = 2f

    var barWidthPercent = 0.8f

    var isDrawValueEnable = false

    var valueColor = Color.BLACK

    var valueTextSize = 24f

    var valueFormatter: IValueFormatter? = null

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else max(minValueCount, values.size)
    }

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        maxVisibleY = -Double.MAX_VALUE
        minVisibleY = Double.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcMinMaxY(value)
        }

        val range = maxVisibleY - minVisibleY
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            minVisibleY -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            maxVisibleY += range * maxValueOffsetPercent
        }
    }

    override fun calcOverlayRatio(
        viewport: Viewport,
        baseDataSet: AbstractDataSet<*>
    ) : Float?{
        if (values.isEmpty()) return null
        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return null
        if (baseDataSet is CandlestickDataSet) {
            val foOpen = visiblePoints.first().values?.getOrNull(0) ?: return null
            val dataSize = baseDataSet.values.size
            val startIndex = max((dataSize * viewport.left).roundToInt(), 0)
            //k线在当前屏幕内的起点
            val fbOpen = baseDataSet.values.getOrNull(startIndex)?.open ?: return null
            if (!foOpen.isNaN() && foOpen != 0.0) {
                overLayRatio = (fbOpen / foOpen).toFloat()
                return overLayRatio
            }
        }
        return null
    }

    override fun calcOverlayMinMax(
        viewport: Viewport,
        ratio: Float?
    ) {
        if (values.isEmpty()) return
        maxVisibleY = -Double.MAX_VALUE
        minVisibleY = Double.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return
        overLayRatio = ratio

        for (value in visiblePoints) {
            calcMinMaxY(value)
        }

        val range = maxVisibleY - minVisibleY
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            minVisibleY -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            maxVisibleY += range * maxValueOffsetPercent
        }
    }

    protected fun calcMinMaxY(value: BarValue?) {
        if (value == null || !value.isEnable) return
        val values = value.values
        if (values != null && values.isNotEmpty()) {
            for (v in values) {
                if (v.isNaN()) continue
                if (v < minVisibleY) {
                    minVisibleY = v
                }
                if (v > maxVisibleY) {
                    maxVisibleY = v
                }
            }
        }
    }

    override fun addEntry(value: BarValue?): Boolean {
        if (value == null) return false
        calcMinMaxY(value)
        return values.add(value)
    }

    override fun removeEntry(value: BarValue?): Boolean {
        if (value == null) return false
        calcMinMaxY(value)
        return values.remove(value)
    }
}
