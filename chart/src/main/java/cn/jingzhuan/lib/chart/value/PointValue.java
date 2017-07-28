package cn.jingzhuan.lib.chart.value;

/**
 * Created by Donglua on 17/7/18.
 */

public class PointValue {

    private float value;
    private float x;
    private float y;

    public PointValue(float value) {
        this.value = value;
    }

    public PointValue() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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
