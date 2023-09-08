package cn.jingzhuan.lib.chart2.demo.chart3

import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: KlineChartView

    private val lastClose = 3388.98f

    private var leftTime = ""

    private var rightTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart3)

        initView()

        initListener()

        setChartData()

    }

    private fun initView() {
        klineMain = findViewById(R.id.kline_main)
//        klineMain.scaleSensitivity = 1.1f

    }


    private fun initListener() {

//        combineChart.addOnViewportChangeListener { viewPort ->
//        }

    }


    private fun setChartData() {
        val candlestickList = DataConfig.candlestickList

        val dataSet = CandlestickDataSet(candlestickList).apply {
            increasingPaintStyle = Paint.Style.STROKE
            strokeThickness = 2f
        }


        val data = CombineData().apply {
            add(dataSet)
        }

        klineMain.setCombineData(data)

    }

}
