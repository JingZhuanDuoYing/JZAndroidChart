package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Shader
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.LineValue

import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-05
 * created by lei
 */
open class LineDataSet @JvmOverloads constructor(
    lineValues: List<LineValue>,
    @AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH
) : AbstractDataSet<LineValue>(lineValues, axisDependency) {

    open var lastClose = -1.0

    var lineThickness = 2

    var headPoint: LineValue? = null

    var shader: Shader? = null

    var shaderTop: Shader? = null

    var shaderBottom: Shader? = null

    var shaderBaseValue = Double.NaN

    var isLineVisible = true

    /**
     * 线段
     */
    var isPartLine = false

    /**
     * 带状线
     */
    var isDrawBand = false

    /**
     * 单值水平线
     */
    var isHorizontalLine = false

    var horizontalLeft = false

    /**
     * 点状线
     */
    var isPointLine = false

    var interval = 2f

    var phase = 0f

    var radius = 2f

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else max(minValueCount, values.size)
    }

    override fun calcMinMax(viewport: Viewport) {
        if (values.isEmpty()) return
        calcViewportY(viewport)
    }

    private fun calcViewportY(viewport: Viewport) {
        maxVisibleY = -Float.MAX_VALUE
        minVisibleY = Float.MAX_VALUE

        if (headPoint != null) {
            calcViewportMinMax(headPoint)
        }

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcViewportMinMax(value)
        }

        val range = maxVisibleY - minVisibleY
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            minVisibleY -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            maxVisibleY += range * maxValueOffsetPercent
        }
        if (maxVisibleY == 0f && minVisibleY == 0f) {
            maxVisibleY = 0.01f
            minVisibleY = -0.01f
        }
    }

    private fun calcViewportMinMax(value: LineValue?) {
        if (value == null || value.value.isNaN() || value.value.isInfinite()) return
        minVisibleY = min(value.value.toFloat(), minVisibleY)
        maxVisibleY = max(value.value.toFloat(), maxVisibleY)
//        if (value.value < minVisibleY) minVisibleY = value.value
//        if (value.value > maxVisibleY) maxVisibleY = value.value
        if (isDrawBand) {
            minVisibleY = min(value.secondValue.toFloat(), minVisibleY)
            maxVisibleY = max(value.secondValue.toFloat(), maxVisibleY)
//            if (value.secondValue < value.value) {
//                if (value.secondValue < minVisibleY) minVisibleY = value.secondValue
//            }
//            if (value.secondValue > value.value) {
//                if (value.secondValue > maxVisibleY) maxVisibleY = value.secondValue
//            }
        }
    }

    override fun addEntry(value: LineValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)

        // add the entry
        return values.add(value)
    }

    override fun removeEntry(value: LineValue?): Boolean {
        return if (value == null) false else values.remove(value)

        // remove the entry
    }

    val lines: List<LineValue>
        get() = values

    fun setShaderBaseValue(shaderBaseValue: Double, shaderTop: Shader?, shaderBottom: Shader?) {
        this.shaderBaseValue = shaderBaseValue
        this.shaderTop = shaderTop
        this.shaderBottom = shaderBottom
    }
}
