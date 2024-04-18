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
import cn.jingzhuan.lib.chart3.data.value.CandlestickValue
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.AbstractDrawLine
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import cn.jingzhuan.lib.chart3.drawline.EndAnchorDrawLine
import cn.jingzhuan.lib.chart3.drawline.FBNCDrawLine
import cn.jingzhuan.lib.chart3.drawline.FontDrawLine
import cn.jingzhuan.lib.chart3.drawline.HJFGDrawLine
import cn.jingzhuan.lib.chart3.drawline.HorizonDrawLine
import cn.jingzhuan.lib.chart3.drawline.ParallelDrawLine
import cn.jingzhuan.lib.chart3.drawline.PriceLabelDrawLine
import cn.jingzhuan.lib.chart3.drawline.RayDrawLine
import cn.jingzhuan.lib.chart3.drawline.RectDrawLine
import cn.jingzhuan.lib.chart3.drawline.SegmentDrawLine
import cn.jingzhuan.lib.chart3.drawline.StraightDrawLine
import cn.jingzhuan.lib.chart3.drawline.VerticalDrawLine
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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

//    private val mTouchSlop by lazy { ViewConfiguration.get(chart.context).scaledTouchSlop }

    /**
     * 上一次触摸的x坐标
     */
    private var lastPreX = 0f

    /**
     * 上一次触摸的y坐标
     */
    private var lastPreY = 0f

    /**
     * 吸附时最小距离
     */
    private val lessDistanceY by lazy { 25 }

    init {
        initDraw(chart)
    }

    private fun initDraw(chart: AbstractChartView<T>) {
        drawMap[DrawLineType.ltSegment.ordinal] = SegmentDrawLine(chart)
        drawMap[DrawLineType.ltParallelLine2.ordinal] = StraightDrawLine(chart)
        drawMap[DrawLineType.ltEndAnchorLine.ordinal] = EndAnchorDrawLine(chart)
        drawMap[DrawLineType.ltRect.ordinal] = RectDrawLine(chart)
        drawMap[DrawLineType.ltParallelLine.ordinal] = ParallelDrawLine(chart)
        drawMap[DrawLineType.ltHJFG.ordinal] = HJFGDrawLine(chart)
        drawMap[DrawLineType.ltFBNC.ordinal] = FBNCDrawLine(chart)
        drawMap[DrawLineType.ltHorizon.ordinal] = HorizonDrawLine(chart)
        drawMap[DrawLineType.ltVerticalLine.ordinal] = VerticalDrawLine(chart)
        drawMap[DrawLineType.ltRaysLine.ordinal] = RayDrawLine(chart)
        drawMap[DrawLineType.ltPriceLabel.ordinal] = PriceLabelDrawLine(chart)
        drawMap[DrawLineType.ltFont.ordinal] = FontDrawLine(chart)
    }

    override fun renderer(canvas: Canvas) {
        val chartData = (chart.chartData as CombineData)
        val baseDataSet = chartData.getTouchDataSet() ?: return
        val drawLineDataSets = chartData.getDrawLineDataSets()
        if (drawLineDataSets.isEmpty()) return

        for (dataSet in drawLineDataSets) {
            if (dataSet.isVisible) {
                drawDataSet(canvas, dataSet, baseDataSet, chartData.leftMax, chartData.leftMin)
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
//                val diffX = abs(event.x - lastPreX)
//                val diffY = abs(event.y - lastPreY)
//                if (diffX <= mTouchSlop && diffY <= mTouchSlop) {
//                    return false
//                }
                onMoveDrawLine(event)
            }

            MotionEvent.ACTION_UP -> {
                onUpDrawLine(event)
            }
        }
        return false
    }

    private fun onUpDrawLine(event: MotionEvent): Boolean {
        val chartData = (chart.chartData as CombineData?) ?: return false
        val drawLineChartData = chartData.drawLineChartData
        val preDrawLine = drawLineChartData.dataSets.findLast { it.isSelect } ?: return false
        if (preDrawLine.lineType != DrawLineType.ltFont.ordinal) {
            chartView.drawLineListener?.onDrag(
                PointF(event.rawX, event.rawY),
                PointF(event.x, event.y),
                ChartConstant.DRAW_LINE_NONE
            )
        }
        preDrawLine.isActionUp = true
        if (preDrawLine.lineState == DrawLineState.complete)
            chartView.postInvalidate()
        return true
    }

    private fun onMoveDrawLine(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val chartData = (chart.chartData as CombineData?) ?: return false
        val drawLineChartData = chartData.drawLineChartData
        val baseDataSet = chartData.getTouchDataSet() ?: return false

        val preDrawLine = drawLineChartData.dataSets.findLast { it.isSelect } ?: return false
        preDrawLine.isActionUp = false
        when (dragState) {
            ChartConstant.DRAW_LINE_DRAG_LEFT -> {
                // 移动起点
                if (preDrawLine.startDrawValue != null) {
                    val point = PointF(x, y)
                    val startDrawValue = getValue(
                        point,
                        baseDataSet.values,
                        chartData.leftMax,
                        chartData.leftMin,
                        chart.isDrawLineAdsorb,
                        historyTimes = preDrawLine.historyTimeList
                    )
                    if (startDrawValue != null) {
                        val mx = event.x
                        if (mx > chartView.width || mx < 0) {
                            return true
                        }
                        // 斐波那挈 起点不能大于等于终点
                        val endDrawValue = preDrawLine.endDrawValue
                        if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal
                            && endDrawValue != null
                            && startDrawValue.dataIndex >= endDrawValue.dataIndex
                        ) {
                            return true
                        }

                        preDrawLine.startDrawValue = startDrawValue
                        preDrawLine.isSelect = true
//                        Log.d("onPressDrawLine", "移动起点->index=${startDrawValue.dataIndex}, startX= ${startDrawValue.x}, startY= $y,endX= ${preDrawLine.endDrawValue?.x},endY= ${preDrawLine.endDrawValue?.y},")
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
                    val endDrawValue = getValue(
                        point,
                        baseDataSet.values,
                        chartData.leftMax,
                        chartData.leftMin,
                        chart.isDrawLineAdsorb,
                        historyTimes = preDrawLine.historyTimeList
                    )
                    if (endDrawValue != null) {
                        val mx = event.x
                        if (mx > chartView.width || mx < 0) {
                            return true
                        }
                        // 斐波那挈 终点不能小于等于起点
                        val startDrawValue = preDrawLine.startDrawValue
                        if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal
                            && startDrawValue != null
                            && endDrawValue.dataIndex <= startDrawValue.dataIndex) {
                            return true
                        }
                        preDrawLine.endDrawValue = endDrawValue
                        preDrawLine.isSelect = true
//                        Log.d("onPressDrawLine", "移动终点->index=${endDrawValue.dataIndex}, startX= ${preDrawLine.startDrawValue?.x}, startY= ${preDrawLine.startDrawValue?.y},endX= ${endDrawValue.x},endY= ${endDrawValue.y},")
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
                    val thirdDrawValue = getValue(
                        point,
                        baseDataSet.values,
                        chartData.leftMax,
                        chartData.leftMin,
                        chart.isDrawLineAdsorb,
                        historyTimes = preDrawLine.historyTimeList
                    )
                    if (thirdDrawValue != null) {
                        val mx = event.x
                        if (mx > chartView.width || mx < 0) {
                            return true
                        }
                        preDrawLine.thirdDrawValue = thirdDrawValue
                        preDrawLine.isSelect = true
//                        Log.d("onPressDrawLine", "移动平行点->index=${thirdDrawValue.dataIndex}, thirdX= ${thirdDrawValue.x}, thirdY= ${thirdDrawValue.y}, startX= ${preDrawLine.startDrawValue?.x}, startY= ${preDrawLine.startDrawValue?.y}, endX= ${preDrawLine.endDrawValue?.x}, endY= ${preDrawLine.endDrawValue?.y}")
                        chartView.drawLineListener?.onDrag(PointF(event.rawX, event.rawY), point, ChartConstant.DRAW_LINE_DRAG_BOTH)
                        chartView.postInvalidate()
                    }
                }
                return true
            }

            ChartConstant.DRAW_LINE_DRAG_BOTH -> {
                // 一起滑动

                val startValue = preDrawLine.startDrawValue
                val endValue = preDrawLine.endDrawValue

                val enableSpecial = ((preDrawLine.lineType == DrawLineType.ltHorizon.ordinal
                        || preDrawLine.lineType == DrawLineType.ltVerticalLine.ordinal
                        || preDrawLine.lineType == DrawLineType.ltFont.ordinal
                        ) && startValue != null)

                if ((startValue != null && endValue != null) || enableSpecial) {
                    val deltaX = x - lastPreX
                    val deltaY = y - lastPreY

                    val startX = startValue?.x ?: 0f
                    val startY = startValue?.y ?: 0f

                    val endX = endValue?.x ?: 0f
                    val endY = endValue?.y ?: 0f

                    val thirdValue = preDrawLine.thirdDrawValue

                    if (deltaY > 0) {
                        // 向下
                        val bottomY = contentRect.bottom - preDrawLine.pointOuterR * 2f
                        var minY = startY
                        if (endValue != null) {
                            minY = min(minY, endY)
                        }
                        if (thirdValue != null) {
                            minY = min(minY, preDrawLine.thirdDrawValue?.y ?: 0f)
                        }
//                        Log.d("画线工具","同时滑动-> 向下 deltaY=$deltaY minY=$minY bottomY=$bottomY")
                        if (minY > bottomY) {
                            return true
                        }
                    }

                    if (deltaY < 0){
                        // 向上
                        val topY = contentRect.top + preDrawLine.pointOuterR * 2f
                        var maxY = startY
                        if (endValue != null) {
                            maxY = max(maxY, endY)
                        }
                        if (thirdValue != null) {
                            maxY = max(maxY, preDrawLine.thirdDrawValue?.y ?: 0f)
                        }
//                        Log.d("画线工具","同时滑动-> 向上 deltaY=$deltaY maxY=$maxY topY=$topY")
                        if (maxY < topY) {
                            return true
                        }

                    }

                    if (deltaX > 0) {
                        // 向右
                        val rightX = drawMap[preDrawLine.lineType]?.getEntryX(baseDataSet.values.size - 1, baseDataSet) ?: chartView.width.toFloat()
                        var maxX = startX
                        if (endValue != null) {
                            maxX = max(maxX, endX)
                        }
                        if (thirdValue != null) {
                            maxX = max(maxX, (preDrawLine.thirdDrawValue?.x ?: 0f))
                        }
//                        Log.d("画线工具","同时滑动-> 向右 deltaX=$deltaX maxX=$maxX rightX=$rightX")
                        if (maxX > rightX) {
                            return true
                        }
                    }

                    if (deltaX < 0) {
                        // 向左
                        val leftX = drawMap[preDrawLine.lineType]?.getEntryX(0, baseDataSet) ?: 0f
                        var minX = startX
                        if (endValue != null) {
                            minX = min(minX, endX)
                        }
                        if (thirdValue != null) {
                            minX = min(minX, (preDrawLine.thirdDrawValue?.x ?: 0f))
                        }
//                        Log.d("画线工具","同时滑动-> 向左 deltaX=$deltaX minX=$minX leftX=$leftX")
                        if (minX < leftX) {
                            return true
                        }
                    }

                    val visibleValues = baseDataSet.getVisiblePoints(currentViewport)
                    if (!visibleValues.isNullOrEmpty()) {
                        val nowStartX = startX + deltaX
                        var nowStartY = startY + deltaY
                        if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal) {
                            nowStartY = startY
                        }
                        val startPoint = PointF(nowStartX, nowStartY)
                        val startDrawValue = getValue(
                            startPoint,
                            baseDataSet.values,
                            chartData.leftMax,
                            chartData.leftMin,
                            historyTimes = preDrawLine.historyTimeList
                        )
                        startDrawValue?.x = nowStartX

                        val mx = startDrawValue?.x ?: 0f
                        if (startDrawValue?.dataIndex == 0 || (chartView.totalEntryCount < chartView.currentVisibleEntryCount && (mx > chartView.width || mx < 0))) {
                            return true
                        }

                        preDrawLine.startDrawValue = startDrawValue

                        if (endValue != null) {
                            val nowEndX = endX + deltaX
                            var nowEndY = endY + deltaY
                            if (preDrawLine.lineType == DrawLineType.ltFBNC.ordinal) {
                                nowEndY = endY
                            }
                            val endPoint = PointF(nowEndX, nowEndY)
                            val endDrawValue = getValue(
                                endPoint,
                                baseDataSet.values,
                                chartData.leftMax,
                                chartData.leftMin,
                                historyTimes = preDrawLine.historyTimeList
                            )
                            endDrawValue?.x = nowEndX

                            preDrawLine.endDrawValue = endDrawValue
                        }

                        if (thirdValue != null) {
                            val thirdX = thirdValue.x
                            val thirdY = thirdValue.y

                            val nowThirdX = thirdX + deltaX
                            val nowThirdY = thirdY + deltaY

                            val thirdPoint = PointF(nowThirdX, nowThirdY)
                            val thirdDrawValue = getValue(
                                thirdPoint,
                                baseDataSet.values,
                                chartData.leftMax,
                                chartData.leftMin,
                                historyTimes = preDrawLine.historyTimeList
                            )
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
            chartView.drawLineListener?.onTouch(DrawLineState.complete, "", lineType)
            return false
        }

        (chartView as BaseChartView).cleanHighlight()

        preDrawLine.isActionUp = false

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

                when (preDrawLine.lineType) {
                    DrawLineType.ltFont.ordinal -> {
                        dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                    }
                    // 垂直|水平线只有一个点 所以只有一步 点击即完成
                    DrawLineType.ltHorizon.ordinal, DrawLineType.ltVerticalLine.ordinal -> {
                        dragState = ChartConstant.DRAW_LINE_DRAG_LEFT
                        preDrawLine.lineState = DrawLineState.complete
                    }
                    else -> {
                        preDrawLine.lineState = DrawLineState.first
                    }
                }
                preDrawLine.isSelect = true
                preDrawLine.startDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin, historyTimes = preDrawLine.historyTimeList)

                if (preDrawLine.lineType == DrawLineType.ltFont.ordinal) {
                    chartView.drawLineListener?.onTouchText {
                        preDrawLine.text = it
                        preDrawLine.lineState = DrawLineState.complete
                        chartView.postInvalidate()
                    }
                } else {
                    chartView.drawLineListener?.onTouch(preDrawLine.lineState, preDrawLine.lineKey ?: "", lineType)
                    chartView.postInvalidate()
                }
            }

            DrawLineState.first -> {
                // 第二步
                if (preDrawLine.lineType == DrawLineType.ltParallelLine.ordinal) {
                    preDrawLine.lineState = DrawLineState.second
                } else {
                    dragState = ChartConstant.DRAW_LINE_DRAG_RIGHT
                    preDrawLine.lineState = DrawLineState.complete
                }
                preDrawLine.isSelect = true
                preDrawLine.endDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin, historyTimes = preDrawLine.historyTimeList)

                chartView.drawLineListener?.onTouch(preDrawLine.lineState, preDrawLine.lineKey ?: "", lineType)
                chartView.postInvalidate()
            }

            DrawLineState.second -> {
                if (preDrawLine.lineType == DrawLineType.ltParallelLine.ordinal) {
                    dragState = ChartConstant.DRAW_LINE_DRAG_THIRD
                    preDrawLine.lineState = DrawLineState.complete
                    preDrawLine.thirdDrawValue = getValue(point, baseDataSet.values, chartData.leftMax, chartData.leftMin, historyTimes = preDrawLine.historyTimeList)
                    preDrawLine.isSelect = true
                    chartView.drawLineListener?.onTouch(preDrawLine.lineState, preDrawLine.lineKey ?: "", lineType)
                    chartView.postInvalidate()
                }
            }

            DrawLineState.complete -> {
                preDrawLine.isSelect = dragState != ChartConstant.DRAW_LINE_NONE
                chartView.drawLineListener?.onTouch(preDrawLine.lineState, preDrawLine.lineKey ?: "", lineType, true)
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
        val drawingDataset = dataSets.findLast { it.lineState != DrawLineState.complete }
        if (drawingDataset != null) return drawingDataset
        for (dataSet in dataSets) {
            if (dataSet.lineState == DrawLineState.complete) {
                val inLine = checkIfInLine(
                    dataSet,
                    PointF(e.x, e.y),
                    baseDataSet,
                    chartData.leftMax,
                    chartData.leftMin
                )
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

    private fun checkIfInLine(
        dataSet: DrawLineDataSet,
        point: PointF,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float,
    ): Boolean {
        val visibleValues = baseDataSet.getVisiblePoints(chartView.currentViewport)
        if (visibleValues.isNullOrEmpty()) return false

        val startValue = dataSet.startDrawValue
        val endValue = dataSet.endDrawValue
        val thirdValue = dataSet.thirdDrawValue
        val radius = dataSet.pointOuterR * 1.5f

        if (startValue == null && endValue == null) return false

        // 检查是否能拖动起点
        val startX = drawMap[dataSet.lineType]?.getEntryX(startValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val startY = chartView.getScaleY(startValue?.value ?: 0f, lMax, lMin)
        var startRect = RectF(startX - radius, startY - radius, startX + radius, startY + radius)
        if (dataSet.lineType == DrawLineType.ltFBNC.ordinal) {
            startRect = RectF(startX - radius, 0f, startX + radius, contentRect.height().toFloat())
        }
        // 文本不拖动起点
        if (checkInRect(point, startRect) && dataSet.lineType != DrawLineType.ltFont.ordinal) {
            dragState = ChartConstant.DRAW_LINE_DRAG_LEFT
            return true
        }

        // 检查是否能拖动终点
        val endX = drawMap[dataSet.lineType]?.getEntryX(endValue?.dataIndex ?: -1, baseDataSet) ?: -1f
        val endY = chartView.getScaleY(endValue?.value ?: 0f, lMax, lMin)
        var endRect = RectF(endX - radius, endY - radius, endX + radius, endY + radius)
        if (dataSet.lineType == DrawLineType.ltFBNC.ordinal) {
            endRect = RectF(endX - radius, 0f, endX + radius, contentRect.height().toFloat())
        }
        // 文本不拖动终点
        if (checkInRect(point, endRect) && dataSet.lineType != DrawLineType.ltFont.ordinal) {
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
        if (dataSet.lineType == DrawLineType.ltFBNC.ordinal || dataSet.lineType == DrawLineType.ltRect.ordinal) {
            // 斐波那挈 能同时拖动
            val region = dataSet.selectRegions.findLast { it?.contains(point.x.roundToInt(), point.y.roundToInt()) == true }
            if (region != null) {
                dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                return true
            }
        } else {
            val region = dataSet.selectRegion
            val parallelRegion = dataSet.parallelSelectRegion

            if (region?.contains(point.x.roundToInt(), point.y.roundToInt()) == true
                || parallelRegion?.contains(point.x.roundToInt(), point.y.roundToInt()) == true
            ) {
                dragState = ChartConstant.DRAW_LINE_DRAG_BOTH
                return true
            }
        }

        dragState = ChartConstant.DRAW_LINE_NONE

        return false
    }

    override fun getEntryIndex(x: Float): Int {
        val chartData = (chart.chartData as CombineData?) ?: return -1

        val dataSet = chartData.getTouchDataSet() ?: return 0

        val valueCount = max(chartView.totalEntryCount, dataSet.forceValueCount)

        var index = (((x - contentRect.left) * currentViewport.width() / contentRect.width() + currentViewport.left) * valueCount.toFloat()).toInt()
        if (index >= dataSet.values.size) index = dataSet.values.size - 1

        return index
    }

    private fun getValue(
        point: PointF,
        baseValues: MutableList<out AbstractValue>,
        lMax: Float,
        lMin: Float,
        adsorb: Boolean = false,
        historyTimes: List<Long>
    ): DrawLineValue? {
        val index = getEntryIndex(point.x)
        val hisIndex = index + historyTimes.size - baseValues.size
        val baseValue = baseValues.getOrNull(index)

        val time = historyTimes.getOrNull(hisIndex) ?: baseValue?.time ?: 0
        val selectX = point.x
        var selectY = point.y

        // 如果是K线才有吸附功能
        if (adsorb && baseValue != null && baseValue is CandlestickValue) {
            val openY = chartView.getScaleY(baseValue.open, lMax, lMin)
            val closeY = chartView.getScaleY(baseValue.close, lMax, lMin)
            val highY = chartView.getScaleY(baseValue.high, lMax, lMin)
            val lowY = chartView.getScaleY(baseValue.low, lMax, lMin)

            when (selectY) {
                in highY - lessDistanceY..highY -> {
                    selectY = highY
                }

                in closeY - lessDistanceY..closeY -> {
                    selectY = closeY
                }

                in openY - lessDistanceY..openY -> {
                    selectY = openY
                }

                in lowY - lessDistanceY..lowY -> {
                    selectY = lowY
                }
            }
        }

        val value = lMax - selectY / contentRect.height() * (lMax - lMin)

        if (index < 0 && hisIndex < 0) {

            val leftIndex = 0
            val leftTime = baseValues.getOrNull(leftIndex)?.time ?: return null
            val leftX = getEntryX(leftIndex)
            return DrawLineValue(value, leftTime).apply { dataIndex = leftIndex; x = leftX; y = selectY }
        }

        return DrawLineValue(value, time).apply { dataIndex = index; x = selectX; y = selectY }
    }

    fun checkIfHaveDrawing(): Boolean {
        val dataSets = (chartView.chartData as CombineData).drawLineChartData.dataSets
        val data = dataSets.findLast { it.isSelect }
        return data != null
    }
}