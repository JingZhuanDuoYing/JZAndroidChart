package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.PointF
import androidx.collection.ArrayMap
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

    private fun initListener(chart: AbstractChartView<T>) {
        chart.setOnDrawLineListener(object : OnDrawLineListener{
            override fun onTouch(state: DrawLineState, point: PointF, type: Int) {
                val dataSets = (chart.chartData as CombineData).getDrawLineDataSets()
                for (dataSet in dataSets) {
                    val drawLineType = dataSet.lineType
                    val draw = drawMap[drawLineType]
                    if (drawLineType == type) {
                        draw?.onTouch(state, point, dataSet)
                    } else {
                        draw?.onTouch(DrawLineState.none, point, dataSet)
                    }
                }
                chart.postInvalidate()
            }

            override fun onComplete(point1: PointF, point2: PointF, type: Int, dataSet: DrawLineDataSet) {
                val chartData = (chart.chartData as CombineData)

                val baseValues = chartData.getTouchDataSet()?.values ?: return

                val viewportMax = chartData.leftMax
                val viewportMin = chartData.leftMin

                val startIndex = chartView.getEntryIndex(point1.x)
                val startTime = baseValues.getOrNull(startIndex)?.time ?: 0L
                val startValue =  viewportMax - point1.y / contentRect.height() * (viewportMax - viewportMin)
                dataSet.values.add(DrawLineValue(startValue, startTime))

                val endIndex = chartView.getEntryIndex(point2.x)
                val endTime = baseValues.getOrNull(endIndex)?.time ?: 0L
                val endValue =  viewportMax - point2.y / contentRect.height() * (viewportMax - viewportMin)
                dataSet.values.add(DrawLineValue(endValue, endTime))

                chart.postInvalidate()
            }

        })

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

    private fun drawDataSet(canvas: Canvas, dataSet: DrawLineDataSet, baseDataSet: AbstractDataSet<*>, leftMax: Float, leftMin: Float) {
        val type = dataSet.lineType
        val draw = drawMap[type]
        draw?.onDraw(canvas, dataSet, baseDataSet, leftMax, leftMin)
    }
}