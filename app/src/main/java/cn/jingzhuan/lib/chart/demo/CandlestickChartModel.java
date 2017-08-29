package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyDataBindingLayouts;
import com.airbnb.epoxy.EpoxyModelClass;

/**
 * Created by donglua on 8/29/17.
 */
@EpoxyModelClass(layout = R.layout.layout_candlestick_chart)
public abstract class CandlestickChartModel extends DataBindingEpoxyModel {
  
  public CandlestickChartModel() {
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {

  }


}
