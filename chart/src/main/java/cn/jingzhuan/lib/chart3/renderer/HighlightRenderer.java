package cn.jingzhuan.lib.chart3.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart.data.ValueFormatter;
import cn.jingzhuan.lib.chart.data.ValueIndexFormatter;
import cn.jingzhuan.lib.chart.utils.FloatUtils;
import cn.jingzhuan.lib.chart3.base.AbstractChartView;

/**
 * @since 2023-09-04
 * created by lei
 */
public class HighlightRenderer<V extends Value, T extends AbstractDataSet<V>> extends AbstractRenderer<V, T> {

    private Highlight mHighlight;

    private final AbstractChartView<V, T> chart;

    private Paint mHighlightLinePaint;

    public HighlightRenderer(AbstractChartView<V, T> chart) {
        super(chart);
        this.chart = chart;
        initPaints();
    }

    private void initPaints() {
        mHighlightLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightLinePaint.setStyle(Paint.Style.STROKE);
        mHighlightLinePaint.setStrokeWidth(chart.getHighlightThickness());

        mLabelTextPaint.setTextSize(chart.getHighlightTextSize());
        mLabelTextPaint.setColor(chart.getHighlightTextColor());
        mLabelTextPaint.setTextAlign(Paint.Align.CENTER);

        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setColor(chart.getHighlightTextBgColor());
    }

    @Override
    public void renderer(Canvas canvas) {
        if (mHighlight == null) return;
        int textHeight = chart.getHighlightTextBgHeight();
        Rect contentRect = chart.getContentRect();
        if (chart.isEnableVerticalHighlight()) {
            drawHighlightVertical(canvas);

            if (chart.isEnableHighlightLeftText()) {
                drawHighlightLeft(canvas, contentRect, textHeight);
            }

            if (chart.isEnableHighlightRightText()) {
                drawHighlightRight(canvas, contentRect, textHeight);
            }
        }
        if (chart.isEnableHorizontalHighlight()) {
            drawHighlightHorizontal(canvas);

            if (chart.isEnableHighlightBottomText()) {
                drawHighlightBottom(canvas, contentRect);
            }
        }
    }

    /**
     * 画十字光标垂直线
     */
    private void drawHighlightVertical(Canvas canvas) {
        float x = getHighlight().getX();
        if (Float.isNaN(x)) return;

        float thickness = chart.getHighlightThickness();

        if (x < mContentRect.left + thickness * 0.5f) {
            x = mContentRect.left + thickness * 0.5f;
        }

        if (x > mContentRect.right - thickness * 0.5f){
            x = mContentRect.right - thickness * 0.5f;
        }

        canvas.drawLine(x, mContentRect.top, x, mContentRect.bottom, mHighlightLinePaint);
    }

    /**
     * 画十字光标水平线
     */
    private void drawHighlightHorizontal(Canvas canvas) {
        float y = getHighlight().getY();
        if (Float.isNaN(y)) return;

        float thickness = chart.getHighlightThickness();

        if (y < mContentRect.top + thickness * 0.5f) {
            y = mContentRect.top + thickness * 0.5f;
        }

        if (y > mContentRect.bottom - thickness * 0.5f){
            y = mContentRect.bottom - thickness * 0.5f;
        }
        canvas.drawLine(0, y, mContentRect.right, y, mHighlightLinePaint);
    }


    /**
     * 画十字光标左侧文本
     */
    public void drawHighlightLeft(Canvas canvas, Rect rect, int textHeight) {
        Highlight highlight = getHighlight();
        float y = highlight.getY();
        if (Float.isNaN(y)) return;

        float top = y - textHeight * 0.5f;
        float bottom = y + textHeight * 0.5f;
        if (y < textHeight * 0.5f) {
            top = rect.top;
            bottom = top + textHeight;
        }

        if (y > rect.bottom - textHeight * 0.5f){
            bottom = rect.bottom;
            top = bottom - textHeight;
        }

//            Log.d("drawHighlightLeft", "y="+ y + "top=" + top + "bottom: "+bottom);
        ChartData<T> chartData = chart.getChartData();
        float leftMax = chartData.getLeftMax();
        float leftMin = chartData.getLeftMin();
        float price = getTouchPriceByY(y, leftMax, leftMin);
        ValueFormatter valueFormatter = chart.getAxisLeftRenderer().getAxis().getLabelValueFormatter();
        String text;
        if (valueFormatter == null) {
            text = price == -1 ? "--" : String.valueOf(FloatUtils.keepPrecision(price, 2));
        } else {
            text = valueFormatter.format(price, 0);
        }

        if (text.isEmpty()) return;

        int width = calculateWidth(text);

        // 画背景
        Rect bgRect = new Rect(0, (int) top, width, (int) bottom);
        canvas.drawRect(bgRect, mRenderPaint);

        // 画文本
        Paint.FontMetrics fontMetrics = mLabelTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = bgRect.centerY() + distance;

        canvas.drawText(text, bgRect.centerX(), baseline, mLabelTextPaint);
    }

