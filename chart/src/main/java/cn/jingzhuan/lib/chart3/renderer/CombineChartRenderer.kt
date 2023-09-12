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
import cn.jingzhuan.lib.chart3.draw.BarDraw
import cn.jingzhuan.lib.chart3.draw.CandlestickDraw
import cn.jingzhuan.lib.chart3.draw.LineDraw
import cn.jingzhuan.lib.chart3.draw.MaxMinArrowDraw
import cn.jingzhuan.lib.chart3.draw.ScatterDraw
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.roundToInt

/**
 * @since 2023-09-06
 */
class CombineChartRenderer(chart: AbstractChartView<AbstractDataSet<*>>) : AbstractRenderer<AbstractDataSet<*>>(chart) {

    private var combineData: CombineData? = null

    private var maxMinArrowDraw: MaxMinArrowDraw

    private var candlestickDraw: CandlestickDraw

    private var barDraw: BarDraw

    private var lineDraw: LineDraw

    private var scatterDraw: ScatterDraw

    init {
        maxMinArrowDraw = MaxMinArrowDraw(chart.maxMinValueTextColor, chart.maxMinValueTextSize)

        candlestickDraw = CandlestickDraw(chart.contentRect, renderPaint)

        barDraw = BarDraw(chart.contentRect, renderPaint, labelTextPaint, chart.getChartAnimator() ?: ChartAnimator())

        lineDraw = LineDraw(chart.contentRect, renderPaint, chart.getChartAnimator() ?: ChartAnimator())

        scatterDraw = ScatterDraw(chart.contentRect)

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

                val from = (currentViewport.left * valueCount).roundToInt()
                val to = (currentViewport.right * valueCount).roundToInt()
                val index: Int = getEntryIndex(x)
                if (index in from until to && index < dataSet.values.size) {
                    val value = dataSet.getEntryForIndex(index) ?: return

                    xPosition = value.x
                    yPosition = if (chart.isFollowFingerY) y else value.y
                    highlight.x = xPosition
                    highlight.y = yPosition
                    highlight.dataIndex = index
                    chart.highlightValue(highlight)
                }
            }
        })
    }

    /**
     * 开始绘制
     */
    override fun renderer(canvas: Canvas) {
        val combineData = getChartData()

        val sortedDataSets = combineData?.allDataSet ?: return

        for (i in sortedDataSets.indices) {
            val dataSet: AbstractDataSet<*> = sortedDataSets[i]
            if (dataSet is TreeDataSet) {

            }

            // 蜡烛
            if (dataSet is CandlestickDataSet) {
                candlestickDraw.drawDataSet(canvas, combineData.candlestickChartData, dataSet, currentViewport)
            }

            // 线
            if (dataSet is LineDataSet) {
                if (dataSet.isHorizontalLine) {
                    lineDraw.setHighLightState(chartView.highlightState != ChartConstant.HIGHLIGHT_STATUS_INITIAL)
                }
                lineDraw.drawDataSet(canvas, combineData.lineChartData, dataSet, currentViewport)

            }

            // 柱子
            if (dataSet is BarDataSet) {
                barDraw.drawDataSet(canvas, combineData.barChartData, dataSet, currentViewport)

            }

            // 图标
            if (dataSet is ScatterDataSet) {
                scatterDraw.drawDataSet(canvas, combineData.scatterChartData, dataSet, currentViewport)

            }

        }

        // 画最大最小值
        if (chartView.isShowMaxMinValue) {
            val chartData = getChartData() ?: return
            val dataSet = chartData.getTouchDataSet()
            if (dataSet is CandlestickDataSet) {
                val max = chartData.leftMax
                val min = chartData.leftMin

                val maxIndex = dataSet.maxIndex
                val maxData = dataSet.values[maxIndex]
                val maxX = maxData.x
                val maxValue = maxData.high
                val maxY = (max - maxValue) / (max - min) * contentRect.height()

                val minIndex = dataSet.minIndex
                val minData = dataSet.values[minIndex]
                val minX = dataSet.values[minIndex].x
                val minValue = minData.low
                val minY = (max - minValue) / (max - min) * contentRect.height()

                maxMinArrowDraw.drawMaxMin(canvas, contentRect.width(), maxX, minX, maxY, minY, maxValue, minValue, chartView.decimalDigitsNumber)
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