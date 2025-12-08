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
        viewportYMax = -Float.MAX_VALUE
        viewportYMin = Float.MAX_VALUE

        if (headPoint != null) {
            calcViewportMinMax(headPoint)
        }

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcViewportMinMax(value)
        }

        val range = viewportYMax - viewportYMin
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMin -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMax += range * maxValueOffsetPercent
        }
        if (viewportYMax == 0f && viewportYMin == 0f) {
            viewportYMax = 0.01f
            viewportYMin = -0.01f
        }
    }

    private fun calcViewportMinMax(value: LineValue?) {
        if (value == null || value.value.isNaN() || value.value.isInfinite()) return
        viewportYMin = min(value.value.toFloat(), viewportYMin)
        viewportYMax = max(value.value.toFloat(), viewportYMax)
//        if (value.value < viewportYMin) viewportYMin = value.value
//        if (value.value > viewportYMax) viewportYMax = value.value
        if (isDrawBand) {
            viewportYMin = min(value.secondValue.toFloat(), viewportYMin)
            viewportYMax = max(value.secondValue.toFloat(), viewportYMax)
//            if (value.secondValue < value.value) {
//                if (value.secondValue < viewportYMin) viewportYMin = value.secondValue
//            }
//            if (value.secondValue > value.value) {
//                if (value.secondValue > viewportYMax) viewportYMax = value.secondValue
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
