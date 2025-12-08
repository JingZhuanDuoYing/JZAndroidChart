package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterTextDataSet
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.draw.BarDraw
import cn.jingzhuan.lib.chart3.draw.CandlestickDraw
import cn.jingzhuan.lib.chart3.draw.LineDraw
import cn.jingzhuan.lib.chart3.draw.MaxMinArrowDraw
import cn.jingzhuan.lib.chart3.draw.ScatterDraw
import cn.jingzhuan.lib.chart3.draw.ScatterTextDraw
import cn.jingzhuan.lib.chart3.draw.TreeDraw
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.roundToInt

/**
 * @since 2023-09-06
 */
class CombineChartRenderer(chart: AbstractChartView<AbstractDataSet<*>>) : AbstractRenderer<AbstractDataSet<*>>(chart) {

    private var combineData: CombineData? = null

    private val maxMinArrowDraw by lazy {
        MaxMinArrowDraw(chart.maxMinValueTextColor, chart.maxMinValueTextSize)
    }

    private val candlestickDraw by lazy {
        CandlestickDraw(contentRect, renderPaint)
    }

    private val barDraw by lazy {
        BarDraw(contentRect, renderPaint, labelTextPaint, chart.getChartAnimator() ?: ChartAnimator())
    }

    private val lineDraw by lazy {
        LineDraw(contentRect, renderPaint, chart.getChartAnimator() ?: ChartAnimator())
    }

    private val scatterDraw by lazy {
        ScatterDraw(contentRect)
    }

    private val scatterTextDraw by lazy {
        ScatterTextDraw(contentRect, renderPaint, labelTextPaint, chart.getChartAnimator() ?: ChartAnimator() )
    }

    private val treeDraw by lazy {
        TreeDraw(contentRect, renderPaint, chart.getChartAnimator() ?: ChartAnimator())
    }

