package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter

class KlineChartView(ctx: Context, attrs: AttributeSet?) : CombineChartView(ctx, attrs) {


    override fun initChart() {
        isEnableVerticalHighlight = true
        isEnableHorizontalHighlight = true
        isEnableHighlightLeftText = true
        isEnableHighlightRightText = true
        isEnableHighlightBottomText = true
        highlightTextBgHeight = 50
        highlightTextBgColor = Color.BLACK
        highlightTextColor = Color.WHITE
        highlightTextSize = 28f
        super.initChart()

        isScaleEnable = true

        axisLeft.apply {
            gridCount = 3
            isLabelEnable = true
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    return if (index == 1 || index == 3) "" else String.format("%.2f", value)
                }
            }

        }

        axisRight.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisBottom.apply {
            gridCount = 3
            isLabelEnable = true
            labelHeight = 50
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
        }

    }

    override fun isCanZoomIn(): Boolean {
        return currentVisibleEntryCount >= minVisibleEntryCount && super.isCanZoomIn()
    }

    override fun isCanZoomOut(): Boolean {
        return currentVisibleEntryCount <= maxVisibleEntryCount && super.isCanZoomOut()
    }

    override fun zoomIn(forceAlignX: Int) {
        if (currentVisibleEntryCount <= minVisibleEntryCount) return
        super.zoomIn(forceAlignX)
    }

    override fun zoomOut(forceAlignX: Int) {
        if (currentVisibleEntryCount >= maxVisibleEntryCount) return
        super.zoomOut(forceAlignX)
    }

}