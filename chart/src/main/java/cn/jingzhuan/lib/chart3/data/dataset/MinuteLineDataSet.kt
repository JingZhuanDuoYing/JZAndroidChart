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
                    maxVisibleY = maxOf(highPrice, maxVisibleY)
                    minVisibleY = minOf(lowPrice, minVisibleY)
                }

                var maxDiff = max(abs(minVisibleY - lastClose), abs(maxVisibleY - lastClose))
                maxDiff = max((lastClose * 0.01), maxDiff)

                minVisibleY = lastClose - maxDiff
                maxVisibleY = lastClose + maxDiff
            } else {
                minVisibleY = lastClose * 0.99
                maxVisibleY = lastClose * 1.01
            }

        }
    }

}