package cn.jingzhuan.lib.chart3.data.value

/**
 * @since 2023-09-05
 * created by lei
 */
class LineValue : AbstractValue {
    var value = 0.0

    var isPathEnd = false

    /**
     * 多段path 不同颜色 应用于: 指标 神龙趋势线
     */
    var pathColor = -1

    /**
     * 第二条数据 应用于: 指标 高抛低吸
     */
    var secondValue = 0.0

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

    constructor(value: Double) {
        this.value = value
    }

    constructor(value: Double, color: Int) {
        this.value = value
        this.color = color
    }

    constructor(value: Double, secondValue: Double) {
        this.value = value
        this.secondValue = secondValue
    }

    constructor(value: Double, drawCircle: Boolean) {
        this.value = value
        this.isDrawCircle = drawCircle
    }

    constructor(value: Double, time: Long) {
        this.value = value
        this.time = time
    }

    constructor(value: Double, color: Int, time: Long) {
        this.value = value
        this.color = color
        this.time = time
    }

    constructor(value: Double, secondValue: Double, time: Long) {
        this.value = value
        this.secondValue = secondValue
        this.time = time
    }

    constructor(value: Double, drawCircle: Boolean, time: Long) {
        this.value = value
        this.isDrawCircle = drawCircle
        this.time = time
    }

    // ------ Float Compatibility Constructors ------

    constructor(value: Float) {
        this.value = value.toDouble()
    }

    constructor(value: Float, color: Int) {
        this.value = value.toDouble()
        this.color = color
    }

    constructor(value: Float, secondValue: Float) {
        this.value = value.toDouble()
        this.secondValue = secondValue.toDouble()
    }

    constructor(value: Float, drawCircle: Boolean) {
        this.value = value.toDouble()
        this.isDrawCircle = drawCircle
    }

    constructor(value: Float, time: Long) {
        this.value = value.toDouble()
        this.time = time
    }

    constructor(value: Float, color: Int, time: Long) {
        this.value = value.toDouble()
        this.color = color
        this.time = time
    }

    constructor(value: Float, secondValue: Float, time: Long) {
        this.value = value.toDouble()
        this.secondValue = secondValue.toDouble()
        this.time = time
    }

    constructor(value: Float, drawCircle: Boolean, time: Long) {
        this.value = value.toDouble()
        this.isDrawCircle = drawCircle
        this.time = time
    }

    // ---------------------------------------------

    val isValueNaN: Boolean
        get() = value.isNaN()

    fun applyMultiPath(): Boolean {
        return pathColor != -1
    }
}
