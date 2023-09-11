package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet

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
        if (getChartData() == null) return -1
        val data = getChartData()

        val valueCount = data!!.getTouchEntryCount()

        var index: Int = (((x - contentRect.left) * currentViewport.width() / contentRect.width() + currentViewport.left) * valueCount.toFloat()).toInt()
        if (index >= valueCount) index = valueCount - 1
        if (index < 0) index = 0

        return index
    }

    open fun getEntryX(index: Int): Float {
        if (getChartData() == null) return -1f
        val data = getChartData()

        val valueCount = data!!.getTouchEntryCount()

        var x: Float = contentRect.left + (index / valueCount.toFloat() - currentViewport.left) / currentViewport.width() * contentRect.width()
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

    abstract fun getChartData(): ChartData<T>?

    abstract fun renderer(canvas: Canvas)
}
