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

    constructor(value: Float, color: Int) {
        this.value = value
        this.color = color
    }

    constructor(value: Float, secondValue: Float) {
        this.value = value
        this.secondValue = secondValue
    }

    constructor(value: Float, drawCircle: Boolean) {
        this.value = value
        isDrawCircle = drawCircle
    }

    val isValueNaN: Boolean
        get() = value.isNaN()

    fun applyMultiPath(): Boolean {
        return pathColor != -1
    }
}
