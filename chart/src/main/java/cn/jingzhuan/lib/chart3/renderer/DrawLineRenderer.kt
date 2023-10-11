package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.collection.ArrayMap
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.AbstractDrawLine
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import cn.jingzhuan.lib.chart3.drawline.SegmentDrawLine
import cn.jingzhuan.lib.chart3.event.OnDrawLineListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.absoluteValue

/**
 * @since 2023-10-09
 * created by lei
 * 画线工具
 */
class DrawLineRenderer<T : AbstractDataSet<*>>(
    private val chart: AbstractChartView<T>,
) : AbstractRenderer<T>(chart) {

    private val drawMap = ArrayMap<Int, AbstractDrawLine<T>>()

    private var dragState = ChartConstant.DRAW_LINE_NONE

    private var preDrawLine = DrawLineDataSet()

    init {
        initDraw(chart)
        initListener(chart)
    }

    private fun initListener(chart: AbstractChartView<T>) {
//        chart.setOnDrawLineListener(object : OnDrawLineListener{
//            override fun onTouch(state: DrawLineState, point: PointF, type: Int) {
//                val dataSets = (chart.chartData as CombineData).getDrawLineDataSets()
//                for (dataSet in dataSets) {
//                    val drawLineType = dataSet.lineType
//                    val draw = drawMap[drawLineType]
//                    if (drawLineType == type) {
//                        draw?.onTouch(state, point, dataSet)
//                    } else {
//                        draw?.onTouch(DrawLineState.none, point, dataSet)
//                    }
//                }
//                chart.postInvalidate()
//            }
//
//            override fun onComplete(point1: PointF, point2: PointF, type: Int, dataSet: DrawLineDataSet) {
//                val chartData = (chart.chartData as CombineData)
//
//                val baseValues = chartData.getTouchDataSet()?.values ?: return
//
//                val viewportMax = chartData.leftMax
//                val viewportMin = chartData.leftMin
//
//                val startIndex = chartView.getEntryIndex(point1.x)
//                val startTime = baseValues.getOrNull(startIndex)?.time ?: 0L
//                val startValue =  viewportMax - point1.y / contentRect.height() * (viewportMax - viewportMin)
//                dataSet.values.add(DrawLineValue(startValue, startTime))
//
//                val endIndex = chartView.getEntryIndex(point2.x)
//                val endTime = baseValues.getOrNull(endIndex)?.time ?: 0L
//                val endValue =  viewportMax - point2.y / contentRect.height() * (viewportMax - viewportMin)
//                dataSet.values.add(DrawLineValue(endValue, endTime))
//
//                chart.postInvalidate()
//            }
//
//        })

    }

    private fun initDraw(chart: AbstractChartView<T>) {
        drawMap[DrawLineType.ltSegment.ordinal] = SegmentDrawLine(chart)
    }

    override fun renderer(canvas: Canvas) {
        val chartData = (chart.chartData as CombineData)
        val drawLineChartData = chartData.drawLineChartData
        val baseDataSet = chartData.getTouchDataSet()
        if (baseDataSet != null) {
            Log.d("onPressDrawLine", "renderer: ${drawLineChartData.dataSets.size}")
            for (dataSet in drawLineChartData.dataSets) {
                if (dataSet.isVisible) {
                    drawDataSet(canvas, dataSet, baseDataSet, chartData.leftMax, chartData.leftMin)
                }
            }
        }
    }

    private fun drawDataSet(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        leftMax: Float,
        leftMin: Float,
    ) {
        val type = dataSet.lineType
        val draw = drawMap[type]
        draw?.onDraw(canvas, dataSet, baseDataSet, leftMax, leftMin)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onPressDrawLine(event)
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("onPressDrawLine", "onTouchEvent: ACTION_MOVE")
                val x = event.x
                val y = event.y
                if (dragState == ChartConstant.DRAW_LINE_DRAG_LEFT) {
                    // 移动起点
                    if (preDrawLine.startDrawValue != null) {
                        val chartData = (chart.chartData as CombineData)
                        val baseDataSet = chartData.getTouchDataSet()
                        if (baseDataSet != null) {
                            val index = chartView.getEntryIndex(x)
                            val baseValue = baseDataSet.getEntryForIndex(index)
                            if (baseValue != null) {
                                val time = baseValue.time
                                val value = baseDataSet.viewportYMax - y / contentRect.height() * (baseDataSet.viewportYMax - baseDataSet.viewportYMin)
                                preDrawLine.startDrawValue = DrawLineValue(value, time)
                                preDrawLine.lineState = DrawLineState.second
                                chartView.postInvalidate()
                            }
                        }
                    }
                    return true
                } else if(dragState == ChartConstant.DRAW_LINE_DRAG_RIGHT){
                    // 移动终点
                    if (preDrawLine.endDrawValue != null) {
                        val chartData = (chart.chartData as CombineData)
                        val baseDataSet = chartData.getTouchDataSet()
                        if (baseDataSet != null) {
                            val index = chartView.getEntryIndex(x)
                            val baseValue = baseDataSet.getEntryForIndex(index)
                            if (baseValue != null) {
                                val time = baseValue.time
                                val value = baseDataSet.viewportYMax - y / contentRect.height() * (baseDataSet.viewportYMax - baseDataSet.viewportYMin)
                                preDrawLine.endDrawValue = DrawLineValue(value, time)
                                preDrawLine.lineState = DrawLineState.second
                                chartView.postInvalidate()
                            }
                        }
                    }
                } else if (dragState == ChartConstant.DRAW_LINE_DRAG_BOTH){
                    Log.d("onPressDrawLine", "DRAW_LINE_DRAG_BOTH: 一起滑动")
                    return true
                }

                return false
            }

            MotionEvent.ACTION_UP -> {
                dragState = ChartConstant.DRAW_LINE_NONE

            }
        }
        return false
    }

    private fun onPressDrawLine(e: MotionEvent): Boolean {
        if (!chartView.isOpenDrawLine) return false
        val point = PointF(e.x, e.y)

        val preDrawLine = getPreDrawLine(e)

        if (preDrawLine == null || preDrawLine.lineType == 0) {
            return false
        }

        this.preDrawLine = preDrawLine

        // 当前 画线类型
        val type = preDrawLine.lineType

        val chartData = (chart.chartData as CombineData)
        val baseDataSet = chartData.getTouchDataSet() ?: return false

        when (preDrawLine.lineState) {
            DrawLineState.none -> {
                // 第一步
                chartView.setDrawLineTouchState(DrawLineState.first)
                preDrawLine.lineState = DrawLineState.first
                preDrawLine.startDrawValue = getValue(point, baseDataSet.values, baseDataSet.viewportYMax, baseDataSet.viewportYMin)

                chartView.drawLineListener?.onTouch(DrawLineState.first, point, type)
                chartView.postInvalidate()
            }

            DrawLineState.first -> {
                // 第二步
                chartView.setDrawLineTouchState(DrawLineState.second)

                preDrawLine.lineState = DrawLineState.second
                preDrawLine.endDrawValue = getValue(point, baseDataSet.values, baseDataSet.viewportYMax, baseDataSet.viewportYMin)

                chartView.drawLineListener?.onTouch(DrawLineState.second, point, type)
                chartView.postInvalidate()
            }

            DrawLineState.second -> {
                // 完成
                if (preDrawLine.startDrawValue!= null && preDrawLine.endDrawValue != null ) {
                    if (dragState != ChartConstant.DRAW_LINE_NONE) {

                    } else {
                        preDrawLine.lineState = DrawLineState.complete
                        chartView.setDrawLineTouchState(DrawLineState.complete)
                        chartView.drawLineListener?.onTouch(DrawLineState.complete, point, type)
                        chartView.postInvalidate()
                    }
                }
            }

            DrawLineState.complete -> {
                if (dragState != ChartConstant.DRAW_LINE_NONE) {
                    preDrawLine.lineState = DrawLineState.second
                    chartView.setDrawLineTouchState(DrawLineState.second)
                    chartView.postInvalidate()
                } else {
                    chartView.drawLineListener?.onTouch(DrawLineState.complete, point, type)
                }

            }

            DrawLineState.drag -> {}
        }
        return true
    }

    private fun getPreDrawLine(e: MotionEvent): DrawLineDataSet? {
        val chartData = (chart.chartData as CombineData)
        val drawLineChartData = chartData.drawLineChartData
        val baseDataSet = chartData.getTouchDataSet() ?: return null

        val dataSets = drawLineChartData.dataSets

        var preDrawLine: DrawLineDataSet? = null
        // 遍历当前画线DataSets(如果有) || 点击的区域内有
        // 起点或终点区域 可拖动
        // 起点和终点之前的区域 整体平移
        for (dataSet in dataSets) {
            if (dataSet.lineState == DrawLineState.complete) {
                val inArea = checkInDrawLineArea(dataSet, PointF(e.x, e.y), baseDataSet)
                if (inArea) {
                    preDrawLine = dataSet
                    break
                }
            } else {
                preDrawLine = dataSet
                break
            }
        }

        return preDrawLine
    }

    private fun checkInDrawLineArea(dataSet: DrawLineDataSet, point: PointF, baseDataSet: AbstractDataSet<*>): Boolean {
        val startValue = dataSet.startDrawValue
        val endValue =  dataSet.endDrawValue
        val radius = dataSet.pointRadiusOut * 3f

        if (startValue == null && endValue == null) return false

        val startX = baseDataSet.values.find { it.time == startValue?.time }?.x ?: 0f
        val startY = chartView.getScaleY(startValue?.value ?: 0f, baseDataSet.viewportYMax, baseDataSet.viewportYMin)

        val endX = baseDataSet.values.find { it.time == endValue?.time }?.x ?: 0f
        val endY = chartView.getScaleY(endValue?.value ?: 0f, baseDataSet.viewportYMax, baseDataSet.viewportYMin)

        val startRect = RectF(startX - radius, startY - radius, startX + radius, startY + radius)

        val endRect = RectF(endX - radius, endY - radius, endX + radius, endY + radius)

        val centerY = (startY + endY).absoluteValue * 0.5f
        val centerRect = RectF(startX, centerY + radius, endX, centerY - radius)

        if (startRect.contains(point.x, point.y)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_LEFT
            return true
        }

        if (endRect.contains(point.x, point.y)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_RIGHT
            return true
        }

        if (centerRect.contains(point.x, point.y)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
            return true
        }
        return false
    }

    private fun getValue(
        point: PointF,
        baseValues: MutableList<out AbstractValue>,
        viewportMax: Float, viewportMin: Float
    ) : DrawLineValue {
        val index = chartView.getEntryIndex(point.x)
        val startTime = baseValues.getOrNull(index)?.time ?: 0L
        val startValue = viewportMax - point.y / contentRect.height() * (viewportMax - viewportMin)
        return DrawLineValue(startValue, startTime)
    }
}