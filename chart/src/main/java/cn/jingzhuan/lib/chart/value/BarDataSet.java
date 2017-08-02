package cn.jingzhuan.lib.chart.value;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;


/**
 * Created by Donglua on 17/8/1.
 */

public class BarDataSet extends AbstractDataSet<BarValue> {

    private List<BarValue> mBarValues;
    private float mBarWidth = 20;
    private int mForceValueCount = -1;

    public BarDataSet(List<BarValue> mBarValues) {
        this.mBarValues = mBarValues;

        calcMinMax();
    }

    public BarDataSet() {
    }

    @Override
    public int getEntryCount() {
        if (mForceValueCount > 0) return mForceValueCount;

        if (mBarValues != null) {
            return mBarValues.size();
        }
        return 0;
    }

    @Override
    public void calcMinMax() {

        if (mBarValues == null || mBarValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (BarValue e : mBarValues) {
            calcMinMaxY(e);
        }

//        calcViewportY(mViewport);

        if (mAxisLeft != null) {
            mAxisLeft.setYMax(mYMax);
            mAxisLeft.setYMin(mYMin);
        }
        if (mAxisRight != null) {
            mAxisRight.setYMax(mYMax);
            mAxisRight.setYMin(mYMin);
        }
    }

    private void calcMinMaxY(BarValue e) {

        if (e == null) return;

        for (float v : e.getValues()) {
            if (v < mYMin) mYMin = v;
            if (v > mYMax) mYMax = v;
        }
    }

    @Override
    public void setValues(List<BarValue> values) {
        this.mBarValues = values;

        calcMinMax();
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

    public void setChart(Chart chart) {
        switch (mDepsAxis) {
            case AxisY.DEPENDENCY_LEFT:
                this.mAxisLeft = chart.getAxisLeft();
                this.mAxisRight = null;
                break;
            case AxisY.DEPENDENCY_RIGHT:
                this.mAxisLeft = null;
                this.mAxisRight = chart.getAxisRight();
                break;
            case AxisY.DEPENDENCY_BOTH:
                this.mAxisLeft = chart.getAxisLeft();
                this.mAxisRight = chart.getAxisRight();
                break;
        }

        this.mViewport = chart.getCurrentViewport();

        calcMinMax();
    }

}
