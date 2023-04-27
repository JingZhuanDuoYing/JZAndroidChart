package cn.jingzhuan.lib.chart2.demo;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import cn.jingzhuan.lib.chart2.demo.databinding.LayoutTextButtonBinding;


@EpoxyModelClass(layout = R.layout.layout_text_button)
public abstract class TextButtonModel extends DataBindingEpoxyModel {
    @EpoxyAttribute
    String name = "文本";

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener onClick = null;


    public TextButtonModel() {
    }

    @Override
    public View buildView(@NonNull ViewGroup parent) {
        View rootView = super.buildView(parent);
        return rootView;
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

        if(binding instanceof LayoutTextButtonBinding) {
            LayoutTextButtonBinding binding1 = (LayoutTextButtonBinding) binding;
            binding1.setDesc(name);
            binding1.btn.setOnClickListener(onClick);
        }
    }
}
