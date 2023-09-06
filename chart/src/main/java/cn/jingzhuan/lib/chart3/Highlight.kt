package cn.jingzhuan.lib.chart3

/**
 * @since 2023-09-06
 */
class Highlight {
    var x = Float.NaN
    var y = Float.NaN
    var dataIndex = 0
    var touchX = Float.NaN
    var touchY = Float.NaN

    constructor()
    constructor(x: Float, y: Float, dataIndex: Int) {
        this.x = x
        this.y = y
        this.dataIndex = dataIndex
    }
}