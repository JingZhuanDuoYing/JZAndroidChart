package cn.jingzhuan.lib.chart.data;

import android.graphics.RectF;

public class ScatterTextValue extends Value{

    private boolean visible = false;
    private float high;
    private float low;
    private RectF roundRect;
    private float anchor;

    public ScatterTextValue() {
    }

    public ScatterTextValue(boolean visible, float high, float low) {
        this.visible = visible;
        this.high = high;
        this.low = low;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public RectF getRoundRect() {
        return roundRect;
    }

    public void setRoundRect(RectF roundRect) {
        this.roundRect = roundRect;
    }

    public float getAnchor() {
        return anchor;
    }

    public void setAnchor(float anchor) {
        this.anchor = anchor;
    }
}
