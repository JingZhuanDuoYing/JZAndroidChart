package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart.component.AxisX
import cn.jingzhuan.lib.chart.component.AxisY
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart3.data.ChartData
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue
import cn.jingzhuan.lib.chart3.renderer.AxisRenderer
import cn.jingzhuan.lib.chart3.renderer.HighlightRenderer
import cn.jingzhuan.lib.chart3.state.HighlightState
import java.lang.ref.WeakReference
import kotlin.math.max

/**
 * @since 2023-09-05
 * created by lei
 */
abstract class AbstractChartView<V : AbstractValue, T : AbstractDataSet<V>> : ScrollAndScaleView, IChartView {

    private var mDrawBitmap: WeakReference<Bitmap>? = null

    private var mBitmapConfig = Bitmap.Config.ARGB_8888

    private var bitmapCanvas: Canvas? = null

    lateinit var axisLeftRenderer: AxisRenderer<V, T>

    lateinit var axisRightRenderer: AxisRenderer<V, T>

    lateinit var axisTopRenderer: AxisRenderer<V, T>

    lateinit var axisBottomRenderer: AxisRenderer<V, T>

    protected lateinit var highlightRenderer: HighlightRenderer<V, T>

    var minChartWidth = 0

    var minChartHeight = 0

    /**
     * 背景颜色
     */
    var bgColor = 0

    /**
     * 坐标轴刻度文本 是否画在底层
     */
    var isDrawLabelsInBottom = false

    /**
     * 是否需要展示水印
     */
    var isShowWaterMark = false

    /**
     * 是否黑夜模式
     */
    var isNightMode = false

    // <editor-fold desc="十字光标 配置">    ----------------------------------------------------------
    /**
     * 是否需要展示水平交叉线-默认展示
     */
    var isEnableHorizontalHighlight = true

    /**
     * 是否需要展示垂直交叉线-默认展示
     */
    var isEnableVerticalHighlight = true

    /**
     * 是否需要展示水平交叉线左边文本-默认展示
     */
    var isEnableHighlightLeftText = true

    /**
     * 是否需要展示水平交叉线右边文本-默认不展示
     */
    var isEnableHighlightRightText = false

    /**
     * 是否需要展示垂直交叉线底部文本-默认展示
     */
    var isEnableHighlightBottomText = true

    /**
     * 交叉线文本大小
     */
    var highlightTextSize = 0f

    /**
     * 交叉线文本颜色
     */
    var highlightTextColor = Color.TRANSPARENT

    /**
     * 交叉线文本背景颜色
     */
    var highlightTextBgColor = Color.TRANSPARENT

    /**
     * 交叉线文本背景高度
     */
    var highlightTextBgHeight = 0

    /**
     * 交叉线厚度
     */
    var highlightThickness = 3f

