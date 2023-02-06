package cn.jingzhuan.lib.chart2.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.BLACK
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.AbstractDataSet
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart2.renderer.CombineChartRenderer
import cn.jingzhuan.lib.chart2.widget.CombineChart


const val MIN_VISIBLE_ENTRY_COUNT = 15
const val MAX_VISIBLE_ENTRY_COUNT = 250
const val DEFAULT_VISIBLE_ENTRY_COUNT = 40

class TestChartKLineView(ctx: Context, attrs: AttributeSet?) : CombineChart(ctx, attrs) {
    private var viewportMin: Float = 0f
    private var viewportMax: Float = 0f

    override fun initChart() {
        super.initChart()
        setRenderer(object : CombineChartRenderer(this) {
            init {
                highlightColor = BLACK
            }

            override fun calcDataSetMinMax() {
                super.calcDataSetMinMax()
                viewportMin = chartData.leftMin
                viewportMax = chartData.leftMax
            }

            override fun renderHighlighted(canvas: Canvas?, highlights: Array<out Highlight>) {
                super.renderHighlighted(
                    canvas,
                    highlights.map { it.y = it.touchY; it }.toTypedArray()
                )
            }
        })

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

    override fun addDataSet(abstractDataSet: AbstractDataSet<*>?) {
        if (abstractDataSet is CandlestickDataSet) {
            if (abstractDataSet.neutralColor != Color.TRANSPARENT)
                abstractDataSet.neutralColor = Color.GRAY
            super.addDataSet(abstractDataSet)
        }
    }


}