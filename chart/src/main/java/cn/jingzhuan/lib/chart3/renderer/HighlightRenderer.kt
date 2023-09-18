package cn.jingzhuan.lib.chart3.renderer

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.Highlight
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.event.OnBottomAreaClickListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_HISTORY_MINUTE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LHB
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_LIMIT_UP
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_NOTICE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_SIMULATE_TRADE_DETAIL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.FLAG_TRADE_DETAIL
import java.lang.Float.isNaN
import kotlin.math.roundToInt

/**
 * @since 2023-09-12
 * created by lei
 * 绘制光标
 */
class HighlightRenderer<T : AbstractDataSet<*>>(
    private val chart: AbstractChartView<T>,
) : AbstractRenderer<T>(chart) {

    var highlight: Highlight? = null

    private lateinit var highlightLinePaint: Paint

    private val sb = StringBuilder()

    // 左、右背景
    private lateinit var bgRect: Rect

    // 交易详情flag
    private lateinit var tradeRect: Rect

    // 交易详情(模)flag
    private lateinit var tradeSimulateRect: Rect

    // 涨停分析flag
    private lateinit var limitUpRect: Rect

    // 主题历史炒作flag
    private lateinit var fireHistoryRect: Rect

    // 公告flag
    private lateinit var noticeRect: Rect

    // 时间flag
    private lateinit var dateRect: Rect

    // 龙虎榜flag
    private lateinit var lhbRect: Rect

    init {
        initPaints()

        initRect()

        chart.setOnBottomFlagsClickListener(object : OnBottomAreaClickListener {
            override fun onClick(x: Float, y: Float) {
                if (highlight == null) return
                val index = highlight?.dataIndex ?: 0
                if (tradeRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_TRADE_DETAIL, index)
                } else if (tradeSimulateRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_SIMULATE_TRADE_DETAIL, index)
                } else if (limitUpRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_LIMIT_UP, index)
                } else if (noticeRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_NOTICE, index)
                } else if (dateRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_HISTORY_MINUTE, index)
                } else if (lhbRect.contains(x.roundToInt(), y.roundToInt())) {
                    chart.onFlagCallback(FLAG_LHB, index)
                }
            }

        })
    }

    private fun initRect() {
        bgRect = Rect()
        tradeRect = Rect()
        tradeSimulateRect = Rect()
        limitUpRect = Rect()
        fireHistoryRect = Rect()
        noticeRect = Rect()
        dateRect = Rect()
        lhbRect = Rect()
    }

    private fun setRectEmpty() {
        bgRect.setEmpty()
        tradeRect.setEmpty()
        tradeSimulateRect.setEmpty()
        limitUpRect.setEmpty()
        fireHistoryRect.setEmpty()
        noticeRect.setEmpty()
        dateRect.setEmpty()
        lhbRect.setEmpty()
    }

    private fun initPaints() {
        highlightLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        highlightLinePaint.style = Paint.Style.FILL_AND_STROKE
        highlightLinePaint.strokeWidth = chart.highlightThickness.toFloat()
        highlightLinePaint.color = chart.highlightColor

        labelTextPaint.textSize = chart.highlightTextSize.toFloat()
        labelTextPaint.color = chart.highlightTextColor
        labelTextPaint.textAlign = Paint.Align.CENTER

        renderPaint.style = Paint.Style.FILL
        renderPaint.color = chart.highlightTextBgColor
    }

    override fun renderer(canvas: Canvas) {
        if (highlight == null) return
        setRectEmpty()
        val textHeight = chart.highlightTextBgHeight
        if (chart.isEnableVerticalHighlight) {
            drawHighlightVertical(canvas)
            if (chart.isEnableHighlightBottomText) {
                drawHighlightBottom(canvas)
            }
        }
        if (chart.isEnableHorizontalHighlight) {
            drawHighlightHorizontal(canvas)
            if (chart.isEnableHighlightLeftText) {
                drawHighlightLeft(canvas, textHeight)
            }
            if (chart.isEnableHighlightRightText) {
                drawHighlightRight(canvas, textHeight)
            }
        }
    }

    /**
     * 画十字光标垂直线
     */
    private fun drawHighlightVertical(canvas: Canvas) {
        var x = highlight!!.x
        if (isNaN(x)) return
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
        if (isNaN(y)) return
        val thickness = chart.highlightThickness
        if (y < contentRect.top + thickness * 0.5f) {
            y = contentRect.top + thickness * 0.5f
        }
        if (y > contentRect.bottom - thickness * 0.5f) {
            y = contentRect.bottom - thickness * 0.5f
        }
        canvas.drawLine(
            contentRect.left.toFloat(),
            y,
            contentRect.right.toFloat(),
            y,
            highlightLinePaint
        )
    }

    /**
     * 画十字光标左侧文本
     */
    private fun drawHighlightLeft(canvas: Canvas, textHeight: Int) {
        val highlight = highlight
        val y = highlight!!.y
        if (y.isNaN()) return
        var top = y - textHeight * 0.5f
        var bottom = y + textHeight * 0.5f
        if (y < textHeight * 0.5f) {
            top = contentRect.top.toFloat()
            bottom = top + textHeight
        }
        if (y > contentRect.bottom - textHeight * 0.5f) {
            bottom = contentRect.bottom.toFloat()
            top = bottom - textHeight
        }

        val chartData = chart.chartData
        val leftMax = chartData!!.leftMax
        val leftMin = chartData.leftMin
        val price = getTouchPriceByY(y, leftMax, leftMin)
        val valueFormatter = chart.axisLeft.labelValueFormatter
        val text: String = valueFormatter?.format(price, -1)
            ?: if (price == -1f) "--" else String.format("%.2f", price)
        if (text.isEmpty()) return

        val width = calculateWidth(text)

        // 画背景
        bgRect.set(contentRect.left, top.toInt(), contentRect.left + width, bottom.toInt())
        renderPaint.color = chart.highlightTextBgColor
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
    private fun drawHighlightRight(canvas: Canvas, textHeight: Int) {
        val highlight = highlight
        val y = highlight!!.y
        if (y.isNaN()) return

        var top = y - textHeight * 0.5f
        var bottom = y + textHeight * 0.5f
        if (y < textHeight * 0.5f) {
            top = contentRect.top.toFloat()
            bottom = top + textHeight
        }
        if (y > contentRect.bottom - textHeight * 0.5f) {
            bottom = contentRect.bottom.toFloat()
            top = bottom - textHeight
        }

        val chartData = chart.chartData
        val rightMax = chartData!!.rightMax
        val rightMin = chartData.rightMin
        val price = getTouchPriceByY(y, rightMax, rightMin)
        val valueFormatter = chart.axisRight.labelValueFormatter
        val text: String = valueFormatter?.format(price, -1)
            ?: if (price == -1f) "--" else String.format("%.2f", price)
        if (text.isEmpty()) return

        val width = calculateWidth(text)

        // 画背景
        bgRect.set(contentRect.right - width, top.toInt(), contentRect.right, bottom.toInt())
        renderPaint.color = chart.highlightTextBgColor
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
    private fun drawHighlightBottom(canvas: Canvas) {
        val highlight = highlight
        val x = highlight!!.x
        if (x.isNaN()) return
        val formatter = chart.axisBottom.valueIndexFormatter
        var text = ""
        if (formatter != null) {
            text = formatter.format(highlight.dataIndex)
        }
        if (text.isEmpty()) return

        val chartData = chart.chartData
        if (chart.showBottomFlags) {
            // 画标签组 按固定顺序绘制
            val flags = chartData?.getFlagDataSet()?.values?.get(highlight.dataIndex)?.flags
            sb.clear()
            sb.append(text)
            sb.append(" 分时 >")
            text = sb.toString()

            if (!flags.isNullOrEmpty()) {
                var tradeWidth = 0
                var tradeSimulateWidth = 0
                var limitUpWidth = 0
                var noticeWidth = 0
                val dateWidth = calculateWidth(text)
                var lhbWidth = 0

                var tradeText = ""
                var tradeSimulateText = ""
                var limitUpText = ""
                var noticeText = ""
                var lhbText = ""

                val splitSpace = 8

                if (flags.contains(FLAG_TRADE_DETAIL)) {
                    // 交易详情
                    sb.clear()
                    sb.append("交易详情 >")
                    tradeText = sb.toString()
                    tradeWidth = calculateWidth(sb.toString()) + splitSpace
                }

                if (flags.contains(FLAG_SIMULATE_TRADE_DETAIL)) {
                    // 交易详情(模)
                    sb.clear()
                    sb.append("交易详情(模) >")
                    tradeSimulateText = sb.toString()
                    tradeSimulateWidth = calculateWidth(sb.toString()) + splitSpace
                }

                if (flags.contains(FLAG_LIMIT_UP)) {
                    // 涨停分析
                    sb.clear()
                    sb.append("涨停分析 >")
                    limitUpText = sb.toString()
                    limitUpWidth = calculateWidth(sb.toString()) + splitSpace
                }

                if (flags.contains(FLAG_NOTICE)) {
                    // 公告
                    sb.clear()
                    sb.append("公告 >")
                    noticeText = sb.toString()
                    noticeWidth = calculateWidth(sb.toString()) + splitSpace
                }

                if (flags.contains(FLAG_LHB)) {
                    // 龙虎榜
                    sb.clear()
                    sb.append("龙虎榜 >")
                    lhbText = sb.toString()
                    lhbWidth = calculateWidth(sb.toString())
                }
                val totalWidth = if (lhbWidth == 0) {
                    tradeWidth + tradeSimulateWidth + limitUpWidth + noticeWidth + dateWidth
                } else {
                    tradeWidth + tradeSimulateWidth + limitUpWidth + noticeWidth + dateWidth + splitSpace + lhbWidth
                }

                val bottomRect = chart.bottomRect
                var left = x - totalWidth * 0.5f
                if (x < bottomRect.left + totalWidth * 0.5f) {
                    left = bottomRect.left.toFloat()
                }
                if (x > bottomRect.right - totalWidth * 0.5f) {
                    val right = bottomRect.right.toFloat()
                    left = right - totalWidth
                }
                var top = bottomRect.top
                var bottom = bottomRect.bottom
                if (bottomRect.height() == 0) {
                    val textHeight = chart.highlightTextBgHeight
                    top = contentRect.bottom - textHeight
                    bottom = contentRect.bottom
                }

                var sortLeft = left.toInt()

                if (tradeWidth != 0) {
                    tradeRect.set(sortLeft, top, sortLeft + tradeWidth - splitSpace, bottom)
                    sortLeft += tradeWidth
                    drawFlag(canvas, tradeRect, tradeText, 0xFFFD263F.toInt())
                }

                if (tradeSimulateWidth != 0) {
                    tradeSimulateRect.set(
                        sortLeft,
                        top,
                        sortLeft + tradeSimulateWidth - splitSpace,
                        bottom
                    )
                    sortLeft += tradeSimulateWidth
                    drawFlag(canvas, tradeSimulateRect, tradeSimulateText, 0xFFFD263F.toInt())
                }

                if (limitUpWidth != 0) {
                    limitUpRect.set(sortLeft, top, sortLeft + limitUpWidth - splitSpace, bottom)
                    sortLeft += limitUpWidth
                    drawFlag(canvas, limitUpRect, limitUpText, 0xFfFF9000.toInt())
                }

                if (noticeWidth != 0) {
                    noticeRect.set(sortLeft, top, sortLeft + noticeWidth - splitSpace, bottom)
                    sortLeft += noticeWidth
                    drawFlag(canvas, noticeRect, noticeText, 0xFf216FE1.toInt())
                }

                dateRect.set(sortLeft, top, sortLeft + dateWidth, bottom)
                if (lhbWidth != 0) {
                    sortLeft += (dateWidth + splitSpace)
                }
                drawFlag(canvas, dateRect, text, chart.highlightColor)

                if (lhbWidth != 0) {
                    lhbRect.set(sortLeft, top, sortLeft + lhbWidth, bottom)
                    drawFlag(canvas, lhbRect, lhbText, 0xFfFD263F.toInt())
                }

            } else {
                // 空 只画历史分时
                setDateRect(text, x)
                drawFlag(canvas, dateRect, text, chart.highlightColor)
            }
        } else {
            // 只画时间
            setDateRect(text, x)
            drawFlag(canvas, dateRect, text, chart.highlightColor)
        }
    }

    private fun setDateRect(text: String, x: Float) {
        val width = calculateWidth(text)
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
        dateRect.set(left.toInt(), top, right.toInt(), bottom)
    }

    private fun drawFlag(canvas: Canvas, rect: Rect, text: String, bgColor: Int) {
        // 画背景
        renderPaint.color = bgColor
        canvas.drawRect(rect, renderPaint)

        // 画文本
        val fontMetrics = labelTextPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        val baseline = rect.centerY() + distance
        canvas.drawText(text, rect.centerX().toFloat(), baseline, labelTextPaint)
    }

    /**
     * 计算文本长度
     */
    private fun calculateWidth(text: String): Int {
        val padding = chart.resources.getDimensionPixelSize(R.dimen.jz_chart_highlight_text_padding)
        return labelTextPaint.measureText(text).roundToInt() + padding * 2
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
        this.highlight = highlight
    }

    fun highlightForever(): Highlight? {
        val highlight = highlight ?: return null
        var highlightIndex = highlight.dataIndex

        val viewport = chart.currentViewport

        var highlightX = contentRect.left + (highlightIndex / chart.totalEntryCount.toFloat() - viewport.left) / viewport.width() * contentRect.width()
        if (highlightX > contentRect.right) highlightX = contentRect.right.toFloat()
        if (highlightX < contentRect.left) highlightX = contentRect.left.toFloat()

        val pointWidth = chart.pointWidth

        val leftX = contentRect.left + pointWidth * 0.5f
        val rightX = contentRect.right - pointWidth * 0.5f
        if (highlightX < leftX) {
            highlightX = leftX
            highlightIndex = chart.getEntryIndex(highlightX)
        }
        if (highlightX > rightX) {
            highlightX = rightX
            highlightIndex = chart.getEntryIndex(highlightX)
        }

        highlight.x = highlightX
        highlight.dataIndex = highlightIndex
        return highlight
    }

    fun cleanHighlight() {
        if (highlight == null) return
        highlight = null
    }
}