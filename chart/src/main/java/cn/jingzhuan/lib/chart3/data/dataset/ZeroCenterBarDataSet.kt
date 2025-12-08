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
        super.calcMinMax(viewport)

            if (values.isNotEmpty()) {
                maxVisibleY = -Double.MAX_VALUE
                minVisibleY = Double.MAX_VALUE
                val barValues = getVisiblePoints(viewport) ?: emptyList()
                for (e in barValues) {
                    calcMinMaxY(e)
                }
                val middleValue = 0.0
                val maxDiff = max(
                    abs(maxVisibleY - middleValue),
                    abs(minVisibleY - middleValue),
                )
                // 重新设置最大最小值
                val maxDiffWithOffset = maxDiff * (1.0 + minValueOffsetPercent + maxValueOffsetPercent)
                minVisibleY = middleValue - maxDiffWithOffset
                maxVisibleY = middleValue + maxDiffWithOffset
            }

    }
}
