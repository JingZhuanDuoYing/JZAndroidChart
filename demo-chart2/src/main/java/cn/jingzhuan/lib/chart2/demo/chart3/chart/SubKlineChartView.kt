package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.widget.StaticChartView

class SubKlineChartView(ctx: Context, attrs: AttributeSet?) : StaticChartView(ctx, attrs) {

    init {
        currentVisibleEntryCount = 40
        decimalDigitsNumber = 2
        isEnableHighlightBottomText = false
    }

}