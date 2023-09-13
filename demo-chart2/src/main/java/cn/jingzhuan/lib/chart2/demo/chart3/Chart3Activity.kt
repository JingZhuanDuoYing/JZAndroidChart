package cn.jingzhuan.lib.chart2.demo.chart3

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart2.demo.chart3.chart.MainKlineChartView
import cn.jingzhuan.lib.chart2.demo.chart3.chart.OnSubChartTouchListener
import cn.jingzhuan.lib.chart2.demo.chart3.chart.SubChartView
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.BarDataSet
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ZeroCenterBarDataSet
import cn.jingzhuan.lib.chart3.data.value.BarValue
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.data.value.ScatterValue
import cn.jingzhuan.lib.chart3.event.OnFlagClickListener
import cn.jingzhuan.lib.chart3.event.OnHighlightListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_HISTORY_MINUTE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LHB
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LIMIT_UP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_NOTICE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_SIMULATE_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_TRADE_DETAIL
import kotlin.math.max
import kotlin.math.min

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: MainKlineChartView

    private lateinit var sub1: SubChartView

    private lateinit var sub2: SubChartView

    private lateinit var rg: RadioGroup

    private lateinit var rbDay: RadioButton

    private lateinit var rbYear: RadioButton

    private lateinit var rbMinute: RadioButton

    private val scatterDrawable by lazy { ContextCompat.getDrawable(this@Chart3Activity, R.drawable.ico_range_touch_left) }

    private val subCharts by lazy { mutableListOf(sub1, sub2) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart3)

        initView()

        initListener()

        setMainChartData()

        setSubChartData()
    }

    private fun initView() {
        klineMain = findViewById(R.id.kline_main)
        sub1 = findViewById(R.id.kline_sub1)
        sub2 = findViewById(R.id.kline_sub2)

        subCharts.forEach { it.relatedMainChart(klineMain) }

        rg = findViewById(R.id.rg)
        rbDay = findViewById(R.id.rb_day)
        rbYear = findViewById(R.id.rb_year)
        rbMinute = findViewById(R.id.rb_minute)
    }


    private fun initListener() {
        if (rbDay.isChecked) {
            klineMain.showBottomFlags = true
            klineMain.valueIndexPattern = "yyyy-MM-dd"
        }
        rg.setOnCheckedChangeListener { group, id ->
            when (id) {
                rbDay.id -> {
                    klineMain.showBottomFlags = true
                    klineMain.valueIndexPattern = "yyyy-MM-dd"
                }

                rbYear.id -> {
                    klineMain.showBottomFlags = false
                    klineMain.valueIndexPattern = "dd/HH:mm"
                }

                rbMinute.id -> {
                    klineMain.showBottomFlags = false
                }
            }
        }

        klineMain.apply {
            setViewportChangeListener { viewport->

                subCharts.forEach { it.setCurrentViewport(viewport) }
            }
            setOnHighlightListener(object : OnHighlightListener {
                override fun onHighlightShow(highlight: Highlight?) {
                    Log.d("klineMain", "onHighlightShow: ")
                    subCharts.forEach {
                        val h = Highlight().apply {
                            x = highlight?.x ?: Float.NaN
                            y = Float.NaN
                            dataIndex = highlight?.dataIndex ?: 0
                            touchX = highlight?.touchX ?: Float.NaN
                            touchY = Float.NaN
                        }
                        it.highlightValue(h)
                    }
                }

                override fun onHighlightHide() {
                    Log.d("klineMain", "onHighlightHide: ")
                    subCharts.forEach { it.onHighlightClean() }
                }

            })

            addOnFlagClickListener(object : OnFlagClickListener{
                override fun onClick(type: Int, index: Int) {
                    when (type) {
                        FLAG_HISTORY_MINUTE -> {
                            Toast.makeText(this@Chart3Activity, "历史分时-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_TRADE_DETAIL -> {
                            Toast.makeText(this@Chart3Activity, "交易详情-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_SIMULATE_TRADE_DETAIL -> {
                            Toast.makeText(this@Chart3Activity, "交易详情(模)-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_LIMIT_UP -> {
                            Toast.makeText(this@Chart3Activity, "涨停分析-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_NOTICE -> {
                            Toast.makeText(this@Chart3Activity, "公告-> $index", Toast.LENGTH_SHORT).show()
                        }
                        FLAG_LHB -> {
                            Toast.makeText(this@Chart3Activity, "龙虎榜-> $index", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })
        }

        subCharts.forEach {
            it.apply {
                addSubChartTouchListener(object : OnSubChartTouchListener{
                    override fun touchHighlight(highlight: Highlight, x: Float, y: Float) {
                        touchSubHighlight(highlight, x, y)

                    }

                    override fun checkIfClickToSwitch() {
                        Toast.makeText(this@Chart3Activity, "切换指标", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }


    }

    private fun touchSubHighlight(highlight: Highlight, ex: Float, ey: Float) {
        highlight.touchX = ex
        highlight.touchY = Float.NaN
        var index = max(klineMain.getEntryIndex(ex), 0)
        val dataSet = klineMain.chartData.getTouchDataSet()
        if (dataSet != null && dataSet.values.isNotEmpty()) {
            index = min(index, dataSet.values.size - 1)
            val value = dataSet.values.getOrNull(index) ?: return
            highlight.x = value.x
            highlight.y = Float.NaN
            highlight.dataIndex = index
            klineMain.highlightValue(highlight)
        }
    }

    private fun setSubChartData() {
        val candlestickList = DataConfig.candlestickList
        val barList = ArrayList<BarValue>()
        val zeroBarList = ArrayList<BarValue>()

        var color: Int
        var style: Paint.Style
        candlestickList.forEachIndexed { index, value ->
            if (value.open < value.close) {
                color = Color.RED
                style = Paint.Style.STROKE
            } else if (value.open > value.close) {
                color = Color.GREEN
                style = Paint.Style.FILL
            } else {
                color = Color.GRAY
                style = Paint.Style.FILL
            }
            barList.add(BarValue(value.low, color, style))

            if (index % 3 == 0) {
                zeroBarList.add(BarValue(-value.high, color, Paint.Style.FILL))
            } else {
                zeroBarList.add(BarValue(value.high, color, Paint.Style.FILL))
            }
        }

        val barDataSet = BarDataSet(barList).apply {
            isAutoBarWidth = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val data = CombineData().apply {
            add(barDataSet)
        }

        val zeroBarDataSet = ZeroCenterBarDataSet(zeroBarList).apply {
            isAutoBarWidth = true
            axisDependency = AxisY.DEPENDENCY_LEFT
        }
        val zeroData = CombineData().apply {
            add(zeroBarDataSet)
        }

        subCharts.forEachIndexed { index, subChartView ->
            if (index == 0) {
                subChartView.setCombineData(data)
            }
            if (index == 1) {
                subChartView.setCombineData(zeroData)
            }
        }

    }

    private fun setMainChartData() {
        val candlestickList = DataConfig.candlestickList

        val candlestickDataSet = CandlestickDataSet(candlestickList).apply {
            increasingPaintStyle = Paint.Style.STROKE
            strokeThickness = 3f
        }

        val lineList = ArrayList<LineValue>()
        val scatterList = ArrayList<ScatterValue>()
        candlestickList.forEachIndexed { index, value ->
            lineList.add(LineValue((value.high + value.low) * 0.5f, value.time))
            when (index) {
                118 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(0, 1)))
                }
                108 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(1, 2)))
                }
                98 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(2, 3)))
                }
                88 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(3, 5)))
                }
                78 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(0, 1, 2)))
                }
                68 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(2)))
                }
                58 -> {
                    scatterList.add(ScatterValue(value.close, true, flags = listOf(5)))
                }
                else -> {
                    scatterList.add(ScatterValue(value.close, false))
                }
            }
        }

        val lineDataSet = LineDataSet(lineList).apply {
            color = Color.RED
            lineThickness = 3
        }


        val scatterDataSet = ScatterDataSet(scatterList).apply {
            tag = ChartConstant.FLAG_TAG_NAME
            shape = scatterDrawable
            shapeAlign = cn.jingzhuan.lib.chart.data.ScatterDataSet.SHAPE_ALIGN_PARENT_BOTTOM
            isAutoWidth = false
        }

        val data = CombineData().apply {
            add(candlestickDataSet)
            add(lineDataSet)
            add(scatterDataSet)
        }
        klineMain.setCombineData(data)
    }

}
