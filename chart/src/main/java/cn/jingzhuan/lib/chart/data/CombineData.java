package cn.jingzhuan.lib.chart.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData {

    private List<BarDataSet> barData;
    private List<LineDataSet> lineData;

    public CombineData() {
        barData = new ArrayList<>();
        lineData = new ArrayList<>();
    }

    public List<BarDataSet> getBarData() {
        return barData;
    }

    public List<LineDataSet> getLineData() {
        return lineData;
    }

    public void addDataSet(BarDataSet dataSet) {
        getBarData().add(dataSet);
    }

    public void addDataSet(LineDataSet dataSet) {
        getLineData().add(dataSet);
    }

}
