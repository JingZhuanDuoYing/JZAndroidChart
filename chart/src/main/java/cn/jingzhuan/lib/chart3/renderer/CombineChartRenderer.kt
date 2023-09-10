package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet
import cn.jingzhuan.lib.chart3.draw.CandlestickDraw
import cn.jingzhuan.lib.chart3.draw.LineDraw
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.max

/**
 * @since 2023-09-06
 */
class CombineChartRenderer(chart: AbstractChartView<AbstractDataSet<*>>) : AbstractRenderer<AbstractDataSet<*>>(chart) {

    private var combineData: CombineData? = null

    private var candlestickDraw: CandlestickDraw

    private var lineDraw: LineDraw

    init {
        candlestickDraw = CandlestickDraw(chart.contentRect, renderPaint)

        lineDraw = LineDraw(chart.contentRect, renderPaint, chart.getChartAnimator() ?: ChartAnimator())

        chart.setViewportChangeListener { viewport ->
            currentViewport.set(viewport)
            calcDataSetMinMax()
        }

        val highlight = Highlight()
        chart.addOnTouchPointListener(object : OnTouchPointListener {
            override fun touch(x: Float, y: Float) {
                if (!chart.isHighlightEnable) return
                if (combineData == null) return

                val dataSet = combineData!!.getTouchDataSet()

                val valueCount = dataSet.getEntryCount()
                val xPosition: Float
                val yPosition: Float
                highlight.touchX = x
                highlight.touchY = y
                val index: Int = getEntryIndex(x)
                if (index in 0 until valueCount && index < dataSet.values.size) {
                    val value = dataSet.getEntryForIndex(index) ?: return
                    xPosition = max(contentRect.left.toFloat(), value.x)
                    yPosition = value.y
                    if (xPosition >= 0 && yPosition >= 0) {
                        highlight.x = xPosition
                        highlight.y = if (chart.isFollowFingerY) y else yPosition
                        highlight.dataIndex = index
                        chart.highlightValue(highlight)
                    }
                }
            }
        })
    }

    /**
     * 开始绘制
     */
    override fun renderer(canvas: Canvas) {
        val combineData = getChartData()

        val sortedDataSets = combineData!!.allDataSet

        for (i in sortedDataSets.indices) {
            val dataSet: AbstractDataSet<*> = sortedDataSets[i]
            if (dataSet is TreeDataSet) {

            }
            if (dataSet is CandlestickDataSet) {
                candlestickDraw.drawDataSet(canvas, combineData.candlestickChartData, dataSet, chartView.currentViewport)
            }
            if (dataSet is LineDataSet) {
                lineDraw.setHighLightState(chartView.highlightState != ChartConstant.HIGHLIGHT_STATUS_INITIAL)
                lineDraw.drawDataSet(canvas, combineData.lineChartData, dataSet, chartView.currentViewport)

            }
            if (dataSet is BarDataSet) {

            }
            if (dataSet is ScatterDataSet) {

            }

        }
    }

    override fun addDataSet(dataSet: AbstractDataSet<*>?) {
        if (dataSet == null) return

        super.addDataSet(dataSet)

        calcDataSetMinMax()
    }

    /**
     * 清掉dataset 并 重新计算viewport
     */
    override fun clearDataSet() {
        super.clearDataSet()
//        cleanTreeDataSet()
//        cleanLineDataSet()
//        cleanBarDataSet()
//        cleanCandlestickDataSet()
//        cleanScatterDataSet()

        calcDataSetMinMax()
    }

    override fun getChartData(): CombineData? {
        if (combineData == null) combineData = CombineData()
        return combineData
    }

}