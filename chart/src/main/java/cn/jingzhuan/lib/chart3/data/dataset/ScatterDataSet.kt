package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Rect
import android.graphics.drawable.Drawable
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart.renderer.TextValueRenderer
import cn.jingzhuan.lib.chart3.data.value.ScatterValue
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_CENTER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_BOTTOM
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_PARENT_TOP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.SHAPE_ALIGN_TOP
import java.lang.Float.isNaN
import java.util.Collections
import kotlin.math.max
import kotlin.math.min

/**
 * @since 2023-09-05
 * created by lei
 */
open class ScatterDataSet(scatterValues: List<ScatterValue>) :
    AbstractDataSet<ScatterValue>(scatterValues) {

    var shape: Drawable? = null

    /**
     * 正数向上扩展，负数向下扩展，0上下扩展一半
     */
    var shapeLevel = 0

    var drawOffsetX = 0f

    var drawOffsetY = 0f

    var shapeMinWidth = 0f

    var shapeMaxWidth = Float.NaN

    var shapeAlign: Int = SHAPE_ALIGN_CENTER

    private var ORIGINAL_VIEWPORT_Y_MIN = Float.MAX_VALUE

    private var ORIGINAL_VIEWPORT_Y_MAX = -Float.MAX_VALUE

    var isAutoWidth = true

    var isAutoExpand = true

    private var mTextValueRenderers: MutableList<TextValueRenderer>? = null

    var forceValueCount = -1

    override fun calcMinMax(viewport: Viewport, content: Rect, max: Float, mix: Float) {
        viewportYMax = max
        viewportYMin = mix

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        for (value in visiblePoints) {
            calcViewportMinMax(value)
        }

        ORIGINAL_VIEWPORT_Y_MAX = viewportYMax
        ORIGINAL_VIEWPORT_Y_MIN = viewportYMin
        if (isAutoExpand) {
            for (value in visiblePoints) {
                calcViewportMinMaxExpansion(value, viewport, content)
            }
        }

        val range: Float = viewportYMax - viewportYMin
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMin -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            viewportYMax += range * maxValueOffsetPercent
        }
    }

    private fun calcViewportMinMax(value: ScatterValue?) {
        if (value == null || !value.isVisible) return
        if (isNaN(value.value)) return
        if (value.value < viewportYMin) {
            viewportYMin = value.value
        }
        if (value.value > viewportYMax) {
            viewportYMax = value.value
        }
    }

    private fun calcViewportMinMaxExpansion(value: ScatterValue?, viewport: Viewport, content: Rect) {
        if (value == null || !value.isVisible) return
        if (isNaN(value.value)) return
        if (shape == null) return

        val range: Float = viewportYMax - viewportYMin
        if (range <= 0f) return

        val width = (content.width() - startXOffset - endXOffset) / getVisibleRange(viewport) + 1
        var shapeHeight = shape!!.intrinsicHeight.toFloat()
        if (isAutoWidth) {
            var shapeWidth = max(width * 0.8f, shapeMinWidth)
            if (!isNaN(shapeMaxWidth)) {
                shapeWidth = min(shapeWidth, shapeMaxWidth)
            }
            shapeHeight *= (shapeWidth / shape!!.intrinsicWidth.toFloat())
            shapeHeight += 4f
        }
        val percent = shapeHeight / content.height().toFloat()
        val expand = range * percent
        if (expand <= 0f) return
        val anchor: Float = if (shapeAlign == SHAPE_ALIGN_PARENT_TOP) {
            ORIGINAL_VIEWPORT_Y_MAX
        } else if (shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {
            ORIGINAL_VIEWPORT_Y_MIN
        } else {
            value.value
        }
        val newValue: Float
        if (shapeAlign == SHAPE_ALIGN_BOTTOM || shapeAlign == SHAPE_ALIGN_PARENT_TOP) {
            newValue = anchor + expand
            viewportYMax = max(newValue, viewportYMax)
        } else if (shapeAlign == SHAPE_ALIGN_TOP || shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {
            newValue = anchor - expand
            viewportYMin = min(newValue, viewportYMin)
        } else { // shapeAlign == SHAPE_ALIGN_CENTER
            val newMaxValue = anchor + expand / 2
            val newMinValue = anchor - expand / 2
            viewportYMax = max(newMaxValue, viewportYMax)
            viewportYMin = min(newMinValue, viewportYMin)
        }
    }

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else values.size
    }

    override fun addEntry(value: ScatterValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)
        return values.add(value)
    }

    override fun removeEntry(value: ScatterValue?): Boolean {
        if (value == null) return false
        calcViewportMinMax(value)
        return values.remove(value)
    }

    fun addTextValueRenderer(textValueRenderer: TextValueRenderer) {
        if (mTextValueRenderers == null) {
            mTextValueRenderers = Collections.synchronizedList(ArrayList())
        }
        mTextValueRenderers!!.add(textValueRenderer)
    }

    val textValueRenderers: List<TextValueRenderer>?
        get() = mTextValueRenderers
}

