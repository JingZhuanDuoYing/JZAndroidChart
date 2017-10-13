package cn.jingzhuan.lib.chart.data;

import android.graphics.Paint;

/**
 * Created by Donglua on 17/8/1.
 */
public class BarValue implements Value {

    private float[] yValues;

    private int mColor = 0;

    private float x;
    private float y;
    private Paint.Style mPaintStyle = Paint.Style.FILL;

    public BarValue(float[] yValues) {
        this.yValues = yValues;
    }

    public BarValue(float[] yValues, int color) {
        this.yValues = yValues;
        this.mColor = color;
    }

    public BarValue(float value1, float value2, int color) {
        this.yValues = new float[] {value1, value2};
        this.mColor = color;
    }

    public BarValue(float value1, float value2, int color, Paint.Style paintStyle) {
        this.yValues = new float[] {value1, value2};
        this.mColor = color;
        this.mPaintStyle = paintStyle;
    }

    public BarValue(float value, int color, Paint.Style paintStyle) {
        this.yValues = new float[] {value, 0f};
        this.mColor = color;
        this.mPaintStyle = paintStyle;
    }

    public BarValue(float[] values, int color, Paint.Style paintStyle) {
        this.yValues = values;
        this.mColor = color;
        this.mPaintStyle = paintStyle;
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
        this.yValues = new float[] { yValue, 0f };
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

    public void setPaintStyle(Paint.Style mPaintStyle) {
        this.mPaintStyle = mPaintStyle;
    }

    public Paint.Style getPaintStyle() {
        return mPaintStyle;
    }

}
