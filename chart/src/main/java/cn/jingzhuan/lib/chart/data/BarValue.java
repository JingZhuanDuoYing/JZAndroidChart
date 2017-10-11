package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/8/1.
 */
public class BarValue implements Value {

    private float[] yValues;

    private int mColor = 0;

    private float x;
    private float y;

    public BarValue(float[] yValues) {
        this.yValues = yValues;
    }

    public BarValue(float[] yValues, int color) {
        this.yValues = yValues;
        this.mColor = color;
    }

    public BarValue(float yValue) {
        this.yValues = new float[] { yValue, 0f };
    }

    public BarValue(float yValue, int color) {
        this.yValues = new float[] { yValue, 0f };
        this.mColor = color;
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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
