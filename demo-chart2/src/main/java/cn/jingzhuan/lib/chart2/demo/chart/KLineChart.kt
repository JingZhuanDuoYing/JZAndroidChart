package cn.jingzhuan.lib.chart2.demo.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.util.AttributeSet
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart2.renderer.JZCombineChartRenderer
import cn.jingzhuan.lib.chart2.widget.JZCombineChart


const val MIN_VISIBLE_ENTRY_COUNT = 15
const val MAX_VISIBLE_ENTRY_COUNT = 250
const val DEFAULT_VISIBLE_ENTRY_COUNT = 40

class KLineChart(ctx: Context, attrs: AttributeSet?) : JZCombineChart(ctx, attrs) {
    private var viewportMin: Float = 0f
    private var viewportMax: Float = 0f

    init {
        highlightColor = BLACK

        axisBottom.isLabelEnable = true
        axisBottom.labelHeight = 30
        axisBottom.labelTextSize = 28f
        axisBottom.labelTextColor = BLACK
        setIsMainChart(true)

        setDoubleTapToZoom(true)
        isScaleGestureEnable = true
        isScaleXEnable = true


        axisTop.isGridLineEnable = true
        axisLeft.gridCount = 1
        axisTop.gridCount = 1
        axisRight.gridCount = 3
        axisBottom.gridCount = 3
        axisLeft.isLabelEnable = true
        axisRight.isLabelEnable = false
        axisLeft.setLabelValueFormatter { value, index ->
            if (index == 1 || index == 3) "" else String.format("%.2f", value)
        }
        axisLeft.enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
        axisBottom.enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)

        setMinVisibleEntryCount(MIN_VISIBLE_ENTRY_COUNT)
        setMaxVisibleEntryCount(MAX_VISIBLE_ENTRY_COUNT)
        setDefaultVisibleEntryCount(DEFAULT_VISIBLE_ENTRY_COUNT)

    }


}