    /**
     * 画十字光标右侧文本
     */
    public void drawHighlightRight(Canvas canvas, Rect rect, int textHeight) {
        Highlight highlight = getHighlight();
        float y = highlight.getY();
        if (Float.isNaN(y)) return;
//            if (y > contentRect.bottom || y < contentRect.top) {
//                return;
//            }
        float top = y - textHeight * 0.5f;
        float bottom = y + textHeight * 0.5f;
        if (y < textHeight * 0.5f) {
            top = rect.top;
            bottom = top + textHeight;
        }

        if (y > rect.bottom - textHeight * 0.5f){
            bottom = rect.bottom;
            top = bottom - textHeight;
        }

//        Log.d("drawHighlightRight", "y="+ y + "top=" + top + "bottom: "+bottom);
        ChartData<T> chartData = chart.getChartData();
        float rightMax = chartData.getRightMax();
        float rightMin = chartData.getRightMin();
        float price = getTouchPriceByY(y, rightMax, rightMin);
        ValueFormatter valueFormatter = chart.getAxisRightRenderer().getAxis().getLabelValueFormatter();
        String text;
        if (valueFormatter == null) {
            text = price == -1 ? "--" : String.valueOf(FloatUtils.keepPrecision(price, 2));
        } else {
            text = valueFormatter.format(price, -1);
        }

        if (text.isEmpty()) return;

        int width = calculateWidth(text);

        // 画背景
        Rect bgRect = new Rect(mContentRect.right - width, (int) top, mContentRect.right, (int) bottom);
        canvas.drawRect(bgRect, mRenderPaint);

        // 画文本
        Paint.FontMetrics fontMetrics = mLabelTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = bgRect.centerY() + distance;

        canvas.drawText(text, bgRect.centerX(), baseline, mLabelTextPaint);
    }

    /**
     * 画十字光标底部文本
     */
    public void drawHighlightBottom(Canvas canvas, Rect contentRect) {
        Highlight highlight = getHighlight();
        float x = highlight.getX();
        if (Float.isNaN(x)) return;

        ValueIndexFormatter formatter = chart.getAxisBottomRenderer().getAxis().getValueIndexFormatter();
        String text = "";
        if (formatter != null) {
            text = formatter.format( highlight.getDataIndex());
        }

        if (text.isEmpty()) return;

        int width = calculateWidth(text);

        Rect bottomRect = chart.getBottomRect();

        float left = x - width * 0.5f;
        float right = x + width * 0.5f;
        if (x < width * 0.5f) {
            left = bottomRect.left;
            right = left + width;
        }

        if (x > bottomRect.right - width * 0.5f){
            right = bottomRect.right;
            left = right - width;
        }

        int top = bottomRect.top;
        int bottom = bottomRect.bottom;

        if (bottomRect.height() == 0) {
            int textHeight = chart.getHighlightTextBgHeight();
            top = contentRect.bottom - textHeight;
            bottom = contentRect.bottom;
        }

        // 画背景
        Rect bgRect = new Rect((int) left, top, (int) right, bottom);
        canvas.drawRect(bgRect, mRenderPaint);

        // 画文本
        Paint.FontMetrics fontMetrics = mLabelTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = bgRect.centerY() + distance;

        canvas.drawText(text, bgRect.centerX(), baseline, mLabelTextPaint);
    }

    /**
     * 计算文本长度
     */
    private int calculateWidth(String text) {
        Rect rect = new Rect();
        int padding = chart.getResources().getDimensionPixelSize(R.dimen.jz_chart_highlight_text_padding);
        mLabelTextPaint.getTextBounds(text, 0, text.length(), rect);
        return rect.width() + padding * 2;
    }

    private float getTouchPriceByY(float touchY, float viewportMax, float viewportMin) {
        if (viewportMax > viewportMin && viewportMax > 0) {
            Rect contentRect = chart.getContentRect();
            var price = viewportMin + (viewportMax - viewportMin) / contentRect.height() * (contentRect.height() - touchY);
            if (price > viewportMax) price = viewportMax;
            if (price < viewportMin) price = viewportMin;
            return price;
        } else if (viewportMax == viewportMin) {
            return viewportMax;
        }
        return -1f;
    }

    public void highlightValue(Highlight highlight) {
        if (highlight == null) return;

//        if (mHighlightStatusChangeListener != null) {
//            mHighlightStatusChangeListener.onHighlightShow(highlight);
//        }

        setHighlight(highlight);

    }

    public void cleanHighlight() {
        mHighlight = null;
//        if (mHighlightStatusChangeListener != null)
//            mHighlightStatusChangeListener.onHighlightHide();

    }

    public Highlight getHighlight() {
        return mHighlight;
    }

    public void setHighlight(Highlight highlight) {
        this.mHighlight = highlight;
    }

}
