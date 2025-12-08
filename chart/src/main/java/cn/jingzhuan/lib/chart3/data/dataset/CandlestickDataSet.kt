package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import android.graphics.Paint
import android.util.Pair
import android.util.SparseArray
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.CandlestickValue

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
open class CandlestickDataSet @JvmOverloads constructor(
    candlestickValues: List<CandlestickValue>,
    @AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH,
) : AbstractDataSet<CandlestickValue>(candlestickValues, axisDependency) {

    var isAutoWidth = true

    var candleWidth = -1f

    /**
     * 阳线 颜色
     */
    var increasingColor = -0x7b4b5

    /**
     * 阴线 颜色
     */
    var decreasingColor = -0xe214a5

    /**
     * 中性 颜色
     */
    var neutralColor = Color.WHITE

    var limitUpColor = Color.TRANSPARENT

    var limitUpColor20 = Color.TRANSPARENT

    /**
     * 缺口 颜色
     */
    var gapColor = Color.LTGRAY

    var limitUpPaintStyle: Paint.Style? = null

    var strokeThickness = 4f

    /**
     * 阳线、阴线样式-> 实心或空心
     */
    var increasingPaintStyle = Paint.Style.FILL

    var decreasingPaintStyle = Paint.Style.FILL

    /**
     * 是否显示缺口
     */
    var enableGap = false

    var lowGaps: SparseArray<Pair<Float, Float>> = SparseArray()

    var highGaps: SparseArray<Pair<Float, Float>> = SparseArray()

    var gapMaxSize = 3

    var candleWidthPercent = 0.8f

    var lineThickness = 2

    var lineColor = Color.GRAY

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        maxVisibleY = -Double.MAX_VALUE
        minVisibleY = Double.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcViewportMinMax(value)
        }

        if (enableGap) {
            var max = -Double.MAX_VALUE
            var min = Double.MAX_VALUE
            highGaps.clear()
            lowGaps.clear()
            val leftIndex = (values.size * viewport.left).roundToInt()
            for (i in visiblePoints.indices.reversed()) {
                val e = visiblePoints[i]
                if (e.low.isNaN()) continue
                if (e.high.isNaN()) continue
                if (e.low.isInfinite()) continue
                if (e.high.isInfinite()) continue
                if (min != Double.MAX_VALUE && max != -Double.MAX_VALUE) {
                    val index = leftIndex + i
                    if (min - e.high > 0) {
                        // 上涨缺口
                        highGaps.put(index, Pair(e.high.toFloat(), min.toFloat()))
                    }
                    if (e.low - max > 0) {
                        // 下跌缺口
                        lowGaps.put(index, Pair(e.low.toFloat(), max.toFloat()))
                    }
                }
                if (e.low < min) min = e.low
                if (e.high > max) max = e.high
            }
        }
        val range: Double = maxVisibleY - minVisibleY
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            minVisibleY -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            maxVisibleY += range * maxValueOffsetPercent
        }
    }

    private fun calcViewportMinMax(value: CandlestickValue?) {
        if (value == null || !value.isVisible) return
        if (value.low.isNaN()) return
        if (value.high.isNaN()) return
        if (value.low.isInfinite()) return
        if (value.high.isInfinite()) return
        if (value.low < minVisibleY) {
            minVisibleY = value.low
            minIndex = values.indexOf(value)
        }
        if (value.high > maxVisibleY) {
            maxVisibleY = value.high
            maxIndex = values.indexOf(value)
        }
    }

    override fun getEntryCount(): Int {
        return max(minValueCount, values.size)
    }

    override fun addEntry(value: CandlestickValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)

        // add the entry
        return values.add(value)
    }

    override fun removeEntry(value: CandlestickValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)
        return values.remove(value)
    }
}
