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
    protected var viewport: Viewport

    protected var contentRect: Rect

    var renderPaint: Paint

    protected var labelTextPaint: Paint

    init {
        viewport = chart.currentViewport
        contentRect = chart.contentRect

        renderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        renderPaint.style = Paint.Style.STROKE

        labelTextPaint = Paint()
        labelTextPaint.isAntiAlias = true
    }

    /**
     * Computes the pixel offset for the given X lib value. This may be outside the view bounds.
     */
    protected fun getDrawX(x: Float): Float {
        return contentRect.left + contentRect.width() * (x - viewport.left) / viewport.width()
    }

    /**
     * Computes the pixel offset for the given Y lib value. This may be outside the view bounds.
     */
    protected fun getDrawY(y: Float): Float {
        return contentRect.bottom - contentRect.height() * (y - viewport.top) / viewport.height()
    }

    open fun setTypeface(tf: Typeface?) {
        labelTextPaint.typeface = tf
    }

    open fun addDataSet(dataSet: T?) {
        chartData.add(dataSet)
    }

    /**
     * 清掉chartData
     */
    open fun clearDataSet(){
        chartData.clear()
    }

    /**
     * 重新计算viewport
     */
    protected open fun calcDataSetMinMax() {
        chartData.calcMaxMin(viewport, contentRect)
    }

    open val chartData: ChartData<T>
        get() = ChartData()

    abstract fun renderer(canvas: Canvas)
}
