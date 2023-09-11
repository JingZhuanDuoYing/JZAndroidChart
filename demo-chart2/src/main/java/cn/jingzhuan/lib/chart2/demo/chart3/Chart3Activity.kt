package cn.jingzhuan.lib.chart2.demo.chart3

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart3.data.CombineData
import cn.jingzhuan.lib.chart3.data.dataset.CandlestickDataSet
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.value.LineValue
import cn.jingzhuan.lib.chart3.widget.KlineChartView

class Chart3Activity : AppCompatActivity() {

    private lateinit var klineMain: KlineChartView

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
