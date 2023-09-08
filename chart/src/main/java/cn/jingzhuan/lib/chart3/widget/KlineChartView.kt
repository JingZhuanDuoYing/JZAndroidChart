package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter
import cn.jingzhuan.lib.chart3.formatter.DateTimeFormatter.formatTime
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.formatter.IValueIndexFormatter

class KlineChartView(ctx: Context, attrs: AttributeSet?) : CombineChartView(ctx, attrs) {

    var pattern: String = "yyyy-MM-dd"

    init {
        isScaleEnable = true

        currentVisibleEntryCount = 40

        axisLeft.apply {
            gridCount = 3
            isLabelEnable = true
            enableGridDashPathEffect(floatArrayOf(10f, 10f), 8f)
            labelValueFormatter = object : IValueFormatter {
                override fun format(value: Float, index: Int): String {
                    return if (index == 1 || index == 3) "" else String.format("%.2f", value)
                }
            }

        }

        axisRight.apply {
            gridCount = 0
            isLabelEnable = false
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
                        DateTimeFormatter.ofPattern(pattern).formatTime(time * 1000L)
                    } else ""
                }

            }

            labelValueFormatter = object : IValueFormatter {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun format(value: Float, index: Int): String {
                    val values = chartData.getTouchDataSet()?.getVisiblePoints(currentViewport)
                    return if (values.isNullOrEmpty()) "" else {
                        val leftTime = DateTimeFormatter.ofPattern(pattern)
                            .formatTime(values.first().time * 1000L)
                        val rightTime = DateTimeFormatter.ofPattern(pattern)
                            .formatTime(values.last().time * 1000L)
                        return when (index) {
                            0 -> leftTime
                            4 -> rightTime
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