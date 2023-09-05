package cn.jingzhuan.lib.chart3.data.value

import android.graphics.Paint
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import java.util.Objects

/**
 * @since 2023-09-05
 * created by lei
 */
class CandlestickValue : AbstractValue {
    var high: Float
    var low: Float
    var open: Float
    var close: Float
    var time: Long = -1
    var isVisible = true
    var paintStyle: Paint.Style? = null
    var color = ChartConstant.COLOR_NONE
    var fillBackgroundColor = ChartConstant.COLOR_NONE

    constructor(high: Float, low: Float, open: Float, close: Float) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
    }

    constructor(high: Float, low: Float, open: Float, close: Float, time: Long) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
        this.time = time
    }

    constructor(high: Float, low: Float, open: Float, close: Float, color: Int) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
        this.color = color
    }

    constructor(high: Float, low: Float, open: Float, close: Float, time: Long, color: Int) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
        this.color = color
        this.time = time
    }

    constructor(
        high: Float,
        low: Float,
        open: Float,
        close: Float,
        mPaintStyle: Paint.Style?,
        color: Int
    ) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
        paintStyle = mPaintStyle
        this.color = color
    }

    constructor(high: Float, low: Float, open: Float, close: Float, mPaintStyle: Paint.Style?) {
        this.high = high
        this.low = low
        this.open = open
        this.close = close
        paintStyle = mPaintStyle
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as CandlestickValue
        return that.high.compareTo(high) == 0
                && that.low.compareTo(low) == 0
                && that.open.compareTo(open) == 0
                && that.close.compareTo(close) == 0
                && time == that.time
                && color == that.color
                && paintStyle == that.paintStyle
    }

    override fun hashCode(): Int {
        return Objects.hash(high, low, open, close, time, paintStyle, color, x, y)
    }
}
