package cn.jingzhuan.lib.chart3

import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.max
import kotlin.math.nextUp

/**
 * @since 2023-09-06
 */
class Viewport : RectF {

    @JvmOverloads
    constructor(
        left: Float = AXIS_X_MIN,
        top: Float = AXIS_Y_MIN,
        right: Float = AXIS_X_MAX,
        bottom: Float = AXIS_Y_MAX
    ) : super(left, top, right, bottom)

    constructor(r: RectF?) : super(r)
    constructor(r: Rect?) : super(r)

    /**
     * Ensures that current viewport is inside the viewport extremes defined by [AXIS_X_MIN],
     * [AXIS_X_MAX], [AXIS_Y_MIN] and [AXIS_Y_MAX].
     */
    fun constrainViewport() {
        left = max(AXIS_X_MIN, left)
        right = max(left.nextUp(), right)
    }

    fun initialized(): Boolean {
        return !(left == AXIS_X_MIN && right == AXIS_X_MAX && top == AXIS_Y_MIN && bottom == AXIS_Y_MAX)
    }

    fun moveToEnd(): Viewport {
        val vp = Viewport(this)
        val width = width()
        vp.right = 1f
        vp.left = right - width
        return vp
    }

    companion object {
        const val AXIS_X_MIN = 0f
        const val AXIS_X_MAX = 1f
        const val AXIS_Y_MIN = -1f
        const val AXIS_Y_MAX = 1f
    }
}