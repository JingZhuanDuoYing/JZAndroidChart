package cn.jingzhuan.lib.chart.component;

import android.graphics.Color;
import android.graphics.Paint;

import cn.jingzhuan.lib.chart.AxisAutoValues;

/**
 * Created by Donglua on 17/7/17.
 */

public class Axis extends AbstractComponent {

    protected int mAxisPosition;

    private int mGridColor = Color.GRAY;
    private float mGridThickness = 1;
    private int mGridCount = 5;

    private float mLabelTextSize;
    private float mLabelSeparation = 0;
    private int mLabelTextColor = Color.GREEN;
    private Paint mLabelTextPaint;
    private int mMaxLabelWidth = 80;
    private int mLabelHeight = 0;
    private int mAxisColor = Color.GRAY;
    private float mAxisThickness = 2;

    private AxisAutoValues axisAutoValues = new AxisAutoValues();

    public float[] mLabelEntries = new float[]{};
    private boolean gridLineEnable = true;
    private boolean labelEnable = true;

    Axis(int axisPosition) {
        this.mAxisPosition = axisPosition;
    }

    public void setLabelTextSize(float mLabelTextSize) {
        this.mLabelTextSize = mLabelTextSize;
    }

    public void setLabelSeparation(float mLabelSeparation) {
        this.mLabelSeparation = mLabelSeparation;
    }

    public void setLabelTextColor(int mLabelTextColor) {
        this.mLabelTextColor = mLabelTextColor;
    }

    public void setLabelTextPaint(Paint mLabelTextPaint) {
        this.mLabelTextPaint = mLabelTextPaint;
    }

    public void setMaxLabelWidth(int mMaxLabelWidth) {
        this.mMaxLabelWidth = mMaxLabelWidth;
    }

    public void setLabelHeight(int mLabelHeight) {
        this.mLabelHeight = mLabelHeight;
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public int getLabelSeparation() {
        return Math.round(mLabelSeparation);
    }

    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    public Paint getLabelTextPaint() {
        return mLabelTextPaint;
    }

    public int getMaxLabelWidth() {
        if (isInside()) {
            return 0;
        }
        return mMaxLabelWidth;
    }

    public boolean isInside() {
        switch (getAxisPosition()) {
            case AxisY.LEFT_INSIDE:
            case AxisY.RIGHT_INSIDE:
            case AxisX.BOTTOM_INSIDE:
            case AxisX.TOP_INSIDE:
                return true;
            default:
                return false;
        }
    }

    public int getLabelHeight() {
        return mLabelHeight;
    }

    public AxisAutoValues getAxisAutoValues() {
        return axisAutoValues;
    }

    public int getAxisPosition() {
        return mAxisPosition;
    }

    public int getGridColor() {
        return mGridColor;
    }

    public void setGridColor(int mGridColor) {
        this.mGridColor = mGridColor;
    }

    public float getGridThickness() {
        return mGridThickness;
    }

    public void setGridThickness(float mGridThickness) {
        this.mGridThickness = mGridThickness;
    }

    public int getAxisColor() {
        return mAxisColor;
    }

    public void setAxisColor(int mAxisColor) {
        this.mAxisColor = mAxisColor;
    }

    public float getAxisThickness() {
        return mAxisThickness;
    }

    public void setAxisThickness(float mAxisThickness) {
        this.mAxisThickness = mAxisThickness;
    }

    public int getGridCount() {
        return mGridCount;
    }

    public void setGridCount(int mGridCount) {
        this.mGridCount = mGridCount;
    }

    public void setGridLineEnable(boolean gridLineEnable) {
        this.gridLineEnable = gridLineEnable;
    }

    public boolean isGridLineEnable() {
        return gridLineEnable;
    }

    public boolean isLabelEnable() {
        return labelEnable;
    }

    public void setLabelEnable(boolean labelEnable) {
        this.labelEnable = labelEnable;
    }
}
