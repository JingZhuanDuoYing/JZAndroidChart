package cn.jingzhuan.lib.chart3.drawline

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
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
            val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StaticLayout.Builder.obtain(text, 0, text.length, textPaint, dataSet.maxTextWidth ?: 112)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(true)
                    .build()
            } else {
                StaticLayout(text, textPaint, dataSet.maxTextWidth ?: 112, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true)
            }
            canvas.save()
            canvas.translate(startX + padding, startY + padding)
            layout.draw(canvas)
            canvas.restore()

            val textHeight = layout.height
            val textWidth = layout.width

            val right = startX + textWidth + padding * 2
            val bottom = startY + textHeight + padding * 2

            val rect = RectF(startX, startY, right, bottom)
//            linePaint.style = Paint.Style.STROKE
//            canvas.drawRect(rect, linePaint)

            dataSet.selectRegion = Region(
                startX.toInt(),
                startY.toInt(),
                right.toInt(),
                bottom.toInt()
            )

            // 画选中背景
            if (dataSet.isSelect) {
                linePaint.style = Paint.Style.FILL

                linePaint.alpha = dataSet.selectAlpha
                canvas.drawRect(rect, linePaint)
            }

        } else {
            val textWidth = textPaint.measureText(text).roundToInt()
            textPaint.getTextBounds(text, 0, text.length, mTextBounds)
            val textHeight = mTextBounds.height()

            val right = startX + textWidth + padding * 2
            val bottom = startY + textHeight + padding * 2

            val rect = RectF(startX, startY, right, bottom)
//            linePaint.style = Paint.Style.STROKE
//            canvas.drawRect(rect, linePaint)

            dataSet.selectRegion = Region(
                startX.toInt(),
                startY.toInt(),
                right.toInt(),
                bottom.toInt()
            )

            val fontMetrics = textPaint.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline = rect.centerY() + distance

            canvas.drawText(text, startX + padding, baseline, textPaint)

            // 画选中背景
            if (dataSet.isSelect) {
                linePaint.style = Paint.Style.FILL

                linePaint.alpha = dataSet.selectAlpha
                canvas.drawRect(rect, linePaint)
            }
        }

    }

}