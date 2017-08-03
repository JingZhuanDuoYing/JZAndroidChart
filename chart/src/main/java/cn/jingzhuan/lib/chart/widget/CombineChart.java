package cn.jingzhuan.lib.chart.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import cn.jingzhuan.lib.chart.BaseChart;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.LineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.renderer.CombineChartRenderer;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChart extends BaseChart {

    public CombineChart(Context context) {
        super(context);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();

        mRenderer = new CombineChartRenderer(this);
    }

    public void addDataSet(BarDataSet barDataSet) {
        barDataSet.setChart(this);
        mRenderer.addDataSet(barDataSet);
    }

    public void addDataSet(LineDataSet lineDataSet) {
        lineDataSet.setChart(this);
        mRenderer.addDataSet(lineDataSet);
    }

}
