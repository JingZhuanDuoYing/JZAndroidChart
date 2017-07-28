package cn.jingzhuan.lib.chart.value;


import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

import static cn.jingzhuan.lib.chart.component.AxisY.*;

/**
 * Created by Donglua on 17/7/19.
 */

public class Line<T extends PointValue> extends AbstractDataSet<T> {

    private int mLineColor = Color.GRAY;
    private int mLineThickness = 2;

    protected List<T> mPointValues;

    private int mForceValueCount = -1;

    @AxisDependency private int mDepsAxis = DEPENDENCY_BOTH;
    protected AxisY mAxisLeft;
    protected AxisY mAxisRight;

    public Line(List<T> pointValues) {
        this(pointValues, DEPENDENCY_BOTH);
    }

    public Line(List<T> pointValues, @AxisDependency int depsAxis) {
        mPointValues = pointValues;
        if (mPointValues == null)
            mPointValues = new ArrayList<>();

        setViewport(new Viewport());

        this.mDepsAxis = depsAxis;

        calcMinMax();
    }

    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    public int getLineThickness() {
        return mLineThickness;
    }

    public void setLineThickness(int mLineThickness) {
        this.mLineThickness = mLineThickness;
    }

    public List<T> getLines() {
        return mPointValues;
    }

    @Override
    public int getEntryCount() {
        if (mPointValues == null) return 0;
        return mForceValueCount > 0 ? mForceValueCount : mPointValues.size();
    }

    @Override
    public void calcMinMax() {

        if (mPointValues == null || mPointValues.isEmpty())
            return;

        mYMax = -Float.MAX_VALUE;
        mYMin = Float.MAX_VALUE;
        mXMax = -Float.MAX_VALUE;
        mXMin = Float.MAX_VALUE;

        for (T e : mPointValues) {
            calcMinMax(e);
        }

        calcViewportY(mViewport);

    }

    public void calcViewportY(Viewport viewport) {
        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        Log.d("Line", "calcViewportY");

        for (T e : getVisiblePoints(viewport)) {
            calcViewportMinMaxX(e);
        }

        setAxisViewportY(mAxisLeft, mViewportYMin, mViewportYMax);
        setAxisViewportY(mAxisRight, mViewportYMin, mViewportYMax);
    }

    protected static void setAxisViewportY(AxisY axis, float min, float max) {

        if (axis != null) {

            axis.setYMin(min);
            axis.setYMax(max);
        }
    }

    @Override
    public void setValues(List<T> values) {
        this.mPointValues = values;
        notifyDataSetChanged();
    }

    @Override
    public List<T> getValues() {
        return mPointValues;
    }


    /**
     * Updates the min and max x and y value of this DataSet based on the given Entry.
     *
     * @param e
     */
    protected void calcMinMax(T e) {

        if (e == null)
            return;

        calcMinMaxX(e);

        calcMinMaxY(e);
    }

    protected void calcViewportMinMaxX(T e) {
        if (e.getValue() < mViewportYMin)
            mViewportYMin = e.getValue();

        if (e.getValue() > mViewportYMax)
            mViewportYMax = e.getValue();
    }

    protected void calcMinMaxX(T e) {

        if (e.getX() < mXMin)
            mXMin = e.getX();

        if (e.getX() > mXMax)
            mXMax = e.getX();
    }

    protected void calcMinMaxY(T e) {

        if (e.getValue() < mYMin)
            mYMin = e.getValue();

        if (e.getValue() > mYMax)
            mYMax = e.getValue();
    }

    public void notifyDataSetChanged() {
        calcMinMax();
    }

    @Override
    public boolean addEntry(T e) {

        if (e == null)
            return false;

        List<T> values = getValues();
        if (values == null) {
            values = new ArrayList<T>();
        }

        calcMinMax(e);

        // add the entry
        return values.add(e);
    }

    @Override
    public boolean removeEntry(T e) {

        if (e == null)
            return false;

        if (mPointValues == null)
            return false;

        // remove the entry
        boolean removed = mPointValues.remove(e);

        if (removed) {
            calcMinMax();
        }

        return removed;
    }

    @Override
    public int getEntryIndex(T e) {
        return mPointValues.indexOf(e);
    }

    @Override
    public T getEntryForIndex(int index) {
        return mPointValues.get(index);
    }

    public void setForceValueCount(int mForceValueCount) {
        this.mForceValueCount = mForceValueCount;
    }

    public int getForceValueCount() {
        return mForceValueCount;
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

    public void setViewport(Viewport viewport) {
        this.mViewport = viewport;

        calcViewportY(viewport);
    }

    public AxisY getAxisLeft() {
        return mAxisLeft;
    }

    public AxisY getAxisRight() {
        return mAxisRight;
    }

    protected List<T> getVisiblePoints(Viewport viewport) {
        int from = (int) (viewport.left * mPointValues.size());
        int to  = (int) (viewport.right * mPointValues.size());

        return mPointValues.subList(from, to);
    }
}
