package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.formatter.IValueIndexFormatter

open class KlineChartView(ctx: Context, attrs: AttributeSet?) : CombineChartView(ctx, attrs) {

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
                    return String.format("%.${decimalDigitsNumber}f", value)
                }
            }

        }

        axisTop.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisRight.apply {
            gridCount = 0
            isLabelEnable = false
        }

        axisBottom.apply {
            gridCount = 1
            isLabelEnable = true

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
                            2 -> rightTime
                            else -> ""
                        }
                    }

                }
            }

        }
    }

    override fun isCanZoomIn(): Boolean {
        return currentVisibleEntryCount >= minVisibleEntryCount && super.isCanZoomIn()
    }

    override fun isCanZoomOut(): Boolean {
        return currentVisibleEntryCount <= maxVisibleEntryCount && super.isCanZoomOut()
    }

    override fun zoomIn(forceAlignX: Int) {
        if (currentVisibleEntryCount <= minVisibleEntryCount) return
        super.zoomIn(forceAlignX)
    }

    override fun zoomOut(forceAlignX: Int) {
        if (currentVisibleEntryCount >= maxVisibleEntryCount) return
        super.zoomOut(forceAlignX)
    }

}