package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;


/**
 * Statistic Data Set
 * Created by guobosheng on 22/6/23.
 */

public class TreeDataSet extends AbstractDataSet<TreeValue> {

    private List<TreeValue> mTreeValues;
    private float strokeThickness = 2;
    private int positiveColor = Color.RED;
    private int negativeColor = Color.GREEN;
    private int colorAlpha = 255;

    public TreeDataSet(List<TreeValue> treeValues) {
        this(treeValues, AxisY.DEPENDENCY_BOTH);
    }

    public TreeDataSet(List<TreeValue> treeValues, @AxisDependency int axisDependency) {
        this.mTreeValues = treeValues;
        setAxisDependency(axisDependency);
    }

    @Override
    public int getEntryCount() {
        if (mTreeValues != null) {
            if (getMinValueCount() > mTreeValues.size()) {
                return getMinValueCount();
            } else {
                return mTreeValues.size();
            }
        }
        return 0;
    }

    @Override
    public void calcMinMax(Viewport viewport) {

        if (mTreeValues == null || mTreeValues.isEmpty())
            return;

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        for (TreeValue e : getVisiblePoints(viewport)) {
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

    protected void calcMinMaxY(TreeValue e) {
        if (e == null || !e.isEnable()) return;

        float high = e.getHigh();
        float low = e.getLow();
        if (!Float.isNaN(high) && !Float.isInfinite(high)) {
            mViewportYMin = Math.min(mViewportYMin, low);
            mViewportYMax = Math.max(mViewportYMax, high);
        }
    }

    @Override
    public void setValues(List<TreeValue> values) {
        this.mTreeValues = values;
    }

    @Override
    public List<TreeValue> getValues() {
        return mTreeValues;
    }

    @Override
    public boolean addEntry(TreeValue e) {
        if (e == null) return false;

        if (mTreeValues == null) {
            mTreeValues = new ArrayList<>();
        }

        calcMinMaxY(e);

        return mTreeValues.add(e);
    }

    @Override
    public boolean removeEntry(TreeValue e) {
        if (e == null) return false;
        calcMinMaxY(e);
        return mTreeValues.remove(e);
    }

    @Override
    public int getEntryIndex(TreeValue e) {
        return mTreeValues.indexOf(e);
    }

    @Override
    public TreeValue getEntryForIndex(int index) {
        return mTreeValues.get(index);
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public void setStrokeThickness(float strokeThickness) {
        this.strokeThickness = strokeThickness;
    }

    public int getPositiveColor() {
        return positiveColor;
    }

    public void setPositiveColor(int positiveColor) {
        this.positiveColor = positiveColor;
    }

    public int getNegativeColor() {
        return negativeColor;
    }

    public void setNegativeColor(int negativeColor) {
        this.negativeColor = negativeColor;
    }

    public int getColorAlpha() {
        return colorAlpha;
    }

    public void setColorAlpha(int colorAlpha) {
        if (colorAlpha < 0) {
            this.colorAlpha = 0;
            return;
        }
        if (colorAlpha > 255) {
            this.colorAlpha = 255;
            return;
        }
        this.colorAlpha = colorAlpha;
    }
}
