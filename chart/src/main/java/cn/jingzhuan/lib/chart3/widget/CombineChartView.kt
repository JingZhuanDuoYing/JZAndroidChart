package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.Viewport
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
            val lastCount = this.totalEntryCount
            val viewport = currentViewport
            val from = viewport.left * lastCount + (dataSize - lastCount)
            val to = viewport.right * lastCount + (dataSize - lastCount)
            viewport.left = from / dataSize
            viewport.right = to / dataSize

            this.totalEntryCount = dataSize

            handleLoadMoreIndex(dataSize - lastCount)

            setDataSets(combineData)

            setLoadMoreViewport(viewport)
        }
    }

    fun setCombineData(combineData: CombineData) {
        setDataSets(combineData)

        this.totalEntryCount = combineData.getTouchEntryCount()

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
        } else {
            postInvalidate()
        }
    }

    private fun setDataSets(combineData: CombineData) {
        cleanAllDataSet()

        for (treeDataSet in combineData.getTreeDataSets()) {
            addDataSet(treeDataSet)
        }

        for (lineDataSet in combineData.getLineDataSets()) {
            addDataSet(lineDataSet)
        }

        for (barDataSet in combineData.getBarDataSets()) {
            addDataSet(barDataSet)
        }

        for (candlestickDataSet in combineData.getCandlestickDataSets()) {
            addDataSet(candlestickDataSet)
        }

        for (scatterDataSet in combineData.getScatterDataSets()) {
            addDataSet(scatterDataSet)
        }

        for (scatterTextDataSet in combineData.getScatterTextDataSets()) {
            addDataSet(scatterTextDataSet)
        }

        for (drawLineDataSet in combineData.getDrawLineDataSets()) {
            addDataSet(drawLineDataSet)
        }
    }

    open fun addDataSet(abstractDataSet: AbstractDataSet<*>) {
        getRenderer()?.addDataSet(abstractDataSet)
    }

    fun cleanAllDataSet() {
        getRenderer()?.clearDataSet()
    }

    fun cleanOff() {
        cleanAllDataSet()
        currentViewport = Viewport()
        isLoadMore = false
        totalEntryCount = 0
        pointWidth = 0f
    }

    private fun getRenderer(): CombineChartRenderer? {
        return chartRenderer as CombineChartRenderer?
    }


}