package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/7/18.
 */

public class PointValue extends Value {

    private float value;
    private boolean isPathEnd = false;
    private int pathColor = -1; //多段path 不同颜色 应用于: 指标 神龙趋势线
    private float secondValue; //第二条数据 应用于: 指标 高抛低吸
    private float secondY;//第二条数据: y轴坐标
    private int mColor = -2;

    /**
     * 是否画圆点  此属性暂时只用于PointLineDataSet
     */
    private boolean drawCircle = true;

    public PointValue(float value) {
        this.value = value;
    }

    public PointValue(float value, int color) {
        this.value = value;
        this.mColor = color;
    }

    public PointValue(float value, float secondValue) {
        this.value = value;
        this.secondValue = secondValue;
    }

    public PointValue(float value, boolean drawCircle) {
        this.value = value;
        this.drawCircle = drawCircle;
    }

    public PointValue() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public boolean isPathEnd() {
        return isPathEnd;
    }

    public void setPathEnd(boolean pathEnd) {
        isPathEnd = pathEnd;
    }

    public boolean isValueNaN() {
        return Float.isNaN(value);
    }

    public int getPathColor() {
        return pathColor;
    }

    public void setPathColor(int pathColor) {
        this.pathColor = pathColor;
    }

    public boolean applyMultiPath() {
        return pathColor != -1;
    }

    public float getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(float secondValue) {
        this.secondValue = secondValue;
    }

    public float getSecondY() {
        return secondY;
    }

    public void setSecondY(float secondY) {
        this.secondY = secondY;
    }

    public void setDrawCircle(boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    public boolean isDrawCircle() {
        return drawCircle;
    }
}
