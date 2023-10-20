package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import android.graphics.Paint
import android.util.Pair
import android.util.SparseArray
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.CandlestickValue
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.max

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

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        viewportYMax = -Float.MAX_VALUE
        viewportYMin = Float.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcViewportMinMax(value)
        }

        if (enableGap) {
            var max = -Float.MAX_VALUE
            var min = Float.MAX_VALUE
            for (i in values.indices.reversed()) {
                val e = values[i]
                if (isNaN(e.low)) continue
                if (isNaN(e.high)) continue
                if (isInfinite(e.low)) continue
                if (isInfinite(e.high)) continue
                if (min != Float.MAX_VALUE && max != -Float.MAX_VALUE) {
                    if (min - e.high > 0.01f) {
                        // 上涨缺口
                        highGaps.put(i, Pair(e.high, min))
                    }
                    if (e.low - max > 0.01f) {
                        // 下跌缺口
                        lowGaps.put(i, Pair(e.low, max))
                    }
                }
                if (e.low < min) min = e.low
                if (e.high > max) max = e.high
            }
        }
        val range: Float = viewportYMax - viewportYMin
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMin -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMax += range * maxValueOffsetPercent
        }
    }

    private fun calcViewportMinMax(value: CandlestickValue?) {
        if (value == null || !value.isVisible) return
        if (isNaN(value.low)) return
        if (isNaN(value.high)) return
        if (isInfinite(value.low)) return
        if (isInfinite(value.high)) return
        if (value.low < viewportYMin) {
            viewportYMin = value.low
            minIndex = values.indexOf(value)
        }
        if (value.high > viewportYMax) {
            viewportYMax = value.high
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
