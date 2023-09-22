package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import cn.jingzhuan.lib.chart3.data.dataset.LineDataSet
import cn.jingzhuan.lib.chart3.data.dataset.MinuteLineDataSet
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart3.formatter.ILabelColorSetter
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.formatter.IValueIndexFormatter
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_GREEN
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_RED

open class MinuteChartView(ctx: Context, attrs: AttributeSet?) : CombineChartView(ctx, attrs) {

    var valueIndexPattern = "HH:mm"

    var bottomLabelPattern = "HH:mm"

    init {
        isScaleEnable = false
        isScrollEnable = false

        isEnableHighlightRightText = true

        axisLeft.apply {
            gridCount = 3
            isLabelEnable = true

            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    if (index == 1 || index == 3 || value >= Int.MAX_VALUE || value <= -Int.MAX_VALUE) return ""
                    return String.format("%.${decimalDigitsNumber}f", value)
                }
            }

            labelColorSetter = object : ILabelColorSetter {
                override fun getColorByIndex(position: Int): Int {
                    return when (position) {
                        0 -> COLOR_GREEN
                        2 -> labelTextColor
                        4 -> COLOR_RED
                        else -> Color.TRANSPARENT
                    }
                }

            }

        }

        axisTop.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisRight.apply {
            gridCount = 1
            isLabelEnable = true

            labelColorSetter = object : ILabelColorSetter {
                override fun getColorByIndex(position: Int): Int {
                    return when (position) {
                        0 -> COLOR_GREEN
                        2 -> COLOR_RED
                        else -> Color.TRANSPARENT
                    }
                }
            }

            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    if (index == 1 || value >= Int.MAX_VALUE || value <= -Int.MAX_VALUE) return ""
                    val lineDataSet = chartData.dataSets.find { it is LineDataSet && it.lastClose != -1f } ?: return ""
                    if (lineDataSet is LineDataSet) {
                        val lastClose = lineDataSet.lastClose
                        if (value.isNaN() || lastClose <= 0.0f) return ""
                        val result = (value - lastClose) / lastClose
                        return String.format("%.2f%%", result / 0.01f)
                    }
                    return ""
                }
            }
        }

        axisBottom.apply {
            gridCount = 3
            isLabelEnable = true
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
            valueIndexFormatter = object : IValueIndexFormatter {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun format(index: Int): String {
                    val values = chartData.getTouchDataSet()?.values
                    val time = values?.getOrNull(index)?.time
                    return if (time != null) {
                        DateTimeFormatter.ofPattern(valueIndexPattern).formatTime(time * 1000L)
                    } else ""
                }

            }

            labelValueFormatter = object : IValueFormatter {
                @RequiresApi(Build.VERSION_CODES.O)
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