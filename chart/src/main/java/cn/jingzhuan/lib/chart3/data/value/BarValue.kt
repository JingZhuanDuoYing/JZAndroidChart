package cn.jingzhuan.lib.chart3.data.value

import android.graphics.Paint

/**
 * @since 2023-09-05
 * created by lei
 */
class BarValue : AbstractValue {
    var values: DoubleArray? = null

    var color = -2

    var paintStyle = Paint.Style.FILL

    var isEnable = true

    var gradientColors: IntArray? = null

    /**
     * 这里tag比dataSet的优先级高
     */
    var tag: String? = null

    val valueCount: Int
        get() = values?.size ?: 0

    // 兼容 double
    constructor(yValues: DoubleArray?) {
        values = yValues
    }

    constructor(yValues: DoubleArray?, color: Int) {
        values = yValues
        this.color = color
    }

    constructor(value1: Double, value2: Double, color: Int) {
        values = doubleArrayOf(value1, value2)
        this.color = color
    }

    constructor(value1: Double, value2: Double, color: Int, paintStyle: Paint.Style) {
        values = doubleArrayOf(value1, value2)
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(value: Double, color: Int, paintStyle: Paint.Style) {
        values = doubleArrayOf(value, 0.0)
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(values: DoubleArray?, color: Int, paintStyle: Paint.Style) {
        this.values = values
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(yValue: Double) {
        values = doubleArrayOf(yValue, 0.0)
    }

    constructor(yValue: Double, color: Int) {
        values = doubleArrayOf(yValue, 0.0)
        this.color = color
    }

    fun setValues(yValue: Double) {
        values = doubleArrayOf(yValue, 0.0)
    }

    // ------ 兼容 Float 类型重载 ------
    constructor(yValues: FloatArray?) : this(yValues?.map { it.toDouble() }?.toDoubleArray())
    constructor(yValues: FloatArray?, color: Int) : this(yValues?.map { it.toDouble() }?.toDoubleArray(), color)
    constructor(value1: Float, value2: Float, color: Int) : this(value1.toDouble(), value2.toDouble(), color)
    constructor(value1: Float, value2: Float, color: Int, paintStyle: Paint.Style) : this(value1.toDouble(), value2.toDouble(), color, paintStyle)
    constructor(value: Float, color: Int, paintStyle: Paint.Style) : this(value.toDouble(), color, paintStyle)
    constructor(values: FloatArray?, color: Int, paintStyle: Paint.Style) : this(values?.map { it.toDouble() }?.toDoubleArray(), color, paintStyle)
    constructor(yValue: Float) : this(yValue.toDouble())
    constructor(yValue: Float, color: Int) : this(yValue.toDouble(), color)

    fun setValues(yValue: Float) = setValues(yValue.toDouble())
    // -------------------------------

    fun setGradientColors(colorTop: Int, colorBottom: Int) {
        gradientColors = intArrayOf(colorTop, colorBottom)
    }
}
