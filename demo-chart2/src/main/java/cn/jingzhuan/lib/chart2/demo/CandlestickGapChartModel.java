package cn.jingzhuan.lib.chart2.demo;

import androidx.databinding.ViewDataBinding;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutCombineChartBinding;

import static cn.jingzhuan.lib.chart.component.AxisY.LEFT_OUTSIDE;
import static cn.jingzhuan.lib.chart.component.AxisY.RIGHT_INSIDE;
import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * Created by donglua on 8/29/17.
 */
@EpoxyModelClass(layout = R.layout.layout_combine_chart)
public abstract class CandlestickGapChartModel extends DataBindingEpoxyModel {

  List<CandlestickValue> candlestickValues = new ArrayList<>();
  @EpoxyAttribute(DoNotHash) HighlightStatusChangeListener highlightStatusChangeListener;

  public CandlestickGapChartModel() {
    candlestickValues.add(new CandlestickValue(21.91f, 21.4f, 21.58f, 21.9f));
    candlestickValues.add(new CandlestickValue(23.3f, 21.98f, 22.0f, 23.1f));
    candlestickValues.add(new CandlestickValue(23.92f, 23.1f, 23.1f, 23.6f));
    candlestickValues.add(new CandlestickValue(23.9f, 22.95f, 23.58f, 22.95f));
    candlestickValues.add(new CandlestickValue(23.27f, 22.56f, 23.2f, 22.98f));
    candlestickValues.add(new CandlestickValue(23.22f, 22.63f, 22.91f, 22.94f));
    candlestickValues.add(new CandlestickValue(23.42f, 22.78f, 23.08f, 23.1f));
    candlestickValues.add(new CandlestickValue(24.38f, 23.23f, 23.4f, 24.01f));
    candlestickValues.add(new CandlestickValue(24.47f, 23.86f, 24.03f, 24.11f));
    candlestickValues.add(new CandlestickValue(24.6f, 23.85f, 24.47f, 24.04f));
    candlestickValues.add(new CandlestickValue(24.39f, 23.62f, 23.75f, 23.8f));
    candlestickValues.add(new CandlestickValue(24.19f, 23.68f, 23.97f, 23.94f));
    candlestickValues.add(new CandlestickValue(24.08f, 23.56f, 23.91f, 23.58f));
    candlestickValues.add(new CandlestickValue(23.99f, 23.32f, 23.44f, 23.95f));
    candlestickValues.add(new CandlestickValue(24.22f, 23.81f, 23.96f, 24.16f));
    candlestickValues.add(new CandlestickValue(24.47f, 23.87f, 24.25f, 24.02f));
    candlestickValues.add(new CandlestickValue(24.14f, 23.68f, 24.06f, 23.68f));
    candlestickValues.add(new CandlestickValue(23.87f, 23.53f, 23.7f, 23.55f));
    candlestickValues.add(new CandlestickValue(24.2f, 23.4f, 23.59f, 24.17f));
    candlestickValues.add(new CandlestickValue(24.53f, 23.97f, 24.03f, 24.04f));
    candlestickValues.add(new CandlestickValue(24.76f, 23.65f, 23.94f, 24.73f));
    candlestickValues.add(new CandlestickValue(24.63f, 23.0f, 24.42f, 23.32f));
    candlestickValues.add(new CandlestickValue(20.99f, 20.99f, 20.99f, 20.99f));
    candlestickValues.add(new CandlestickValue(20.7f, 19.25f, 19.26f, 20.23f));
    candlestickValues.add(new CandlestickValue(20.97f, 20.2f, 20.28f, 20.47f));
    candlestickValues.add(new CandlestickValue(20.97f, 20.15f, 20.4f, 20.84f));
    candlestickValues.add(new CandlestickValue(22.39f, 20.6f, 20.65f, 22.27f));
    candlestickValues.add(new CandlestickValue(23.77f, 22.4f, 22.5f, 23.17f));
    candlestickValues.add(new CandlestickValue(23.9f, 22.44f, 23.32f, 23.0f));
    candlestickValues.add(new CandlestickValue(25.1f, 23.2f, 23.28f, 24.7f));
    candlestickValues.add(new CandlestickValue(25.22f, 24.21f, 24.61f, 24.51f));
    candlestickValues.add(new CandlestickValue(25.66f, 24.4f, 24.69f, 25.27f));
    candlestickValues.add(new CandlestickValue(27.8f, 25.44f, 25.5f, 27.8f));
    candlestickValues.add(new CandlestickValue(30.58f, 28.77f, 29.02f, 30.58f));
    candlestickValues.add(new CandlestickValue(30.9f, 29.2f, 30.9f, 29.25f));
    candlestickValues.add(new CandlestickValue(30.56f, 28.66f, 29.31f, 30.2f));
    candlestickValues.add(new CandlestickValue(32.99f, 31.01f, 31.3f, 31.8f));
    candlestickValues.add(new CandlestickValue(34.18f, 31.41f, 32.6f, 33.4f));
  }

  @Override public View buildView(@NonNull ViewGroup parent) {
    View rootView = super.buildView(parent);

    final LayoutCombineChartBinding b = (LayoutCombineChartBinding) rootView.getTag();

    CandlestickDataSet dataSet = new CandlestickDataSet(candlestickValues);
    dataSet.setHighlightedVerticalEnable(true);
    dataSet.setHighlightedHorizontalEnable(true);
    dataSet.setEnableGap(true);

    b.combineChart.getAxisLeft().setAxisPosition(LEFT_OUTSIDE);
    b.combineChart.getAxisRight().setAxisPosition(RIGHT_INSIDE);
    b.combineChart.setMaxVisibleEntryCount(70);
    b.combineChart.setMinVisibleEntryCount(10);
    b.combineChart.setHighlightColor(Color.BLACK);
    //b.combineChart.setOnHighlightStatusChangeListener(highlightStatusChangeListener);
    //b.combineChart.setDataSet(new CandlestickDataSetArrowDecorator(dataSet));
    b.combineChart.setDataSet(dataSet);

    b.combineChart.setScaleGestureEnable(true);
    b.combineChart.setScaleXEnable(true);
    b.combineChart.setDraggingToMoveEnable(false);
    b.combineChart.setDoubleTapToZoom(true);
    b.combineChart.setHighlightDisable(false);
    b.combineChart.setDraggingToMoveEnable(true);
    b.combineChart.setOnHighlightStatusChangeListener(new HighlightStatusChangeListener() {
      @Override public void onHighlightShow(Highlight[] highlights) {
        b.combineChart.setDraggingToMoveEnable(false);
      }

      @Override public void onHighlightHide() {
        b.combineChart.setDraggingToMoveEnable(true);
      }
    });
    return rootView;
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {
  }
}
