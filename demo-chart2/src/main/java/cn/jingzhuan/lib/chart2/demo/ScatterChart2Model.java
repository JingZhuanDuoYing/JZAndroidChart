package cn.jingzhuan.lib.chart2.demo;

import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import cn.jingzhuan.lib.chart.renderer.TextValueRenderer;
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutScatterChartBinding;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

@EpoxyModelClass(layout = R.layout.layout_scatter_chart)
public abstract class ScatterChart2Model extends DataBindingEpoxyModel {

  private ScatterDataSet scatterDataSet;

  private final List<String> textList =
      Arrays.asList("data1", "data2", "data3", "data4", "data5", "data6");

  public ScatterChart2Model() {

    final List<ScatterValue> scatterValues = new ArrayList<>();

    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));
    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));
    scatterValues.add(new ScatterValue(1f));
    scatterValues.add(new ScatterValue(2f));

    scatterDataSet = new ScatterDataSet(scatterValues);
    scatterDataSet.setMinValueOffsetPercent(0.5f);
    scatterDataSet.setMaxValueOffsetPercent(0.5f);
    scatterDataSet.setAutoWidth(false);
    scatterDataSet.addTextValueRenderer(new TextValueRenderer() {
      @Override public void render(Canvas canvas, Paint textPaint, int index, float x, float y) {
        textPaint.setTextSize(24f);
        canvas.drawText(textList.get(index), x, y, textPaint);
      }
    });
  }

  @Override protected void setDataBindingVariables(ViewDataBinding binding) {

    if (binding instanceof LayoutScatterChartBinding) {

      Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(),
                                                         R.drawable.shape_circle);
      scatterDataSet.setShape(drawable);

      LayoutScatterChartBinding bd = (LayoutScatterChartBinding) binding;

      bd.combineChart.getAxisBottom().setGridCount(1);
      bd.combineChart.getAxisLeft().setGridCount(1);
      bd.combineChart.addDataSet(scatterDataSet);

    }

  }

}
