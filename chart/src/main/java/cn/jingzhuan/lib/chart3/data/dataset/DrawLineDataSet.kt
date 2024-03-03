package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Color
import android.graphics.Region
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.axis.AxisY
import cn.jingzhuan.lib.chart3.data.value.DrawLineValue
import cn.jingzhuan.lib.chart3.drawline.DrawLineState
import java.lang.Float.isInfinite
import java.lang.Float.isNaN
import kotlin.math.max

/**
 * @since 2023-10-09
 * created by lei
 */
class DrawLineDataSet@JvmOverloads constructor(
    drawLineValues: List<DrawLineValue> = emptyList(),
    @AxisY.AxisDependency axisDependency: Int = AxisY.DEPENDENCY_BOTH
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
    var lineSize = 2f

    /**
     * 字体大小
     */
    var fontSize = 24

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
    var bcap: Int? = null

    /**
     * 瞄点起点
     */
    var startDrawValue: DrawLineValue? = null

    /**
     * 瞄点终点
     */
    var endDrawValue: DrawLineValue? = null

    /**
     * 瞄点第三个点
     */
    var thirdDrawValue: DrawLineValue? = null

    /**
     * 点 内圆半径
     */
    var pointInnerR = 8f

    /**
     * 点 外圆半径
     */
    var pointOuterR = 16f

    /**
     * 画线步骤
     */
    var lineState = DrawLineState.complete

    /**
     * 选中背景的透明度 默认10%
     */
    var selectAlpha = 25

    /**
     * 选中的区域  用于判断点击的点是否在此区域中
     */
    var selectRegion: Region? = null

    /**
     * 平行点选中的区域  用于判断点击的点是否在此区域中
     */
    var parallelSelectRegion: Region? = null

    /**
     * 选中的区域  用于判断点击的点是否在此集合任一区域中
     */
    var selectRegions = ArrayList<Region?>()

    /**
     * 是否选中
     */
    var isSelect = false

    /**
     * 是否抬起
     */
    var isActionUp = true

    /**
     * 周期
     */
    var cycle: Int = 8

    /**
     * 历史时间 分时要显示十日内的线
     */
    var historyTimeList: List<Long> = emptyList()

    /**
     * 操作类型 暂定 1-添加 2-更新 3-删除
     */
    var operateType: Int = -1

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