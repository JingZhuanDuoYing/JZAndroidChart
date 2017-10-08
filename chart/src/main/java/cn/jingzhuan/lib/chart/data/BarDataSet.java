package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.Viewport;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;


/**
 * Created by Donglua on 17/8/1.
 */

public class BarDataSet extends AbstractDataSet<BarValue> {

    private List<BarValue> mBarValues;
    private float mBarWidth = 20;
    private boolean mAutoBarWidth = false;
    private int mForceValueCount = -1;

    public BarDataSet(List<BarValue> barValues) {
        this(barValues, AxisY.DEPENDENCY_BOTH);
    }

    public BarDataSet(List<BarValue> mBarValues, @AxisDependency int axisDependency) {
        this.mBarValues = mBarValues;

        calcMinMax(new Viewport());

        setAxisDependency(axisDependency);
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
    public void calcMinMax(Viewport viewport) {

        if (mBarValues == null || mBarValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (BarValue e : getVisiblePoints(viewport)) {
            calcMinMaxY(e);
        }

        mViewportYMax = mYMax;
        mViewportYMin = mYMin;

    }

    public List<BarValue> getVisiblePoints(Viewport viewport) {
        int from = (int) (viewport.left * mBarValues.size());
        int to  = (int) (viewport.right * mBarValues.size());

        return mBarValues.subList(from, to);
    }

    public int getVisibleValueCount(Viewport viewport) {
        return getVisiblePoints(viewport).size();
    }

    public void calcMinMaxY(BarValue e) {

        if (e == null) return;

        for (float v : e.getValues()) {
            if (v < mYMin) mYMin = v;
            if (v > mYMax) mYMax = v;
        }
    }

    @Override
    public void setValues(List<BarValue> values) {
        this.mBarValues = values;

        //setMinMax();
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
}
