package cn.jingzhuan.lib.chart3.data.dataset

import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.value.LineValue
import kotlin.math.abs
import kotlin.math.max

class MinuteLineDataSet(
    lineValues: List<LineValue>,
    override var lastClose: Double,
    private val highPrice: Double = 0.0,
    private val lowPrice: Double = 0.0
) : LineDataSet(lineValues) {

    constructor(
        lineValues: List<LineValue>,
        lastClose: Float,
        highPrice: Float = 0f,
        lowPrice: Float = 0f
    ) : this(lineValues, lastClose.toDouble(), highPrice.toDouble(), lowPrice.toDouble())


    override fun calcMinMax(viewport: Viewport) {
        super.calcMinMax(viewport)

        if (lastClose > 0) {
            if (values.isNotEmpty()) {
                if (!highPrice.isNaN() && !lowPrice.isNaN() && highPrice > 0.1 && lowPrice > 0.1) {
                    viewportYMax = maxOf(highPrice.toFloat(), viewportYMax)
                    viewportYMin = minOf(lowPrice.toFloat(), viewportYMin)
                }

                var maxDiff = max(abs(viewportYMin - lastClose), abs(viewportYMax - lastClose)).toFloat()
                maxDiff = max((lastClose * 0.01).toFloat(), maxDiff)

                viewportYMin = (lastClose - maxDiff).toFloat()
                viewportYMax = (lastClose + maxDiff).toFloat()
            } else {
                viewportYMin = (lastClose * 0.99).toFloat()
                viewportYMax = (lastClose * 1.01).toFloat()
            }

        }
    }

}