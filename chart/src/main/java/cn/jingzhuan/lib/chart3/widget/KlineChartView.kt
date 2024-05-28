package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.formatter.IValueIndexFormatter
import cn.jingzhuan.lib.chart3.utils.NumberUtils

open class KlineChartView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CombineChartView(context, attrs, defStyleAttr) {

    var valueIndexPattern = "yyyy-MM-dd"

    var bottomLabelPattern = "yyyy-MM-dd"

    init {
        isScaleEnable = true

        axisLeft.apply {
            gridCount = 3
            isLabelEnable = true
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    if (value >= Int.MAX_VALUE || value <= -Int.MAX_VALUE) return ""
                    return NumberUtils.keepPrecision(value.toString(), decimalDigitsNumber)
                }
            }

        }

        axisTop.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisRight.apply {
            gridCount = 1
            isLabelEnable = false
        }

        axisBottom.apply {
            gridCount = 1
            isLabelEnable = true
            isGridLineEnable = false

            valueIndexFormatter = object : IValueIndexFormatter {
                override fun format(index: Int): String {
                    val values = chartData.getTouchDataSet()?.values
                    val time = values?.getOrNull(index)?.time
                    return if (time != null) {
                        DateTimeFormatter.ofPattern(valueIndexPattern).formatTime(time * 1000L)
                    } else ""
                }

            }

            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    val values = chartData.getTouchDataSet()?.getVisiblePoints(currentViewport)
                    return if (values.isNullOrEmpty()) "" else {
                        val leftTime = DateTimeFormatter.ofPattern(bottomLabelPattern)
                            .formatTime(values.first().time * 1000L)
                        val rightTime = DateTimeFormatter.ofPattern(bottomLabelPattern)
                            .formatTime(values.last().time * 1000L)
                        return when (index) {
                            0 -> leftTime
                            axisBottom.gridCount + 1 -> rightTime
                            else -> ""
                        }
                    }

                }
            }

        }
    }

}