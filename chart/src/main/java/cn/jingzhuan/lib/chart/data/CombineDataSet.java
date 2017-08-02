package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Point;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.AxisY.AxisDependency;

import static cn.jingzhuan.lib.chart.component.AxisY.DEPENDENCY_BOTH;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineDataSet implements IDataSet {

    List<BarDataSet> barDataSets = new CopyOnWriteArrayList<>();
    List<LineDataSet> lineDataSets = new CopyOnWriteArrayList<>();

    private float mYMin = Float.MAX_VALUE;
    private float mYMax = -Float.MAX_VALUE;

    private int mAxisDependency = AxisY.DEPENDENCY_LEFT;
    public Viewport mViewport;

    public CombineDataSet() {

    }

    @Override
    public void calcMinMax() {
        for (BarDataSet barDataSet : barDataSets) {
            for (BarValue barValue : barDataSet.getValues()) {
                calcMinMaxY(barValue);
            }
        }
        for (LineDataSet lineDataSet : lineDataSets) {
            for (PointValue point : lineDataSet.getValues()) {
                calcMinMaxY(point);
            }
        }

    }

    private void calcMinMaxY(BarValue e) {
        if (e == null) return;

        for (float v : e.getValues()) {
            if (v < mYMin) mYMin = v;
            if (v > mYMax) mYMax = v;
        }
    }


    private void calcMinMaxY(PointValue e) {
        if (e == null) return;

        if (e.getValue() < mYMin)
            mYMin = e.getValue();

        if (e.getValue() > mYMax)
            mYMax = e.getValue();
    }

    @Override
    public int getEntryCount() {

        return Math.max(barDataSets.size(), lineDataSets.size());
    }

    public List<BarDataSet> getBarDataSets() {
        return barDataSets;
    }

    public List<LineDataSet> getLineDataSets() {
        return lineDataSets;
    }


}
