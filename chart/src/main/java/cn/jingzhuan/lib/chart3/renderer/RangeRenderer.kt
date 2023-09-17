package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.utils.ChartConstant.COLOR_RED
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_BOTH
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_LEFT
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_NONE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.RANGE_TOUCH_RIGHT
import kotlin.math.min
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
    private val maxDiffEntry = 1

    /**
     * 区间起点
     */
    private var startIndex = -1

    /**
     * 区间终点
     */
    private var endIndex = -1

    /**
     * 区间起点 x坐标
     */
    private var startX = 0f

    /**
     * 区间终点 x坐标
     */
    private var endX = 0f

    /**
     * 区间统计触摸类型
     */
    private var touchType = RANGE_TOUCH_NONE

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

    /**
     * 上一次触摸的x坐标
     */
    private var lastPreX = 0f

    init {
        initPaints()
        initRect()
        leftTouchBitmap = BitmapFactory.decodeResource(chart.resources, R.drawable.ico_range_touch_left)
        rightTouchBitmap = BitmapFactory.decodeResource(chart.resources, R.drawable.ico_range_touch_right)
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
        if(startX >= endX) startX = endX

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


         leftTouchRect = RectF(
            startX - bitmapSpanX * 6f,
             contentRect.top.toFloat(),
             startX + bitmapSpanX * 6f,
             contentRect.bottom.toFloat()
        )

        rightTouchRect = RectF(
            endX - bitmapSpanX * 6f,
            contentRect.top.toFloat(),
            endX + bitmapSpanX * 6f,
            contentRect.bottom.toFloat()
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

    /**
     * 打开区间统计 区分光标选中和未选中
     */
    fun setRange(highlightX: Float?) {
        val dataSet = chart.chartData?.getTouchDataSet() ?: return
        val listSize = dataSet.values.size
        if (highlightX == null) {
            // 光标未选中 画可视区域内
            startIndex = (currentViewport.left * listSize).roundToInt()
            endIndex = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())

            // 获取区间统计开始坐标
            startX = chart.getEntryX(startIndex)

            // 获取区间统计结束坐标
            endX = chart.getEntryX(endIndex)
        } else {
            startX = highlightX
            startIndex = chart.getEntryIndex(startX)

            endIndex = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())
            endX = chart.getEntryX(endIndex)
        }
        touchType = RANGE_TOUCH_NONE

        onRangeChange()
    }

    fun onViewChange() {
        if (currentViewport.width() == 1.0f) return
        if (startX != 0f && endX != 0f) {
            val start = chart.getEntryIndex(startX)
            val end = chart.getEntryIndex(endX)
            if (chart.isScaling || chart.computeZoom()) {
                Log.d("RangeRenderer", "onViewChange: 1")
                val dataSet = chart.chartData?.getTouchDataSet() ?: return
                val listSize = dataSet.values.size
                val chartLeftIndex = (currentViewport.left * listSize).roundToInt()
                val chartRightIndex = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())
                if (startIndex < chartLeftIndex) {
                    endIndex = chartLeftIndex + endIndex - startIndex
                    if (endIndex > chartRightIndex) {
                        endIndex = chartRightIndex
                    }
                    startIndex = chartLeftIndex
                }
                if (endIndex > chartRightIndex) {
                    endIndex = chartRightIndex
                }
                startX = chart.getEntryX(startIndex)
                endX = chart.getEntryX(endIndex)
            } else {
                if (start != 0 && end != 0 && startIndex != start && endIndex != end) {
                    Log.d("RangeRenderer", "onViewChange: 2")
                    startIndex = start
                    endIndex = end
                }
            }
            touchType = RANGE_TOUCH_NONE
            onRangeChange()
        }
    }

    fun cleanRange() {
        startIndex = -1
        endIndex = -1
        startX = 0f
        endX = 0f
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val isBottom = chart.bottomRect.contains(event.x.roundToInt(), event.y.roundToInt())
                if (isBottom) {
                    onCloseClick(event.x, event.y)
                    return false
                }
                touchType = RANGE_TOUCH_NONE
                val currentX = event.x
                val currentY = event.y
                lastPreX = currentX

                return if (currentX >= leftTouchRect.right && currentX <= rightTouchRect.left) {
                    // 在左右touch之内 认为是同时滑动
                    touchType = RANGE_TOUCH_BOTH
                    true
                } else if (leftTouchRect.contains(currentX, currentY)) {
                    touchType = RANGE_TOUCH_LEFT
                    true
                } else if (rightTouchRect.contains(currentX, currentY)) {
                    touchType = RANGE_TOUCH_RIGHT
                    true
                } else {
                    false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x
                try {
                    when (touchType) {
                        RANGE_TOUCH_BOTH -> {
                            touchBothToLeftOrRight(currentX)
                        }
                        RANGE_TOUCH_LEFT -> {
                            touchToLeft(currentX)
                        }
                        RANGE_TOUCH_RIGHT -> {
                            touchToRight(currentX)
                        }
                        else -> return false
                    }
                } finally {
                    lastPreX = currentX
                }
                return true
            }

            MotionEvent.ACTION_UP -> {
                touchType = RANGE_TOUCH_NONE
                onRangeChange()
            }
        }
        return false
    }

    private fun touchToLeft(currentX: Float): Boolean {
        val touchDataSet = chart.chartData?.getTouchDataSet() ?: return false
        val leftIndex = chart.getEntryIndex(currentX)
        val rightIndex = chart.getEntryIndex(endX)
        val listSize = touchDataSet.values.size
        val chartLeftIndex = (currentViewport.left * listSize).roundToInt()
        var chartRightIndex = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())

        if (chartRightIndex > listSize - 1) chartRightIndex = listSize - 1
        val newStartX = currentX - lastPreX + startX

        if (leftIndex != startIndex && leftIndex >= chartLeftIndex && leftIndex < chartRightIndex) {
            if (rightIndex - leftIndex <= maxDiffEntry && newStartX >= startX) {
                startIndex = leftIndex
                endIndex = leftIndex + 1
            } else {
                startIndex = leftIndex
                endIndex = rightIndex
            }
            startX = chart.getEntryX(startIndex)
            endX = chart.getEntryX(endIndex)
            chart.invalidate()
        }
        onRangeChange()
        return true
    }

    private fun touchToRight(currentX: Float): Boolean {
        val touchDataSet = chart.chartData?.getTouchDataSet() ?: return false
        val leftIndex = chart.getEntryIndex(startX)
        val rightIndex = chart.getEntryIndex(currentX)
        val listSize = touchDataSet.values.size

        val chartLeftIndex = (currentViewport.left * listSize).roundToInt()
        var chartRightIndex: Int = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())

        if (chartRightIndex > listSize - 1) chartRightIndex = listSize - 1
        val newEndX: Float = currentX - lastPreX + endX
        if (rightIndex != endIndex && rightIndex > chartLeftIndex && rightIndex <= chartRightIndex) {
            endIndex = rightIndex
            if (rightIndex - leftIndex <= maxDiffEntry && newEndX < endX) {
                startIndex = rightIndex - 1
            } else {
                startIndex = leftIndex
            }
            startX = chart.getEntryX(startIndex)
            endX = chart.getEntryX(endIndex)
            chart.invalidate()
        }
        onRangeChange()
        return true
    }

    private fun touchBothToLeftOrRight(currentX: Float): Boolean {
        val touchDataSet = chart.chartData?.getTouchDataSet() ?: return false
        val currentIndex = chart.getEntryIndex(currentX)
        val lastPreIndex = chart.getEntryIndex(lastPreX)
        val deltaIndex = currentIndex - lastPreIndex
        if (deltaIndex == 0) return true
        val listSize = touchDataSet.values.size

        val chartLeftIndex = (currentViewport.left * listSize).roundToInt()
        var chartRightIndex: Int = min(listSize - 1, (currentViewport.right * listSize - 1).roundToInt())

        if (chartRightIndex > listSize - 1) chartRightIndex = listSize - 1
        val leftIndex: Int = startIndex + deltaIndex
        val rightIndex: Int = endIndex + deltaIndex
        if (leftIndex >= chartLeftIndex && rightIndex <= chartRightIndex) {
            startIndex = leftIndex
            endIndex = rightIndex
            startX = chart.getEntryX(startIndex)
            endX = chart.getEntryX(endIndex)
            chart.invalidate()
        }
        onRangeChange()
        return true
    }

    private fun onRangeChange() {
        if (chart.rangeChangeListener != null){
            chart.rangeChangeListener?.onRange(startX, endX, touchType)
        }
    }

    private fun onCloseClick(x: Float, y: Float) {
        if (closeRect.contains(x, y)) {
            chart.closeRange()
        }
    }
}