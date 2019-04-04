package cn.jingzhuan.lib.chart2.demo;

import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutBarChartClickableItemBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by donglua on 11/20/17.
 */

@EpoxyModelClass(layout = R.layout.layout_bar_chart_clickable_item)
public abstract class BarChartClickableModel extends DataBindingEpoxyModel {

  private BarDataSet barDataSet;
  private List<BarValue> barValueList = new ArrayList<>();
  private List<String> labels = Arrays.asList("data1", "data2", "data3", "data4", "data5", "data6");

  public BarChartClickableModel() {

    barValueList.add(new BarValue(11));
    barValueList.add(new BarValue(4));
    barValueList.add(new BarValue(6));
    barValueList.add(new BarValue(13));
    barValueList.add(new BarValue(8));
    barValueList.add(new BarValue(9));

    barDataSet = new BarDataSet(barValueList);
    barDataSet.setMaxValueOffsetPercent(0.2f);
    barDataSet.setDrawValueEnable(true);
    barDataSet.setValueTextSize(24);
    barDataSet.setAutoBarWidth(true);
  }

  @Override protected View buildView(final ViewGroup parent) {
    View rootView = super.buildView(parent);

    LayoutBarChartClickableItemBinding barBinding = (LayoutBarChartClickableItemBinding) rootView.getTag();

    barBinding.barChart.setDataSet(barDataSet);
    barBinding.barChart.getAxisRight().setLabelTextColor(Color.BLACK);
    barBinding.barChart.getAxisBottom().setLabels(labels);
    barBinding.barChart.getAxisBottom().setLabelTextColor(Color.BLACK);

    return rootView;
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {
  }
}
