package cn.jingzhuan.lib.chart.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import cn.jingzhuan.lib.chart.BaseChart;
import cn.jingzhuan.lib.chart.renderer.BarChartRenderer;
import cn.jingzhuan.lib.chart.data.BarDataSet;

/**
 * Created by Donglua on 17/8/2.
 */

public class BarChart extends BaseChart {

    public BarChart(Context context) {
        super(context);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();

        mRenderer = new BarChartRenderer(this);
    }

    public void addDataSet(BarDataSet barDataSet) {

        mRenderer.addDataSet(barDataSet);
    }

    public void setDataSet(BarDataSet barDataSet) {
        mRenderer.clearDataSet();
        addDataSet(barDataSet);
    }

}
