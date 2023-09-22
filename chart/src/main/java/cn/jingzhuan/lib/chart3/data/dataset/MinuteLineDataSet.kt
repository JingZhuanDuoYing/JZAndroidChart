package cn.jingzhuan.lib.chart3.data.dataset

import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.value.LineValue
import kotlin.math.abs
import kotlin.math.max

class MinuteLineDataSet(
    lineValues: List<LineValue>,
    override var lastClose: Float,
    private val highPrice: Float = 0f,
    private val lowPrice: Float = 0f
) : LineDataSet(lineValues) {


    override fun calcMinMax(viewport: Viewport) {
        super.calcMinMax(viewport)

        if (lastClose > 0) {
            if (values.isNotEmpty()) {
                if (!highPrice.isNaN() && !lowPrice.isNaN() && highPrice > 0.1f && lowPrice > 0.1f) {
                    viewportYMax = maxOf(highPrice, viewportYMax)
                    viewportYMin = minOf(lowPrice, viewportYMin)
                }

                var maxDiff = max(abs(viewportYMin - lastClose), abs(viewportYMax - lastClose))
                maxDiff = max(lastClose * 0.01f, maxDiff)

                viewportYMin = lastClose - maxDiff
                viewportYMax = lastClose + maxDiff
            } else {
                viewportYMin = lastClose * 0.99f
                viewportYMax = lastClose * 1.01f
            }

        }
    }

}