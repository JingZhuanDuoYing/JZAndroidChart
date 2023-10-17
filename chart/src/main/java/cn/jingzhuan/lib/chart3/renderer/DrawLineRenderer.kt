package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.collection.ArrayMap
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.base.BaseChartView
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.AbstractDrawLine
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import cn.jingzhuan.lib.chart3.drawline.EndAnchorDrawLine
import cn.jingzhuan.lib.chart3.drawline.ParallelDrawLine
import cn.jingzhuan.lib.chart3.drawline.RectDrawLine
import cn.jingzhuan.lib.chart3.drawline.SegmentDrawLine
import cn.jingzhuan.lib.chart3.drawline.StraightDrawLine
import cn.jingzhuan.lib.chart3.event.OnDrawLineTouchListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

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

    /**
     * 上一次触摸的x坐标
     */
    private var lastPreX = 0f

    /**
     * 上一次触摸的y坐标
     */
    private var lastPreY = 0f

    init {
        initDraw(chart)

        chart.addOnDrawLineTouchListener(object : OnDrawLineTouchListener{

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ) {
                onMoveDrawLine(e2)
            }

        })
    }

    private fun initDraw(chart: AbstractChartView<T>) {
        drawMap[DrawLineType.ltSegment.ordinal] = SegmentDrawLine(chart)
        drawMap[DrawLineType.ltStraightLine.ordinal] = StraightDrawLine(chart)
        drawMap[DrawLineType.ltEndAnchorLine.ordinal] = EndAnchorDrawLine(chart)
        drawMap[DrawLineType.ltRect.ordinal] = RectDrawLine(chart)
        drawMap[DrawLineType.ltParallelLine.ordinal] = ParallelDrawLine(chart)
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
                lastPreX = event.x
                lastPreY = event.y
                onPressDrawLine(event)
            }

            MotionEvent.ACTION_MOVE -> {
//                onMoveDrawLine(event)
            }

            MotionEvent.ACTION_UP -> {
                chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), PointF(event.x, event.y), ChartConstant.DRAW_LINE_NONE)
            }
        }
        return false
    }

    private fun onMoveDrawLine(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val chartData = (chart.chartData as CombineData?)
        val drawLineChartData = chartData?.drawLineChartData
        val baseDataSet = chartData?.getTouchDataSet() ?: return false

        val preDrawLine = drawLineChartData?.dataSets?.find { it.isSelect } ?: return false
        if (dragState == ChartConstant.DRAW_LINE_DRAG_LEFT) {
            // 移动起点
            if (preDrawLine.startDrawValue != null) {
                val point = PointF(x, y)
                val startDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                if (startDrawValue != null) {
                    preDrawLine.startDrawValue = startDrawValue
                    preDrawLine.isSelect = true
                    Log.d("onPressDrawLine", "移动起点->index=${startDrawValue.dataIndex}, startX= ${startDrawValue.x}, startY= $y,endX= ${preDrawLine.endDrawValue?.x},endY= ${preDrawLine.endDrawValue?.y},")
                    chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_LEFT)
                    chartView.postInvalidate()
                }
            }
            return true
        } else if(dragState == ChartConstant.DRAW_LINE_DRAG_RIGHT){
            // 移动终点
            if (preDrawLine.endDrawValue != null) {
                val point = PointF(x, y)
                val endDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                if (endDrawValue != null) {
                    preDrawLine.endDrawValue = endDrawValue
                    preDrawLine.isSelect = true
                    Log.d("onPressDrawLine", "移动终点->index=${endDrawValue.dataIndex}, startX= ${preDrawLine.startDrawValue?.x}, startY= ${preDrawLine.startDrawValue?.y},endX= ${endDrawValue.x},endY= ${endDrawValue.y},")
                    chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_RIGHT)
                    chartView.postInvalidate()
                }
            }
            return true
        } else if (dragState == ChartConstant.DRAW_LINE_DRAG_BOTH){
            // 一起滑动
            if (preDrawLine.startDrawValue != null && preDrawLine.endDrawValue != null) {
                val deltaX = x - lastPreX
                val deltaY = y - lastPreY
                val visibleValues = baseDataSet.getVisiblePoints(currentViewport)
                if (!visibleValues.isNullOrEmpty()) {
                    val nowStartX = preDrawLine.startDrawValue!!.x + deltaX
                    val nowStartY = preDrawLine.startDrawValue!!.y + deltaY
                    val startPoint = PointF(nowStartX, nowStartY)
                    val startDrawValue = getValue(startPoint, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    startDrawValue?.x = nowStartX

                    preDrawLine.startDrawValue = startDrawValue

                    val nowEndX = preDrawLine.endDrawValue!!.x + deltaX
                    val nowEndY = preDrawLine.endDrawValue!!.y + deltaY
                    val endPoint = PointF(nowEndX, nowEndY)
                    val endDrawValue = getValue(endPoint, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    endDrawValue?.x = nowEndX

                    preDrawLine.endDrawValue = endDrawValue

                    preDrawLine.isSelect = true

                    chartView.postInvalidate()
                }
            }
            lastPreX = event.x
            lastPreY = event.y
            return true
        }

        return false
    }

    private fun onPressDrawLine(event: MotionEvent): Boolean {
        if (!chartView.isOpenDrawLine) return false
        val point = PointF(event.x, event.y)

        val chartData = (chart.chartData as CombineData)

        val baseDataSet = chartData.getTouchDataSet() ?: return false

        val preDrawLine = getPreDrawLine(event, chartData)

        // 当前 画线类型
        val lineType = preDrawLine?.lineType ?: 0

        if (preDrawLine == null || lineType == 0) {
            return false
        }

        (chartView as BaseChartView).cleanHighlight()

        when (preDrawLine.lineState) {
            DrawLineState.prepare -> {
                // 第一步
                preDrawLine.lineState = DrawLineState.first
                preDrawLine.isSelect = true
                preDrawLine.startDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)

                chartView.drawLineListener?.onTouch(preDrawLine.lineState, point, lineType)
                chartView.postInvalidate()
            }

            DrawLineState.first -> {
                // 第二步
                if (preDrawLine.lineType == DrawLineType.ltParallelLine.ordinal) {
                    preDrawLine.lineState = DrawLineState.second
                } else {
                    preDrawLine.lineState = DrawLineState.complete
                }
                preDrawLine.isSelect = true
                preDrawLine.endDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)

                chartView.drawLineListener?.onTouch(preDrawLine.lineState, point, lineType)
                chartView.postInvalidate()
            }

            DrawLineState.second -> {
                if (preDrawLine.lineType == DrawLineType.ltParallelLine.ordinal) {
                    preDrawLine.lineState = DrawLineState.complete
                    preDrawLine.thirdDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    preDrawLine.isSelect = true
                    chartView.drawLineListener?.onTouch(preDrawLine.lineState, point, lineType)
                    chartView.postInvalidate()
                }
            }

            DrawLineState.complete -> {
                preDrawLine.isSelect = dragState != ChartConstant.DRAW_LINE_NONE
                Log.d("onPressDrawLine", "完成->preDrawLine.isSelect=${preDrawLine.isSelect}")
                chartView.postInvalidate()
            }
        }
        return true
    }

    private fun getPreDrawLine(e: MotionEvent, chartData: CombineData): DrawLineDataSet? {
        val drawLineChartData = chartData.drawLineChartData
        val baseDataSet = chartData.getTouchDataSet() ?: return null

        val dataSets = drawLineChartData.dataSets

        var preDrawLine: DrawLineDataSet? = null
        // 遍历当前画线DataSets(如果有) || 点击的区域内有
        // 起点或终点区域 可拖动
        // 起点和终点之前的区域 整体平移
        dataSets.forEach { it.isSelect = false }
        for (dataSet in dataSets) {
            if (dataSet.lineState == DrawLineState.complete) {
                val inArea = checkInDrawLineArea(dataSet, PointF(e.x, e.y), baseDataSet, chartData.leftMax, chartData.leftMin)
                if (inArea) {
                    dataSet.isSelect = true
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

    private fun checkInDrawLineArea(dataSet: DrawLineDataSet, point: PointF, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float): Boolean {
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return false

        val startValue = dataSet.startDrawValue
        val endValue =  dataSet.endDrawValue
        val radius = dataSet.pointRadiusOut * 3f

        if (startValue == null && endValue == null) return false

        val startX = drawMap[dataSet.lineType]?.getEntryX(startValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val startY = chartView.getScaleY(startValue?.value ?: 0f, lMax, lMin)

        val endX = drawMap[dataSet.lineType]?.getEntryX(endValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val endY = chartView.getScaleY(endValue?.value ?: 0f, lMax, lMin)

        Log.d("onPressDrawLine", "checkInDrawLineArea->startX= $startX, startValue?.dataIndex=${startValue?.dataIndex}, startY= $startY,endX= $endX,endY= $endY,")

        val startRect = RectF(startX - radius, startY - radius, startX + radius, startY + radius)

        val endRect = RectF(endX - radius, endY - radius, endX + radius, endY + radius)

        val centerY = (startY + endY).absoluteValue * 0.5f

        val centerRect = RectF(startX, centerY + radius, endX, centerY - radius)

        if (point.x in min(startRect.left, startRect.right)..max(startRect.left, startRect.right)
            && point.y in min(startRect.top, startRect.bottom)..max(startRect.top, startRect.bottom)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_LEFT
            return true
        }

        if (point.x in min(endRect.left, endRect.right)..max(endRect.left, endRect.right)
            && point.y in min(endRect.top, endRect.bottom)..max(endRect.top, endRect.bottom)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_RIGHT
            return true
        }

        if (point.x in min(centerRect.left, centerRect.right)..max(centerRect.left, centerRect.right)
            && point.y in min(centerRect.top, centerRect.bottom)..max(centerRect.top, centerRect.bottom)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
            return true
        }
        dragState = ChartConstant.DRAW_LINE_NONE
        return false
    }

    private fun getValue(
        point: PointF,
        baseValues: MutableList<out AbstractValue>,
        viewportMax: Float,
        viewportMin: Float
    ) : DrawLineValue? {
        val index = chartView.getEntryIndex(point.x)
        val baseValue = baseValues.getOrNull(index) ?: return null
        val startTime = baseValue.time
        val selectX = baseValue.x

        val startValue = viewportMax - point.y / contentRect.height() * (viewportMax - viewportMin)
        val selectY = point.y
        return DrawLineValue(startValue, startTime).apply { dataIndex = index; x = selectX; y = selectY }
    }

    fun checkIfHaveDrawing(): Boolean {
        val dataSets = (chartView.chartData as CombineData).drawLineChartData.dataSets
        val data = dataSets.findLast { it.isSelect }
        return data != null
    }
}