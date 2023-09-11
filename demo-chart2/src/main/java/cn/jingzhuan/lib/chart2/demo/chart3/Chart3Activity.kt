package cn.jingzhuan.lib.chart2.demo.chart3

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: KlineChartView

    private lateinit var rg: RadioGroup

    private lateinit var rbDay: RadioButton

    private lateinit var rbYear: RadioButton

    private lateinit var rbMinute: RadioButton

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
        rg.setOnCheckedChangeListener { group, id ->
            when (id) {
                rbDay.id -> {

                }
                rbYear.id -> {

                }
                rbMinute.id -> {

                }
            }
        }
//        combineChart.addOnViewportChangeListener { viewPort ->
//        }

    }


    private fun setChartData() {
        val candlestickList = DataConfig.candlestickList

        val  candlestickDataSet = CandlestickDataSet(candlestickList).apply {
            increasingPaintStyle = Paint.Style.STROKE
            strokeThickness = 3f
        }

        val lineList = ArrayList<LineValue>()
        candlestickList.forEach {
            lineList.add(LineValue((it.high + it.low) * 0.5f, it.time))
        }

        val lineDataSet = LineDataSet(lineList).apply {
            color = Color.RED
            lineThickness = 3
        }

        val data = CombineData().apply {
            add(candlestickDataSet)
            add(lineDataSet)
        }

        klineMain.setCombineData(data)

    }

}
