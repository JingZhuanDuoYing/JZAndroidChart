package cn.jingzhuan.lib.chart3.data.value

/**
 * @since 2023-09-13
 * created by lei
 */
class ScatterTextValue : AbstractValue {

    var isVisible = false

    var high = 0f

    var low = 0f

    constructor()

    constructor(visible: Boolean, high: Float, low: Float) {
        this.isVisible = visible
        this.high = high
        this.low = low
    }

}
