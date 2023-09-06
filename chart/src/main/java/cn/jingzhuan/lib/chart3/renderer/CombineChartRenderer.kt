package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet
import cn.jingzhuan.lib.chart3.draw.CandlestickDraw

/**
 * @since 2023-09-06
 */
class CombineChartRenderer(chart: AbstractChartView<AbstractDataSet<*>>) : AbstractRenderer<AbstractDataSet<*>>(chart)  {

    private val combineData: CombineData? = null

    private var candlestickDraw = CandlestickDraw()

    /**
     * 开始绘制
     */
    override fun renderer(canvas: Canvas) {
        val combineData = chartData

        val sortedDataSets = combineData.allDataSet

        for (i in sortedDataSets.indices) {
            val dataSet: AbstractDataSet<*> = sortedDataSets[i]
            if (dataSet is TreeDataSet) {

            }
            if (dataSet is CandlestickDataSet) {
                candlestickDraw.drawDataSet(canvas, combineData.candlestickChartData, dataSet)
            }
            if (dataSet is LineDataSet) {

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

    override val chartData: CombineData
        get() = combineData ?: CombineData()

}