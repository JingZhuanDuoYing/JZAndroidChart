package cn.jingzhuan.lib.chart2.demo.chart3

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.ScatterDataSet
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.data.value.ScatterValue
import cn.jingzhuan.lib.chart3.event.OnFlagClickListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_HISTORY_MINUTE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LHB
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LIMIT_UP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_NOTICE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_SIMULATE_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: KlineChartView

    private lateinit var rg: RadioGroup

    private lateinit var rbDay: RadioButton

    private lateinit var rbYear: RadioButton

    private lateinit var rbMinute: RadioButton

    private val scatterDrawable by lazy { ContextCompat.getDrawable(this@Chart3Activity, R.drawable.ico_range_touch_left) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart3)

        initView()

        initListener()

        setChartData()

    }

    private fun initView() {
        klineMain = findViewById(R.id.kline_main)
        rg = findViewById(R.id.rg)
        rbDay = findViewById(R.id.rb_day)
        rbYear = findViewById(R.id.rb_year)
        rbMinute = findViewById(R.id.rb_minute)
    }


    private fun initListener() {
        if (rbDay.isChecked) {
            klineMain.showBottomFlags = true
        }
        rg.setOnCheckedChangeListener { group, id ->
            when (id) {
                rbDay.id -> {
                    klineMain.showBottomFlags = true
                }

                rbYear.id -> {
                    klineMain.showBottomFlags = false
                }

                rbMinute.id -> {
                    klineMain.showBottomFlags = false
                }
            }
        }

        klineMain.addOnFlagClickListener(object : OnFlagClickListener{
            override fun onClick(type: Int, index: Int) {
                if (type == FLAG_HISTORY_MINUTE) {
                    Toast.makeText(this@Chart3Activity, "历史分时-> $index", Toast.LENGTH_SHORT).show()
                } else if (type == FLAG_TRADE_DETAIL) {
                    Toast.makeText(this@Chart3Activity, "交易详情-> $index", Toast.LENGTH_SHORT).show()
                } else if (type == FLAG_SIMULATE_TRADE_DETAIL) {
                    Toast.makeText(this@Chart3Activity, "交易详情(模)-> $index", Toast.LENGTH_SHORT).show()
                } else if (type == FLAG_LIMIT_UP) {
                    Toast.makeText(this@Chart3Activity, "涨停分析-> $index", Toast.LENGTH_SHORT).show()
                } else if (type == FLAG_NOTICE) {
                    Toast.makeText(this@Chart3Activity, "公告-> $index", Toast.LENGTH_SHORT).show()
                } else if (type == FLAG_LHB) {
                    Toast.makeText(this@Chart3Activity, "龙虎榜-> $index", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }


    private fun setChartData() {
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
