package cn.jingzhuan.lib.chart3.data.dataset

import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.value.LineValue
import kotlin.math.abs
import kotlin.math.max

/**
 * @since 2023-09-21
 * created by lei
 * 水平中轴为0
 */
class ZeroCenterLineDataSet constructor(
    lineValues: List<LineValue>,
    @AxisY.AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH,
) : LineDataSet(lineValues, axisDependency) {

    private val middleValue = 0f

    override fun calcMinMax(viewport: Viewport) {
        if (values.isNotEmpty()) {
            super.calcMinMax(viewport)
            val middleValue = 0.0
            val maxDiff = max(
                abs(maxVisibleY - middleValue),
                abs(minVisibleY - middleValue),
            )
            // 重新设置最大最小值
            minVisibleY = middleValue - maxDiff
            maxVisibleY = middleValue + maxDiff
        }
    }
}
