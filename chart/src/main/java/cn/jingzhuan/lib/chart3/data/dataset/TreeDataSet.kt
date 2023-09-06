package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.TreeValue
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-05
 * created by lei
 */
open class TreeDataSet : AbstractDataSet<TreeValue> {

    constructor(treeValues: List<TreeValue>) : this(treeValues, AxisY.DEPENDENCY_BOTH)

    constructor(treeValues: List<TreeValue>, @AxisDependency axisDependency: Int) : super(treeValues, axisDependency)

    var strokeThickness = 2f

    var positiveColor = Color.RED

    var negativeColor = Color.GREEN

    var colorAlpha = 255
        set(colorAlpha) {
            if (colorAlpha < 0) {
                field = 0
                return
            }
            if (colorAlpha > 255) {
                field = 255
                return
            }
            field = colorAlpha
        }

    override fun getEntryCount(): Int {
        return values.size
    }

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        viewportYMax = -Float.MAX_VALUE
        viewportYMin = Float.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)

        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcMinMaxY(value)
        }

        val range = viewportYMax - viewportYMin
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMin -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMax += range * maxValueOffsetPercent
        }
    }

    private fun calcMinMaxY(value: TreeValue?) {
        if (value == null || !value.isEnable) return
        val high = value.high
        val low = value.low
        if (!isNaN(high) && !isInfinite(high)) {
            viewportYMin = min(viewportYMin, low)
            viewportYMax = max(viewportYMax, high)
        }
    }

    override fun addEntry(value: TreeValue?): Boolean {
        if (value == null) return false
        calcMinMaxY(value)
        return values.add(value)
    }

    override fun removeEntry(value: TreeValue?): Boolean {
        if (value == null) return false
        calcMinMaxY(value)
        return values.remove(value)
    }
}
