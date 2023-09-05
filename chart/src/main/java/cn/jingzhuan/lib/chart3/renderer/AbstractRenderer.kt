package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue

/**
 * @since 2023-09-05
 * created by lei
 */
abstract class AbstractRenderer<V : AbstractValue, T : AbstractDataSet<V>>(chart: AbstractChartView<V, T>) {
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

    fun setTypeface(tf: Typeface?) {
        labelTextPaint.typeface = tf
    }

    val chartData: ChartData<V, T>
        get() = ChartData()

    abstract fun renderer(canvas: Canvas)
}
