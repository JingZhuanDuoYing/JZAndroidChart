package cn.jingzhuan.lib.chart.data;

import static cn.jingzhuan.lib.chart.component.AxisY.DEPENDENCY_BOTH;

import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

public class PointLineDataSet extends AbstractDataSet<PointValue>{
    private int mLineThickness = 2;

    protected List<PointValue> mPointValues;

    private int mForceValueCount = -1;
    private Shader shader;
    private Shader mShaderTop;
    private Shader mShaderBottom;
    private float mShaderBaseValue = Float.NaN;

    private boolean isLineVisible = true;

    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float interval = 2;
    private float phase = 0;
    private float radius = 2;

    public PointLineDataSet(List<PointValue> pointValues) {
        this(pointValues, DEPENDENCY_BOTH);
    }

    public PointLineDataSet(List<PointValue> pointValues, @AxisY.AxisDependency int depsAxis) {
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
        return Math.max(getMinValueCount(), mPointValues.size());
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

        for (PointValue e : getVisiblePoints(viewport)) {
            calcViewportMinMax(e);
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

    private void calcViewportMinMax(PointValue e) {

        if (Float.isNaN(e.getValue()) || Float.isInfinite(e.getValue())) return;

        if (e.getValue() < mViewportYMin)
            mViewportYMin = e.getValue();

        if (e.getValue() > mViewportYMax)
            mViewportYMax = e.getValue();
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
}
