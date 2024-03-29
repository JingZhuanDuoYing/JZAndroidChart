package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.utils.FloatUtils;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align;

/**
 * Created by donglua on 2/9/18.
 */

public class CandlestickDataSetArrowDecorator extends CandlestickDataSet {

    public final static String ARROW_LEFT = "←";
    public final static String ARROW_RIGHT = "→";

    private final Paint mPaint;

    private final char[] mLabelBuffer = new char[20];

    private final Rect mTextBounds = new Rect();

    private float currentMaxValue = -1f;

    private float currentMinValue = -1f;

    private int textColor = 0xffA1abbb;

    private int textSize = 30;

    public CandlestickDataSetArrowDecorator(CandlestickDataSet candlestickDataSet) {
        super(candlestickDataSet.getValues(), candlestickDataSet.getAxisDependency());

        setMinValueCount(candlestickDataSet.getMinValueCount());

        setMinVisibleEntryCount(candlestickDataSet.getMinVisibleEntryCount());
        setMaxVisibleEntryCount(candlestickDataSet.getMaxVisibleEntryCount());
        setDefaultVisibleEntryCount(candlestickDataSet.getDefaultVisibleEntryCount());

        setTag(candlestickDataSet.getTag());
        setDecreasingPaintStyle(candlestickDataSet.getDecreasingPaintStyle());
        setDecreasingColor(candlestickDataSet.getDecreasingColor());
        setIncreasingPaintStyle(candlestickDataSet.getIncreasingPaintStyle());
        setIncreasingColor(candlestickDataSet.getIncreasingColor());
        setNeutralColor(candlestickDataSet.getNeutralColor());
        setLimitUpColor(candlestickDataSet.getLimitUpColor());
        setLimitUpColor20(candlestickDataSet.getLimitUpColor20());
        setGapColor(candlestickDataSet.getGapColor());

        setAutoWidth(candlestickDataSet.isAutoWidth());
        setCandleWidth(candlestickDataSet.getCandleWidth());
        setCandleWidthPercent(candlestickDataSet.getCandleWidthPercent());

        setHighlightedHorizontalEnable(candlestickDataSet.isHighlightedHorizontalEnable());
        setHighlightedVerticalEnable(candlestickDataSet.isHighlightedVerticalEnable());

        setEnable(candlestickDataSet.isEnable());
        setEnableGap(candlestickDataSet.isEnableGap());
        setStrokeThickness(candlestickDataSet.getStrokeThickness());
        setVisible(candlestickDataSet.isVisible());

        setTag(candlestickDataSet.getTag());

        mPaint = new Paint(ANTI_ALIAS_FLAG);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        super.calcMinMax(viewport);
    }

    public void draw(Canvas canvas, CandlestickValue candlestick, Rect contentRect, float candleWidth, float x, float highY, float lowY) {
        draw(canvas, candlestick, contentRect, candleWidth, x, highY, lowY, 2);
    }

    public void draw(Canvas canvas, CandlestickValue candlestick, Rect contentRect, float candleWidth, float x, float highY, float lowY, int decimalDigitsNumber) {

        final float highValue = candlestick.getHigh();
        final float lowValue = candlestick.getLow();

        float viewportYMax = getViewportYMax();
        float viewportYMin = getViewportYMin();

        // max + percent * (max - min) = viewportYMax => (1 + percent)max - percent * min = viewportYMax
        // min - percent * (max - min) = viewportYMin

        // max + min = viewportYMax + viewportYMin => (1 + percent)max + (1 + percent)min = (viewportYMax + viewportYMin) * (1 + percent)

        // 推断 (1 + percent + percent)min = (viewportYMax + viewportYMin) * (1 + percent) - viewportYMax
        // => min = ((viewportYMax + viewportYMin) * (1 + percent) - viewportYMax) / (1 + percent + percent)

        float offsetPercent = getOffsetPercent();

        float minValue = ((viewportYMax + viewportYMin) * (1 + offsetPercent) - viewportYMax) / (1 + 2 * offsetPercent);
        float maxValue = viewportYMax + viewportYMin - minValue;

        float formatMaxValue = FloatUtils.keepPrecision(maxValue, decimalDigitsNumber);
        float formatMinValue = FloatUtils.keepPrecision(minValue, decimalDigitsNumber);
        float formatHighValue = FloatUtils.keepPrecision(highValue, decimalDigitsNumber);
        float formatLowValue = FloatUtils.keepPrecision(lowValue, decimalDigitsNumber);

        if (Float.compare(formatHighValue, formatMaxValue) == 0 && currentMaxValue < 0) {

            currentMaxValue = highValue;

            final int length = FloatUtils.formatFloatValue(mLabelBuffer, highValue, decimalDigitsNumber);

            String value = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);

            if (x < contentRect.width() >> 1) {
                mPaint.setTextAlign(Align.LEFT);
                String text = ARROW_LEFT + value;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.5f, highY + mTextBounds.height() * 0.5f, getPaint());
            } else {
                mPaint.setTextAlign(Align.RIGHT);
                String text = value + ARROW_RIGHT;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.5f, highY + mTextBounds.height() * 0.5f, getPaint());
            }

        }

        if (Float.compare(formatLowValue, formatMinValue) == 0 && currentMinValue < 0) {

            currentMinValue = lowValue;

            final int length = FloatUtils.formatFloatValue(mLabelBuffer, lowValue, decimalDigitsNumber);

            String value = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);

            if (x < contentRect.width() >> 1) {
                mPaint.setTextAlign(Align.LEFT);
                String text = ARROW_LEFT + value;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.5f, lowY + mTextBounds.height() * 0.5f, getPaint());
            } else {
                mPaint.setTextAlign(Align.RIGHT);
                String text = value + ARROW_RIGHT;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.5f, lowY + mTextBounds.height() * 0.5f, getPaint());
            }

        }

    }

    public void reset() {
        currentMinValue = -1f;
        currentMaxValue = -1f;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaint.setColor(textColor);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mPaint.setTextSize(textSize);
    }

    public int getTextSize() {
        return textSize;
    }
}
