package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
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

    protected val linePaint by lazy { Paint() }

    protected val bgPaint by lazy { Paint() }

    protected val textPaint by lazy { Paint() }

    private var baseValues: List<AbstractValue> = ArrayList()

    protected var viewportMax = 0f

    protected var viewportMin = 0f


    init {
        this.chartView = chart
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
        linePaint.style = Paint.Style.FILL
        when (dataSet.lineState) {
            DrawLineState.none -> {
                Log.d("onPressDrawLine", "drawTouchState: none")
//                drawTypeShape(canvas)
            }
            DrawLineState.first -> {
                Log.d("onPressDrawLine", "drawTouchState: first")
                // 第一步 画起点
                drawStartPoint(canvas, dataSet, baseDataSet)
            }
            DrawLineState.second -> {
                Log.d("onPressDrawLine", "drawTouchState: second")
                // 第二步 画起点 、终点、 高亮背景、存入数据
                drawStartPoint(canvas, dataSet, baseDataSet)
                drawEndPoint(canvas, dataSet, baseDataSet)
                drawTypeShape(canvas, dataSet, baseDataSet)
            }
            DrawLineState.complete -> {
                Log.d("onPressDrawLine", "drawTouchState: complete")
//                drawStartPoint(canvas)
//                drawEndPoint(canvas)
//                drawTypeShape(canvas)
            }
            DrawLineState.drag -> {
                Log.d("onPressDrawLine", "drawTouchState: drag")
            }
        }
    }


    /**
     * 画起点
     */
    private fun drawStartPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>) {
        val value = dataSet.startDrawValue ?: return

        val x = baseDataSet.values.find { it.time == value.time }?.x ?: return
        val y = chartView.getScaleY(value.value, viewportMax, viewportMin)

        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointRadiusIn, linePaint)
    }

    /**
     * 画终点
     */
    private fun drawEndPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>) {
        val value = dataSet.endDrawValue ?: return

        val x = baseDataSet.values.find { it.time == value.time }?.x ?: return
        val y = chartView.getScaleY(value.value, viewportMax, viewportMin)

        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointRadiusIn, linePaint)
    }

    protected open fun getScaleY(value: Float): Float {
        return if (viewportMax > viewportMin && viewportMax > 0) {
            (viewportMax - value) / (viewportMax - viewportMin) * chartView.contentRect.height()
        } else -1f
    }
}