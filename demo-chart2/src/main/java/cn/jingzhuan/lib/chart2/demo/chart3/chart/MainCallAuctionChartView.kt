package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.widget.MinuteChartView


class MainCallAuctionChartView(ctx: Context, attrs: AttributeSet?) : MinuteChartView(ctx, attrs) {

    init {
        minVisibleEntryCount = 600
        maxVisibleEntryCount = 600

        isEnableHighlightLeftText = false

        axisLeft.apply {
            isLabelEnable = false
            gridCount = 3
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
        }
        axisRight.apply {
            isLabelEnable = false
            gridCount = 1
        }

        axisTop.apply {
            isLabelEnable = false
            isGridLineEnable = false
        }
        axisBottom.apply {
            isGridLineEnable = false
            gridCount = 1
            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    return when (index) {
                        0 -> "09:15-25"
                        else -> ""
                    }
                }
            }
        }
    }
}