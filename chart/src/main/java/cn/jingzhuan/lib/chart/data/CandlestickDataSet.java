package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.util.SparseArray;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 8/29/17.
 */

public class CandlestickDataSet extends AbstractDataSet<CandlestickValue> {

    private List<CandlestickValue> candlestickValues;

    private boolean mAutoWidth = true;
    private float mCandleWidth = -1;

    /**
     * 阳线 颜色
     */
    private int mIncreasingColor = 0xFFf84b4b;

    /**
     * 阴线 颜色
     */
    private int mDecreasingColor = 0xFF1deb5b;

    /**
     * 十字光标线 颜色
     */
    private int mNeutralColor = Color.WHITE;

    private int mLimitUpColor = Color.TRANSPARENT;

    private int mLimitUpColor20 = Color.TRANSPARENT;

    /**
     * 缺口 颜色
     */
    private int mGapColor = Color.LTGRAY;

    private Paint.Style mLimitUpPaintStyle = null;

    private float strokeThickness = 4;

    /**
     * 阳线、阴线样式-> 实心或空心
     */
    private Paint.Style mIncreasingPaintStyle = Paint.Style.FILL;

    private Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

    /**
     * 是否显示缺口
     */
    private boolean mEnableGap = false;

    private SparseArray<Pair<Float, Float>> mLowGaps;

    private SparseArray<Pair<Float, Float>> mHighGaps;

    private float candleWidthPercent = 0.8f;

    public CandlestickDataSet(List<CandlestickValue> candlestickValues) {
        this(candlestickValues, AxisY.DEPENDENCY_BOTH);
    }

