package cn.jingzhuan.lib.chart.data;

import java.util.Objects;

/**
 * @since 2023-08-28
 */

public class DrawLineValue extends Value {

    private float value;

    private long time;

    private int index;

    private boolean visible = true;

    public DrawLineValue(float value, long time, int index) {
        this.value = value;
        this.time = time;
        this.index = index;
    }

    public DrawLineValue(float value, long time, int index, boolean visible) {
        this.value = value;
        this.time = time;
        this.index = index;
        this.visible = visible;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawLineValue that = (DrawLineValue) o;
        return Float.compare(that.value, value) == 0 && time == that.time && index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, time, index, getX(), getY());
    }

}
