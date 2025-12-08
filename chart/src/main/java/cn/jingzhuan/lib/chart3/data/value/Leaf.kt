package cn.jingzhuan.lib.chart3.data.value


/**
 * @since 2023-09-05
 * created by lei
 */
data class Leaf(
    val high: Double,
    val leftValue: Double,
    val rightValue: Double,
    val sumValue: Double
) {
    // ------ Float Compatibility Constructors ------
    constructor(
        high: Float,
        leftValue: Float,
        rightValue: Float,
        sumValue: Float
    ) : this(
        high.toDouble(),
        leftValue.toDouble(),
        rightValue.toDouble(),
        sumValue.toDouble()
    )
}