    public CandlestickDataSet(List<CandlestickValue> candlestickValues, @AxisY.AxisDependency int axisDependency) {
        this.candlestickValues = candlestickValues;

        setAxisDependency(axisDependency);
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        if (candlestickValues == null || candlestickValues.isEmpty()) return;

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        List<CandlestickValue> visiblePoints = getVisiblePoints(viewport);

        for (int i = 0; i < visiblePoints.size(); i++) {
            CandlestickValue e = visiblePoints.get(i);
            calcViewportMinMax(e);
        }

        if (mEnableGap) {
            float max = -Float.MAX_VALUE;
            float min = Float.MAX_VALUE;

            for (int i = candlestickValues.size() - 1; i >= 0; i--) {
                CandlestickValue e = candlestickValues.get(i);

                if (Float.isNaN(e.getLow())) continue;
                if (Float.isNaN(e.getHigh())) continue;

                if (Float.isInfinite(e.getLow())) continue;
                if (Float.isInfinite(e.getHigh())) continue;

                if (min != Float.MAX_VALUE && max != -Float.MAX_VALUE) {
                    if (min - e.getHigh() > 0.01f) { // 上涨缺口
                        mHighGaps.put(i, new Pair<>(e.getHigh(), min));
                    }
                    if (e.getLow() - max > 0.01f) { // 下跌缺口
                        mLowGaps.put(i, new Pair<>(e.getLow(), max));
                    }
                }
                if (e.getLow() < min) min = e.getLow();
                if (e.getHigh() > max) max = e.getHigh();
            }
        }

        float range = mViewportYMax - mViewportYMin;
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMin = mViewportYMin - range * getOffsetPercent();
        }
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMax = mViewportYMax + range * getOffsetPercent();
        }
    }

    public void setEnableGap(boolean enableGap) {
        this.mEnableGap = enableGap;
        if (mLowGaps == null) {
            mLowGaps = new SparseArray<>();
        }
        if (mHighGaps == null) {
            mHighGaps = new SparseArray<>();
        }
    }

    private void calcViewportMinMax(CandlestickValue e) {

        if (e == null || !e.isVisible()) return;

        if (Float.isNaN(e.getLow())) return;
        if (Float.isNaN(e.getHigh())) return;

        if (Float.isInfinite(e.getLow())) return;
        if (Float.isInfinite(e.getHigh())) return;

        if (e.getLow() < mViewportYMin) {
            mViewportYMin = e.getLow();
        }

        if (e.getHigh() > mViewportYMax) {
            mViewportYMax = e.getHigh();
        }
    }

    @Override
    public int getEntryCount() {
        if (candlestickValues == null) return 0;
        return Math.max(getMinValueCount(), candlestickValues.size());
    }

    public int getRealEntryCount() {
        if (candlestickValues == null) return 0;
        return candlestickValues.size();
    }

    @Override
    public void setValues(List<CandlestickValue> values) {
        this.candlestickValues = values;
    }

    @Override
    public List<CandlestickValue> getValues() {
        return candlestickValues;
    }

    @Override
    public boolean addEntry(CandlestickValue value) {
        if (value == null)
            return false;

        if (candlestickValues == null) {
            candlestickValues = new ArrayList<>();
        }

        calcViewportMinMax(value);

        // add the entry
        return candlestickValues.add(value);
    }

    @Override
    public boolean removeEntry(CandlestickValue value) {
        if (value == null) return false;

        calcViewportMinMax(value);

        return candlestickValues.remove(value);
    }

    @Override
    public int getEntryIndex(CandlestickValue e) {
        return candlestickValues.indexOf(e);
    }

    @Override
    public CandlestickValue getEntryForIndex(int index) {
        return candlestickValues.get(index);
    }

    public void setAutoWidth(boolean mAutoWidth) {
        this.mAutoWidth = mAutoWidth;
    }

    public boolean isAutoWidth() {
        return mAutoWidth;
    }

    public int getDecreasingColor() {
        return mDecreasingColor;
    }

    public void setDecreasingColor(int decreasingColor) {
        this.mDecreasingColor = decreasingColor;
    }

    public int getIncreasingColor() {
        return mIncreasingColor;
    }

    public void setIncreasingColor(int increasingColor) {
        this.mIncreasingColor = increasingColor;
    }

    public float getCandleWidth() {
        return mCandleWidth;
    }

    public void setCandleWidth(float mCandleWidth) {
        this.mCandleWidth = mCandleWidth;
    }

    public int getNeutralColor() {
        return mNeutralColor;
    }

    public void setNeutralColor(int mNeutralColor) {
        this.mNeutralColor = mNeutralColor;
    }

    public void setLimitUpColor(int mLimitUpColor) {
        this.mLimitUpColor = mLimitUpColor;
    }

    public int getLimitUpColor() {
        return mLimitUpColor;
    }

    public void setLimitUpColor20(int mLimitUpColor) {
        this.mLimitUpColor20 = mLimitUpColor;
    }

    public int getLimitUpColor20() {
        return mLimitUpColor20;
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public void setStrokeThickness(float strokeThickness) {
        this.strokeThickness = strokeThickness;
    }

    public Paint.Style getIncreasingPaintStyle() {
        return mIncreasingPaintStyle;
    }

    public void setIncreasingPaintStyle(Paint.Style increasingPaintStyle) {
        this.mIncreasingPaintStyle = increasingPaintStyle;
    }

    public Paint.Style getDecreasingPaintStyle() {
        return mDecreasingPaintStyle;
    }


    public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
        this.mDecreasingPaintStyle = decreasingPaintStyle;
    }

    public Paint.Style getLimitUpPaintStyle() {
        return mLimitUpPaintStyle;
    }

    public void setLimitUpPaintStyle(Paint.Style limitUpPaintStyle) {
        this.mLimitUpPaintStyle = limitUpPaintStyle;
    }

    public SparseArray<Pair<Float, Float>> getLowGaps() {
        return mLowGaps;
    }

    public SparseArray<Pair<Float, Float>> getHighGaps() {
        return mHighGaps;
    }

    public boolean isEnableGap() {
        return mEnableGap;
    }

    public int getGapColor() {
        return mGapColor;
    }

    public void setGapColor(int mGapColor) {
        this.mGapColor = mGapColor;
    }

    public float getCandleWidthPercent() {
        return candleWidthPercent;
    }

    public void setCandleWidthPercent(float candleWidthPercent) {
        this.candleWidthPercent = candleWidthPercent;
    }

}
