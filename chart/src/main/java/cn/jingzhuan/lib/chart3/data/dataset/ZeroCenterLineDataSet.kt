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
        super.calcMinMax(viewport)
        var maxDiff = max(
            abs(this.viewportYMin - this.middleValue),
            abs(this.viewportYMax - this.middleValue)
        )
        maxDiff = max(this.middleValue * 0.01f, maxDiff)
        this.viewportYMin = this.middleValue - maxDiff
        this.viewportYMax = this.middleValue + maxDiff
    }
}
