package cn.jingzhuan.lib.chart3.base

import android.content.Context
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
import cn.jingzhuan.lib.chart3.renderer.AbstractRenderer
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL

/**
 * @since 2023-09-05
 * created by lei
 */
open class BaseChartView<T : AbstractDataSet<*>> : AbstractChartView<T> {

    protected var chartRenderer: AbstractRenderer<T>? = null

    private val waterMarkPaint = Paint()

    private lateinit var chartAnimator: ChartAnimator

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initChart() {
        chartAnimator = ChartAnimator { postInvalidate() }
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
        axisLeftRenderer.renderer(canvas)

        axisRightRenderer.renderer(canvas)

        axisTopRenderer.renderer(canvas)

        axisBottomRenderer.renderer(canvas)

    }

    /**
     * 画坐标轴文本 (左、右、上 如果配置了)
     */
    override fun drawAxisLabels(canvas: Canvas) {
        axisLeftRenderer.drawAxisLabels(canvas)

        axisRightRenderer.drawAxisLabels(canvas)

        axisTopRenderer.drawAxisLabels(canvas)

    }

    /**
     * 画坐标轴底部文本
     */
    override fun drawBottomLabels(canvas: Canvas) {
        axisBottomRenderer.drawAxisLabels(canvas)
    }

    /**
     * 画网格线
     */
    override fun drawGridLine(canvas: Canvas) {
        // 左右 只画一次
        val axisLeft = axisLeftRenderer.axis
        val drawLeft = axisLeft.isEnable && axisLeft.isGridLineEnable && axisLeft.gridCount > 0

        val axisRight = axisRightRenderer.axis
        val drawRight = axisRight.isEnable && axisRight.isGridLineEnable && axisRight.gridCount > 0

        if (drawLeft && drawRight) {
            axisLeftRenderer.drawGridLines(canvas)
        } else if (drawLeft) {
            axisLeftRenderer.drawGridLines(canvas)
        } else if (drawRight) {
            axisRightRenderer.drawGridLines(canvas)
        }

        // 上下 只画一次
        val axisTop = axisTopRenderer.axis
        val drawTop = axisTop.isEnable && axisTop.isGridLineEnable && axisTop.gridCount > 0

        val axisBottom = axisBottomRenderer.axis
        val drawBottom = axisBottom.isEnable && axisBottom.isGridLineEnable && axisBottom.gridCount > 0

        if (drawTop && drawBottom) {
            axisTopRenderer.drawGridLines(canvas)
        } else if (drawTop) {
            axisTopRenderer.drawGridLines(canvas)
        } else if (drawBottom) {
            axisBottomRenderer.drawGridLines(canvas)
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
            highlightRenderer.highlightValue(highlight)
            invalidate()
        }
    }

    override fun cleanHighlight() {
        highlightState = HIGHLIGHT_STATUS_INITIAL
        highlightRenderer.cleanHighlight()
        invalidate()
    }

    override fun onTouchPoint(event: MotionEvent) {}

    override val renderPaint: Paint?
        get() = chartRenderer?.renderPaint

    override val chartData: ChartData<T>
        get() = chartRenderer?.chartData ?: ChartData()

    fun animateX(durationMillis: Int) {
        chartAnimator.animateX(durationMillis)
    }

    fun animateX(durationMillis: Int, easing: EasingFunction?) {
        chartAnimator.animateX(durationMillis, easing)
    }

    fun animateY(durationMillis: Int) {
        chartAnimator.animateY(durationMillis)
    }

    fun animateY(durationMillis: Int, easing: EasingFunction?) {
        chartAnimator.animateY(durationMillis, easing)
    }

    fun animateXY(durationMillisX: Int, durationMillisY: Int) {
        chartAnimator.animateXY(durationMillisX, durationMillisY)
    }

    fun animateXY(durationMillisX: Int, durationMillisY: Int, easing: EasingFunction?) {
        chartAnimator.animateXY(durationMillisX, durationMillisY, easing)
    }

    fun animateXY(
        durationMillisX: Int, durationMillisY: Int, easingX: EasingFunction?,
        easingY: EasingFunction?
    ) {
        chartAnimator.animateXY(durationMillisX, durationMillisY, easingX, easingY)
    }
}
