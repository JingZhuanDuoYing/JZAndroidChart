package cn.jingzhuan.lib.chart2.demo;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutCommonButtonBinding;


@EpoxyModelClass(layout = R.layout.layout_common_button)
public abstract class CommonButtonModel extends DataBindingEpoxyModel {
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener onClickListener = null;

    @EpoxyAttribute
    String buttonText = "";

    public CommonButtonModel() {
    }

    @Override
    public View buildView(@NonNull ViewGroup parent) {
        View rootView = super.buildView(parent);

        final LayoutCommonButtonBinding binding = (LayoutCommonButtonBinding) rootView.getTag();

        binding.btnText.setOnClickListener(v -> onClickListener.onClick(v));

        binding.btnText.setText(buttonText);
        return rootView;
    }

    @Override protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}
