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
                    maxVisibleY = maxOf(highPrice.toFloat(), maxVisibleY)
                    minVisibleY = minOf(lowPrice.toFloat(), minVisibleY)
                }

                var maxDiff = max(abs(minVisibleY - lastClose), abs(maxVisibleY - lastClose)).toFloat()
                maxDiff = max((lastClose * 0.01).toFloat(), maxDiff)

                minVisibleY = (lastClose - maxDiff).toFloat()
                maxVisibleY = (lastClose + maxDiff).toFloat()
            } else {
                minVisibleY = (lastClose * 0.99).toFloat()
                maxVisibleY = (lastClose * 1.01).toFloat()
            }

        }
    }

}