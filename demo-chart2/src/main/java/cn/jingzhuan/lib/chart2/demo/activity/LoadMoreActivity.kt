package cn.jingzhuan.lib.chart2.demo.activity

import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.data.CandlestickDataSet
import cn.jingzhuan.lib.chart.data.CandlestickValue
import cn.jingzhuan.lib.chart.data.CombineData
import cn.jingzhuan.lib.chart.data.ScatterDataSet
import cn.jingzhuan.lib.chart.data.ScatterValue
import cn.jingzhuan.lib.chart.event.OnScaleStateListener
import cn.jingzhuan.lib.chart.renderer.CandlestickDataSetArrowDecorator
import cn.jingzhuan.lib.chart2.demo.ChartDataConfig
import cn.jingzhuan.lib.chart2.demo.R
import cn.jingzhuan.lib.chart2.demo.TestChartKLineView

class LoadMoreActivity : AppCompatActivity() {

    private var candlestickValues: MutableList<CandlestickValue> = ArrayList()

    private lateinit var combineChart: TestChartKLineView

    private lateinit var btnHighlightAlways: AppCompatButton

    private var highlightAlwaysShow = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_more)
        combineChart = findViewById(R.id.combine_chart)
        btnHighlightAlways = findViewById(R.id.btn_highlight_always)

        candlestickValues.addAll(getData())

        val dataSet = transformDataSet()

        setCombineData(CandlestickDataSetArrowDecorator(dataSet).apply { offsetPercent = 0.1f }, true)

        combineChart.setOnLoadMoreKlineListener {
            candlestickValues.addAll(getData().reversed())
            transformDataSet()
            val dataSetMore = transformDataSet()
            setCombineData(CandlestickDataSetArrowDecorator(dataSetMore).apply { offsetPercent = 0.1f })
        }

        combineChart.setOnScaleStateListener(object : OnScaleStateListener{
            override fun onScaleMaximum(maximum: Boolean) {
                if(maximum) {
                    Toast.makeText(this@LoadMoreActivity, "已放大至最大值", Toast.LENGTH_LONG).show()
                }
            }

            override fun onScaleMinimum(minimum: Boolean) {
                if(minimum) {
                    Toast.makeText(this@LoadMoreActivity, "已缩小为最小值", Toast.LENGTH_LONG).show()
                }
            }
        })

        btnHighlightAlways.setOnClickListener {
            if(highlightAlwaysShow) {
                highlightAlwaysShow = false
                combineChart.isAlwaysHighlight = false
            }else {
                highlightAlwaysShow = true
                combineChart.isAlwaysHighlight = true
            }
        }

    }

    private fun transformDataSet(): CandlestickDataSet {
        val dataSet = CandlestickDataSet(candlestickValues)
        dataSet.isHighlightedHorizontalEnable = true
        dataSet.isHighlightedVerticalEnable = true
        dataSet.increasingPaintStyle = Paint.Style.STROKE
        dataSet.strokeThickness = 2f
        return dataSet
    }

    private fun getScatterValues(): MutableList<ScatterValue> {
        val scatterValues: MutableList<ScatterValue> = mutableListOf()

        candlestickValues.forEachIndexed { index, value ->
            if(index % 2 == 0) {
                scatterValues.add(ScatterValue(value.low, true))
            } else {
                scatterValues.add(ScatterValue(Float.NaN, false))
            }
        }
        return scatterValues

    }

    private fun setCombineData(dataSet: CandlestickDataSetArrowDecorator, firstLoad: Boolean = false) {
        if(firstLoad) {
            combineChart.addDataSet(dataSet)
        }else {
            val combineData = CombineData()
            combineData.add(dataSet)

            val scatterDataSet1 = ScatterDataSet(getScatterValues())
            val drawable1 = AppCompatResources.getDrawable(this, R.drawable.ic_example)
            scatterDataSet1.isAutoWidth = true
            scatterDataSet1.shape = drawable1
            scatterDataSet1.axisDependency = AxisY.DEPENDENCY_LEFT
            scatterDataSet1.shapeAlign = ScatterDataSet.SHAPE_ALIGN_TOP

            val scatterDataSet2 = ScatterDataSet(getScatterValues())
            val drawable2 = AppCompatResources.getDrawable(this, R.drawable.ico_roket_sbf)
            scatterDataSet2.isAutoWidth = true
            scatterDataSet2.shape = drawable2
            scatterDataSet2.axisDependency = AxisY.DEPENDENCY_LEFT
            scatterDataSet2.shapeAlign = ScatterDataSet.SHAPE_ALIGN_TOP

            val scatterDataSet3 = ScatterDataSet(getScatterValues())
            val drawable3 = AppCompatResources.getDrawable(this, R.drawable.ico_roket_sbf)
            scatterDataSet3.isAutoWidth = true
            scatterDataSet3.shape = drawable3
            scatterDataSet3.axisDependency = AxisY.DEPENDENCY_LEFT
            scatterDataSet3.shapeAlign = ScatterDataSet.SHAPE_ALIGN_TOP

            combineData.add(scatterDataSet1)

            combineData.add(scatterDataSet2)

            combineData.add(scatterDataSet3)

            combineChart.setCombineData(combineData, true)
        }

    }

    private fun getData() : MutableList<CandlestickValue> = ChartDataConfig.getDefaultKlineList().toMutableList()

}
