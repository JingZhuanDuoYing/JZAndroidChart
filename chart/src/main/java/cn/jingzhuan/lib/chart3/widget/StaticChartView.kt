package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.util.AttributeSet

/**
 * 无法点击 无法主动滑动 无法缩放
 */
open class StaticChartView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CombineChartView(context, attrs, defStyleAttr) {

    init {
        isStatic = true
        isScrollEnable = false
        isScaleEnable = false

        axisLeft.apply {
            gridCount = 1
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
        }

        axisTop.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisRight.apply {
            gridCount = 0
            isGridLineEnable = false
            isLabelEnable = false
        }

        axisBottom.apply {
            labelHeight = 0
            isLabelEnable = false
            isGridLineEnable = false
        }

    }

}