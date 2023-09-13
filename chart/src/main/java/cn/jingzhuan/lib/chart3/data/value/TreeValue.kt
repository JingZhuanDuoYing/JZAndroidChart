package cn.jingzhuan.lib.chart3.data.value

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
class TreeValue(var leafs: List<Leaf>?) : AbstractValue() {

    var isEnable = true

    fun getLeaf(high: Float): Leaf? {
        if (high.isNaN()) return null

        // 精确到小数点后2位
        return leafs?.findLast { (it.high * 100.0f).roundToInt() == (high * 100.0f).roundToInt() }
    }

    val high: Float
        get() = leafs?.maxBy { it.high }?.high ?: Float.NaN

    val low: Float
        get() = leafs?.minBy { it.high }?.high ?: Float.NaN

    val leftValue: Float
        get() = leafs?.sumOf { it.leftValue.toDouble() }?.toFloat() ?: Float.NaN

    val rightValue: Float
        get() = leafs?.sumOf { it.rightValue.toDouble() }?.toFloat() ?: Float.NaN

    val maxLeafValue: Float
        get() = max(leafs?.maxBy { it.leftValue }?.leftValue ?: Float.NaN, leafs?.maxBy { it.rightValue }?.rightValue ?: Float.NaN)

    val leafCount: Int
        get() = leafs?.size ?: 0
}