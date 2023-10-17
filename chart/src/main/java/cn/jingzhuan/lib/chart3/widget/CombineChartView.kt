package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.base.BaseChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.renderer.CombineChartRenderer
import kotlin.math.max

/**
 * @since 2023-09-06
 * 综合ChartView
 */
open class CombineChartView : BaseChartView<AbstractDataSet<*>> {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initChart() {
        super.initChart()
        chartRenderer = CombineChartRenderer(this)
    }

    fun setCombineDataByLoadMore(combineData: CombineData) {
        val dataSize = combineData.getTouchEntryCount()
        if (dataSize > this.totalEntryCount) {
            isLoadMore = true
            val lastCount = this.totalEntryCount
            val viewport = currentViewport
            val from = viewport.left * lastCount + (dataSize - lastCount)
            val to = viewport.right * lastCount + (dataSize - lastCount)
            viewport.left = from / dataSize
            viewport.right = to / dataSize

            handleLoadMoreIndex(dataSize - lastCount)

            setCurrentViewport(viewport)

            setCombineData(combineData)
            isLoadMore = false
        }
    }

    fun setCombineData(combineData: CombineData) {
        var totalEntryCount = 0

        cleanAllDataSet()

        for (treeDataSet in combineData.getTreeDataSets()) {
            addDataSet(treeDataSet)
            totalEntryCount = max(totalEntryCount, treeDataSet.values.size)
        }

        for (lineDataSet in combineData.getLineDataSets()) {
            addDataSet(lineDataSet)
            totalEntryCount = max(totalEntryCount, lineDataSet.values.size)
        }

        for (barDataSet in combineData.getBarDataSets()) {
            addDataSet(barDataSet)
            totalEntryCount = max(totalEntryCount, barDataSet.values.size)
        }

        for (candlestickDataSet in combineData.getCandlestickDataSets()) {
            addDataSet(candlestickDataSet)
            totalEntryCount = max(totalEntryCount, candlestickDataSet.values.size)
        }

        for (scatterDataSet in combineData.getScatterDataSets()) {
            addDataSet(scatterDataSet)
            totalEntryCount = max(totalEntryCount, scatterDataSet.values.size)
        }

        for (scatterTextDataSet in combineData.getScatterTextDataSets()) {
            addDataSet(scatterTextDataSet)
            totalEntryCount = max(totalEntryCount, scatterTextDataSet.values.size)
        }

        for (drawLineDataSet in combineData.getDrawLineDataSets()) {
            addDataSet(drawLineDataSet)
        }

        this.totalEntryCount = totalEntryCount

        if (!currentViewport.initialized() && totalEntryCount > 0) {
            // 移动到最新的K线
            val newViewport = currentViewport.moveToEnd()

            val visibleCount = currentVisibleEntryCount

            if (visibleCount > 0) {
                val viewportWidth = visibleCount.toFloat() / totalEntryCount
                if (totalEntryCount >= visibleCount) {
                    newViewport.left = newViewport.right - viewportWidth
                } else {
                    newViewport.left = 0f
                    newViewport.right = viewportWidth
                }
            }

            setCurrentViewport(newViewport)
        }
    }

    open fun addDataSet(abstractDataSet: AbstractDataSet<*>) {
        getRenderer()?.addDataSet(abstractDataSet)
    }

    fun cleanAllDataSet() {
        getRenderer()?.clearDataSet()
    }

    private fun getRenderer(): CombineChartRenderer? {
        return chartRenderer as CombineChartRenderer?
    }


}