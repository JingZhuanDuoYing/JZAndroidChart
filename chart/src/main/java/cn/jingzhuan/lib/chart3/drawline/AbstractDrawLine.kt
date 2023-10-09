package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue

/**
 * @since 2023-10-09
 * created by lei
 */
abstract class AbstractDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : IDrawLine {

    protected val chartView: AbstractChartView<T>

    protected val viewport: Viewport

    protected val contentRect: Rect

    protected val linePaint by lazy { Paint() }

    protected val bgPaint by lazy { Paint() }

    protected val textPaint by lazy { Paint() }

    protected var radiusIn = 0f

    protected var radiusOut = 0f

    private var baseValues: List<AbstractValue> = ArrayList()

    protected var viewportMax = 0f

    protected var viewportMin = 0f

    private var touchState = DrawLineState.none

    protected var pointStart: PointF? = null

    protected var pointEnd: PointF? = null

    init {
        this.chartView = chart
        this.viewport = chart.currentViewport
        this.contentRect = chart.contentRect

        radiusIn = chart.resources.getDimensionPixelSize(R.dimen.jz_draw_line_point_in).toFloat()
        radiusOut = chart.resources.getDimensionPixelSize(R.dimen.jz_draw_line_point_out).toFloat()
        initPaint()
    }

    private fun initPaint() {
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        bgPaint.isAntiAlias = true
        bgPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
    }

    private fun setPaint(dataSet: DrawLineDataSet) {
        linePaint.color = dataSet.lineColor
        linePaint.strokeWidth = dataSet.lineSize
        bgPaint.color = dataSet.lineColor
        textPaint.textSize = dataSet.fontSize.toFloat()
        textPaint.color = dataSet.lineColor
    }

    override fun onDraw(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        baseValues = baseDataSet.values
        viewportMax = lMax
        viewportMin = lMin
        setPaint(dataSet)
        drawTouchState(canvas)
    }

    override fun onTouch(state: DrawLineState, point: PointF, dataSet: DrawLineDataSet) {
        touchState = state
        when (state) {
            DrawLineState.none -> {
                pointStart = PointF()
                pointEnd = PointF()
            }

            DrawLineState.first -> {
                pointStart = point
            }
            DrawLineState.second -> {
                pointEnd = point
            }
            DrawLineState.drag -> {

            }
            DrawLineState.complete -> {
                if (pointStart != null && pointEnd != null) {
                    if (chartView.drawLineListener != null) {
                        chartView.drawLineListener?.onComplete(pointStart!!, pointEnd!!, dataSet.lineType, dataSet)
                    }
                }
            }
        }
    }

    open fun drawTouchState(canvas: Canvas) {
        linePaint.style = Paint.Style.FILL
        if (touchState == DrawLineState.first) {
            // 第一步 画起点
            drawStartPoint(canvas)
        } else if (touchState == DrawLineState.second) {
            // 第二步 画起点 、终点、 高亮背景、存入数据
            drawStartPoint(canvas)
            drawEndPoint(canvas)
            drawTypeShape(canvas)
        }
    }


    /**
     * 画起点
     */
    private fun drawStartPoint(canvas: Canvas) {
        if (pointStart == null) return
        val index = chartView.getEntryIndex(pointStart!!.x)
        val cx = baseValues[index].x
        pointStart?.x = cx
        bgPaint.alpha = 30
        canvas.drawCircle(cx, pointStart!!.y, radiusOut, bgPaint)
        canvas.drawCircle(cx, pointStart!!.y, radiusIn, linePaint)
    }

    /**
     * 画终点
     */
    private fun drawEndPoint(canvas: Canvas) {
        if (pointEnd == null) return
        val index = chartView.getEntryIndex(pointEnd!!.x)
        val cx = baseValues[index].x
        pointEnd?.x = cx
        bgPaint.alpha = 30
        canvas.drawCircle(cx, pointEnd!!.y, radiusOut, bgPaint)
        canvas.drawCircle(cx, pointEnd!!.y, radiusIn, linePaint)
    }

    protected open fun getScaleY(value: Float): Float {
        return if (viewportMax > viewportMin && viewportMax > 0) {
            (viewportMax - value) / (viewportMax - viewportMin) * contentRect.height()
        } else -1f
    }
}