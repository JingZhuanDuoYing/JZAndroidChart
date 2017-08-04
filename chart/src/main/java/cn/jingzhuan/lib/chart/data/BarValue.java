package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarValue implements Value {

    private float[] yValues;

    private int mColor = -1;

    public BarValue(float[] yValues) {
        this.yValues = yValues;
    }

    public BarValue(float yValue) {
        this.yValues = new float[] { yValue };
    }

    public float[] getValues() {
        return yValues;
    }

    public void setValues(float[] yValues) {
        this.yValues = yValues;
    }

    public void setValues(float yValue) {
        this.yValues = new float[] { yValue };
    }

    public int getValueCount() {

        if (yValues == null) return 0;

        return yValues.length;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
