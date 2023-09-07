package cn.jingzhuan.lib.chart3.axis

import android.graphics.Color
import android.graphics.Paint
import cn.jingzhuan.lib.chart3.formatter.IGirdLineColorSetter
import cn.jingzhuan.lib.chart3.formatter.ILabelColorSetter
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import cn.jingzhuan.lib.chart3.formatter.IValueIndexFormatter

/**
 * @since 2023-09-06
 * created by lei
 */
open class Axis internal constructor(var axisPosition: Int) : AbstractAxis() {

    /**
     * 网格线颜色
     */
    var gridColor = Color.GRAY

    /**
     * 网格线颜色 设置器
     */
    var girdLineColorSetter: IGirdLineColorSetter? = null

    /**
     * 网格线宽度
     */
    var gridThickness = 1f

    /**
     * 网格线 行、列数量
     */
    var gridCount = 3

    /**
     * 坐标轴文本颜色设置器
     */
    var labelColorSetter: ILabelColorSetter? = null

    /**
     * 坐标轴文本字体大小
     */
    var labelTextSize = 0f

    var labelSeparation = 0f

    /**
     * 坐标轴文本字体颜色
     */
    var labelTextColor = Color.GREEN

    var labelTextPaint: Paint? = null

    private var mLabelWidth = 100

    var labelHeight = 0

    /**
     * 坐标轴颜色
     */
    var axisColor = Color.GRAY

    /**
     * 坐标轴线宽
     */
    var axisThickness = 2f

    var labels: List<String>? = null

    var labelValueFormatter: IValueFormatter? = null

    var valueIndexFormatter: IValueIndexFormatter? = null

    var labelEntries = floatArrayOf()

    var isGridLineEnable = true

    var isLabelEnable = true

    var dashedGridIntervals: FloatArray? = null
        private set

    var dashedGridPhase = -1f
        private set


    var labelWidth: Int
        get() = if (isInside) {
            0
        } else mLabelWidth
        set(mLabelWidth) {
            this.mLabelWidth = mLabelWidth
        }

    val isInside: Boolean
        get() {
            return when (axisPosition) {
                AxisY.LEFT_INSIDE, AxisY.RIGHT_INSIDE, AxisX.BOTTOM_INSIDE, AxisX.TOP_INSIDE -> true
                else -> false
            }
        }

    fun enableGridDashPathEffect(intervals: FloatArray?, phase: Float) {
        dashedGridIntervals = intervals
        dashedGridPhase = phase
    }
}