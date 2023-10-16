package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import cn.jingzhuan.lib.chart3.drawline.DrawLineType
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.max

/**
 * @since 2023-10-09
 * created by lei
 */
class DrawLineDataSet@JvmOverloads constructor(
    drawLineValues: List<DrawLineValue> = emptyList(),
    @AxisY.AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH,
    drawLineType: DrawLineType = DrawLineType.ltNone
) : AbstractDataSet<DrawLineValue>(drawLineValues, axisDependency) {

    /**
     * 自绘线标记
     */
    var lineKey: String? = null

    /**
     * 自绘线类型
     */
    var lineType = 0

    /**
     * 自绘线颜色
     */
    var lineColor = Color.RED

    /**
     * 自绘线宽度
     */
    var lineSize = 3f

    /**
     * 字体大小
     */
    var fontSize = 0

    /**
     * 字体类型
     */
    var fontName: String? = null

    /**
     * 文本内容
     */
    var text: String? = null

    /**
     * 线形
     */
    var dash: String? = null

    /**
     * 是否除权
     */
    var bcap: String? = null

    /**
     * 瞄点起点
     */
    var startDrawValue: DrawLineValue? = null

    /**
     * 瞄点终点
     */
    var endDrawValue: DrawLineValue? = null

    var pointRadiusIn = 8f

    var pointRadiusOut = 16f

    var lineState = DrawLineState.complete

    /**
     * 是否选中
     */
    var isSelect = false

    /**
     * 两点之间X距离 当且仅当两个点的时候适用
     */
    var distanceX = 0f

    /**
     * 两点之间Y距离 当且仅当两个点的时候适用
     */
    var distanceY = 0f


    var leftCrossValue = -1f

    var rightCrossValue = -1f

    override fun removeEntry(value: DrawLineValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)
        return values.remove(value)
    }

    override fun addEntry(value: DrawLineValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)

        return values.add(value)
    }

    override fun getEntryCount(): Int {
        return max(minValueCount, values.size)
    }

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        viewportYMax = -Float.MAX_VALUE
        viewportYMin = Float.MAX_VALUE

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (i in visiblePoints.indices) {
            val e = visiblePoints[i]
            calcViewportMinMax(e)
        }

        val range = viewportYMax - viewportYMin
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMin -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMax += range * maxValueOffsetPercent
        }

    }

    private fun calcViewportMinMax(e: DrawLineValue?) {
        if (e == null || !e.isVisible) return
        if (isNaN(e.value)) return
        if (isInfinite(e.value)) return
        if (e.value < viewportYMin) {
            viewportYMin = e.value
        }
        if (e.value > viewportYMax) {
            viewportYMax = e.value
        }
    }

}