package cn.jingzhuan.lib.chart3.data.dataset

import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.data.value.ScatterTextValue
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SCATTER_TEXT_ALIGN_TOP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SCATTER_TEXT_HORIZONTAL_CENTER
import kotlin.math.max

/**
 * @since 2023-09-13
 * created by lei
 */
open class ScatterTextDataSet(scatterTextValues: List<ScatterTextValue>) :
    AbstractDataSet<ScatterTextValue>(scatterTextValues) {

    var lineThickness = 2

    var lineColor = 0

    var frameColor = 0

    var text: String? = null

    var textBgColor = 0

    var textColor = 0

    var textSize = 11

    var textPadding = 10

    var lineDashHeight = 40

    var align = SCATTER_TEXT_ALIGN_TOP

    var horizontalAlignment = SCATTER_TEXT_HORIZONTAL_CENTER

    /**
     * 安全空间
     */
    var safeSpace = 2

    /**
     * 是否画圆形背景
     */
    var isCircle = false

    override fun addEntry(value: ScatterTextValue?): Boolean {
        if (value == null) return false
        return values.add(value)
    }

    override fun removeEntry(value: ScatterTextValue?): Boolean {
        if (value == null) return false
        return values.remove(value)
    }

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else max(minValueCount, values.size)
    }

    override fun calcMinMax(viewport: Viewport) {
        viewportYMax = -Float.MAX_VALUE
        viewportYMin = Float.MAX_VALUE

        val list = getVisiblePoints(viewport) ?: return

        if (list.size == 1) {
            viewportYMin = list[0].low.toFloat()
            viewportYMax = list[0].high.toFloat()
            val range: Float = viewportYMax - viewportYMin
            viewportYMin -= range * 0.2f
            return
        }
    }

}

