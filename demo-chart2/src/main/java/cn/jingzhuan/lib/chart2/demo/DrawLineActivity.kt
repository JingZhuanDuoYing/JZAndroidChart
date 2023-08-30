package cn.jingzhuan.lib.chart2.demo

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.CombineData
import cn.jingzhuan.lib.chart.data.DrawLineDataSet
import cn.jingzhuan.lib.chart.data.DrawLineValue
import cn.jingzhuan.lib.chart.event.OnScaleListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart2.demo.utils.JZDateTimeFormatter
import cn.jingzhuan.lib.chart2.demo.utils.JZDateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart2.drawline.DrawLineType
import kotlin.math.round

/**
 * 画线工具demo
 */
class DrawLineActivity : AppCompatActivity() {

    private var candlestickValues: MutableList<CandlestickValue> = ArrayList()


    private lateinit var combineChart: TestChartKLineView

    private lateinit var btnDrawLine: AppCompatButton

    private lateinit var btnDrawSegment: AppCompatButton

    private val lastClose = 3388.98f

    private var leftTime = ""

    private var rightTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_line_drawing_tool)

        initView()

        initListener()

        setChartData()

    }

    private fun initView() {
        btnDrawLine = findViewById(R.id.btn_draw_line)
        btnDrawSegment = findViewById(R.id.btn_draw_segment)
        combineChart = findViewById(R.id.combine_chart)
        combineChart.scaleSensitivity = 1.1f

        combineChart.axisRight.setLabelValueFormatter { value, index ->
            if (index == 1 || value.isNaN() || value >= Int.MAX_VALUE || value <= -Int.MAX_VALUE)
                return@setLabelValueFormatter ""
            val v = when (index) {
                0 -> combineChart.axisLeft.yMin
                2 -> combineChart.axisLeft.yMax
                else -> value
            }
            if (v.isNaN() || v >= Int.MAX_VALUE || v <= -Int.MAX_VALUE)
                return@setLabelValueFormatter ""
            return@setLabelValueFormatter if (lastClose > 0.0) {
                val result =
                    (v - lastClose) / lastClose
                String.format("%.2f%%", result / 0.01)
            } else ""
        }
        combineChart.axisBottom.setLabelValueFormatter { _, index ->
            when (index) {
                0 -> leftTime
                4 -> rightTime
                else -> ""
            }
        }

        combineChart.axisBottom.setValueIndexFormatter { index ->
            val time = candlestickValues.getOrNull(index)?.time
            if (time != null) {
                JZDateTimeFormatter.ofPattern("yyyy-MM-dd").formatTime(time * 1000L)
            } else ""
        }

    }

    private fun setLeftRightTime(
        viewport: Viewport
    ) {
        val candlestickDataSet = combineChart.candlestickDataSet
        if (candlestickDataSet != null && candlestickDataSet.firstOrNull() != null) {
            val values = candlestickDataSet.first().getVisiblePoints(viewport)
            if (values.isNotEmpty()) {
                leftTime = JZDateTimeFormatter.ofPattern("yyyy-MM-dd").formatTime(values.first().time * 1000L)
                rightTime = JZDateTimeFormatter.ofPattern("yyyy-MM-dd").formatTime(values.last().time * 1000L)
            }
        }
    }

    private fun initListener() {
        btnDrawLine.setOnClickListener {
            combineChart.isOpenDrawLine = true
            combineChart.postInvalidate()
        }

        btnDrawSegment.setOnClickListener {
            combineChart.preDrawLineDataSet.apply {
                lineType = DrawLineType.ltSegment.ordinal
            }
            combineChart.postInvalidate()
        }

        combineChart.addOnViewportChangeListener { viewPort ->
            setLeftRightTime(viewPort)
        }

        combineChart.setOnLoadMoreKlineListener {
            loadMoreChartData()
        }

        combineChart.setOnDrawLineCompleteListener { point1, point2, type ->
            val candlestickDataSet = combineChart.candlestickDataSet.getOrNull(0)
            val candlestickValues = candlestickDataSet?.values
            if (!candlestickValues.isNullOrEmpty()) {
                // 当前数据存入dateSet
                val viewportMax = candlestickDataSet.viewportYMax
                val viewportMin = candlestickDataSet.viewportYMin

                Log.d("JZChart", "viewportMax=$viewportMax, viewportMin=$viewportMin")

                val startValue = combineChart.getScaleValue(point1.y, viewportMax, viewportMin)
                val endValue = combineChart.getScaleValue(point2.y, viewportMax, viewportMin)

                val drawLineValues = ArrayList<DrawLineValue>()
                val startIndex = combineChart.getEntryIndexByCoordinate(point1.x, point1.y)
                val startTime = candlestickValues.getOrNull(startIndex)?.time ?: 0L
                drawLineValues.add(DrawLineValue(startValue, startTime))

                val endIndex = combineChart.getEntryIndexByCoordinate(point2.x, point2.y)
                val endTime = candlestickValues.getOrNull(endIndex)?.time ?: 0L
                drawLineValues.add(DrawLineValue(endValue, endTime))
                val dataSet = combineChart.preDrawLineDataSet.apply {
                    values = drawLineValues
                    lineColor = Color.RED
                    lineSize = 5f
                }
                val data = combineChart.currentCombineData.apply {
                    addDataSet(dataSet)
                }
                combineChart.setCombineData(data)
            }
        }
    }

    private fun loadMoreChartData() {
        val lastSize = candlestickValues.size
        candlestickValues.addAll(ChartDataConfig.getDefaultKlineList())
        val newSize = candlestickValues.size
        reCalcViewportByLoadMore(lastSize, newSize)

        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f

        val arrowDataSet = CandlestickDataSetArrowDecorator(dataSet).apply { offsetPercent = 0.05f }

        combineChart.currentCombineData.addDataSet(arrowDataSet)

    }

    private fun reCalcViewportByLoadMore(originCount : Int, newCount: Int) {
        if (originCount != newCount) {
            val viewport = combineChart.currentViewport
            val from = viewport.left * originCount + (newCount - originCount);
            val to = viewport.right * originCount + (newCount - originCount);
            viewport.left = from / newCount
            viewport.right = to / newCount

            if (!combineChart.isHighlightVolatile) {
                val highlight = combineChart.highlights?.firstOrNull()
                val x = highlight?.x ?: 0f
                val y = highlight?.y ?: 0f
                val dataIndex = getEntryIndexByCoordinate(x, viewport)
                Log.d("JZChart", "reCalcViewportByLoadMore $dataIndex, x=$x")
                combineChart.highlightValue(Highlight(x, y, dataIndex))
            }
            combineChart.setCurrentViewport(viewport)
        }
    }

    private fun getEntryIndexByCoordinate(x: Float, viewport: Viewport): Int {
        val contentRect = combineChart.contentRect
        val valueCount = candlestickValues.size
        var index: Int =
            (((x - contentRect.left) * viewport.width() / contentRect.width() + viewport.left) * valueCount.toFloat()).toInt()
        if (index >= valueCount) index = valueCount - 1
        if (index < 0) index = 0
        return index
    }

    private fun getEntryCoordinateByIndex(index: Int, viewport: Viewport): Float {
        val contentRect = combineChart.contentRect
        val valueCount = candlestickValues.size
        var x: Float = contentRect.left + (index / valueCount.toFloat() - viewport.left) / viewport.width() * contentRect.width()
        if (x > contentRect.right) x = contentRect.right.toFloat()
        if (x < contentRect.left) x = contentRect.left.toFloat()
        return x
    }

    private fun setChartData() {
        candlestickValues.clear()
        candlestickValues.addAll(ChartDataConfig.getDefaultKlineList())

        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f

        val arrowDataSet = CandlestickDataSetArrowDecorator(dataSet).apply {
            offsetPercent = 0.05f
            textSize = 32
            textColor = 0xff3F51B5.toInt()
        }

        val drawLineValues = ArrayList<DrawLineValue>()
        val startIndex = 100
        val startCandlestickValue = candlestickValues[startIndex]
        drawLineValues.add(DrawLineValue(startCandlestickValue.open + 50, startCandlestickValue.time))
        val endIndex = 110
        val endCandlestickValue = candlestickValues[endIndex]
        drawLineValues.add(DrawLineValue(endCandlestickValue.open + 50, endCandlestickValue.time))

        val drawLineDataSet = DrawLineDataSet(drawLineValues).apply {
            lineType = DrawLineType.ltSegment.ordinal
            lineColor = Color.RED
            lineSize = 5f
        }

        val data = CombineData().apply {
            add(arrowDataSet)
            add(drawLineDataSet)
        }

        combineChart.setCombineData(data)
        setLeftRightTime(combineChart.currentViewport)

    }

}
