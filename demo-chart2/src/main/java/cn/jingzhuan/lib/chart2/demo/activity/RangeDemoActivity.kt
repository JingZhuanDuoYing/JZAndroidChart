package cn.jingzhuan.lib.chart2.demo.activity

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet
import cn.jingzhuan.lib.chart.data.ScatterTextValue
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart2.demo.ChartDataConfig
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart2.demo.TestChartKLineView

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_demo)
        llRangeInfo = findViewById(R.id.ll_range_info)
        tvCloseRange = findViewById(R.id.tv_close_range)
        tvInfo = findViewById(R.id.tv_info)
        tvOpen = findViewById(R.id.tv_open)
        tvNumber = findViewById(R.id.tv_number)
        combineChart = findViewById(R.id.combine_chart)
        btnRange = findViewById(R.id.btn_range)
        btnAddTag = findViewById(R.id.btn_add_tag)

        candlestickValues.addAll(addList())

        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f

        combineChart.addDataSet(CandlestickDataSetArrowDecorator(dataSet).apply { offsetPercent = 0.1f })

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
            combineChart.isEnableHighlight = false
            combineChart.postInvalidate()
            tvCloseRange.visibility = View.VISIBLE
            llRangeInfo.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE
        }

        tvCloseRange.setOnClickListener {
            combineChart.rangeEnable = false
            combineChart.isEnableHighlight = true
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
            combineChart.isEnableHighlight = false
            combineChart.postInvalidate()
            llRangeInfo.visibility = View.VISIBLE
            tvCloseRange.visibility = View.VISIBLE
            tvOpen.visibility = View.GONE
            tvNumber.visibility = View.VISIBLE

        }

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

    private fun addList() : MutableList<CandlestickValue> = ChartDataConfig.getDefaultKlineList().toMutableList()

}
