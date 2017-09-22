package cn.jingzhuan.lib.chart.data;


import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

import static cn.jingzhuan.lib.chart.component.AxisY.*;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineDataSet extends AbstractDataSet<PointValue> {

    private int mLineThickness = 2;

    protected List<PointValue> mPointValues;

    private int mForceValueCount = -1;
    public LineDataSet(List<PointValue> pointValues) {
        this(pointValues, DEPENDENCY_BOTH);
    }

    public LineDataSet(List<PointValue> pointValues, @AxisDependency int depsAxis) {
        mPointValues = pointValues;
        if (mPointValues == null)
            mPointValues = new ArrayList<>();

        onViewportChange(new Viewport());

        setAxisDependency(depsAxis);

        calcMinMax();
    }

    public int getLineThickness() {
        return mLineThickness;
    }

    public void setLineThickness(int mLineThickness) {
        this.mLineThickness = mLineThickness;
    }

    public List<PointValue> getLines() {
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

        for (PointValue e : mPointValues) {
            calcMinMax(e);
        }

        calcViewportY(mViewport);

    }

    public void calcViewportY(Viewport viewport) {
        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        for (PointValue e : getVisiblePoints(viewport)) {
            calcViewportMinMax(e);
        }
    }

    @Override
    public void setValues(List<PointValue> values) {
        this.mPointValues = values;
        notifyDataSetChanged();
    }

    @Override
    public List<PointValue> getValues() {
        return mPointValues;
    }


    /**
     * Updates the min and max x and y value of this DataSet based on the given Entry.
     *
     * @param e
     */
    public void calcMinMax(PointValue e) {

        if (e == null)
            return;

        calcMinMaxX(e);

        calcMinMaxY(e);
    }

    protected void calcViewportMinMax(PointValue e) {
        if (e.getValue() < mViewportYMin)
            mViewportYMin = e.getValue();

        if (e.getValue() > mViewportYMax)
            mViewportYMax = e.getValue();
    }

    protected void calcMinMaxX(PointValue e) {

//        if (e.getX() < mXMin)
//            mXMin = e.getX();
//
//        if (e.getX() > mXMax)
//            mXMax = e.getX();
    }

    protected void calcMinMaxY(PointValue e) {

        if (e.getValue() < mYMin)
            mYMin = e.getValue();

        if (e.getValue() > mYMax)
            mYMax = e.getValue();
    }

    public void notifyDataSetChanged() {
        calcMinMax();
    }

    @Override
    public boolean addEntry(PointValue e) {

        if (e == null)
            return false;

        if (mPointValues == null) {
            mPointValues = new ArrayList<>();
        }

        calcMinMax(e);

        // add the entry
        return mPointValues.add(e);
    }

    @Override
    public boolean removeEntry(PointValue e) {

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
    public int getEntryIndex(PointValue e) {
        return mPointValues.indexOf(e);
    }

    @Override
    public PointValue getEntryForIndex(int index) {
        return mPointValues.get(index);
    }

    public void setForceValueCount(int mForceValueCount) {
        this.mForceValueCount = mForceValueCount;
    }

    public int getForceValueCount() {
        return mForceValueCount;
    }


    public void onViewportChange(Viewport viewport) {
        this.mViewport = viewport;

        calcViewportY(viewport);
    }

    protected List<PointValue> getVisiblePoints(Viewport viewport) {
        int from = (int) (viewport.left * mPointValues.size());
        int to  = (int) (viewport.right * mPointValues.size());

        return mPointValues.subList(from, to);
    }

}
