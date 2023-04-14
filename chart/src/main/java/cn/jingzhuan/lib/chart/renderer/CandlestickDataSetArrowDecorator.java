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
        mPaint.setColor(0xffA1abbb);
        mPaint.setTextSize(30);
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        super.calcMinMax(viewport);
    }

    public void setTextSize(int textSize) {
        mPaint.setTextSize(textSize);
    }

    public void draw(Canvas canvas, CandlestickValue candlestick, Rect contentRect, float candleWidth,
                     float x, float highY, float lowY) {

        final float highValue = candlestick.getHigh();
        final float lowValue = candlestick.getLow();

        float realViewportYMax = getViewportYMax() - getViewportYMax() * getMaxValueOffsetPercent();

        if (Float.compare(highValue, realViewportYMax) == 0 && currentMaxValue < 0) {

            currentMaxValue = highValue;

            final int length = FloatUtils.formatFloatValue(mLabelBuffer, highValue, 2);

            String value = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);
            if (x < contentRect.width() >> 1) {

                mPaint.setTextAlign(Align.LEFT);
                String text = ARROW_LEFT + value;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.6f, highY + mTextBounds.height(), getPaint());

            } else {

                mPaint.setTextAlign(Align.RIGHT);
                String text = value + ARROW_RIGHT;
                mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
                canvas.drawText(text, x + candleWidth * 0.4f, highY + mTextBounds.height(), getPaint());

            }

        }

        float realViewportYMin = getViewportYMin() + getViewportYMin() * getMinValueOffsetPercent();

        if (Float.compare(lowValue, realViewportYMin) == 0 && currentMinValue < 0) {

            currentMinValue = lowValue;

            final int length = FloatUtils.formatFloatValue(mLabelBuffer, lowValue, 2);

            String value = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);
            if (x < contentRect.width() >> 1) {

                mPaint.setTextAlign(Align.LEFT);
                String text = ARROW_LEFT + value;
                canvas.drawText(text, x + candleWidth * 0.6f, lowY, getPaint());

            } else {

                mPaint.setTextAlign(Align.RIGHT);
                String text = value + ARROW_RIGHT;
                canvas.drawText(text, x + candleWidth * 0.4f, lowY, getPaint());

            }

        }

    }

    public void reset() {
        currentMinValue = -1f;
        currentMaxValue = -1f;
    }
}
