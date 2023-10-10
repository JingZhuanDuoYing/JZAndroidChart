package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.collection.ArrayMap
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.AbstractDrawLine
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import cn.jingzhuan.lib.chart3.drawline.SegmentDrawLine
import cn.jingzhuan.lib.chart3.event.OnDrawLineListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant

/**
 * @since 2023-10-09
 * created by lei
 * 画线工具
 */
class DrawLineRenderer<T : AbstractDataSet<*>>(
    private val chart: AbstractChartView<T>,
) : AbstractRenderer<T>(chart) {

    private val drawMap = ArrayMap<Int, AbstractDrawLine<T>>()

    init {
        initDraw(chart)
        initListener(chart)
    }

    fun onViewportChange(viewport: Viewport) {
        Log.d("onPressDrawLine", "viewport")
        val chartData = (chart.chartData as CombineData)
        val dataSet = chartView.preDrawLine
        val baseDataSet = chartData.getTouchDataSet() ?: return
        val viewportMax = baseDataSet.viewportYMax
        val viewportMin = baseDataSet.viewportYMin
        val values = dataSet.values
        if (values.size != 2) return
        val startValue = values[0]
        val endValue = values[1]
        val visibleValues = baseDataSet.getVisiblePoints(viewport)
        if (visibleValues.isNullOrEmpty()) return
        var startX = -1f
        var endX = -1f
        for (baseValue in visibleValues) {
            if (baseValue.time == startValue.time) {
                startX = baseValue.x
            }
            if (baseValue.time == endValue.time) {
                endX = baseValue.x
            }
        }
        val startY = (viewportMax - startValue.value) / (viewportMax - viewportMin) * chartView.contentRect.height()
        val endY = (viewportMax - endValue.value) / (viewportMax - viewportMin) * chartView.contentRect.height()

        dataSet.pointStart = PointF(startX, startY)
        dataSet.pointEnd = PointF(endX, endY)

        val radius = dataSet.pointRadiusOut

        dataSet.startPointRect = RectF(startX - radius, startY - radius, startX + radius, startY + radius)

        dataSet.endPointRect = RectF(endX - radius, endY - radius, endX + radius, endY + radius)
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

//                return true
            }

            MotionEvent.ACTION_UP -> {

            }
        }
        return false
    }

    private fun onPressDrawLine(e: MotionEvent) {
        if (!chartView.isOpenDrawLine) return
        val point = PointF(e.x, e.y)

        val chartData = (chart.chartData as CombineData)
        val drawLineChartData = chartData.drawLineChartData

        val dataSets = drawLineChartData.dataSets

        // 先检查是否有未开始的画线
        var preDrawLine = dataSets.findLast { it.lineState != DrawLineState.complete}

        // 再看当前点所在的位置
        if (preDrawLine == null) {
            for (dataSet in dataSets) {
                if (dataSet.lineState == DrawLineState.complete) {
                    if (dataSet.pointStart!= null && dataSet.pointEnd != null ) {
                        if (dataSet.startPointRect.contains(point.x, point.y) || dataSet.endPointRect.contains(point.x, point.y)) {
                            preDrawLine = dataSet
                            break
                        }
                    }
                }
            }
        }

        if (preDrawLine == null || preDrawLine.lineType == 0) {
            return
        }

        // 当前 画线类型
        val type = preDrawLine.lineType

        val radius = preDrawLine.pointRadiusOut

        when (preDrawLine.lineState) {
            DrawLineState.none -> {
                // 第一步
                chartView.setDrawLineTouchState(DrawLineState.first)
                preDrawLine.lineState = DrawLineState.first
                preDrawLine.pointStart = point
                preDrawLine.startPointRect = RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius)

                chartView.drawLineListener?.onTouch(DrawLineState.first, point, type)
                chartView.postInvalidate()
            }

            DrawLineState.first -> {
                // 第二步
                chartView.setDrawLineTouchState(DrawLineState.second)

                preDrawLine.lineState = DrawLineState.second
                preDrawLine.pointEnd = point
                preDrawLine.endPointRect = RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius)

                chartView.drawLineListener?.onTouch(DrawLineState.second, point, type)
                chartView.postInvalidate()

                onComplete(preDrawLine)
            }

            DrawLineState.second -> {
                // 完成
                if (preDrawLine.pointStart!= null && preDrawLine.pointEnd != null ) {
                    if (preDrawLine.startPointRect.contains(point.x, point.y)) {

                    } else if (preDrawLine.endPointRect.contains(point.x, point.y)) {

                    } else {
                        preDrawLine.lineState = DrawLineState.complete

                        chartView.setDrawLineTouchState(DrawLineState.complete)
                        chartView.drawLineListener?.onTouch(DrawLineState.complete, point, type)
                    }
                    chartView.postInvalidate()
                }
            }

            DrawLineState.complete -> {
                if (preDrawLine.pointStart!= null && preDrawLine.pointEnd != null ) {
                    if (preDrawLine.startPointRect.contains(point.x, point.y)) {
                        preDrawLine.lineState = DrawLineState.second
                        chartView.setDrawLineTouchState(DrawLineState.second)
                    } else if (preDrawLine.endPointRect.contains(point.x, point.y)) {
                        preDrawLine.lineState = DrawLineState.second
                        chartView.setDrawLineTouchState(DrawLineState.second)
                    } else {
                        chartView.drawLineListener?.onTouch(DrawLineState.complete, point, type)
                    }
                    chartView.postInvalidate()
                }

            }

            DrawLineState.drag -> {}
        }
    }

    private fun onComplete(dataSet: DrawLineDataSet) {
        val chartData = (chart.chartData as CombineData)

        val baseValues = chartData.getTouchDataSet()?.values ?: return

        val viewportMax = chartData.leftMax
        val viewportMin = chartData.leftMin

        val startIndex = chartView.getEntryIndex(dataSet.pointStart!!.x)
        val startTime = baseValues.getOrNull(startIndex)?.time ?: 0L
        val startValue = viewportMax - dataSet.pointStart!!.y / contentRect.height() * (viewportMax - viewportMin)
        dataSet.values.add(DrawLineValue(startValue, startTime))

        val endIndex = chartView.getEntryIndex(dataSet.pointEnd!!.x)
        val endTime = baseValues.getOrNull(endIndex)?.time ?: 0L
        val endValue = viewportMax - dataSet.pointEnd!!.y / contentRect.height() * (viewportMax - viewportMin)
        dataSet.values.add(DrawLineValue(endValue, endTime))
    }
}