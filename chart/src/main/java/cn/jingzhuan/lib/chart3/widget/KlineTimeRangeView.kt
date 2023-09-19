package cn.jingzhuan.lib.chart3.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.data.TimeRange
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_LEFT
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_NONE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_RIGHT
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class KlineTimeRangeView : View {
    /**
     * 字体颜色
     */
    var textColor = 0

    /**
     * 字体大小
     */
    var textSize = 0

    /**
     * 字体背景颜色
     */
    var textBackgroundColor = 0

    /**
     * 字体背景圆角
     */
    var textBackgroundRadius = 0

    /**
     * 分割线颜色
     */
    var lineColor = 0

    /**
     * 分割线宽
     */
    var lineThickness = 2

    private lateinit var bgPaint: Paint

    private lateinit var textPaint: Paint

    private lateinit var linePaint: Paint

    private lateinit var leftRect: RectF

    private lateinit var rightRect: RectF

    private lateinit var centerRect: RectF

    var timeRange: TimeRange? = null
        set(data) {
            if(data == null) return
            val diff = field != data
            field = data
            if (!diff) return
            postInvalidate()
        }

    constructor(context: Context?) : super(context) {
        init(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.KlineTimeRangeView, defStyleAttr, defStyleAttr)

        val textColor = ta.getColor(R.styleable.KlineTimeRangeView_textColor, Color.TRANSPARENT)
        this.textColor = textColor

        val textSize = ta.getDimensionPixelSize(R.styleable.KlineTimeRangeView_textSize, 28)
        this.textSize = textSize

        val textBackgroundColor = ta.getColor(R.styleable.KlineTimeRangeView_textBackgroundColor, Color.TRANSPARENT)
        this.textBackgroundColor = textBackgroundColor

        val textBackgroundRadius = ta.getDimensionPixelSize(R.styleable.KlineTimeRangeView_textBackgroundRadius, 0)
        this.textBackgroundRadius = textBackgroundRadius

        val lineColor = ta.getColor(R.styleable.KlineTimeRangeView_lineColor, Color.TRANSPARENT)
        this.lineColor = lineColor

        val lineThickness = ta.getDimensionPixelSize(R.styleable.KlineTimeRangeView_lineThickness, 2)
        this.lineThickness = lineThickness

        initPaints()

        initRect()
    }

    private fun initRect() {
        leftRect = RectF()
        rightRect = RectF()
        centerRect = RectF()
    }

    private fun initPaints() {
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.style = Paint.Style.FILL_AND_STROKE
        bgPaint.color = lineColor
        bgPaint.strokeWidth = lineThickness.toFloat()

        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.textSize = textSize.toFloat()
        textPaint.color = textColor
        textPaint.textAlign = Paint.Align.CENTER

        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.style = Paint.Style.FILL
        linePaint.color = lineColor
        linePaint.strokeWidth = lineThickness.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (timeRange == null) return
        val padding = resources.getDimensionPixelSize(R.dimen.jz_range_time_text_padding)

        val touchType = timeRange?.touchType ?: RANGE_TOUCH_NONE

        val startTime = timeRange?.startTime ?: ""
        val leftTextWidth = textPaint.measureText(startTime) + padding * 2f

        val endTime = timeRange?.endTime ?: ""
        val rightTextWidth = textPaint.measureText(endTime) + padding * 2f

        val cycle = timeRange?.cycle ?: ""
        val cycleTextWidth = textPaint.measureText(cycle) + padding * 4f

        val minWidth = leftTextWidth + cycleTextWidth + rightTextWidth

        val startX = timeRange?.startX ?: 0f
        val endX = timeRange?.endX ?: 0f
        val centerX = (startX + endX) * 0.5f

        var transLeftX = max(startX - leftTextWidth * 0.5f, left.toFloat())
        var transRightX = min(endX + rightTextWidth * 0.5f, right.toFloat())

        val transWidth = (transRightX - transLeftX)

        if(transWidth < minWidth) {
            transLeftX = centerX - minWidth * 0.5f
            transRightX = centerX + minWidth * 0.5f
        }

        val transX = (transLeftX + transRightX) * 0.5f

        if (transWidth.isNaN()) return

        if (transX < left + minWidth * 0.5f) {
            transLeftX = 0f
            transRightX = transLeftX + minWidth
        }

        if (transX > right - minWidth * 0.5f) {
            transRightX = right.toFloat()
            transLeftX = transRightX - minWidth
        }

        // 画开始时间背景
        leftRect.set(transLeftX, 0f, transLeftX + leftTextWidth, height.toFloat())
        setTouchPaint(touchType, RANGE_TOUCH_LEFT)
        canvas.drawRoundRect(leftRect, textBackgroundRadius.toFloat(), textBackgroundRadius.toFloat(), bgPaint)

        // 画开始时间
        drawText(canvas, leftRect, startTime)

        // 画中间周期 以及分割线
        val transCenterX = (transLeftX + transRightX) * 0.5f

        val leftLineRightX = transCenterX - cycleTextWidth * 0.5f

        val rightLineLeftX = transCenterX + cycleTextWidth * 0.5f

        canvas.drawLine(transLeftX + leftTextWidth, height * 0.5f, leftLineRightX, height * 0.5f, linePaint)

        centerRect.set(leftLineRightX, 0f, rightLineLeftX, height.toFloat())
        textPaint.color = textColor
        drawText(canvas, centerRect, cycle)

        canvas.drawLine(rightLineLeftX, height * 0.5f, transRightX - rightTextWidth, height * 0.5f, linePaint)

        // 画结束时间背景
        rightRect.set(transRightX - rightTextWidth, 0f, transRightX, height.toFloat())
        setTouchPaint(touchType, RANGE_TOUCH_RIGHT)
        canvas.drawRoundRect(rightRect, textBackgroundRadius.toFloat(), textBackgroundRadius.toFloat(), bgPaint)

        // 画结束时间
        drawText(canvas, rightRect, endTime)

    }

    private fun setTouchPaint(touchType: Int, targetType: Int) {
        if (touchType == targetType) {
            bgPaint.style = Paint.Style.FILL
            bgPaint.color = textBackgroundColor
            textPaint.color = Color.WHITE
        } else {
            bgPaint.style = Paint.Style.STROKE
            bgPaint.color = lineColor
            textPaint.color = textColor
        }
    }

    private fun drawText(canvas: Canvas, rectF: RectF, text: String) {
        // 画时间
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = rectF.centerY() + distance
        canvas.drawText(text, rectF.centerX(), baseline, textPaint)
    }

}