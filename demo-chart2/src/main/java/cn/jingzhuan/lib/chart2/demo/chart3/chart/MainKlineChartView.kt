package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class MainKlineChartView(ctx: Context, attrs: AttributeSet?) : KlineChartView(ctx, attrs) {

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

        setRangeTouchBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ico_red_dot), BitmapFactory.decodeResource(context.resources, R.drawable.ico_red_dot))
        setShowRangeLine(true)
        setRangeHedgeWhole(true)
        setRangeMaxDiffEntry(5)

    }
    init {
        currentVisibleEntryCount = 250
        minVisibleEntryCount = 10
        decimalDigitsNumber = 3
        axisRight.gridCount = 0
        axisLeft.isGridSlidIndex = 2
        showScaleMin = true
    }

}