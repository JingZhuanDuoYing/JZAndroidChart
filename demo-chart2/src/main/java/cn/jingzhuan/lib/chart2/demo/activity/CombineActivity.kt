package cn.jingzhuan.lib.chart2.demo.activity

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CombineData
import cn.jingzhuan.lib.chart2.demo.ChartDataConfig
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart2.demo.chart.KLineChart

class CombineActivity : AppCompatActivity() {
    private lateinit var mainChart: KLineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combine)
        initView()
        setChartData()
    }

    private fun initView() {
        mainChart = findViewById(R.id.main_chart)
    }

    private fun setChartData() {
        val candlestickValues = ChartDataConfig.getDefaultKlineList()

        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f

        val combineData = CombineData().apply {
            addDataSet(dataSet)
        }

        mainChart.setData(combineData)
    }
}