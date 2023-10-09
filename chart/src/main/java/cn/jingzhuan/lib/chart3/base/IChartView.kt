package cn.jingzhuan.lib.chart3.base

import android.graphics.Canvas

/**
 * @since 2023-09-05
 * created by lei
 */
interface IChartView {
    /**
     * 画水印
     */
    fun drawWaterMark(canvas: Canvas)

    /**
     * 画坐标轴
     */
    fun drawAxis(canvas: Canvas)

    /**
     * 画坐标轴文本 (左、右、上) 底部单独处理
     */
    fun drawAxisLabels(canvas: Canvas)

    /**
     * 画底部坐标轴文本
     */
    fun drawBottomLabels(canvas: Canvas)

    /**
     * 画网格线
     */
    fun drawGridLine(canvas: Canvas)

    /**
     * 画十字光标
     */
    fun drawHighlight(canvas: Canvas)

    /**
     * 初始化
     */
    fun initChart()

    /**
     * 画图表内容
     */
    fun drawChart(canvas: Canvas)

    /**
     * 画区间统计
     */
    fun drawRangeArea(canvas: Canvas)

    /**
     * 画线工具
     */
    fun drawLineTool(canvas: Canvas)
}