    private val gapsTextPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = chart.maxMinValueTextSize.toFloat()
            color = chart.maxMinValueTextColor
        }
    }

    init {

        chart.addInternalViewportChangeListener { viewport ->
            currentViewport.set(viewport)
            calcDataSetMinMax()
        }

        val highlight = Highlight()
        chart.addOnTouchPointListener(object : OnTouchPointListener {
            override fun touch(x: Float, y: Float) {
                if (!chart.isHighlightEnable) return
                if (combineData == null) return

                val dataSet = combineData?.getTouchDataSet() ?: return

                val valueCount = dataSet.getEntryCount()
                var xPosition: Float
                val yPosition: Float
                highlight.touchX = x
                highlight.touchY = y

                val from = (currentViewport.left * valueCount).roundToInt()
                val to = (currentViewport.right * valueCount).roundToInt()
                var index: Int = getEntryIndex(x)
                if (index in from until to && index < dataSet.values.size) {
                    val value = dataSet.getEntryForIndex(index) ?: return

                    xPosition = value.x

                    if (xPosition == -1f && dataSet is LineDataSet) {
                        index = dataSet.values.take(index).indexOfLast {
                            !it.value.isNaN()
                        }
                        if(index < 0) return
                        val nextValue = dataSet.getEntryForIndex(index) ?: return
                        xPosition = nextValue.x
                    }

                    if (xPosition != -1f) {
                        yPosition = if (chart.isFollowFingerY) y else value.y
                        highlight.x = xPosition
                        highlight.y = yPosition
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

        val sortedDataSets = combineData?.allDataSet ?: return

        if (sortedDataSets.isEmpty()) return

        if (chartView.pointWidth == 0f) {
            chartView.setEntryWidth()
        }

        if (chartView.showMinLine()) {// 画K线缩放到最小的折线 (没有设置基准K线 还是画蜡烛不画折线)
            val basisCandlestickDataSet = combineData.candlestickChartData.dataSets.find { it.isBasis }
            if (basisCandlestickDataSet != null) {
                candlestickDraw.showScaleMin = true
                candlestickDraw.drawDataSet(canvas, combineData.candlestickChartData, basisCandlestickDataSet, currentViewport)
            }
        } else {
            for (i in sortedDataSets.indices) {
                val dataSet: AbstractDataSet<*> = sortedDataSets[i]
                if (dataSet is TreeDataSet) {
                    if (chartView.focusIndex == -1) chartView.focusIndex = dataSet.values.size - 1
                    treeDraw.setFocusIndex(chartView.focusIndex)
                    treeDraw.drawDataSet(canvas, combineData.treeChartData, dataSet, currentViewport)
                }

                // 蜡烛
                if (dataSet is CandlestickDataSet) {
                    if (dataSet.enableGap) {
                        candlestickDraw.textPaint = gapsTextPaint
                        candlestickDraw.decimalDigitsNumber = chartView.decimalDigitsNumber
                    }
                    candlestickDraw.showScaleMin = false
                    candlestickDraw.drawDataSet(canvas, combineData.candlestickChartData, dataSet, currentViewport)
                }

                // 线
                if (dataSet is LineDataSet) {
                    if (dataSet.isHorizontalLine) {
                        lineDraw.setHighLightState(chartView.highlightState != ChartConstant.HIGHLIGHT_STATUS_INITIAL)
                    }
                    if (dataSet.isDrawBand) {
                        lineDraw.setMaxVisibleCount(chartView.maxVisibleEntryCount)
                    }
                    lineDraw.drawDataSet(canvas, combineData.lineChartData, dataSet, currentViewport)

                }

                // 柱子
                if (dataSet is BarDataSet) {
                    barDraw.drawDataSet(canvas, combineData.barChartData, dataSet, currentViewport)
                }

                // 标签文本
                if (dataSet is ScatterTextDataSet) {
                    scatterTextDraw.drawDataSet(canvas, combineData.scatterTextChartData, dataSet, currentViewport)
                }
            }

            // 图标 画在上层
            scatterDraw.drawDataSet(canvas, combineData.scatterChartData, currentViewport)
        }

        // 画最大最小值
        if (chartView.isShowMaxMinValue) {
            val dataSet = combineData.getTouchDataSet() ?: return
            if (dataSet is CandlestickDataSet) {
                val max = combineData.leftMax
                val min = combineData.leftMin

                val maxIndex = dataSet.maxIndex
                val maxData = dataSet.values[maxIndex]
                val maxX = maxData.x
                val maxValue = maxData.high
                val maxY = (max - maxValue).toFloat() / (max - min) * contentRect.height()

                val minIndex = dataSet.minIndex
                val minData = dataSet.values[minIndex]
                val minX = dataSet.values[minIndex].x
                val minValue = minData.low
                val minY = (max - minValue).toFloat() / (max - min) * contentRect.height()

                maxMinArrowDraw.drawMaxMin(canvas, contentRect.width(), maxX, minX, maxY, minY, maxValue.toFloat(), minValue.toFloat(), chartView.decimalDigitsNumber)
            }
        }

        // 画现价线
        if (chartView.isShowLastPriceLine && chartView.highlightState == ChartConstant.HIGHLIGHT_STATUS_INITIAL) {
            val dataSet = combineData.getTouchDataSet() ?: return
            if (dataSet is CandlestickDataSet) {
                val max = combineData.leftMax
                val min = combineData.leftMin

                renderPaint.strokeWidth = chartView.highlightThickness.toFloat()
                val lastPrice = dataSet.values.toList().lastOrNull()?.close ?: return
                val yPosition: Float = (max - lastPrice.toFloat()) / (max - min) * contentRect.height()

                renderPaint.pathEffect = DashPathEffect(floatArrayOf(5f, 5f, 5f, 5f), 0f)
                renderPaint.color = chartView.lastPriceLineColor
                canvas.drawLine(0f, yPosition, contentRect.width().toFloat(), yPosition, renderPaint)

                renderPaint.pathEffect = null
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
        getChartData()?.clearAllChartData()
        calcDataSetMinMax()
//        chartView.invalidate()
    }

    override fun getChartData(): CombineData? {
        if (combineData == null) combineData = CombineData()
        return combineData
    }

}