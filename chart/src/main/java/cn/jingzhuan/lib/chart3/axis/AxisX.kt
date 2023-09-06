package cn.jingzhuan.lib.chart3.axis

import androidx.annotation.IntDef

/**
 * @since 2023-09-06
 * X轴
 */
class AxisX(@AxisXPosition axisPosition: Int) : Axis(axisPosition) {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(TOP, BOTTOM, TOP_INSIDE, BOTTOM_INSIDE)
    internal annotation class AxisXPosition

    companion object {
        const val TOP = 101
        const val BOTTOM = 102
        const val TOP_INSIDE = 103
        const val BOTTOM_INSIDE = 104
    }
}
