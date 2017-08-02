package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.demo.databinding.LayoutBarChartBinding;
import cn.jingzhuan.lib.chart.value.BarDataSet;
import cn.jingzhuan.lib.chart.value.BarValue;

/**
 * Created by Donglua on 17/8/2.
 */
@EpoxyModelClass(layout = R.layout.layout_bar_chart)
public abstract class BarChartModel extends DataBindingEpoxyModel {

    private BarDataSet barDataSet;

    public BarChartModel() {

        List<BarValue> barValueList = new ArrayList<>();

        barValueList.add(new BarValue(11));
        barValueList.add(new BarValue(10));
        barValueList.add(new BarValue(11));
        barValueList.add(new BarValue(13));
        barValueList.add(new BarValue(11));
        barValueList.add(new BarValue(12));
        barValueList.add(new BarValue(12));
        barValueList.add(new BarValue(13));
        barValueList.add(new BarValue(15));

        barDataSet = new BarDataSet(barValueList);
        barDataSet.setAutoBarWidth(true);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
        if (binding instanceof LayoutBarChartBinding) {

            LayoutBarChartBinding barBinding = (LayoutBarChartBinding) binding;

            barBinding.barChart.setDataSet(barDataSet);
        }
    }
}
