package cn.jingzhuan.lib.chart3.data.value

import android.graphics.Paint

/**
 * @since 2023-09-05
 * created by lei
 */
class BarValue : AbstractValue {
    var values: FloatArray?

    var color = -2

    var paintStyle = Paint.Style.FILL

    var isEnable = true

    var gradientColors: IntArray? = null

    /**
     * 这里tag比dataSet的优先级高
     */
    var tag: String? = null

    val valueCount: Int
        get() = if (values == null) 0 else values!!.size

    constructor(yValues: FloatArray?) {
        values = yValues
    }

    constructor(yValues: FloatArray?, color: Int) {
        values = yValues
        this.color = color
    }

    constructor(value1: Float, value2: Float, color: Int) {
        values = floatArrayOf(value1, value2)
        this.color = color
    }

    constructor(value1: Float, value2: Float, color: Int, paintStyle: Paint.Style) {
        values = floatArrayOf(value1, value2)
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(value: Float, color: Int, paintStyle: Paint.Style) {
        values = floatArrayOf(value, 0f)
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(values: FloatArray?, color: Int, paintStyle: Paint.Style) {
        this.values = values
        this.color = color
        this.paintStyle = paintStyle
    }

    constructor(yValue: Float) {
        values = floatArrayOf(yValue, 0f)
    }

    constructor(yValue: Float, color: Int) {
        values = floatArrayOf(yValue, 0f)
        this.color = color
    }

    fun setValues(yValue: Float) {
        values = floatArrayOf(yValue, 0f)
    }

    fun setGradientColors(colorTop: Int, colorBottom: Int) {
        gradientColors = intArrayOf(colorTop, colorBottom)
    }
}
