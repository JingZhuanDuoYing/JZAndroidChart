package cn.jingzhuan.lib.chart.data

data class Leaf(
    val high: Float,
    val leftValue: Float,
    val rightValue: Float,
    val sumValue: Float
) {
    override fun toString(): String {
        return "${String.format("%.2f", leftValue/100f)}手(${String.format("%.2f", leftValue / sumValue * 100f)}%) <- $high -> ${String.format("%.2f", rightValue/100f)}手(${String.format("%.2f", rightValue / sumValue * 100f)}%)"
//        return "${leftValue/100f}(${String.format("%.2f", leftValue / sumValue * 100f)}%) <- $high -> ${rightValue/100f}(${String.format("%.2f", rightValue / sumValue * 100f)}%)"
    }
}
