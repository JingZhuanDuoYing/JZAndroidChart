package cn.jingzhuan.lib.chart2.demo

import android.content.pm.ActivityInfo
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.CombineData
import cn.jingzhuan.lib.chart.event.OnScaleListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart2.demo.utils.JZDateTimeFormatter
import cn.jingzhuan.lib.chart2.demo.utils.JZDateTimeFormatter.formatTime
import kotlin.math.round

/**
 * 画线工具demo
 */
class LineDrawingToolActivity : AppCompatActivity() {

    private var candlestickValues: MutableList<CandlestickValue> = ArrayList()


    private lateinit var combineChart: TestChartKLineView

    private val lastClose = 3388.98f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_line_drawing_tool)

        initView()

        initListener()

        setChartData()

    }

    private fun initView() {
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
        combineChart.axisBottom.setLabelValueFormatter { value, index ->
            val time = candlestickValues.getOrNull(index)?.time
            when (index) {
                0 -> {
                    if (time != null) {
                        JZDateTimeFormatter.ofPattern("yyyy-MM-dd").formatTime(time * 1000L)
                    } else ""
                }
                4 -> {
                    if (time != null) {
                        JZDateTimeFormatter.ofPattern("yyyy-MM-dd").formatTime(time * 1000L)
                    } else ""
                }
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

    private fun initListener() {

        combineChart.setOnScaleListener(object : OnScaleListener{
            override fun onScaleStart(viewport: Viewport) {

            }

            override fun onScale(viewport: Viewport) {
            }

            override fun onScaleEnd(viewport: Viewport) {
                val from = round(candlestickValues.size * viewport.left)
                val to = round(candlestickValues.size * viewport.right)
                Log.d("Chart", "onScaleEnd->size=${to - from}")
            }

        })

        combineChart.setOnLoadMoreKlineListener {
            loadMoreChartData()
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

        val data = CombineData().apply {
            add(arrowDataSet)
        }

        combineChart.setCombineData(data)

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
        candlestickValues.clear();
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

        val data = CombineData().apply {
            add(arrowDataSet)
        }

        combineChart.setCombineData(data)

    }

}