    // </editor-fold desc="十字光标 配置">    ----------------------------------------------------------

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs, defStyleAttr)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr)
    }

    final override fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        super.init(attrs, defStyleAttr)
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.Chart, defStyleAttr, defStyleAttr)
        try {
            val minChartWidth = ta.getDimensionPixelSize(R.styleable.Chart_minChartWidth, 0)
            this.minChartWidth = minChartWidth

            val minChartHeight = ta.getDimensionPixelSize(R.styleable.Chart_minChartHeight, 0)
            this.minChartHeight = minChartHeight

            val backgroundColor = ta.getColor(R.styleable.Chart_backgroundColor, Color.TRANSPARENT)
            this.bgColor = backgroundColor

            val drawLabelsInBottom = ta.getBoolean(R.styleable.Chart_drawLabelsInBottom, false)
            isDrawLabelsInBottom = drawLabelsInBottom

            val showWaterMark = ta.getBoolean(R.styleable.Chart_showWaterMark, false)
            isShowWaterMark = showWaterMark

            val isNightMode = ta.getBoolean(R.styleable.Chart_isNightMode, false)
            this.isNightMode = isNightMode

            initAxisRenderers(ta)
            highlightRenderer = HighlightRenderer(this)
            ta.recycle()
        } catch (e: Exception) {
            ta.recycle()
        }
        initChart()
    }

    private fun initAxisRenderers(ta: TypedArray) {
        val axisLeft = AxisY(AxisY.LEFT_INSIDE)
        axisLeftRenderer = AxisRenderer(this, axisLeft)

        val axisRight = AxisY(AxisY.RIGHT_INSIDE)
        axisRightRenderer = AxisRenderer(this, axisRight)

        val axisTop = AxisX(AxisX.TOP)
        axisTopRenderer = AxisRenderer(this, axisTop)

        val axisBottom = AxisX(AxisX.BOTTOM)
        axisBottomRenderer = AxisRenderer(this, axisBottom)

        val mAxisRenderers: MutableList<AxisRenderer<V, T>> = ArrayList(4)
        mAxisRenderers.add(axisLeftRenderer)
        mAxisRenderers.add(axisRightRenderer)
        mAxisRenderers.add(axisTopRenderer)
        mAxisRenderers.add(axisBottomRenderer)

        val labelTextSize = ta.getDimension(R.styleable.Chart_labelTextSize, 28f)
        val labelSeparation = ta.getDimensionPixelSize(R.styleable.Chart_labelSeparation, 10).toFloat()
        val labelTextColor = ta.getColor(R.styleable.Chart_labelTextColor, Color.GRAY)
        val gridThickness = ta.getDimension(R.styleable.Chart_gridThickness, 2f)
        val axisThickness = ta.getDimension(R.styleable.Chart_axisThickness, 2f)
        val gridColor = ta.getColor(R.styleable.Chart_gridColor, Color.GRAY)
        val axisColor = ta.getColor(R.styleable.Chart_axisColor, Color.GRAY)
        for (axisRenderer in mAxisRenderers) {
            val axis = axisRenderer.axis
            axis.labelTextSize = labelTextSize
            axis.labelTextColor = labelTextColor
            axis.setLabelSeparation(labelSeparation)
            axis.gridColor = gridColor
            axis.gridThickness = gridThickness
            axis.axisColor = axisColor
            axis.axisThickness = axisThickness
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Clips the next few drawing operations to the content area
        val clipRestoreCount = canvas.save()

        // 剪切ContentRect区域
        canvas.clipRect(contentRect)

        // 画背景
        if (bgColor != Color.TRANSPARENT) {
            canvas.drawColor(bgColor)
        }

        // 画水印
        if (isShowWaterMark) {
            drawWaterMark(canvas)
        }

        // 画坐标轴
        drawAxis(canvas)

        // 画网格线
        drawGridLine(canvas)

        // 画坐标轴文本 (左、右、上)
        if (isDrawLabelsInBottom) {
            drawAxisLabels(canvas)
        }

        createBitmapCache()

        if (bitmapCanvas != null && renderPaint != null) {
            drawChart(bitmapCanvas!!)
            canvas.drawBitmap(mDrawBitmap?.get()!!, 0f, 0f, renderPaint)
        }

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount)

        if (!isDrawLabelsInBottom) {
            drawAxisLabels(canvas)
        }

        if (axisBottomRenderer.axis.labelHeight != 0) {
            drawBottomLabels(canvas)
        }

        // 画十字光标
        if (highlightState != HighlightState.Initial) {
            drawHighlight(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        val axisLeft = axisLeftRenderer.axis
        val chartLeft = paddingLeft + if (axisLeft.isInside) 0 else axisLeft.labelWidth

        val axisRight = axisRightRenderer.axis
        val chartRight = width - paddingRight - if (axisRight.isInside) 0 else axisRight.labelWidth

        val axisBottom = axisBottomRenderer.axis
        val contentBottom = height - paddingBottom - axisBottom.labelHeight
        contentRect[chartLeft, paddingTop, chartRight] = contentBottom
        bottomRect[chartLeft, contentBottom, chartRight] = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val axisLeft = axisLeftRenderer.axis
        val width = minChartWidth + paddingLeft + (if (axisLeft.isInside) 0 else axisLeft.labelWidth) + paddingRight

        val axisBottom = axisBottomRenderer.axis
        val height = minChartHeight + (if (axisBottom.isInside) 0 else axisBottom.labelHeight) + paddingBottom

        setMeasuredDimension(
            max(suggestedMinimumWidth, resolveSize(width, widthMeasureSpec)),
            max(suggestedMinimumHeight, resolveSize(height, heightMeasureSpec))
        )
    }

    fun setTypeface(tf: Typeface?) {
        axisLeftRenderer.setTypeface(tf)
        axisLeftRenderer.setTypeface(tf)
        axisTopRenderer.setTypeface(tf)
        axisBottomRenderer.setTypeface(tf)
    }

    private fun createBitmapCache() {
        val width = contentRect.width() + contentRect.left
        val height = contentRect.height()
        if (mDrawBitmap == null || mDrawBitmap?.get() == null || mDrawBitmap?.get()?.width != width || mDrawBitmap?.get()?.height != height) {
            if (width > 0 && height > 0) {
                mDrawBitmap = WeakReference(
                    Bitmap.createBitmap(
                        resources.displayMetrics, width, height, mBitmapConfig
                    )
                )
                bitmapCanvas = Canvas(mDrawBitmap?.get()!!)
            } else return
        }
        mDrawBitmap?.get()?.eraseColor(Color.TRANSPARENT)
    }

    private fun releaseBitmap() {
        if (bitmapCanvas != null) {
            bitmapCanvas?.setBitmap(null)
            bitmapCanvas = null
        }
        if (mDrawBitmap != null) {
            if (mDrawBitmap?.get() != null) mDrawBitmap?.get()?.recycle()
            mDrawBitmap?.clear()
            mDrawBitmap = null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        releaseBitmap()
    }

    abstract val chartData: ChartData<V, T>?

    abstract val renderPaint: Paint?

    /**
     * 十字光标选中
     */
    abstract fun highlightValue(highlight: Highlight?)

    /**
     * 清除十字光标
     */
    abstract fun cleanHighlight()
}
