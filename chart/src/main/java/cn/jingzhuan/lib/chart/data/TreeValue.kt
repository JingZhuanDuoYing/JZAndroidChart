package cn.jingzhuan.lib.chart.data

import kotlin.math.max

/**
 * Created by guobosheng on 22/6/23.
 */
class TreeValue(var leafs: List<Leaf>?) : Value() {
    var isEnable = true

    val high: Float
        get() = leafs?.maxBy { it.high }?.high ?: Float.NaN

    val leftValue: Float
        get() = leafs?.sumByDouble { it.leftValue.toDouble() }?.toFloat() ?: Float.NaN

    val rightValue: Float
        get() = leafs?.sumByDouble { it.rightValue.toDouble() }?.toFloat() ?: Float.NaN

    val maxLeafValue: Float
        get() = max(leafs?.maxBy { it.leftValue }?.leftValue ?: Float.NaN, leafs?.maxBy { it.rightValue }?.rightValue ?: Float.NaN)

    val leafCount: Int
        get() = leafs?.size ?: 0
}