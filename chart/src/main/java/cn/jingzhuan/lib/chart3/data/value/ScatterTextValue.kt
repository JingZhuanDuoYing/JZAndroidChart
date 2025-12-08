package cn.jingzhuan.lib.chart3.data.value

/**
 * @since 2023-09-13
 * created by lei
 */
class ScatterTextValue : AbstractValue {

    var isVisible = false

    var high = 0.0

    var low = 0.0

    constructor()

    constructor(visible: Boolean, high: Double, low: Double) {
        this.isVisible = visible
        this.high = high
        this.low = low
    }

    // ------ Float Compatibility Constructors ------

    constructor(visible: Boolean, high: Float, low: Float) {
        this.isVisible = visible
        this.high = high.toDouble()
        this.low = low.toDouble()
    }

    // ---------------------------------------------

}
