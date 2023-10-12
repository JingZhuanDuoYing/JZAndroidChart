package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet

/**
 * @since 2023-10-09
 * created by lei
 */
abstract class AbstractDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : IDrawLine {

    protected val chartView: AbstractChartView<T>

    protected val linePaint by lazy { Paint() }

    protected val bgPaint by lazy { Paint() }

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
        linePaint.style = Paint.Style.FILL
        when (dataSet.lineState) {
            DrawLineState.prepare -> {
                Log.d("onPressDrawLine", "准备阶段")
            }
            DrawLineState.first -> {
                Log.d("onPressDrawLine", "第一步，画起点")
                // 第一步 画起点
                drawStartPoint(canvas, dataSet, baseDataSet, lMax, lMin)
            }
            DrawLineState.complete -> {
                // 第二步 画起点 、终点、 高亮背景、存入数据
                if (dataSet.isSelect) {
                    Log.d("onPressDrawLine", "第二步 选中->画起点 、终点、 高亮背景、存入数据")
                    drawStartPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                    drawEndPoint(canvas, dataSet, baseDataSet, lMax, lMin)
                    drawTypeShape(canvas, dataSet, baseDataSet, lMax, lMin)
                }
            }
        }
    }


    /**
     * 画起点
     */
    private fun drawStartPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val value = dataSet.startDrawValue ?: return

        val x = baseDataSet.values.find { it.time == value.time }?.x ?: return
        val y = chartView.getScaleY(value.value, lMax, lMin)

        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointRadiusIn, linePaint)

//        if (dragState == ChartConstant.DRAW_LINE_DRAG_LEFT) {
//            Log.d("onPressDrawLine", "renderer: ")
//            canvas.save()
//            canvas.clipRect(x - 100f, y - 100f, x + 100f, y +100f)
//            canvas.drawColor(Color.RED)
//            canvas.restore()
//        }
    }

    /**
     * 画终点
     */
    private fun drawEndPoint(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float) {
        val value = dataSet.endDrawValue ?: return

        val x = baseDataSet.values.find { it.time == value.time }?.x ?: return
        val y = chartView.getScaleY(value.value, lMax, lMin)

        bgPaint.alpha = 30
        canvas.drawCircle(x, y, dataSet.pointRadiusOut, bgPaint)
        canvas.drawCircle(x, y, dataSet.pointRadiusIn, linePaint)
    }
}