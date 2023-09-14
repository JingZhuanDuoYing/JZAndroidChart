package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import android.graphics.Rect
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.axis.AxisY.AxisDependency
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import cn.jingzhuan.lib.chart3.formatter.IValueFormatter
import kotlin.math.roundToInt

/**
 * @since 2023-09-05
 * created by lei
 */
abstract class AbstractDataSet<T : AbstractValue> : IDataSet {

    var values: MutableList<T> = ArrayList()

    /**
     * 是否显示
     */
    var isVisible = true

    var enable = true

    var viewportYMin = Float.MAX_VALUE

    var viewportYMax = -Float.MAX_VALUE

    var minValueOffsetPercent = 0f

    var maxValueOffsetPercent = 0f

    var startXOffset = 0f

    var endXOffset = 0f

    @get:AxisDependency
    var axisDependency = AxisY.DEPENDENCY_LEFT

    var color = Color.GRAY

    var colorDynamic = false

    /**
     * 当前绘制索引
     */
    var drawIndex = -1

    var minValueCount = -1

    /**
     * 当前Dataset的标签
     */
    var tag: String? = null

    /**
     * 数据格式化器
     */
    var formatter: IValueFormatter? = null

    var maxIndex = 0

    var minIndex = 0

    var forceValueCount = -1

    constructor()

    constructor(tag: String?) {
        this.tag = tag
    }

    constructor(values: List<T>) {
        this.values = values.toMutableList()
    }

    constructor(values: List<T>, @AxisDependency axisDependency: Int) {
        this.values = values.toMutableList()
        this.axisDependency = axisDependency
    }

    override fun calcMinMax(viewport: Viewport) {}

    override fun calcMinMax(viewport: Viewport, content: Rect, max: Float, min: Float) {
        calcMinMax(viewport)
    }

    abstract fun addEntry(value: T?): Boolean

    abstract fun removeEntry(value: T?): Boolean

    fun getEntryIndex(value: T): Int {
        return values.indexOf(value)
    }

    fun getEntryForIndex(index: Int): T? {
        return values.getOrNull(index)
    }

    fun getVisiblePoints(viewport: Viewport): List<T>? {
        val allValue = ArrayList(values)
        val listSize = allValue.size
        val from = (viewport.left * listSize).roundToInt()
        val to = (viewport.right * listSize).roundToInt()
        return safeSubList(allValue, from, to)
    }

    private fun safeSubList(list: List<T>?, from: Int, to: Int): List<T>? {
        if (list.isNullOrEmpty()) return list
        val size = list.size
        val safeFrom = if (from >= size) 0 else from
        val safeTo = to.coerceAtMost(size)
        return list.subList(safeFrom, safeTo)
    }

    fun getVisibleRange(viewport: Viewport): Float {
        return (viewport.right - viewport.left) * getEntryCount()
    }
}
