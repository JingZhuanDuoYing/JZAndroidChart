package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutBarChartBinding;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutCombineChartBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

@EpoxyModelClass(layout = R.layout.layout_combine_chart)
public abstract class ScatterChartModel extends DataBindingEpoxyModel {

  private ScatterDataSet scatterDataSet;

  public ScatterChartModel() {

    final List<ScatterValue> scatterValues = new ArrayList<>();

    scatterValues.add(new ScatterValue(2));
    scatterValues.add(new ScatterValue(3));
    scatterValues.add(new ScatterValue(4));
    scatterValues.add(new ScatterValue(6));
    scatterValues.add(new ScatterValue(9));
    scatterValues.add(new ScatterValue(2));
    scatterValues.add(new ScatterValue(4));
    scatterValues.add(new ScatterValue(6));
    scatterValues.add(new ScatterValue(9));
    scatterValues.add(new ScatterValue(0));
    scatterValues.add(new ScatterValue(8));
    scatterValues.add(new ScatterValue(9));
    scatterValues.add(new ScatterValue(4));
    scatterValues.add(new ScatterValue(1));
    scatterValues.add(new ScatterValue(2));

    scatterDataSet = new ScatterDataSet(scatterValues);
    scatterDataSet.setAutoWidth(true);
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {

    if (binding instanceof LayoutCombineChartBinding) {

      Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), R.drawable.ic_example);
      scatterDataSet.setShape(drawable);

      LayoutCombineChartBinding bd = (LayoutCombineChartBinding) binding;

      bd.combineChart.addDataSet(scatterDataSet);
    }

  }

}
