package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.HasValueYOffset;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;


/**
 * Bar Data Set
 * Created by Donglua on 17/8/1.
 */

public class BarDataSet extends AbstractDataSet<BarValue> implements HasValueYOffset {

    private List<BarValue> mBarValues;
    private float mBarWidth = 20;
    private boolean mAutoBarWidth = false;
    private int mForceValueCount = -1;
    private float strokeThickness = 2;

    private float mBarWidthPercent = 0.8f;

    private boolean drawValueEnable = false;
    private int valueColor = Color.BLACK;
    private float valueTextSize = 24F;
    private ValueFormatter valueFormatter;

    public BarDataSet(List<BarValue> barValues) {
        this(barValues, AxisY.DEPENDENCY_BOTH);
    }

    public BarDataSet(List<BarValue> mBarValues, @AxisDependency int axisDependency) {
        this.mBarValues = mBarValues;
        setAxisDependency(axisDependency);
    }

    @Override
    public int getEntryCount() {
        if (mForceValueCount > 0) return mForceValueCount;

        if (mBarValues != null) {
            if (getMinValueCount() > mBarValues.size()) {
                return getMinValueCount();
            } else {
                return mBarValues.size();
            }
        }
        return 0;
    }

    @Override
    public void calcMinMax(Viewport viewport) {

        if (mBarValues == null || mBarValues.isEmpty())
            return;

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        for (BarValue e : getVisiblePoints(viewport)) {
            calcMinMaxY(e);
        }
        float range = mViewportYMax - mViewportYMin;
        if (Float.compare(getMinValueOffsetPercent(), 0f) > 0f) {
            mViewportYMin = mViewportYMin - range * getMinValueOffsetPercent();
        }
        if (Float.compare(getMaxValueOffsetPercent(), 0f) > 0f) {
            mViewportYMax = mViewportYMax + range * getMaxValueOffsetPercent();
        }
    }

    protected void calcMinMaxY(BarValue e) {

        if (e == null || !e.isEnable()) return;

        for (float v : e.getValues()) {
            if (!Float.isNaN(v) && !Float.isInfinite(v)) {
                mViewportYMin = Math.min(mViewportYMin, v);
                mViewportYMax = Math.max(mViewportYMax, v);
            }
        }
    }

    @Override
    public void setValues(List<BarValue> values) {
        this.mBarValues = values;
    }

    @Override
    public List<BarValue> getValues() {
        return mBarValues;
    }

    @Override
    public boolean addEntry(BarValue e) {
        if (e == null)
            return false;

        if (mBarValues == null) {
            mBarValues = new ArrayList<>();
        }

        calcMinMaxY(e);

        return mBarValues.add(e);
    }

    @Override
    public boolean removeEntry(BarValue e) {

        if (e == null) return false;

        calcMinMaxY(e);

        return mBarValues.remove(e);
    }

    @Override
    public int getEntryIndex(BarValue e) {
        return mBarValues.indexOf(e);
    }

    @Override
    public BarValue getEntryForIndex(int index) {
        return mBarValues.get(index);
    }

    public float getBarWidth() {
        return mBarWidth;
    }

    public void setBarWidth(float mBarWidth) {
        this.mBarWidth = mBarWidth;
    }

    public void setAutoBarWidth(boolean mAutoBarWidth) {
        this.mAutoBarWidth = mAutoBarWidth;
    }

    public boolean isAutoBarWidth() {
        return mAutoBarWidth;
    }

    public void setForceValueCount(int forceValueCount) {
        this.mForceValueCount = forceValueCount;
    }

    public int getForceValueCount() {
        return mForceValueCount;
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public void setStrokeThickness(float strokeThickness) {
        this.strokeThickness = strokeThickness;
    }

    @Override public float getMaxValueOffsetPercent() {
        return maxValueOffsetPercent;
    }

    @Override public float getMinValueOffsetPercent() {
        return minValueOffsetPercent;
    }

    @Override
    public void setMinValueOffsetPercent(float minValueOffsetPercent) {
        this.minValueOffsetPercent = minValueOffsetPercent;
    }

    @Override
    public void setMaxValueOffsetPercent(float maxValueOffsetPercent) {
        this.maxValueOffsetPercent = maxValueOffsetPercent;
    }

    public void setDrawValueEnable(boolean drawValueEnable) {
        this.drawValueEnable = drawValueEnable;
    }

    public boolean isDrawValueEnable() {
        return drawValueEnable;
    }

    public int getValueColor() {
        return valueColor;
    }

    public void setValueColor(int valueColor) {
        this.valueColor = valueColor;
    }

    public float getValueTextSize() {
        return valueTextSize;
    }

    public void setValueTextSize(float valueTextSize) {
        this.valueTextSize = valueTextSize;
    }

    public ValueFormatter getValueFormatter() {
        return valueFormatter;
    }

    public void setValueFormatter(ValueFormatter valueFormatter) {
        this.valueFormatter = valueFormatter;
    }

    public void setBarWidthPercent(float mBarWidthPercent) {
        this.mBarWidthPercent = mBarWidthPercent;
    }

    public float getBarWidthPercent() {
        return mBarWidthPercent;
    }
}
