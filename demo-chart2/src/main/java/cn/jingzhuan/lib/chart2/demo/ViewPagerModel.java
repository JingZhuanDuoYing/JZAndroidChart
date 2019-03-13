package cn.jingzhuan.lib.chart2.demo;

import android.databinding.ViewDataBinding;
import android.support.v4.view.PagerAdapter;
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutViewPagerBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

/**
 * Created by donglua on 9/7/17.
 */
@EpoxyModelClass(layout = R.layout.layout_view_pager)
public abstract class ViewPagerModel extends DataBindingEpoxyModel {

  @EpoxyAttribute PagerAdapter pagerAdapter;

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {

    LayoutViewPagerBinding b = (LayoutViewPagerBinding) binding;

    b.viewPager.setAdapter(pagerAdapter);

  }

  @Override public boolean shouldSaveViewState() {
    return true;
  }
}
