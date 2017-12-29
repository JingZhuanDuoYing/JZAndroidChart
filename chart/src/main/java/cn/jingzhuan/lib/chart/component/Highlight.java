package cn.jingzhuan.lib.chart.component;

/**
 * Created by donglua on 7/31/17.
 */

public class Highlight {

    private float x = Float.NaN;
    private float y = Float.NaN;

    private int dataIndex = 0;

    public Highlight() {
    }

    public Highlight(float x, float y, int dataIndex) {
        this.x = x;
        this.y = y;
        this.dataIndex = dataIndex;
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

    public void setDataIndex(int dataIndex) {
        this.dataIndex = dataIndex;
    }

    public int getDataIndex() {
        return dataIndex;
    }
}
