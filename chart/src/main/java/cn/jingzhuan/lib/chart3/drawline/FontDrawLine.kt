package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.text.Layout
import android.text.StaticLayout
import cn.jingzhuan.lib.chart.R
import cn.jingzhuan.lib.chart3.base.AbstractChartView
import cn.jingzhuan.lib.chart3.data.dataset.AbstractDataSet
import cn.jingzhuan.lib.chart3.data.dataset.DrawLineDataSet
import kotlin.math.roundToInt

/**
 * @since 2023-10-16
 * 文本
 */
class FontDrawLine<T : AbstractDataSet<*>>(chart: AbstractChartView<T>) : AbstractDrawLine<T>(chart) {
    private val mTextBounds = Rect()

    private val padding by lazy {
        chart.resources.getDimensionPixelSize(R.dimen.jz_chart_highlight_text_padding)
    }

    override fun onDraw(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float
    ) {
        super.onDraw(canvas, dataSet, baseDataSet, lMax, lMin)
        if (dataSet.isSelect) return
        drawTypeShape(canvas, dataSet, baseDataSet, lMax, lMin)
    }

    override fun drawTypeShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        baseDataSet: AbstractDataSet<*>,
        lMax: Float,
        lMin: Float,
    ) {
        val text = dataSet.text
        if (text.isNullOrEmpty()) return

        val startPoint = dataSet.startDrawValue ?: return

        // 没有吸附并且没有抬起时平顺滑动
        if (!chartView.isDrawLineAdsorb && !dataSet.isActionUp) {
            val startX = startPoint.x
            val startY = chartView.getScaleY(startPoint.value, lMax, lMin)

            drawShape(canvas, dataSet, startX, startY, text)
            return
        }

        val startIndex = getIndexInTime(dataSet, baseDataSet, startPoint.time)
        val startX = getEntryX(startIndex, baseDataSet) ?: return
        val startY = chartView.getScaleY(startPoint.value, lMax, lMin)

        if (!dataSet.isSelect || dataSet.isActionUp) {
            startPoint.apply { dataIndex = startIndex; x = startX; y = startY }
        }

        drawShape(canvas, dataSet, startX, startY, text)
    }

    private fun drawShape(
        canvas: Canvas,
        dataSet: DrawLineDataSet,
        startX: Float,
        startY: Float,
        text: String
    ) {
        // 画文本
        if (dataSet.maxTextWidth != null) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, dataSet.maxTextWidth ?: 112)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(true)
                .build()
//            StaticLayout(text, textPaint, 300,Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true)
            canvas.save()
            canvas.translate(startX, startY)
            layout.draw(canvas)
            canvas.restore()

            val textHeight = layout.height
            val textWidth = layout.width

            val left = startX - padding
            val top = startY - padding
            val right = startX + textWidth + padding
            val bottom = startY + textHeight + padding

//            val rect = RectF(left, top, right, bottom)
//            linePaint.style = Paint.Style.STROKE
//            canvas.drawRect(rect, linePaint)

            dataSet.selectRegion = Region(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())

        } else {
            val fontMetrics = textPaint.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline = startY + distance

            val textWidth = textPaint.measureText(text).roundToInt()
            textPaint.getTextBounds(text, 0, text.length, mTextBounds)
            val textHeight = mTextBounds.height()

            val top = startY - textHeight * 0.5f - padding
            val right = startX + textWidth
            val bottom = startY + textHeight * 0.5f + padding

//            val rect = RectF(startX, top, right, bottom)
//            linePaint.style = Paint.Style.STROKE
//            canvas.drawRect(rect, linePaint)

            dataSet.selectRegion = Region(startX.toInt(), top.toInt(), right.toInt(), bottom.toInt())

            canvas.drawText(text, startX, baseline, textPaint)
        }

//        // 画选中背景
//        if (dataSet.isSelect) {
//            linePaint.style = Paint.Style.FILL
//
//            linePaint.alpha = dataSet.selectAlpha
//            canvas.drawRect(rect, linePaint)
//        }

    }

}