package cn.jingzhuan.lib.chart.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Donglua on 17/8/2.
 */

public abstract class ChartData<T> {

    private List<T> chartData;

    public ChartData() {
        this.chartData = new CopyOnWriteArrayList<>();
    }

    public List<T> getDataSets() {
        if (chartData == null) {
            chartData = new CopyOnWriteArrayList<>();
        }
        return chartData;
    }

    public boolean add(T e) {
        if (e == null) return false;

        return getDataSets().add(e);
    }

    public boolean remove(T e) {
        if (e == null) return false;

        return getDataSets().remove(e);
    }
}
