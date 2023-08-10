package cn.jingzhuan.lib.chart2.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.BLACK
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.AbstractDataSet
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart2.renderer.CombineChartRenderer
import cn.jingzhuan.lib.chart2.widget.CombineChart
import kotlin.math.roundToInt


const val MIN_VISIBLE_ENTRY_COUNT = 10
const val MAX_VISIBLE_ENTRY_COUNT = 60
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

        minVisibleEntryCount = MIN_VISIBLE_ENTRY_COUNT
        maxVisibleEntryCount = MAX_VISIBLE_ENTRY_COUNT
        defaultVisibleEntryCount = DEFAULT_VISIBLE_ENTRY_COUNT
    }

    override fun addDataSet(abstractDataSet: AbstractDataSet<*>?) {
        if (abstractDataSet is CandlestickDataSet) {
            if (abstractDataSet.neutralColor != Color.TRANSPARENT)
                abstractDataSet.neutralColor = Color.GRAY
        }
        super.addDataSet(abstractDataSet)
    }

    override fun isCanZoomIn(): Boolean {
        return currentVisibleEntryCount >= MIN_VISIBLE_ENTRY_COUNT && super.isCanZoomIn()
    }

    override fun isCanZoomOut(): Boolean {
        return currentVisibleEntryCount <= MAX_VISIBLE_ENTRY_COUNT && super.isCanZoomOut()
    }

    override fun zoomIn(forceAlignX: Int) {
        if (currentVisibleEntryCount <= MIN_VISIBLE_ENTRY_COUNT) return
        super.zoomIn(forceAlignX)
    }

    override fun zoomOut(forceAlignX: Int) {
        if (currentVisibleEntryCount >= MAX_VISIBLE_ENTRY_COUNT) return
        super.zoomOut(forceAlignX)
    }

    fun stepMoveLeft() {
        val visibleSize = candlestickDataSet.firstOrNull()?.getVisibleRange(currentViewport)?.roundToInt() ?: 0
        val width = contentRect.width().toFloat()
        val percent = (width / visibleSize) / width
        moveLeft(percent)
    }

    fun stepMoveRight() {
        val visibleSize = candlestickDataSet.firstOrNull()?.getVisibleRange(currentViewport)?.roundToInt() ?: 0
        val width = contentRect.width().toFloat()
        val percent = (width / visibleSize) / width
        moveRight(percent)
    }


}