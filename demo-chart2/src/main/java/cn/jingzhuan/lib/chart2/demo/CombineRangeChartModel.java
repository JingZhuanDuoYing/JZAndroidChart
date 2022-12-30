package cn.jingzhuan.lib.chart2.demo;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import cn.jingzhuan.lib.chart2.demo.databinding.LayoutCombineRangeChartBinding;


@EpoxyModelClass(layout = R.layout.layout_combine_range_chart)
public abstract class CombineRangeChartModel extends DataBindingEpoxyModel {


    public CombineRangeChartModel() {
    }

    @Override
    public View buildView(@NonNull ViewGroup parent) {
        View rootView = super.buildView(parent);

        final LayoutCombineRangeChartBinding binding = (LayoutCombineRangeChartBinding) rootView.getTag();

        binding.btnRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.getContext().startActivity(new Intent(rootView.getContext(), RangeDemoActivity.class));

            }
        });
        return rootView;
    }

    @Override protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}
