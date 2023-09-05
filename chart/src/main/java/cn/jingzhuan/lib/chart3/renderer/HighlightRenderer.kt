package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart.component.Highlight
import cn.jingzhuan.lib.chart.utils.FloatUtils
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.value.AbstractValue

class HighlightRenderer<V : AbstractValue, T : AbstractDataSet<V>>(
    private val chart: AbstractChartView<V, T>
) : AbstractRenderer<V, T>(chart) {

    var highlight: Highlight? = null

    private lateinit var highlightLinePaint: Paint

    init {
        initPaints()
    }

    private fun initPaints() {
        highlightLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        highlightLinePaint.style = Paint.Style.STROKE
        highlightLinePaint.strokeWidth = chart.highlightThickness

        labelTextPaint.textSize = chart.highlightTextSize
        labelTextPaint.color = chart.highlightTextColor
        labelTextPaint.textAlign = Paint.Align.CENTER

        renderPaint.style = Paint.Style.FILL
        renderPaint.color = chart.highlightTextBgColor
    }

    override fun renderer(canvas: Canvas) {
        if (highlight == null) return
        val textHeight = chart.highlightTextBgHeight
        val contentRect = chart.contentRect
        if (chart.isEnableVerticalHighlight) {
            drawHighlightVertical(canvas)
            if (chart.isEnableHighlightLeftText) {
                drawHighlightLeft(canvas, contentRect, textHeight)
            }
            if (chart.isEnableHighlightRightText) {
                drawHighlightRight(canvas, contentRect, textHeight)
            }
        }
        if (chart.isEnableHorizontalHighlight) {
            drawHighlightHorizontal(canvas)
            if (chart.isEnableHighlightBottomText) {
                drawHighlightBottom(canvas, contentRect)
            }
        }
    }

    /**
     * 画十字光标垂直线
     */
    private fun drawHighlightVertical(canvas: Canvas) {
        var x = highlight!!.x
        if (java.lang.Float.isNaN(x)) return
        val thickness = chart.highlightThickness
        if (x < contentRect.left + thickness * 0.5f) {
            x = contentRect.left + thickness * 0.5f
        }
        if (x > contentRect.right - thickness * 0.5f) {
            x = contentRect.right - thickness * 0.5f
        }
        canvas.drawLine(
            x, contentRect.top.toFloat(), x, contentRect.bottom.toFloat(), highlightLinePaint
        )
    }

    /**
     * 画十字光标水平线
     */
    private fun drawHighlightHorizontal(canvas: Canvas) {
        var y = highlight!!.y
        if (java.lang.Float.isNaN(y)) return
        val thickness = chart.highlightThickness
        if (y < contentRect.top + thickness * 0.5f) {
            y = contentRect.top + thickness * 0.5f
        }
        if (y > contentRect.bottom - thickness * 0.5f) {
            y = contentRect.bottom - thickness * 0.5f
        }
        canvas.drawLine(0f, y, contentRect.right.toFloat(), y, highlightLinePaint)
    }

    /**
     * 画十字光标左侧文本
     */
    private fun drawHighlightLeft(canvas: Canvas, rect: Rect, textHeight: Int) {
        val highlight = highlight
        val y = highlight!!.y
        if (y.isNaN()) return
        var top = y - textHeight * 0.5f
        var bottom = y + textHeight * 0.5f
        if (y < textHeight * 0.5f) {
            top = rect.top.toFloat()
            bottom = top + textHeight
        }
        if (y > rect.bottom - textHeight * 0.5f) {
            bottom = rect.bottom.toFloat()
            top = bottom - textHeight
        }

        val chartData = chart.chartData
        val leftMax = chartData!!.leftMax
        val leftMin = chartData.leftMin
        val price = getTouchPriceByY(y, leftMax, leftMin)
        val valueFormatter = chart.axisLeftRenderer.axis.labelValueFormatter
        val text: String = if (valueFormatter == null) {
            if (price == -1f) "--" else FloatUtils.keepPrecision(price, 2).toString()
        } else {
            valueFormatter.format(price, 0)
        }
        if (text.isEmpty()) return
        val width = calculateWidth(text)

        // 画背景
        val bgRect = Rect(0, top.toInt(), width, bottom.toInt())
        canvas.drawRect(bgRect, renderPaint)

        // 画文本
        val fontMetrics = labelTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = bgRect.centerY() + distance
        canvas.drawText(text, bgRect.centerX().toFloat(), baseline, labelTextPaint)
    }

    /**
     * 画十字光标右侧文本
     */
    private fun drawHighlightRight(canvas: Canvas, rect: Rect, textHeight: Int) {
        val highlight = highlight
        val y = highlight!!.y
        if (y.isNaN()) return

        var top = y - textHeight * 0.5f
        var bottom = y + textHeight * 0.5f
        if (y < textHeight * 0.5f) {
            top = rect.top.toFloat()
            bottom = top + textHeight
        }
        if (y > rect.bottom - textHeight * 0.5f) {
            bottom = rect.bottom.toFloat()
            top = bottom - textHeight
        }

        val chartData = chart.chartData
        val rightMax = chartData!!.rightMax
        val rightMin = chartData.rightMin
        val price = getTouchPriceByY(y, rightMax, rightMin)
        val valueFormatter = chart.axisRightRenderer.axis.labelValueFormatter
        val text: String = if (valueFormatter == null) {
            if (price == -1f) "--" else FloatUtils.keepPrecision(price, 2).toString()
        } else {
            valueFormatter.format(price, -1)
        }
        if (text.isEmpty()) return
        val width = calculateWidth(text)

        // 画背景
        val bgRect = Rect(contentRect.right - width, top.toInt(), contentRect.right, bottom.toInt())
        canvas.drawRect(bgRect, renderPaint)

        // 画文本
        val fontMetrics = labelTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = bgRect.centerY() + distance
        canvas.drawText(text, bgRect.centerX().toFloat(), baseline, labelTextPaint)
    }

    /**
     * 画十字光标底部文本
     */
    private fun drawHighlightBottom(canvas: Canvas, contentRect: Rect) {
        val highlight = highlight
        val x = highlight!!.x
        if (x.isNaN()) return
        val formatter = chart.axisBottomRenderer.axis.valueIndexFormatter
        var text = ""
        if (formatter != null) {
            text = formatter.format(highlight.dataIndex)
        }
        if (text.isEmpty()) return
        val width = calculateWidth(text)
        val bottomRect = chart.bottomRect
        var left = x - width * 0.5f
        var right = x + width * 0.5f
        if (x < width * 0.5f) {
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

        // 画背景
        val bgRect = Rect(left.toInt(), top, right.toInt(), bottom)
        canvas.drawRect(bgRect, renderPaint)

        // 画文本
        val fontMetrics = labelTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = bgRect.centerY() + distance
        canvas.drawText(text, bgRect.centerX().toFloat(), baseline, labelTextPaint)
    }

    /**
     * 计算文本长度
     */
    private fun calculateWidth(text: String): Int {
        val rect = Rect()
        val padding = chart.resources.getDimensionPixelSize(R.dimen.jz_chart_highlight_text_padding)
        labelTextPaint.getTextBounds(text, 0, text.length, rect)
        return rect.width() + padding * 2
    }

    private fun getTouchPriceByY(touchY: Float, viewportMax: Float, viewportMin: Float): Float {
        if (viewportMax > viewportMin && viewportMax > 0) {
            val contentRect = chart.contentRect
            var price =
                viewportMin + (viewportMax - viewportMin) / contentRect.height() * (contentRect.height() - touchY)
            if (price > viewportMax) price = viewportMax
            if (price < viewportMin) price = viewportMin
            return price
        } else if (viewportMax == viewportMin) {
            return viewportMax
        }
        return -1f
    }

    fun highlightValue(highlight: Highlight?) {
        if (highlight == null) return
//        if (mHighlightStatusChangeListener != null) {
//            mHighlightStatusChangeListener.onHighlightShow(highlight);
//        }
        this.highlight = highlight
    }

    fun cleanHighlight() {
        highlight = null
        //        if (mHighlightStatusChangeListener != null)
//            mHighlightStatusChangeListener.onHighlightHide();
    }
}