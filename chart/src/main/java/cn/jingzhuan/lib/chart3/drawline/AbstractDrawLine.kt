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
        drawTouchState(canvas, dataSet)
    }

//    override fun onTouch(state: DrawLineState, point: PointF, dataSet: DrawLineDataSet) {
//        touchState = state
//        when (state) {
//            DrawLineState.none -> {
//                pointStart = PointF()
//                pointEnd = PointF()
//            }
//
//            DrawLineState.first -> {
//                pointStart = point
//            }
//            DrawLineState.second -> {
//                pointEnd = point
//            }
//            DrawLineState.complete -> {
//                if (pointStart != null && pointEnd != null) {
//                    if (chartView.drawLineListener != null) {
//                        chartView.drawLineListener?.onComplete(pointStart!!, pointEnd!!, dataSet.lineType, dataSet)
//                    }
//                }
//            }
//            DrawLineState.drag -> {
//
//            }
//        }
//    }

    open fun drawTouchState(canvas: Canvas, dataSet: DrawLineDataSet) {
        linePaint.style = Paint.Style.FILL
        when (dataSet.lineState) {
            DrawLineState.none -> {
                Log.d("onPressDrawLine", "drawTouchState: none")
//                drawTypeShape(canvas)
            }
            DrawLineState.first -> {
                Log.d("onPressDrawLine", "drawTouchState: first")
                // 第一步 画起点
                drawStartPoint(canvas, dataSet)
            }
            DrawLineState.second -> {
                Log.d("onPressDrawLine", "drawTouchState: second")
                // 第二步 画起点 、终点、 高亮背景、存入数据
                drawStartPoint(canvas, dataSet)
                drawEndPoint(canvas, dataSet)
                drawTypeShape(canvas, dataSet)
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
    private fun drawStartPoint(canvas: Canvas, dataSet: DrawLineDataSet) {
        if (dataSet.pointStart == null) return
        val index = chartView.getEntryIndex(dataSet.pointStart!!.x)
        val cx = baseValues[index].x
        dataSet.pointStart?.x = cx
        bgPaint.alpha = 30
        canvas.drawCircle(cx, dataSet.pointStart!!.y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(cx, dataSet.pointStart!!.y, dataSet.pointRadiusIn, linePaint)
    }

    /**
     * 画终点
     */
    private fun drawEndPoint(canvas: Canvas, dataSet: DrawLineDataSet) {
        if (dataSet.pointEnd == null) return
        val index = chartView.getEntryIndex(dataSet.pointEnd!!.x)
        val cx = baseValues[index].x
        dataSet.pointEnd?.x = cx
        bgPaint.alpha = 30
        canvas.drawCircle(cx, dataSet.pointEnd!!.y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(cx, dataSet.pointEnd!!.y, dataSet.pointRadiusIn, linePaint)
    }

    protected open fun getScaleY(value: Float): Float {
        return if (viewportMax > viewportMin && viewportMax > 0) {
            (viewportMax - value) / (viewportMax - viewportMin) * chartView.contentRect.height()
        } else -1f
    }
}