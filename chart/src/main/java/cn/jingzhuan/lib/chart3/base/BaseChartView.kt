package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart.animation.Easing.EasingFunction
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.event.OnHighlightListener
import cn.jingzhuan.lib.chart3.renderer.AbstractRenderer
import cn.jingzhuan.lib.chart3.renderer.DrawLineRenderer
import cn.jingzhuan.lib.chart3.renderer.HighlightRenderer
import cn.jingzhuan.lib.chart3.renderer.RangeRenderer
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_PRESS
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_BOTTOM
import kotlin.math.max

/**
 * @since 2023-09-05
 * created by lei
 */
open class BaseChartView<T : AbstractDataSet<*>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractChartView<T>(context, attrs, defStyleAttr) {

    protected var chartRenderer: AbstractRenderer<T>? = null

    private val waterMarkPaint = Paint()

    private lateinit var animator: ChartAnimator

    private var highlightListener: OnHighlightListener? = null

    override fun initChart() {
        animator = ChartAnimator { postInvalidate() }
        highlightRenderer = HighlightRenderer(this)
        rangeRenderer = RangeRenderer(this)
        drawLineRenderer = DrawLineRenderer(this)
    }

    fun setRenderer(chartRenderer: AbstractRenderer<T>?) {
        this.chartRenderer = chartRenderer
    }

    /**
     * 画内容
     */
    override fun drawChart(canvas: Canvas) {
        chartRenderer?.renderer(canvas)
    }

    /**
     * 画水印
     */
    override fun drawWaterMark(canvas: Canvas) {
        val padding = resources.getDimensionPixelSize(R.dimen.jz_chart_water_mark_padding)
        val waterMarkBitmap = BitmapFactory.decodeResource(
            this.resources,
            if (isNightMode) R.drawable.ico_water_mark_night else R.drawable.ico_water_mark
        )
        val left = width - padding - waterMarkBitmap.width - paddingRight
        canvas.drawBitmap(waterMarkBitmap, left.toFloat(), padding.toFloat(), waterMarkPaint)
    }

    /**
     * 画坐标轴
     */
    override fun drawAxis(canvas: Canvas) {
        axisRenderers.values.forEach {
            it.renderer(canvas)
        }
    }

    /**
     * 画坐标轴文本 (左、右、上 如果配置了)
     */
    override fun drawAxisLabels(canvas: Canvas) {
        axisRenderers.forEach {
            if (it.key != TYPE_AXIS_BOTTOM) {
                it.value.drawLabels(canvas)
            }
        }
    }

    /**
     * 画坐标轴底部文本
     */
    override fun drawBottomLabels(canvas: Canvas) {
        axisRenderers.forEach {
            if (it.key == TYPE_AXIS_BOTTOM) {
                it.value.drawLabels(canvas)
                return
            }
        }
    }

    /**
     * 画网格线
     */
    override fun drawGridLine(canvas: Canvas) {
        axisRenderers.forEach {
            val axis = it.value.axis
            val enable = axis.isEnable && axis.isGridLineEnable && axis.gridCount > 0
            if (enable) it.value.drawGridLines(canvas)
        }
    }

    fun setHighlight(highlight: Highlight?) {
        if (highlight != null) {
            highlightRenderer.highlightValue(highlight)
            postInvalidate()
        }
    }

    /**
     * 画十字交叉线
     */
    override fun drawHighlight(canvas: Canvas) {
        highlightRenderer.renderer(canvas)
    }

    override fun highlightValue(highlight: Highlight?, enableCallback: Boolean) {
        if (highlight != null) {
            if (highlightState == HIGHLIGHT_STATUS_INITIAL && !isStatic) {
                highlightState = HIGHLIGHT_STATUS_PRESS
            }
            highlightRenderer.highlightValue(highlight)
            if (highlightListener != null && enableCallback) {
                highlightListener?.onHighlightShow(highlight)
            }
            postInvalidate()
        }
    }

    fun handleLoadMoreIndex(dataSize: Int) {
        if (highlightState == HIGHLIGHT_STATUS_FOREVER) {
            val highlight = highlightRenderer.highlight ?: return
            highlight.dataIndex += dataSize
            highlight.x = getEntryX(highlight.dataIndex)

            if (highlight.x > contentRect.right - pointWidth * 0.5f) {
                highlight.x = contentRect.right - pointWidth * 0.5f
            }
            highlightRenderer.highlightValue(highlight)
        }
    }

    /**
     * 光标位置更新 正常来说光标的下标不变 滑动时光标的x是变化的，当到达边界时，光标的X是不变的 以边界的x位准 算出下标值
     */
    override fun onHighlightForever() {
        val highlight = highlightRenderer.highlightForever()
        if (highlight != null) {
            if (highlightListener != null) {
                highlightListener?.onHighlightShow(highlight)
            }
//            postInvalidate()
        }
    }

    override fun onHighlightClean() {
        if (highlightRenderer.highlight == null) return
        cleanHighlight()
        invalidate()
    }

