package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.widget.MinuteChartView

class MainMinuteChartView(ctx: Context, attrs: AttributeSet?) : MinuteChartView(ctx, attrs) {

    init {
        maxVisibleEntryCount = 242
        minVisibleEntryCount = 242
        decimalDigitsNumber = 2

    }

}