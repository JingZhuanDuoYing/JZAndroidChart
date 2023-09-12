package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class MainKlineChartView(ctx: Context, attrs: AttributeSet?) : KlineChartView(ctx, attrs) {

    init {
        currentVisibleEntryCount = 40
        decimalDigitsNumber = 3
    }

}