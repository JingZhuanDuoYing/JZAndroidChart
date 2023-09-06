package cn.jingzhuan.lib.chart3.data

import android.graphics.Rect
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.TreeDataSet
import java.util.Collections
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-06
 */
class CombineData : ChartData<AbstractDataSet<*>>() {

    val treeChartData: TreeData = TreeData()

    val barChartData: BarData = BarData()

    val lineChartData: LineData = LineData()

    val candlestickChartData: CandlestickData = CandlestickData()

    val scatterChartData: ScatterData = ScatterData()

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

    fun getTreeDataSets(): List<TreeDataSet> {
        return treeChartData.dataSets
    }

    fun addDataSet(dataSet: BarDataSet): Boolean {
        return barChartData.add(dataSet)
    }

    fun addDataSet(dataSet: LineDataSet): Boolean {
        return lineChartData.add(dataSet)
    }

    fun addDataSet(dataSet: CandlestickDataSet): Boolean {
        return candlestickChartData.add(dataSet)
    }

    fun addDataSet(dataSet: ScatterDataSet): Boolean {
        return scatterChartData.add(dataSet)
    }

    fun addDataSet(dataSet: TreeDataSet): Boolean {
        return treeChartData.add(dataSet)
    }

    override fun calcMaxMin(viewport: Viewport, content: Rect) {
        leftMin = Float.MAX_VALUE
        leftMax = -Float.MAX_VALUE
        rightMin = Float.MAX_VALUE
        rightMax = -Float.MAX_VALUE

        if (treeChartData.dataSets.isNotEmpty()) {
            treeChartData.calcMaxMin(viewport, content)
            leftMin = min(treeChartData.leftMin, leftMin)
            leftMax = max(treeChartData.leftMax, leftMax)
            rightMin = min(treeChartData.rightMin, rightMin)
            rightMax = max(treeChartData.rightMax, rightMax)
        }

        if (candlestickChartData.dataSets.isNotEmpty()) {
            candlestickChartData.calcMaxMin(viewport, content)
            leftMin = min(candlestickChartData.leftMin, leftMin)
            leftMax = max(candlestickChartData.leftMax, leftMax)
            rightMin = min(candlestickChartData.rightMin, rightMin)
            rightMax = max(candlestickChartData.rightMax, rightMax)
        }

        if (lineChartData.dataSets.isNotEmpty()) {
            lineChartData.calcMaxMin(viewport, content)
            leftMin = min(lineChartData.leftMin, leftMin)
            leftMax = max(lineChartData.leftMax, leftMax)
            rightMin = min(lineChartData.rightMin, rightMin)
            rightMax = max(lineChartData.rightMax, rightMax)
        }

        if (barChartData.dataSets.isNotEmpty()) {
            barChartData.calcMaxMin(viewport, content)
            leftMin = min(barChartData.leftMin, leftMin)
            leftMax = max(barChartData.leftMax, leftMax)
            rightMin = min(barChartData.rightMin, rightMin)
            rightMax = max(barChartData.rightMax, rightMax)
        }

        if (scatterChartData.dataSets.isNotEmpty()) {
            scatterChartData.calcMaxMin(viewport, content, leftMax, leftMin, rightMax, rightMin)
            leftMin = min(scatterChartData.leftMin, leftMin)
            leftMax = max(scatterChartData.leftMax, leftMax)
            rightMin = min(scatterChartData.rightMin, rightMin)
            rightMax = max(scatterChartData.rightMax, rightMax)
        }

        treeChartData.leftMax = leftMax
        barChartData.leftMax = leftMax
        lineChartData.leftMax = leftMax
        candlestickChartData.leftMax = leftMax
        scatterChartData.leftMax = leftMax

        treeChartData.leftMin = leftMin
        barChartData.leftMin = leftMin
        lineChartData.leftMin = leftMin
        candlestickChartData.leftMin = leftMin
        scatterChartData.leftMin = leftMin

        treeChartData.rightMax = rightMax
        barChartData.rightMax = rightMax
        lineChartData.rightMax = rightMax
        candlestickChartData.rightMax = rightMax
        scatterChartData.rightMax = rightMax

        treeChartData.rightMin = rightMin
        barChartData.rightMin = rightMin
        lineChartData.rightMin = rightMin
        candlestickChartData.rightMin = rightMin
        scatterChartData.rightMin = rightMin

        setMinMax()
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
        return super.add(e)
    }

    override fun setMinMax() {
        if (leftAxis != null) {
            leftAxis?.yMin = leftMin
            leftAxis?.yMax = leftMax
        }
        if (rightAxis != null) {
            rightAxis?.yMin = rightMin
            rightAxis?.yMax = rightMax
        }
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
            allDataSet.sortWith { dataSet1, dataSet2 -> dataSet1.drawIndex - dataSet2.drawIndex }
            return allDataSet
        }
}
