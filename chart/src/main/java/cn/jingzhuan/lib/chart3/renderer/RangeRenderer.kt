package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.event.OnBottomAreaClickListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_RED
import kotlin.math.roundToInt

/**
 * @since 2023-09-15
 * created by lei
 * 区间统计
 */
class RangeRenderer<T : AbstractDataSet<*>>(
    private val chart: AbstractChartView<T>,
) : AbstractRenderer<T>(chart) {
    /**
     * 最小间隔
     */
    private val MAX_DIFF_ENTRY = 1

    /**
     * 区间统计遮罩区域
     */
    private lateinit var bgRect: RectF

    /**
     * 区间统计关闭按钮
     */
    private lateinit var closeRect: RectF

    /**
     * 区间起点 指示图标
     */
    private val leftTouchBitmap: Bitmap

    /**
     * 区间终点 指示图标
     */
    private val rightTouchBitmap: Bitmap

    /**
     * 区间起点 矩形触摸区域
     */
    private lateinit var leftTouchRect: RectF

    /**
     * 区间终点 矩形触摸区域
     */
    private lateinit var rightTouchRect: RectF

    /**
     * 用于画bitmap
     */
    private lateinit var btPaint: Paint

    /**
     * 用于画背景
     */
    private lateinit var bgPaint: Paint

    /**
     * 区间统计颜色
     */
    private val rangeColor = COLOR_RED

    init {
        initPaints()
        initRect()
        leftTouchBitmap = BitmapFactory.decodeResource(chart.resources, R.drawable.ico_range_touch_left)
        rightTouchBitmap = BitmapFactory.decodeResource(chart.resources, R.drawable.ico_range_touch_right)

        chart.setOnBottomAreaClickListener(object : OnBottomAreaClickListener {
            override fun onClick(x: Float, y: Float) {
                if (closeRect.contains(x, y)) {
                    chart.closeRange()
                }
            }

        })
    }

    private fun initRect() {
        bgRect = RectF()
        closeRect = RectF()
        leftTouchRect = RectF()
        rightTouchRect = RectF()
    }

    private fun initPaints() {
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = rangeColor
        bgPaint.alpha = 20

        btPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        labelTextPaint.textSize = chart.highlightTextSize.toFloat()
        labelTextPaint.color = Color.WHITE
        labelTextPaint.textAlign = Paint.Align.CENTER

        renderPaint.color = rangeColor
        renderPaint.style = Paint.Style.FILL
    }

    override fun renderer(canvas: Canvas) {
        val dataSet = chart.chartData?.getTouchDataSet() ?: return
        val startX = chart.rangeStartX
        val endX = chart.rangeEndX
        if(startX >= endX) chart.rangeStartX = chart.rangeEndX

        if(startX < contentRect.left || startX > contentRect.right) return;

        if(endX < contentRect.left || endX > contentRect.right ) return

        //绘制区间统计的选择区域
        bgRect.set(startX, contentRect.top.toFloat(), endX, contentRect.bottom.toFloat())
        canvas.drawRect(bgRect, bgPaint)

        // 绘制左右touch图标
        val bitmapSpanX = leftTouchBitmap.width / 2f
        val bitmapSpanY = leftTouchBitmap.height / 2f
        canvas.drawBitmap(
            rightTouchBitmap,
            endX - bitmapSpanX,
            contentRect.height() / 2f - bitmapSpanY,
            btPaint
        )
        canvas.drawBitmap(
            leftTouchBitmap,
            startX - bitmapSpanX,
            contentRect.height() / 2f - bitmapSpanY,
            btPaint
        )
        drawCloseButton(canvas, startX, endX)
    }

    private fun drawCloseButton(canvas: Canvas, startX: Float, endX: Float) {
        val x = (startX + endX) * 0.5f

        val text = "关闭"

        val padding = chart.resources.getDimensionPixelSize(R.dimen.jz_range_close_padding)
        val width = labelTextPaint.measureText(text).roundToInt() + padding * 2

        val bottomRect = chart.bottomRect

        var left = x - width * 0.5f
        var right = x + width * 0.5f
        if (x < bottomRect.left + width * 0.5f) {
            left = bottomRect.left.toFloat()
            right = left + width
        }
        if (x > bottomRect.right - width * 0.5f) {
            right = bottomRect.right.toFloat()
            left = right - width
        }
        var top = bottomRect.top
        var bottom = bottomRect.bottom
        if (bottomRect.height() == 0) {
            val textHeight = chart.highlightTextBgHeight
            top = contentRect.bottom - textHeight
            bottom = contentRect.bottom
        }

        closeRect.set(left, top.toFloat(), right, bottom.toFloat())

        // 画背景
        canvas.drawRoundRect(closeRect, 5f, 5f, renderPaint)

        // 画文本
        val fontMetrics = labelTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = closeRect.centerY() + distance
        canvas.drawText(text, closeRect.centerX(), baseline, labelTextPaint)
    }
}