package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutCombineChartBinding;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutScatterChartBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

@EpoxyModelClass(layout = R.layout.layout_scatter_chart)
public abstract class ScatterChart2Model extends DataBindingEpoxyModel {

  private ScatterDataSet scatterDataSet;

  public ScatterChart2Model() {

    final List<ScatterValue> scatterValues = new ArrayList<>();

    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));
    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));
    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));

    scatterDataSet = new ScatterDataSet(scatterValues);
    scatterDataSet.setMinValueOffsetPercent(0.5f);
    scatterDataSet.setMaxValueOffsetPercent(0.5f);
    scatterDataSet.setAutoWidth(false);
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {

    if (binding instanceof LayoutScatterChartBinding) {

      Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(),
                                                         R.drawable.shape_circle);
      scatterDataSet.setShape(drawable);

      LayoutScatterChartBinding bd = (LayoutScatterChartBinding) binding;

      bd.combineChart.getAxisBottom().setGridCount(1);
      bd.combineChart.getAxisLeft().setGridCount(1);
      bd.combineChart.addDataSet(scatterDataSet);
    }

  }

}
