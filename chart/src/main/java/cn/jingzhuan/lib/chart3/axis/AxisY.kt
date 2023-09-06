package cn.jingzhuan.lib.chart3.axis

import androidx.annotation.IntDef

/**
 * @since 2023-09-06
 * Y轴
 */
class AxisY(axisPosition: Int) : Axis(axisPosition) {

    var yMin = Float.MAX_VALUE
    var yMax = -Float.MAX_VALUE

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(DEPENDENCY_LEFT, DEPENDENCY_RIGHT, DEPENDENCY_BOTH)
    annotation class AxisDependency

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LEFT_OUTSIDE, LEFT_INSIDE, RIGHT_OUTSIDE, RIGHT_INSIDE)
    annotation class AxisYPosition

    companion object {
        const val LEFT_OUTSIDE = 111
        const val LEFT_INSIDE = 112
        const val RIGHT_OUTSIDE = 113
        const val RIGHT_INSIDE = 114
        const val DEPENDENCY_LEFT = 23
        const val DEPENDENCY_RIGHT = 24
        const val DEPENDENCY_BOTH = 25
    }
}