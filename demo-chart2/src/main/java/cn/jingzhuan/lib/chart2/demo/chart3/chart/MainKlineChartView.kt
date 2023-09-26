package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class MainKlineChartView(ctx: Context, attrs: AttributeSet?) : KlineChartView(ctx, attrs) {
//    app:highlightTextBgHeight="18dp"
//    app:highlightColor="#000"
//    app:highlightThickness="1dp"
//    app:highlightTextSize="11dp"

    override fun initChart() {
        super.initChart()
        highlightThickness = 4
        highlightColor = ContextCompat.getColor(
            context,
            android.R.color.holo_red_dark
        )
        highlightTextColor = ContextCompat.getColor(
            context,
            android.R.color.white
        )
        highlightTextSize = 28
    }
    init {
        currentVisibleEntryCount = 40
        decimalDigitsNumber = 3
    }

}