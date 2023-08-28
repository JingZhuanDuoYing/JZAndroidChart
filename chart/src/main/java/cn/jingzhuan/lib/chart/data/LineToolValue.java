package cn.jingzhuan.lib.chart.data;

import java.util.Objects;

/**
 * @since 2023-08-28
 */

public class LineToolValue extends Value {

    private float value;

    private long time;

    private boolean visible = true;

    public LineToolValue(float value, long time) {
        this.value = value;
        this.time = time;
    }

    public LineToolValue(float value, long time, boolean visible) {
        this.value = value;
        this.time = time;
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
        LineToolValue that = (LineToolValue) o;
        return Float.compare(that.value, value) == 0 && time == that.time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, time, getX(), getY());
    }

}
