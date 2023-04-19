package cn.jingzhuan.lib.chart.data;


import android.graphics.Shader;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

import static cn.jingzhuan.lib.chart.component.AxisY.*;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineDataSet extends AbstractDataSet<PointValue> {

    private int mLineThickness = 2;

    protected List<PointValue> mPointValues;
    protected PointValue mHeadPoint;

    private int mForceValueCount = -1;
    private Shader shader;
    private Shader mShaderTop;
    private Shader mShaderBottom;
    private float mShaderBaseValue = Float.NaN;

    private boolean isLineVisible = true;
    private boolean isPartLine = false; // 线段
    private boolean isDrawBand = false; // 带状线
    private boolean isHorizontalLine = false; // 单值水平线

    public LineDataSet(List<PointValue> pointValues) {
        this(pointValues, DEPENDENCY_BOTH);
    }

    public LineDataSet(List<PointValue> pointValues, @AxisDependency int depsAxis) {
        mPointValues = pointValues;
        if (mPointValues == null)
            mPointValues = new ArrayList<>();

        setAxisDependency(depsAxis);
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
        if (mForceValueCount > 0) return mForceValueCount;

        int entryCount = mPointValues.size();
        if (getMinValueCount() > entryCount) {
            entryCount = getMinValueCount();
        }
        return entryCount;
    }

    @Override
    public void calcMinMax(Viewport viewport) {

        if (mPointValues == null || mPointValues.isEmpty())
            return;

        calcViewportY(viewport);
    }

    private void calcViewportY(Viewport viewport) {

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        if (mHeadPoint != null) {
            calcViewportMinMax(mHeadPoint);
        }
        for (PointValue e : getVisiblePoints(viewport)) {
            calcViewportMinMax(e);
        }

        float range = mViewportYMax - mViewportYMin;
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMin = mViewportYMin - range * getOffsetPercent();
        }
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMax = mViewportYMax + range * getOffsetPercent();
        }

        if (mViewportYMax == 0 && mViewportYMin == 0) {
            mViewportYMax = 1;
            mViewportYMin = -1;
        }
    }

    @Override
    public void setValues(List<PointValue> values) {
        this.mPointValues = values;
    }

    @Override
    public List<PointValue> getValues() {
        return mPointValues;
    }

    public PointValue getHeadPoint() {
        return mHeadPoint;
    }

    public void setHeadPoint(PointValue headPoint) {
        this.mHeadPoint = headPoint;
    }

    private void calcViewportMinMax(PointValue e) {

        if (Float.isNaN(e.getValue()) || Float.isInfinite(e.getValue())) return;

        if (e.getValue() < mViewportYMin)
            mViewportYMin = e.getValue();

        if (e.getValue() > mViewportYMax)
            mViewportYMax = e.getValue();

        if (isDrawBand){
            if (e.getSecondValue() < e.getValue()){
                if (e.getSecondValue() < mViewportYMin)
                    mViewportYMin = e.getSecondValue();
            }

            if (e.getSecondValue() > e.getValue()){
                if (e.getSecondValue() > mViewportYMax)
                    mViewportYMax = e.getSecondValue();
            }

        }
    }

    @Override
    public boolean addEntry(PointValue e) {

        if (e == null)
            return false;

        if (mPointValues == null) {
            mPointValues = new ArrayList<>();
        }

        calcViewportMinMax(e);

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

        //if (removed) {
        //    setMinMax();
        //}

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

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public float getShaderBaseValue() {
        return mShaderBaseValue;
    }

    public void setShaderBaseValue(float shaderBaseValue, Shader shaderTop, Shader shaderBottom) {
        this.mShaderBaseValue = shaderBaseValue;
        this.mShaderTop = shaderTop;
        this.mShaderBottom = shaderBottom;
    }

    public Shader getShaderTop() {
        return mShaderTop;
    }

    public Shader getShaderBottom() {
        return mShaderBottom;
    }

    public void setLineVisible(boolean lineVisible) {
        isLineVisible = lineVisible;
    }

    public boolean isLineVisible() {
        return isLineVisible;
    }

    public boolean isPartLine() {
        return isPartLine;
    }

    public void setPartLine(boolean partLine) {
        isPartLine = partLine;
    }

    public boolean isDrawBand() {
        return isDrawBand;
    }

    public void setDrawBand(boolean drawBand) {
        isDrawBand = drawBand;
    }


    public boolean isHorizontalLine() {
        return isHorizontalLine;
    }

    public void setHorizontalLine(boolean horizontalLine) {
        isHorizontalLine = horizontalLine;
    }
}
