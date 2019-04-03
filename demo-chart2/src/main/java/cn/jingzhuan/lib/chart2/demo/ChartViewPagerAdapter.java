package cn.jingzhuan.lib.chart2.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import com.airbnb.epoxy.SimpleEpoxyAdapter;

/**
 * Created by donglua on 9/7/17.
 */

public class ChartViewPagerAdapter extends PagerAdapter {

  private RecyclerView view1;
  private RecyclerView view2;
  private RecyclerView view3;

  private SimpleEpoxyAdapter epoxyAdapter1;
  private SimpleEpoxyAdapter epoxyAdapter2;
  private SimpleEpoxyAdapter epoxyAdapter3;

  @SuppressLint("InflateParams")
  ChartViewPagerAdapter(Context context) {
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    view1 = (RecyclerView) layoutInflater.inflate(R.layout.activity_main, null);
    view2 = (RecyclerView) layoutInflater.inflate(R.layout.activity_main, null);
    view3 = (RecyclerView) layoutInflater.inflate(R.layout.activity_main, null);

    final HighlightStatusChangeListener highlightStatusListener = new HighlightStatusChangeListener() {
      @Override public void onHighlightShow(Highlight[] highlights) {
        view1.getParent().requestDisallowInterceptTouchEvent(true);
      }

      @Override public void onHighlightHide() {
        view1.getParent().requestDisallowInterceptTouchEvent(false);
      }
    };

    epoxyAdapter1 = new SimpleEpoxyAdapter();
    epoxyAdapter1.addModels(new MinuteChartModel_().id(0).highlightStatusChangeListener(highlightStatusListener));

    epoxyAdapter2 = new SimpleEpoxyAdapter();
    epoxyAdapter2.addModels(new CandlestickChartModel_().id(1).highlightStatusChangeListener(highlightStatusListener));

    epoxyAdapter3 = new SimpleEpoxyAdapter();
    epoxyAdapter3.addModels(new CombineChartModel_().id(3));

    view1.setAdapter(epoxyAdapter1);
    view2.setAdapter(epoxyAdapter2);
    view3.setAdapter(epoxyAdapter3);
  }

  @Override public int getCount() {
    return 3;
  }

  @NonNull @Override
  public Object instantiateItem(ViewGroup container, int position) {
    View view = null;
    switch (position) {
      case 0:
        view = view1;
        break;
      case 1:
        view = view2;
        break;
      case 2:
        view = view3;
        break;
    }
    container.addView(view);
    return view;
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView((View) object);
  }
}
