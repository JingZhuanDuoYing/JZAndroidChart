package cn.jingzhuan.lib.chart2.demo.chart3.chart

import cn.jingzhuan.lib.chart3.Highlight

interface OnSubChartTouchListener {

    fun touchHighlight(highlight: Highlight, x: Float, y: Float)

    fun checkIfClickToSwitch()
}