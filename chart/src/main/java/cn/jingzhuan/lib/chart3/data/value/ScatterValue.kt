package cn.jingzhuan.lib.chart3.data.value

import android.graphics.Color
import android.graphics.drawable.Drawable

/**
 * @since 2023-09-05
 * created by lei
 */
class ScatterValue : AbstractValue {
    var value: Float

    var isVisible = true

    var color = Color.TRANSPARENT

    var shape: Drawable? = null

    constructor(value: Float) {
        this.value = value
    }

    constructor(value: Float, visible: Boolean) {
        this.value = value
        this.isVisible = visible
    }

    constructor(value: Float, visible: Boolean, flags: List<Int>) {
        this.value = value
        this.isVisible = visible
        this.flags = flags
    }

    constructor(value: Float, visible: Boolean, color: Int) {
        this.value = value
        this.isVisible = visible
        this.color = color
    }
}
