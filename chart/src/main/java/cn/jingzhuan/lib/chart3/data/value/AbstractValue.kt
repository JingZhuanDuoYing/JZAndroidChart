package cn.jingzhuan.lib.chart3.data.value


/**
 * @since 2023-09-05
 * created by lei
 */
abstract class AbstractValue {
    var x = -1f
    var y = -1f
    var time: Long = -1
    var flags: List<Int> = emptyList()

    fun setCoordinate(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
