package cn.jingzhuan.lib.chart.demo;

import android.util.Log;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

/**
 * Created by Donglua on 17/7/26.
 */

public class DemoAdapterController extends EpoxyController {

    @AutoModel LineChartModel_ lineChartModel;
    @AutoModel MinuteChartModel_ minuteChartModel;

    @Override
    protected void buildModels() {

        new LayoutDescTextBindingModel_().id("Line").text("Line Chart").addTo(this);

        lineChartModel.addTo(this);

        new LayoutDescTextBindingModel_().id("Minute").text("Minute Chart").addTo(this);

        minuteChartModel.addTo(this);
    }
}
