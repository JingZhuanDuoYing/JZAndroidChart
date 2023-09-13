package cn.jingzhuan.lib.chart3.data.dataset

import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.value.BarValue
import kotlin.math.abs
import kotlin.math.max

/**
 * @since 2023-09-13
 * created by lei
 * 水平中轴为0
 */
class ZeroCenterBarDataSet constructor(
    barValues: List<BarValue>,
    @AxisY.AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH
) : BarDataSet(barValues, axisDependency) {

    override fun calcMinMax(viewport: Viewport) {
        if (values.isNotEmpty()) {
            viewportYMax = -Float.MAX_VALUE
            viewportYMin = Float.MAX_VALUE
            val barValues = getVisiblePoints(viewport) ?: emptyList()
            for (e in barValues) {
                calcMinMaxY(e)
            }
            val middleValue = 0f
            val maxDiff = max(
                abs(viewportYMin - middleValue),
                abs(viewportYMax - middleValue)
            )
            val maxDiffWithOffset =
                if (maxValueOffsetPercent == 0f) maxDiff else maxDiff / (1 - maxValueOffsetPercent)
            viewportYMin = middleValue - maxDiffWithOffset
            viewportYMax = middleValue + maxDiffWithOffset
        }
    }
}
