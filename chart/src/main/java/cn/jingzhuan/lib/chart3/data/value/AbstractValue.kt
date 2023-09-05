package cn.jingzhuan.lib.chart3.data.value


/**
 * @since 2023-09-05
 * created by lei
 */
abstract class AbstractValue {
    var x = -1f
    var y = -1f

    fun setCoordinate(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
