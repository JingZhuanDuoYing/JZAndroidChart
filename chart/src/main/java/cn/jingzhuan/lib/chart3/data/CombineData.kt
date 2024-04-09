package cn.jingzhuan.lib.chart3.data

import android.graphics.Rect
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterTextDataSet
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_TAG_NAME
import java.util.Collections

/**
 * @since 2023-09-06
 */
open class CombineData : ChartData<AbstractDataSet<*>>() {

    val treeChartData: TreeData = TreeData()

    val barChartData: BarData = BarData()

    val lineChartData: LineData = LineData()

    val candlestickChartData: CandlestickData = CandlestickData()

    val scatterChartData: ScatterData = ScatterData()

    val scatterTextChartData: ScatterTextData = ScatterTextData()

    val drawLineChartData: DrawLineData = DrawLineData()

    fun getBarDataSets(): List<BarDataSet> {
        return barChartData.dataSets
    }

    fun getLineDataSets(): List<LineDataSet> {
        return lineChartData.dataSets
    }

    fun getCandlestickDataSets(): List<CandlestickDataSet> {
        return candlestickChartData.dataSets
    }

    fun getScatterDataSets(): List<ScatterDataSet> {
        return scatterChartData.dataSets
    }

    fun getScatterTextDataSets(): List<ScatterTextDataSet> {
        return scatterTextChartData.dataSets
    }

    fun getTreeDataSets(): List<TreeDataSet> {
        return treeChartData.dataSets
    }

    fun getDrawLineDataSets(): List<DrawLineDataSet> {
        return drawLineChartData.dataSets
    }

    open fun addDataSet(dataSet: BarDataSet): Boolean {
        return barChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: LineDataSet): Boolean {
        return lineChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: CandlestickDataSet): Boolean {
        return candlestickChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: ScatterDataSet): Boolean {
        return scatterChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: ScatterTextDataSet): Boolean {
        return scatterTextChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: TreeDataSet): Boolean {
        return treeChartData.add(dataSet)
    }

    open fun addDataSet(dataSet: DrawLineDataSet): Boolean {
        return drawLineChartData.add(dataSet)
    }

    fun clearAllChartData(){
        treeChartData.clear()
        barChartData.clear()
        lineChartData.clear()
        candlestickChartData.clear()
        scatterTextChartData.clear()
        scatterChartData.clear()
        drawLineChartData.clear()
    }

    private fun getCombineDataSets(): List<AbstractDataSet<*>> {
        return if (getCandlestickDataSets().isNotEmpty()) {
            getCandlestickDataSets()
        } else if (getLineDataSets().isNotEmpty()) {
            getLineDataSets()
        } else if (getBarDataSets().isNotEmpty()) {
            getBarDataSets()
        } else if (getTreeDataSets().isNotEmpty()) {
            getTreeDataSets()
        } else if (getScatterDataSets().isNotEmpty()) {
            getScatterDataSets()
        } else {
            emptyList()
        }
    }

    override fun calcMaxMin(viewport: Viewport, content: Rect, offsetPercent: Float) {
        if (content.width() == 0 && content.height() == 0) return
        super.calcMaxMin(viewport, content, offsetPercent)
        treeChartData.leftMax = leftMax
        barChartData.leftMax = leftMax
        lineChartData.leftMax = leftMax
        candlestickChartData.leftMax = leftMax
        scatterChartData.leftMax = leftMax
        scatterTextChartData.leftMax = leftMax

        treeChartData.leftMin = leftMin
        barChartData.leftMin = leftMin
        lineChartData.leftMin = leftMin
        candlestickChartData.leftMin = leftMin
        scatterChartData.leftMin = leftMin
        scatterTextChartData.leftMin = leftMin

        treeChartData.rightMax = rightMax
        barChartData.rightMax = rightMax
        lineChartData.rightMax = rightMax
        candlestickChartData.rightMax = rightMax
        scatterChartData.rightMax = rightMax
        scatterTextChartData.rightMax = rightMax

        treeChartData.rightMin = rightMin
        barChartData.rightMin = rightMin
        lineChartData.rightMin = rightMin
        candlestickChartData.rightMin = rightMin
        scatterChartData.rightMin = rightMin
        scatterTextChartData.rightMin = rightMin
    }

    override fun add(e: AbstractDataSet<*>?): Boolean {
        super.add(e)
        if (e is TreeDataSet) {
            return addDataSet(e)
        }
        if (e is CandlestickDataSet) {
            return addDataSet(e)
        }
        if (e is LineDataSet) {
            return addDataSet(e)
        }
        if (e is BarDataSet) {
            return addDataSet(e)
        }
        if (e is ScatterDataSet) {
            return addDataSet(e)
        }
        if (e is ScatterTextDataSet) {
            return addDataSet(e)
        }
        if (e is DrawLineDataSet) {
            return addDataSet(e)
        }
        return super.add(e)
    }

    override fun getTouchDataSet(): AbstractDataSet<*>? {
        val dataSet = getCombineDataSets().findLast { it.isBasis }
        if (dataSet != null) return dataSet
        return getCombineDataSets().firstOrNull()
    }

    override fun getTouchEntryCount(): Int {
        return getTouchDataSet()?.values?.size ?: 0
    }

    /**
     * 目前标签 只使用ScatterDataSet图标
     */
    override fun getFlagDataSet(): AbstractDataSet<*>? {
        return getScatterDataSets().find { it.tag == FLAG_TAG_NAME }
    }

    fun addAll(dataSets: List<AbstractDataSet<*>>) {
        for (dataSet in dataSets) {
            add(dataSet)
        }
    }

    val allDataSet: List<AbstractDataSet<*>>
        get() {
            val allDataSet = Collections.synchronizedList(ArrayList<AbstractDataSet<*>>())
            // 按分类顺序添加，当drawIndex是默认值-1时，按下列顺序绘制
            allDataSet.addAll(treeChartData.dataSets)
            allDataSet.addAll(barChartData.dataSets)
            allDataSet.addAll(candlestickChartData.dataSets)
            allDataSet.addAll(lineChartData.dataSets)
            allDataSet.addAll(scatterChartData.dataSets)
            allDataSet.addAll(scatterTextChartData.dataSets)
            allDataSet.addAll(drawLineChartData.dataSets)
            allDataSet.sortWith { dataSet1, dataSet2 -> dataSet1.drawIndex - dataSet2.drawIndex }
            return allDataSet
        }
}
