package cn.jingzhuan.lib.chart3.data.value

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
class TreeValue(var leafs: List<Leaf>?) : AbstractValue() {

    var isEnable = true

    fun getLeaf(high: Double): Leaf? {
        if (high.isNaN()) return null

        // 精确到小数点后2位
        return leafs?.findLast { (it.high * 100.0).roundToInt() == (high * 100.0).roundToInt() }
    }

    // Float compatibility
    fun getLeaf(high: Float): Leaf? = getLeaf(high.toDouble())

    val high: Double
        get() = leafs?.maxBy { it.high }?.high ?: Double.NaN

    val low: Double
        get() = leafs?.minBy { it.high }?.high ?: Double.NaN

    val leftValue: Double
        get() = leafs?.sumOf { it.leftValue } ?: Double.NaN

    val rightValue: Double
        get() = leafs?.sumOf { it.rightValue } ?: Double.NaN

    val maxLeafValue: Double
        get() = max(leafs?.maxBy { it.leftValue }?.leftValue ?: Double.NaN, leafs?.maxBy { it.rightValue }?.rightValue ?: Double.NaN)

    val leafCount: Int
        get() = leafs?.size ?: 0
}