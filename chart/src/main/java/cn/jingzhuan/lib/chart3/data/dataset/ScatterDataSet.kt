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

    var isAutoWidth = true

    var isAutoExpand = true

    /**
     * 是否自动转向 只支持 SHAPE_ALIGN_TOP、SHAPE_ALIGN_BOTTOM
     */
    var isAutoTurn = false

    /**
     * 自动转向指示器图标 仅当 [isAutoTurn] = true 有效
     */
    var autoTurnPointShape: Drawable? = null

    var autoTurnShapePadding: Int = 3

    private var mTextValueRenderers: MutableList<TextValueRenderer>? = null

    fun calcMinMaxInner(
        viewport: Viewport,
        content: Rect,
        max: Float,
        min: Float,
        originYMax: Float,
        originYMin: Float,
        offsetsMapper: MutableMap<String, MutableMap<String, Float>>
    ) {
        maxVisibleY = max
        minVisibleY = min

        val visiblePoints = getVisiblePoints(viewport)
        if (visiblePoints.isNullOrEmpty()) return

        if (isAutoExpand) {
            for (index in 0 until visiblePoints.size) {
                val value = visiblePoints[index]
                calcViewportMinMaxExpansion(index, value, content, originYMax, originYMin, offsetsMapper)
            }
        } else {
            for (value in visiblePoints) {
                calcViewportMinMax(value)
            }
        }

        val range: Float = maxVisibleY - minVisibleY
        if (minValueOffsetPercent.compareTo(0f) > 0f) {
            minVisibleY -= range * minValueOffsetPercent
        }
        if (maxValueOffsetPercent.compareTo(0f) > 0f) {
            maxVisibleY += range * maxValueOffsetPercent
        }
    }

    private fun calcViewportMinMax(value: ScatterValue?) {
        if (value == null || !value.isVisible) return
        if (value.value.isNaN()) return
        if (value.value < minVisibleY) {
            minVisibleY = value.value.toFloat()
        }
        if (value.value > maxVisibleY) {
            maxVisibleY = value.value.toFloat()
        }
    }

    private fun calcViewportMinMaxExpansion(
        index: Int,
        value: ScatterValue?,
        content: Rect,
        originYMax: Float,
        originYMin: Float,
        offsetsMapper: MutableMap<String, MutableMap<String, Float>>,
    ) {
        if (value == null || !value.isVisible) return
        if (value.value.isNaN()) return
        if (shape == null) return

        val originRange = originYMax - originYMin
        if (originRange <= 0.0) return

        val shapeHeight = shape!!.intrinsicHeight.toFloat()
        if (shapeHeight <= 0.0) return

        var offset = 0f
        if (shapeAlign != SHAPE_ALIGN_CENTER) {
            val indexKey = index.toString()
            val offsets = offsetsMapper[this.shapeAlign.toString()] ?: mutableMapOf<String, Float>()
            if (offsets.contains(indexKey)) {
                offset = offsets[indexKey] ?: 0f
            }
            if (!value.value.isNaN()) {
                offsets[indexKey] = offset + shapeHeight
                offsetsMapper[this.shapeAlign.toString()] = offsets
            }
        }

        val percent = (offset + shapeHeight) / content.height()
        val expandHeight = originRange * percent
        if (expandHeight <= 0.0) return

        val anchor: Float = when (this.shapeAlign) {
            SHAPE_ALIGN_PARENT_TOP -> {
                originYMax;
            }
            SHAPE_ALIGN_PARENT_BOTTOM -> {
                originYMin
            }
            else -> {
                value.value.toFloat()
            }
        }

        when (this.shapeAlign) {
            SHAPE_ALIGN_PARENT_TOP -> {
                maxVisibleY = (anchor + expandHeight).coerceAtLeast(maxVisibleY)
            }
            SHAPE_ALIGN_PARENT_BOTTOM -> {
                minVisibleY = (anchor - expandHeight).coerceAtMost(minVisibleY)
            }
            SHAPE_ALIGN_BOTTOM -> {
                val max = (anchor + expandHeight * 2).coerceAtLeast(maxVisibleY)
                val range = max - minVisibleY
                maxVisibleY = (anchor + range * percent).coerceAtLeast(maxVisibleY)
            }
            SHAPE_ALIGN_TOP -> {
                val min = (anchor - expandHeight * 2).coerceAtMost(minVisibleY)
                val range = maxVisibleY - min
                minVisibleY = (anchor - range * percent).coerceAtMost(minVisibleY)
            }
            else -> {
                val newMaxValue = anchor + expandHeight / 2
                val newMinValue = anchor - expandHeight / 2
                maxVisibleY = newMaxValue.coerceAtLeast(maxVisibleY)
                minVisibleY = newMinValue.coerceAtMost(minVisibleY)
            }
        }
    }

    override fun getEntryCount(): Int {
        return if (forceValueCount > 0) forceValueCount else max(minValueCount, values.size)
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

