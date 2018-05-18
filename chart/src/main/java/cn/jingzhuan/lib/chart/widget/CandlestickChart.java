package cn.jingzhuan.lib.chart.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import cn.jingzhuan.lib.chart.base.BaseChart;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.renderer.CandlestickChartRenderer;

/**
 * K线图
 * Created by donglua on 8/29/17.
 */

public class CandlestickChart extends BaseChart {

  public CandlestickChart(Context context) {
    super(context);
  }

  public CandlestickChart(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CandlestickChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CandlestickChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override public void initChart() {
    super.initChart();

    mRenderer = new CandlestickChartRenderer(this);
  }

  public void addDataSet(CandlestickDataSet candlestickDataSet) {

    mRenderer.addDataSet(candlestickDataSet);
  }

  public void setDataSet(CandlestickDataSet candlestickDataSet) {
    mRenderer.clearDataSet();
    addDataSet(candlestickDataSet);
  }
}
