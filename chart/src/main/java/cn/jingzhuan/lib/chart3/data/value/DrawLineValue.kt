package cn.jingzhuan.lib.chart3.data.value

import java.util.Objects

/**
 * @since 2023-10-09
 * created by lei
 */
class DrawLineValue : AbstractValue {
    var value = 0f

    var dataIndex = -1

    var isVisible = false

    constructor()

    constructor(value: Float) {
        this.value = value
    }

    constructor(value: Float, time: Long) {
        this.value = value
        this.time = time
    }

    constructor(value: Float, time: Long, isVisible: Boolean) {
        this.value = value
        this.time = time
        this.isVisible = isVisible
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as DrawLineValue
        return that.value.compareTo(value) == 0 && time == that.time
    }

    override fun hashCode(): Int {
        return Objects.hash(value, time, x, y)
    }
}