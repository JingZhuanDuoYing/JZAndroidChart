package cn.jingzhuan.lib.chart.data;

import android.graphics.Path;

public class PartLineData {

    public PartLineData(Path path, int color) {
        this.path = path;
        this.color = color;
    }

    public PartLineData(Path path) {
        this.path = path;
    }

    //xian线段
    private Path path;
    //颜色
    private  int color;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}