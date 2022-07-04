package cn.jingzhuan.lib.chart.data

data class Leaf(
    val high: Float,
    val leftValue: Float,
    val leftSumValue: Float,
    val rightValue: Float,
    val rightSumValue: Float
) {
    override fun toString(): String {
        return "${String.format("%.2f", leftValue)}(${String.format("%.2f", leftValue / leftSumValue * 100f)}%) <- $high -> ${String.format("%.2f", rightValue)}(${String.format("%.2f", rightValue / rightSumValue * 100f)}%)"
    }
}
