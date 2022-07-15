package cn.jingzhuan.lib.chart.data

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Created by guobosheng on 22/6/23.
 */
class TreeValue(var leafs: List<Leaf>?) : Value() {
    var isEnable = true

    fun getLeaf(high: Float) = if (high.isNaN()) null else leafs?.findLast { (it.high * 100.0f).roundToInt() == (high * 100.0f).roundToInt() } // 精确到小数点后2位

    val high: Float
        get() = leafs?.maxBy { it.high }?.high ?: Float.NaN

    val low: Float
        get() = leafs?.minBy { it.high }?.high ?: Float.NaN

    val leftValue: Float
        get() = leafs?.sumByDouble { it.leftValue.toDouble() }?.toFloat() ?: Float.NaN

    val rightValue: Float
        get() = leafs?.sumByDouble { it.rightValue.toDouble() }?.toFloat() ?: Float.NaN

    val maxLeafValue: Float
        get() = max(leafs?.maxBy { it.leftValue }?.leftValue ?: Float.NaN, leafs?.maxBy { it.rightValue }?.rightValue ?: Float.NaN)

    val leafCount: Int
        get() = leafs?.size ?: 0
}