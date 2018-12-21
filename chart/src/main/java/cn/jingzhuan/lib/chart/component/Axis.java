package cn.jingzhuan.lib.chart.component;

import android.graphics.Color;
import android.graphics.Paint;

import cn.jingzhuan.lib.chart.AxisAutoValues;
import cn.jingzhuan.lib.chart.data.GirdLineColorSetter;
import cn.jingzhuan.lib.chart.data.LabelColorSetter;
import cn.jingzhuan.lib.chart.data.ValueFormatter;
import java.util.List;

/**
 * Created by Donglua on 17/7/17.
 */

public class Axis extends AbstractComponent {

    private int mAxisPosition;

    private int mGridColor = Color.GRAY;
    private GirdLineColorSetter mGirdLineColorSetter = null;
    private float mGridThickness = 1;
    private int mGridCount = 3;

    private float mLabelTextSize;
    private float mLabelSeparation = 0;
    private int mLabelTextColor = Color.GREEN;
    private Paint mLabelTextPaint;
    private int mLabelWidth = 100;
    private int mLabelHeight = 0;
    private int mAxisColor = Color.GRAY;
    private float mAxisThickness = 2;
    private List<String> mLabels;

    private AxisAutoValues axisAutoValues = new AxisAutoValues();

    private ValueFormatter mLabelValueFormatter;

    public float[] mLabelEntries = new float[]{};
    private boolean gridLineEnable = true;
    private boolean labelEnable = true;

    private float mDashedGridIntervals[] = null;
    private float mDashedGridPhase = -1;

    private LabelColorSetter mLabelColorSetter;

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

    public void setLabelWidth(int mLabelWidth) {
        this.mLabelWidth = mLabelWidth;
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

    public int getLabelWidth() {
        if (isInside()) {
            return 0;
        }
        return mLabelWidth;
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

    public void setAxisPosition(int mAxisPosition) {
        this.mAxisPosition = mAxisPosition;
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

    public ValueFormatter getLabelValueFormatter() {
        return mLabelValueFormatter;
    }

    public void setLabelValueFormatter(ValueFormatter mValueFormatter) {
        this.mLabelValueFormatter = mValueFormatter;
    }

    public float getDashedGridPhase() {
        return mDashedGridPhase;
    }

    public float[] getDashedGridIntervals() {
        return mDashedGridIntervals;
    }

    public void enableGridDashPathEffect(float intervals[], float phase) {
        this.mDashedGridIntervals = intervals;
        this.mDashedGridPhase = phase;
    }

    public void setGirdLineColorSetter(GirdLineColorSetter mGirdLineColorSetter) {
        this.mGirdLineColorSetter = mGirdLineColorSetter;
    }

    public GirdLineColorSetter getGirdLineColorSetter() {
        return mGirdLineColorSetter;
    }


    public List<String> getLabels() {
        return mLabels;
    }

    public void setLabels(List<String> mLabels) {
        this.mLabels = mLabels;
    }

    public LabelColorSetter getLabelColorSetter() {
        return mLabelColorSetter;
    }

    public void setLabelColorSetter(LabelColorSetter mLabelColorSetter) {
        this.mLabelColorSetter = mLabelColorSetter;
    }

}
