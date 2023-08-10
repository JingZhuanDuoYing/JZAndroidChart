package cn.jingzhuan.lib.chart2.demo

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.CombineData
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet
import cn.jingzhuan.lib.chart.data.ScatterTextValue
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener
import cn.jingzhuan.lib.chart.event.OnScaleListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart.utils.ForceAlign
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

class RangeDemoActivity : AppCompatActivity() {

    private var candlestickValues: MutableList<CandlestickValue> = ArrayList()

    private lateinit var llRangeInfo: LinearLayout

    private lateinit var tvCloseRange: TextView

    private lateinit var tvInfo: TextView

    private lateinit var tvOpen: TextView

    private lateinit var tvNumber: TextView

    private lateinit var combineChart: TestChartKLineView

    private lateinit var btnRange: AppCompatButton

    private lateinit var btnAddTag: AppCompatButton

    private lateinit var btnScaleIn: AppCompatButton

    private lateinit var btnScaleOut: AppCompatButton

    private lateinit var btnHighlightAlways: AppCompatButton

    private lateinit var btnMoveLeft: AppCompatButton

    private lateinit var btnMoveRight: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_demo)

        initView()

        initListener()

        setChartData()

//        val dataSet2 = CandlestickDataSet(candlestickValues)
//        dataSet.isHighlightedHorizontalEnable = true
//        dataSet.isHighlightedVerticalEnable = true
//        dataSet.color = 0xFFFD263F.toInt()
//        dataSet.increasingPaintStyle = Paint.Style.FILL_AND_STROKE
//        dataSet.strokeThickness = 2f
//        dataSet.decreasingColor = 0xFF00AA3B.toInt()
//        dataSet.increasingColor = 0xFFFD263F.toInt()
//        combineChart.addDataSet(dataSet2)

        combineChart.onHighlightStatusChangeListener =
            object : HighlightStatusChangeListener {
                override fun onHighlightShow(highlights: Array<out Highlight>?) {
                    llRangeInfo.visibility = View.VISIBLE
                    if (!highlights.isNullOrEmpty()) {
                        val data = candlestickValues[highlights[0].dataIndex]
                        tvInfo.text = "开：${data.open} 高：${data.high} 收：${data.close} 低：${data.low}"
                    }

                }

                override fun onHighlightHide() {
                    llRangeInfo.visibility = View.INVISIBLE
                }
            }

        combineChart.renderer.rangeRenderer.setOnRangeListener{ startX, endX, _ ->
            val startIndex = combineChart.renderer.rangeRenderer.getEntryIndexByCoordinate(startX, 0f)
            val endIndex = combineChart.renderer.rangeRenderer.getEntryIndexByCoordinate(endX, 0f)
            tvNumber.text = "周期数：${endIndex - startIndex}"
            updateCloseRangeButton(startX, endX)
        }
    }

    private fun initView() {
        llRangeInfo = findViewById(R.id.ll_range_info)
        tvCloseRange = findViewById(R.id.tv_close_range)
        tvInfo = findViewById(R.id.tv_info)
        tvOpen = findViewById(R.id.tv_open)
        tvNumber = findViewById(R.id.tv_number)
        combineChart = findViewById(R.id.combine_chart)
        btnRange = findViewById(R.id.btn_range)
        btnAddTag = findViewById(R.id.btn_add_tag)
        btnScaleIn = findViewById(R.id.btn_scale_in)
        btnScaleOut = findViewById(R.id.btn_scale_out)
        btnHighlightAlways = findViewById(R.id.btn_highlight_always)
        btnMoveLeft = findViewById(R.id.btn_move_left)
        btnMoveRight = findViewById(R.id.btn_move_right)
    }

    private fun initListener() {
        btnScaleIn.setOnClickListener {
            combineChart.zoomIn(ForceAlign.RIGHT)
        }

        btnScaleOut.setOnClickListener {
            combineChart.zoomOut(ForceAlign.RIGHT)
        }

        btnAddTag.setOnClickListener {
            val textDataSet = addTextData()
            textDataSet.drawIndex = 11000
            combineChart.addDataSet(textDataSet)
            combineChart.postInvalidate()
        }

        btnRange.setOnClickListener {
            if (combineChart.rangeEnable) return@setOnClickListener
            combineChart.renderer.rangeRenderer.resetData()
            combineChart.cleanHighlight()
            combineChart.rangeEnable = true
            combineChart.isDraggingToMoveEnable = false
            combineChart.isHighlightDisable = true
            combineChart.postInvalidate()
            tvCloseRange.visibility = View.VISIBLE
            llRangeInfo.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE
        }

        btnHighlightAlways.setOnClickListener {
            combineChart.isHighlightVolatile = false
        }

        btnMoveLeft.setOnClickListener {
            combineChart.stepMoveLeft()
        }

        btnHighlightAlways.setOnClickListener {
            combineChart.stepMoveRight()
        }


        tvCloseRange.setOnClickListener {
            combineChart.rangeEnable = false
            combineChart.isDraggingToMoveEnable = true
            combineChart.isHighlightDisable = false
            combineChart.postInvalidate()
            llRangeInfo.visibility = View.INVISIBLE
            tvOpen.visibility = View.VISIBLE
            tvNumber.visibility = View.GONE
            tvCloseRange.visibility = View.GONE
        }

        tvOpen.setOnClickListener {
            if (combineChart.rangeEnable) return@setOnClickListener
            combineChart.cleanHighlight()
            combineChart.rangeEnable = true
            combineChart.isDraggingToMoveEnable = false
            combineChart.isHighlightDisable = true
            combineChart.postInvalidate()
            llRangeInfo.visibility = View.VISIBLE
            tvCloseRange.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE

        }

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

        combineChart.addOnViewportChangeListener {viewPort ->
            if (!combineChart.isHighlightVolatile) {
                val renderer = combineChart.renderer
                val dataSet = combineChart.candlestickDataSet
                val chartWidth = combineChart.contentRect.width()
                val visibleSize = dataSet.firstOrNull()?.getVisibleRange(viewPort)?.roundToInt() ?: 0
                val highlight = combineChart.highlights?.firstOrNull()
                val highLightIndex = highlight?.dataIndex ?: -1
                val highLightX = getEntryCoordinateByIndex(highLightIndex, viewPort)
                val candleWidth = if (dataSet.firstOrNull()?.isAutoWidth == true) {
                    (chartWidth / visibleSize).toFloat()
                } else {
                    dataSet.firstOrNull()?.candleWidth
                } ?: 0f
                val halfCandleWidth = candleWidth.times(0.5f)

                val leftBorder = 0f
                val rightBorder = chartWidth.toFloat() - halfCandleWidth
                val dataIndex = when {
                    highLightX <= leftBorder -> {
                        getEntryIndexByCoordinate(halfCandleWidth, viewPort)
                    }
                    highLightX >= rightBorder -> {
                        getEntryIndexByCoordinate(rightBorder, viewPort)
                    }
                    else -> highLightIndex
                }

                val x = max(halfCandleWidth, min(rightBorder, highLightX + halfCandleWidth))
                val y = highlight?.y ?: Float.NaN
                Log.d("JZChart", "OnViewportChangeListener visibleSize:$visibleSize, highLightIndex:$highLightIndex, highLightX:$highLightX, contentRect.width:${candleWidth}, x:$x, y:$y, dataIndex:$dataIndex")
                combineChart.highlightValue(Highlight(x, y, dataIndex))

            }
//            Log.d("JZChart", "left= ${viewPort.left} +++ right= ${viewPort.right}")
        }
    }

    private fun addTextData(): ScatterTextDataSet {
        val entries = java.util.ArrayList<ScatterTextValue>(candlestickValues.size)
        candlestickValues.forEachIndexed { index, value ->
            entries.add(ScatterTextValue(true, value.high,value.low))
        }
        val dataSet = ScatterTextDataSet(entries)

        dataSet.axisDependency = AxisY.DEPENDENCY_BOTH
        dataSet.text = "加自选"
        dataSet.textColor = 0xffFD263F.toInt()
        dataSet.textBgColor = 0xB3FFFFFF.toInt()
        dataSet.lineColor = 0xffFD263F.toInt()
        dataSet.frameColor = 0xffFD263F.toInt()
        dataSet.textSize = 30

        return dataSet
    }

    private fun updateCloseRangeButton(startX: Float, endX: Float) {

        val centerX = (startX + endX) * 0.5f

        var transX = centerX - tvCloseRange.width * 0.5f

        if (transX < combineChart.left) {
            transX = 0f
        } else if (transX > combineChart.width - tvCloseRange.width) {
            transX = combineChart.width.toFloat() - tvCloseRange.width
        }

        tvCloseRange.translationX = transX
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

        val arrowDataSet = CandlestickDataSetArrowDecorator(dataSet).apply { offsetPercent = 0.1f }

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
            offsetPercent = 0.2f
            textSize = 32
            textColor = 0xff3F51B5.toInt()
        }

        val data = CombineData().apply {
            add(arrowDataSet)
        }

        combineChart.setCombineData(data)

    }

}
