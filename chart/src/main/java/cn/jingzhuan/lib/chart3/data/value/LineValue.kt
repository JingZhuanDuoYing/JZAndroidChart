package cn.jingzhuan.lib.chart3.data.value

/**
 * @since 2023-09-05
 * created by lei
 */
class LineValue : AbstractValue {
    var value = 0f

    var isPathEnd = false

    /**
     * 多段path 不同颜色 应用于: 指标 神龙趋势线
     */
    var pathColor = -1

    /**
     * 第二条数据 应用于: 指标 高抛低吸
     */
    var secondValue = 0f

    /**
     * 第二条数据: y轴坐标
     */
    var secondY = 0f

    var color = -2

    /**
     * 是否画圆点
     */
    var isDrawCircle = true

    constructor()

    constructor(value: Float) {
        this.value = value
    }

    constructor(value: Float, time: Long) {
        this.value = value
        this.time = time
    }

    constructor(value: Float, color: Int, time: Long) {
        this.value = value
        this.color = color
        this.time = time
    }

    constructor(value: Float, secondValue: Float, time: Long) {
        this.value = value
        this.secondValue = secondValue
        this.time = time
    }

    constructor(value: Float, drawCircle: Boolean, time: Long) {
        this.value = value
        isDrawCircle = drawCircle
        this.time = time
    }

    val isValueNaN: Boolean
        get() = value.isNaN()

    fun applyMultiPath(): Boolean {
        return pathColor != -1
    }
}
