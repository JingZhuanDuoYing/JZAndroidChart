package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.max

/**
 * @since 2023-10-09
 * created by lei
 */
abstract class AbstractDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : IDrawLine {

    protected val chartView: AbstractChartView<T>

    protected val linePaint by lazy { Paint() }

    private val bgPaint by lazy { Paint() }

    protected val textPaint by lazy { Paint() }


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
        setPaint(dataSet)
        if (!dataSet.isSelect) return
        linePaint.style = Paint.Style.FILL
        when (dataSet.lineState) {
            DrawLineState.prepare -> {
                Log.d("onPressDrawLine", "准备阶段")
            }
            DrawLineState.first -> {
                // 第一步 画起点
                drawStartPoint(canvas, dataSet, baseDataSet, lMax, lMin)
            }
            DrawLineState.second -> {
                // 第二步 画起点 、终点、高亮背景
                drawStartPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                drawEndPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                drawTypeShape(canvas, dataSet, baseDataSet, lMax, lMin)
            }
            DrawLineState.complete -> {
                // 画起点 、终点、平行点(如果有) 高亮背景、存入数据
                drawStartPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                drawEndPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                drawThirdPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                drawTypeShape(canvas, dataSet, baseDataSet, lMax, lMin)
            }
        }
    }


    /**
     * 画起点
     */
    private fun drawStartPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val value = dataSet.startDrawValue ?: return

        val x = getEntryX(value.dataIndex, baseDataSet) ?: return
        val y = chartView.getScaleY(value.value, lMax, lMin)
        Log.d("onPressDrawLine", "画起点, dataIndex=${value.dataIndex}, x=$x, y=$y")
        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointOuterR, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointInnerR, linePaint)
    }

    /**
     * 画终点
     */
    private fun drawEndPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val value = dataSet.endDrawValue ?: return

        val x = getEntryX(value.dataIndex, baseDataSet) ?: return
        val y = chartView.getScaleY(value.value, lMax, lMin)

        Log.d("onPressDrawLine", "画终点, dataIndex=${value.dataIndex}, x=$x, y=$y")
        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointOuterR, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointInnerR, linePaint)
    }

    /**
     * 画平行点
     */
    private fun drawThirdPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val value = dataSet.thirdDrawValue ?: return

        val x = getEntryX(value.dataIndex, baseDataSet) ?: return
        val y = chartView.getScaleY(value.value, lMax, lMin)

        Log.d("onPressDrawLine", "画平行点, dataIndex=${value.dataIndex}, x=$x, y=$y")
        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointOuterR, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointInnerR, linePaint)
    }

    fun getEntryX(index: Int, baseDataSet: AbstractDataSet<*>): Float? {

        val contentRect = chartView.contentRect
        val currentViewport = chartView.currentViewport

        if (baseDataSet.values.isEmpty()) return null

        val valueCount = baseDataSet.values.size

        val scale = 1.0f / currentViewport.width()

        val visibleRange = baseDataSet.getVisibleRange(currentViewport)
        val pointWidth = contentRect.width() / max(visibleRange, baseDataSet.minValueCount.toFloat())

        val step = contentRect.width() * scale / valueCount
        val startX = contentRect.left - currentViewport.left * contentRect.width() * scale

        val x = startX + step * index + pointWidth * 0.5f

        Log.d("onPressDrawLine", "valueCount=$valueCount, step=$step, pointX= $x, index=$index, pointWidth=$pointWidth,")

        return x
    }
}