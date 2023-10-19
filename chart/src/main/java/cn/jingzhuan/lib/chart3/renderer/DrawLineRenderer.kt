package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
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
import cn.jingzhuan.lib.chart3.drawline.FBNCDrawLine
import cn.jingzhuan.lib.chart3.drawline.HJFGDrawLine
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
                Log.d("DrawLineRenderer", "onScroll")
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
        drawMap[DrawLineType.ltHJFG.ordinal] = HJFGDrawLine(chart)
        drawMap[DrawLineType.ltFBNC.ordinal] = FBNCDrawLine(chart)
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
                Log.d("DrawLineRenderer", "MotionEvent.ACTION_DOWN->dragState=$dragState")
                lastPreX = event.x
                lastPreY = event.y
                onPressDrawLine(event)
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("DrawLineRenderer", "MotionEvent.ACTION_MOVE")
//                onMoveDrawLine(event)
            }

            MotionEvent.ACTION_UP -> {
                Log.d("DrawLineRenderer", "MotionEvent.ACTION_UP")
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

        val preDrawLine = drawLineChartData?.dataSets?.findLast { it.isSelect } ?: return false
        when (dragState) {
            ChartConstant.DRAW_LINE_DRAG_LEFT -> {
                // 移动起点
                if (preDrawLine.startDrawValue != null) {
                    val point = PointF(x, y)
                    val startDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    if (startDrawValue != null) {
                        // 斐波那挈 起点不能大于等于终点
                        if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal && startDrawValue.dataIndex >= preDrawLine.endDrawValue!!.dataIndex) {
                            return true
                        }

                        preDrawLine.startDrawValue = startDrawValue
                        preDrawLine.isSelect = true
                        Log.d("onPressDrawLine", "移动起点->index=${startDrawValue.dataIndex}, startX= ${startDrawValue.x}, startY= $y,endX= ${preDrawLine.endDrawValue?.x},endY= ${preDrawLine.endDrawValue?.y},")
                        chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_LEFT)
                        chartView.postInvalidate()
                    }
                }
                return true
            }
            ChartConstant.DRAW_LINE_DRAG_RIGHT -> {
                // 移动终点
                if (preDrawLine.endDrawValue != null) {
                    val point = PointF(x, y)
                    val endDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    if (endDrawValue != null) {
                        // 斐波那挈 终点不能小于等于起点
                        if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal && endDrawValue.dataIndex <= preDrawLine.startDrawValue!!.dataIndex) {
                            return true
                        }
                        preDrawLine.endDrawValue = endDrawValue
                        preDrawLine.isSelect = true
                        Log.d("onPressDrawLine", "移动终点->index=${endDrawValue.dataIndex}, startX= ${preDrawLine.startDrawValue?.x}, startY= ${preDrawLine.startDrawValue?.y},endX= ${endDrawValue.x},endY= ${endDrawValue.y},")
                        chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_RIGHT)
                        chartView.postInvalidate()
                    }
                }
                return true
            }
            ChartConstant.DRAW_LINE_DRAG_THIRD -> {
                // 移动平行点
                if (preDrawLine.thirdDrawValue != null) {
                    val point = PointF(x, y)
                    val thirdDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                    if (thirdDrawValue != null) {
                        preDrawLine.thirdDrawValue = thirdDrawValue
                        preDrawLine.isSelect = true
                        Log.d("onPressDrawLine", "移动平行点->index=${thirdDrawValue.dataIndex}, thirdX= ${thirdDrawValue.x}, thirdY= ${thirdDrawValue.y}, startX= ${preDrawLine.startDrawValue?.x}, startY= ${preDrawLine.startDrawValue?.y}, endX= ${preDrawLine.endDrawValue?.x}, endY= ${preDrawLine.endDrawValue?.y}")
                        chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_BOTH)
                        chartView.postInvalidate()
                    }
                }
                return true
            }
            ChartConstant.DRAW_LINE_DRAG_BOTH -> {
                // 一起滑动
                if (preDrawLine.startDrawValue != null && preDrawLine.endDrawValue != null) {
                    val deltaX = x - lastPreX
                    val deltaY = y - lastPreY

//                    if (deltaX > 0 && ((preDrawLine.startDrawValue?.dataIndex ?: 0) >= baseDataSet.values.size - 1
//                        || (preDrawLine.endDrawValue?.dataIndex ?: 0) >= baseDataSet.values.size - 1
//                        || (preDrawLine.thirdDrawValue?.dataIndex ?: 0) >= baseDataSet.values.size - 1)) {
//                        return true
//                    }

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

                        if (preDrawLine.thirdDrawValue != null) {
                            val nowThirdX = preDrawLine.thirdDrawValue!!.x + deltaX
                            val nowThirdY = preDrawLine.thirdDrawValue!!.y + deltaY

                            val thirdPoint = PointF(nowThirdX, nowThirdY)
                            val thirdDrawValue = getValue(thirdPoint, baseDataSet.values, chartData.leftMax, chartData.leftMin)
                            thirdDrawValue?.x = nowThirdX
                            preDrawLine.thirdDrawValue = thirdDrawValue
                        }

                        preDrawLine.isSelect = true

                        chartView.postInvalidate()
                    }
                }
                lastPreX = event.x
                lastPreY = event.y
                return true
            }
            else -> return false
        }

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

        if (lineType == DrawLineType.ltFBNC.ordinal) {
            if (preDrawLine.lineState == DrawLineState.first && point.x < (preDrawLine.startDrawValue?.x ?: -1f)) {
                Toast.makeText(chart.context, "斐波那挈终点不能早于或等于起点", Toast.LENGTH_SHORT).show()
                preDrawLine.isSelect = true
                chartView.postInvalidate()
                return true
            }
        }

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
                val inLine = checkIfInLine(dataSet, PointF(e.x, e.y), baseDataSet, chartData.leftMax, chartData.leftMin)
                if (inLine) {
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

    private fun checkInRect(point: PointF, rectF: RectF): Boolean {
        return (point.x in min(rectF.left, rectF.right)..max(rectF.left, rectF.right)
                && point.y in min(rectF.top, rectF.bottom)..max(rectF.top, rectF.bottom))
    }

    /**
     * 检查是否在直线、平行线内
     */
    private fun checkInStraight(dataSet: DrawLineDataSet, point: PointF, lMax: Float, lMin: Float, radius: Float) : Boolean{
        if (dataSet.thirdDrawValue != null) {
            val leftY = chartView.getScaleY(dataSet.sLeftCrossValue, lMax, lMin)
            val rightY = chartView.getScaleY(dataSet.sRightCrossValue, lMax, lMin)
            val rectF = RectF(0f, leftY - radius, chartView.contentRect.width().toFloat(), rightY + radius)
            if (checkInRect(point, rectF)) {
                return true
            }
        }

        val leftY = chartView.getScaleY(dataSet.leftCrossValue, lMax, lMin)
        val rightY = chartView.getScaleY(dataSet.rightCrossValue, lMax, lMin)
        val rectF = RectF(0f, leftY - radius, chartView.contentRect.width().toFloat(), rightY + radius)
        if (checkInRect(point, rectF)) {
            return true
        }
        return false
    }

    /**
     * 检查是否在黄金分割范围内
     */
    private fun checkInHJFG(dataSet: DrawLineDataSet, point: PointF, startY: Float, endY: Float) : Boolean{
        val radius = dataSet.pointOuterR * 2
        val rectF = RectF(0f, startY - radius, chartView.contentRect.width().toFloat(), endY + radius)
        if (checkInRect(point, rectF)) {
            return true
        }
        return false
    }

    private fun checkIfInLine(dataSet: DrawLineDataSet, point: PointF, baseDataSet: AbstractDataSet<*>, lMax: Float, lMin: Float): Boolean {
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return false

        val startValue = dataSet.startDrawValue
        val endValue =  dataSet.endDrawValue
        val thirdValue = dataSet.thirdDrawValue
        val radius = dataSet.pointOuterR * 3f

        if (startValue == null && endValue == null) return false

        // 检查是否能拖动起点
        val startX = drawMap[dataSet.lineType]?.getEntryX(startValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val startY = chartView.getScaleY(startValue?.value ?: 0f, lMax, lMin)
        val startRect = RectF(startX - radius, startY - radius, startX + radius, startY + radius)
        if (checkInRect(point, startRect)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_LEFT
            return true
        }

        // 检查是否能拖动终点
        val endX = drawMap[dataSet.lineType]?.getEntryX(endValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val endY = chartView.getScaleY(endValue?.value ?: 0f, lMax, lMin)
        val endRect = RectF(endX - radius, endY - radius, endX + radius, endY + radius)
        if (checkInRect(point, endRect)) {
            dragState = ChartConstant.DRAW_LINE_DRAG_RIGHT
            return true
        }

        // 检查是否能拖动平行点
        if (thirdValue != null) {
            val x = drawMap[dataSet.lineType]?.getEntryX(thirdValue.dataIndex, baseDataSet) ?: -1f
            val y = chartView.getScaleY(thirdValue.value, lMax, lMin)
            val rectF = RectF(x - radius, y - radius, x + radius, y + radius)
            if (checkInRect(point, rectF)) {
                dragState = ChartConstant.DRAW_LINE_DRAG_THIRD
                return true
            }
        }

        // 检查是否能同时拖动
        if (dataSet.lineType == DrawLineType.ltStraightLine.ordinal || dataSet.lineType == DrawLineType.ltParallelLine.ordinal) {
            if (checkInStraight(dataSet, point, lMax, lMin, radius)) {
                dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                return true
            }
        } else if (dataSet.lineType == DrawLineType.ltHJFG.ordinal) {
            if (checkInHJFG(dataSet, point, startY, endY)) {
                dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                return true
            }
        } else if (dataSet.lineType == DrawLineType.ltFBNC.ordinal) {
            // 斐波那挈 不能同时拖动
            dragState = ChartConstant.DRAW_LINE_NONE
            return false
        } else {
            val runY = (startY + endY).absoluteValue * 0.5f
            val runRect = RectF(startX, runY + radius, endX, runY - radius)
            if (checkInRect(point, runRect) || checkInStraight(dataSet, point, lMax, lMin, radius)) {
                dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                return true
            }
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