package cn.jingzhuan.lib.chart2.demo.chart3.chart

import android.content.Context
import android.util.AttributeSet
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.widget.KlineChartView
import cn.jingzhuan.lib.chart3.widget.StaticChartView

class SubChartView(ctx: Context, attrs: AttributeSet?) : StaticChartView(ctx, attrs) {

    private var mainChartView: KlineChartView? = null

    private var subChartTouchListener : OnSubChartTouchListener? = null

    init {
        currentVisibleEntryCount = 40
        decimalDigitsNumber = 2
        isEnableHighlightBottomText = false

        axisLeft.labelValueFormatter = SubKlineLeftValueFormatter(decimalDigitsNumber)

        val highlight = Highlight()
        addOnTouchPointListener(object : OnTouchPointListener {
            override fun touch(x: Float, y: Float) {
                val chartMain = mainChartView ?: return
                if (chartMain.isOpenRange) return
                // 如果历史分时已打开
                val mainHighlightState = chartMain.highlightState
                if (mainHighlightState == HIGHLIGHT_STATUS_FOREVER) {
                    return
                }

                // 如果此时正在长按 不往下处理
                if (isLongPress) {
                    if (!chartMain.isHighlightEnable) return
                    if (subChartTouchListener != null) {
                        subChartTouchListener?.touchHighlight(highlight, x, y)
                    }
                    return
                }
                if (mainHighlightState != HIGHLIGHT_STATUS_INITIAL) {
                    // 如果光标正在显示 清除
                    chartMain.onHighlightClean()
                } else {
                    // 光标未显示 判断是否可以切换指标
                    if (subChartTouchListener != null) {
                        subChartTouchListener?.checkIfClickToSwitch()
                    }
                }
            }
        })

    }

    fun addSubChartTouchListener(listener: OnSubChartTouchListener) {
        this.subChartTouchListener = listener
    }

    fun relatedMainChart(chartView: KlineChartView) {
        this.mainChartView = chartView
    }

}