    fun cleanHighlight() {
        highlightState = HIGHLIGHT_STATUS_INITIAL
        if (highlightRenderer.highlight == null) return
        highlightRenderer.cleanHighlight()
        if (highlightListener != null){
            highlightListener?.onHighlightHide()
        }
        focusIndex = -1
    }

    protected fun getCurrentHighlight(): Highlight? = highlightRenderer.highlight

    /**
     * 画区间统计
     */
    override fun drawRangeArea(canvas: Canvas) {
        if (isOpenRange) {
            rangeRenderer.renderer(canvas)
        }
    }

    /**
     * 打开区间统计
     */
    override fun openRange() {
        if (isOpenRange) return
        if (highlightState == HIGHLIGHT_STATUS_INITIAL) {
            // 光标未显示 画当前区域内
            rangeRenderer.setRange(null)
        } else {
            val highlight = highlightRenderer.highlight ?: return
            rangeRenderer.setRange(highlight.x)
            onHighlightClean()
        }
        isOpenRange = true
        invalidate()
    }

    /**
     * 打开区间统计
     * [startIndex] - 开始位置
     * [endIndex] - 结束位置
     */
    override fun setIntervalRange(startIndex: Int, endIndex: Int) {
        if (!isOpenRange) return
        rangeRenderer.setIntervalRange(startIndex, endIndex)
        invalidate()
    }

    override fun closeRange() {
        cleanRange()
        invalidate()
    }

    fun cleanRange() {
        isOpenRange = false
        rangeRenderer.cleanRange()
        if (rangeChangeListener != null){
            rangeChangeListener?.onClose()
        }
    }

    override fun onRangeViewPortChange() {
        rangeRenderer.onViewportChange()
    }

    /**
     * 画线工具
     */
    override fun drawLineTool(canvas: Canvas) {
        drawLineRenderer.renderer(canvas)
    }

    override fun getEntryIndex(x: Float): Int {
        return chartRenderer?.getEntryIndex(x) ?: -1
    }

    override fun getEntryX(index: Int): Float {
        return chartRenderer?.getEntryX(index) ?: -1f
    }

    open fun setOnHighlightListener(listener: OnHighlightListener) {
        this.highlightListener = listener
    }

    override val renderPaint: Paint?
        get() = chartRenderer?.renderPaint

    override val chartData: ChartData<T>
        get() = chartRenderer?.getChartData() ?: ChartData()

    fun animateX(durationMillis: Int) {
        animator.animateX(durationMillis)
    }

    fun animateX(durationMillis: Int, easing: EasingFunction?) {
        animator.animateX(durationMillis, easing)
    }

    fun animateY(durationMillis: Int) {
        animator.animateY(durationMillis)
    }

    fun animateY(durationMillis: Int, easing: EasingFunction?) {
        animator.animateY(durationMillis, easing)
    }

    fun animateXY(durationMillisX: Int, durationMillisY: Int) {
        animator.animateXY(durationMillisX, durationMillisY)
    }

    fun animateXY(durationMillisX: Int, durationMillisY: Int, easing: EasingFunction?) {
        animator.animateXY(durationMillisX, durationMillisY, easing)
    }

    fun animateXY(
        durationMillisX: Int, durationMillisY: Int, easingX: EasingFunction?,
        easingY: EasingFunction?,
    ) {
        animator.animateXY(durationMillisX, durationMillisY, easingX, easingY)
    }

    override fun getChartAnimator(): ChartAnimator? {
        return animator
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)
        chartRenderer?.calcDataSetMinMax()
        setEntryWidth()
    }

    override fun setEntryWidth() {
        val dataSet = chartData.getTouchDataSet()
        if (dataSet != null && contentRect.width() != 0) {
            val visibleRange = dataSet.getVisibleRange(currentViewport)
            val minValueCount = if (currentVisibleEntryCount == -1) dataSet.minValueCount.toFloat() else currentVisibleEntryCount.toFloat()
            val width = (contentRect.width() / max(visibleRange, minValueCount))
            pointWidth = width
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isOpenRange && isTouchRangeEnable) {
            if (rangeRenderer.onTouchEvent(event)) {
                return true
            }
        }

        if (isOpenDrawLine) {
            if (drawLineRenderer.onTouchEvent(event)) {
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun isDrawingLine(): Boolean {
        return super.isDrawingLine() && drawLineRenderer.checkIfHaveDrawing()
    }

    fun getHighlightTextPaint() = highlightRenderer.getTextPaint()

    fun setRangeTouchBitmap(leftBitmap: Bitmap, rightBitmap: Bitmap) {
        rangeRenderer.leftTouchBitmap = leftBitmap
        rangeRenderer.rightTouchBitmap = rightBitmap
    }

    fun setShowRangeCloseButton(isShow: Boolean) {
        rangeRenderer.showRangeCloseButton = isShow
    }

    fun setShowRangeLine(isShow: Boolean) {
        rangeRenderer.showRangeLine = isShow
    }

    fun setRangeHedgeWhole(hedgeWhole: Boolean) {
        rangeRenderer.isRangeHedgeWhole = hedgeWhole
    }

    fun setRangeMaxDiffEntry(max: Int) {
        rangeRenderer.maxDiffEntry = max
    }
}
