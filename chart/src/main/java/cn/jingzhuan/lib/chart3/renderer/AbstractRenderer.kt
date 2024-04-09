package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import kotlin.math.max

/**
 * @since 2023-09-05
 * @author lei
 */
abstract class AbstractRenderer<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) {
    protected val chartView: AbstractChartView<T>

    protected var currentViewport: Viewport

    protected var contentRect: Rect

    var renderPaint: Paint

    protected var labelTextPaint: Paint

    init {
        this.chartView = chart

        currentViewport = chart.currentViewport
        contentRect = chart.contentRect

        renderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        renderPaint.style = Paint.Style.STROKE

        labelTextPaint = Paint()
        labelTextPaint.isAntiAlias = true

        this.getChartData()?.setChart(chart)
    }

    open fun getEntryIndex(x: Float): Int {
        val dataSet = getChartData()?.getTouchDataSet() ?: return 0

        val valueCount = max(chartView.totalEntryCount, dataSet.forceValueCount)

        var index: Int = (((x - contentRect.left) * currentViewport.width() / contentRect.width() + currentViewport.left) * valueCount.toFloat()).toInt()
        if (index >= dataSet.values.size) index = dataSet.values.size - 1
        if (index < 0) index = 0

        return index
    }

    open fun getEntryX(index: Int): Float {
        val data = getChartData() ?: return -1f

        val dataSet = data.getTouchDataSet() ?: return -1f

        val valueCount = data.getTouchEntryCount()

        val scale = 1.0f / currentViewport.width()

        val visibleRange = dataSet.getVisibleRange(currentViewport)
        val pointWidth = contentRect.width() / max(visibleRange, dataSet.minValueCount.toFloat())

        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - currentViewport.left * contentRect.width() * scale

        var x = startX + step * index + pointWidth * 0.5f

        if (x > contentRect.right) x = contentRect.right.toFloat()
        if (x < contentRect.left) x = contentRect.left.toFloat()

        return x
    }

    /**
     * Computes the pixel offset for the given X lib value. This may be outside the view bounds.
     */
    protected fun getDrawX(x: Float): Float {
        return contentRect.left + contentRect.width() * (x - currentViewport.left) / currentViewport.width()
    }

    /**
     * Computes the pixel offset for the given Y lib value. This may be outside the view bounds.
     */
    protected fun getDrawY(y: Float): Float {
        return contentRect.bottom - contentRect.height() * (y - currentViewport.top) / currentViewport.height()
    }

    open fun setTypeface(tf: Typeface?) {
        labelTextPaint.typeface = tf
    }

    open fun addDataSet(dataSet: T?) {
        getChartData()?.add(dataSet)
    }

    /**
     * 清掉chartData
     */
    open fun clearDataSet(){
        getChartData()?.clear()
    }

    /**
     * 重新计算viewport
     */
    open fun calcDataSetMinMax() {
        getChartData()?.calcMaxMin(currentViewport, contentRect, chartView.offsetPercent)
    }

    open fun getChartData(): ChartData<T>? {
        return null
    }

    abstract fun renderer(canvas: Canvas)
}
