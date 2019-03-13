package cn.jingzhuan.lib.chart2.demo;

import android.content.Context;
import android.util.LayoutDirection;
import android.view.View;
import android.widget.Toast;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

/**
 * Created by Donglua on 17/7/26.
 */

public class DemoAdapterController extends EpoxyController {

    @AutoModel LineChartModel_ lineChartModel;
    @AutoModel MinuteChartModel_ minuteChartModel;
    @AutoModel BarChartModel_ barChartModel_;
    @AutoModel BarChartClickableModel_ barChartClickableModel_;
    @AutoModel CombineChartModel_ combineChartModel_;
    @AutoModel CandlestickChartModel_ candlestickChartModel_;
    @AutoModel ViewPagerModel_ viewPagerModel_;
    @AutoModel ScatterChartModel_ scatterChartModel_;
    @AutoModel ScatterChart2Model_ scatterChart2Model_;

    private Context context;

    public DemoAdapterController(Context context) {
        this.context = context;
    }

    @Override
    protected void buildModels() {

        new LayoutDescTextBindingModel_().id("LineDataSet").text("LineDataSet Chart").addTo(this);
        lineChartModel.addTo(this);

        new LayoutDescTextBindingModel_().id("Minute").text("Minute Chart").addTo(this);
        minuteChartModel.onClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_SHORT).show();
            }
        }).addTo(this);

        new LayoutDescTextBindingModel_().id("Bar").text("Bar Chart").addTo(this);
        barChartModel_.addTo(this);
        barChartClickableModel_.addTo(this);

        new LayoutDescTextBindingModel_().id("Combine").text("Combine Chart").addTo(this);
        combineChartModel_.addTo(this);

        new LayoutDescTextBindingModel_().id("Candlestick").text("Candlestick Chart").addTo(this);
        candlestickChartModel_.addTo(this);

        new LayoutDescTextBindingModel_().id("View Pager").text("ViewPager").addTo(this);
        viewPagerModel_.pagerAdapter(new ChartViewPagerAdapter(context)).addTo(this);

        new LayoutDescTextBindingModel_().id("Scatter Chart").text("Scatter Chart").addTo(this);
        scatterChartModel_.addTo(this);
        scatterChart2Model_.addTo(this);

    }
}
