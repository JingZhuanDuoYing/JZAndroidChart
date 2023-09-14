package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart.animation.ChartAnimator
import cn.jingzhuan.lib.chart.animation.Easing.EasingFunction
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.event.OnHighlightListener
import cn.jingzhuan.lib.chart3.renderer.AbstractRenderer
import cn.jingzhuan.lib.chart3.renderer.HighlightRenderer
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_PRESS
import cn.jingzhuan.lib.chart3.utils.ChartConstant.TYPE_AXIS_BOTTOM

/**
 * @since 2023-09-05
 * created by lei
 */
open class BaseChartView<T : AbstractDataSet<*>> : AbstractChartView<T> {

    protected var chartRenderer: AbstractRenderer<T>? = null

    private val waterMarkPaint = Paint()

    private lateinit var animator: ChartAnimator

    private var highlightListener: OnHighlightListener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initChart() {
        animator = ChartAnimator { postInvalidate() }
        highlightRenderer = HighlightRenderer(this)

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

    /**
     * 画十字交叉线
     */
    override fun drawHighlight(canvas: Canvas) {
        highlightRenderer.renderer(canvas)
    }

    override fun highlightValue(highlight: Highlight?) {
        if (highlight != null) {
            if (highlightState == HIGHLIGHT_STATUS_INITIAL && !isStatic) {
                highlightState = HIGHLIGHT_STATUS_PRESS
            }
            highlightRenderer.highlightValue(highlight)
            if (highlightListener != null) {
                highlightListener?.onHighlightShow(highlight)
            }
            invalidate()
        }
    }

    override fun getEntryIndex(x: Float): Int {
        return chartRenderer?.getEntryIndex(x) ?: -1
    }

    override fun getEntryX(index: Int): Float {
        return chartRenderer?.getEntryX(index) ?: -1f
    }

    override fun onHighlightClean() {
        highlightState = HIGHLIGHT_STATUS_INITIAL
        highlightRenderer.cleanHighlight()
        if (highlightListener != null){
            highlightListener?.onHighlightHide()
        }
        focusIndex = -1
        invalidate()
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
}
