package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/7/18.
 */

public class PointValue extends Value {

    private float value;
    private boolean isPathEnd = false;

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

    public boolean isPathEnd() {
        return isPathEnd;
    }

    public void setPathEnd(boolean pathEnd) {
        isPathEnd = pathEnd;
    }

    public boolean isValueNaN() {
        return Float.isNaN(value);
    }
}
