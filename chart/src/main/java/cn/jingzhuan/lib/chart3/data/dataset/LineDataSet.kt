package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Shader
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.LineValue
import java.lang.Float.isInfinite
import java.lang.Float.isNaN

/**
 * @since 2023-09-05
 * created by lei
 */
class LineDataSet @JvmOverloads constructor(
    lineValues: List<LineValue>,
    @AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH
) :
    AbstractDataSet<LineValue>(lineValues, axisDependency) {

    var lineThickness = 2

    var headPoint: LineValue? = null

    var forceValueCount = -1

    var shader: Shader? = null

    var shaderTop: Shader? = null

    var shaderBottom: Shader? = null

    var shaderBaseValue = Float.NaN

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

    /**
     * 点状线
     */
    var isPointLine = false

    var interval = 2f

    var phase = 0f

    var radius = 2f

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else values.size
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
        for (value in getVisiblePoints(viewport)!!) {
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
        if (isNaN(value!!.value) || isInfinite(value.value)) return
        if (value.value < viewportYMin) viewportYMin = value.value
        if (value.value > viewportYMax) viewportYMax = value.value
        if (isDrawBand) {
            if (value.secondValue < value.value) {
                if (value.secondValue < viewportYMin) viewportYMin = value.secondValue
            }
            if (value.secondValue > value.value) {
                if (value.secondValue > viewportYMax) viewportYMax = value.secondValue
            }
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

    fun setShaderBaseValue(shaderBaseValue: Float, shaderTop: Shader?, shaderBottom: Shader?) {
        this.shaderBaseValue = shaderBaseValue
        this.shaderTop = shaderTop
        this.shaderBottom = shaderBottom
    }
}
