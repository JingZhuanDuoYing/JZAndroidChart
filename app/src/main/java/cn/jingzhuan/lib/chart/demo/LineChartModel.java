package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import cn.jingzhuan.lib.chart.Viewport;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutLineChartBinding;

import static cn.jingzhuan.lib.chart.Viewport.*;

/**
 * Created by Donglua on 17/7/26.
 */
@EpoxyModelClass(layout = R.layout.layout_line_chart)
public abstract class LineChartModel extends DataBindingEpoxyModel {

    private LineDataSet line;

    public LineChartModel() {

        final List<Float> floats = Arrays.asList(3134.55f, 3134.62f, 3134.34f, 3133.53f, 3133.37f,
                3132.10f, 3131.55f, 3132.10f, 3133.30f, 3133.39f, 3133.02f, 3133.32f, 3132.60f,
                3132.88f, 3132.46f, 3131.71f, 3132.14f, 3132.83f, 3132.40f, 3133.32f, 3134.26f,
                3135.62f, 3136.88f, 3138.13f, 3138.51f, 3138.17f, 3138.73f, 3138.40f, 3138.65f,
                3137.40f, 3137.05f, 3136.25f, 3136.70f, 3137.04f, 3136.28f, 3136.26f, 3135.62f,
                3135.91f, 3135.85f, 3135.80f, 3136.21f, 3136.12f, 3136.41f, 3136.54f, 3136.30f,
                3136.35f, 3135.62f, 3134.05f, 3133.15f, 3132.52f, 3132.28f, 3132.98f, 3133.08f,
                3132.93f, 3133.18f, 3133.12f, 3134.12f, 3133.87f, 3133.84f, 3134.03f, 3134.16f,
                3134.62f, 3135.23f, 3135.51f, 3135.59f, 3135.79f, 3136.02f, 3135.46f, 3135.90f,
                3135.09f, 3135.05f, 3134.57f, 3135.03f, 3134.52f, 3134.82f, 3134.57f, 3134.78f,
                3135.44f, 3135.13f, 3136.28f, 3136.62f, 3137.25f, 3137.16f, 3137.62f, 3138.21f,
                3138.07f, 3138.09f, 3138.47f, 3139.63f, 3139.70f, 3140.38f, 3140.87f, 3142.22f,
                3142.80f, 3143.58f, 3142.84f, 3143.13f, 3143.77f, 3143.77f, 3146.54f, 3145.62f,
                3144.90f, 3144.80f, 3144.78f, 3144.76f, 3144.40f, 3144.15f, 3144.60f, 3145.46f,
                3146.13f, 3145.82f, 3146.05f, 3144.65f, 3144.27f, 3144.29f, 3143.62f, 3143.67f,
                3143.56f, 3142.93f, 3142.19f, 3142.72f, 3142.29f, 3142.39f, 3141.31f, 3141.92f,
                3142.13f, 3141.65f, 3141.60f, 3140.42f, 3139.55f, 3139.94f, 3140.05f, 3139.12f,
                3139.35f, 3138.90f, 3139.02f, 3138.87f, 3138.83f, 3138.53f, 3139.31f, 3139.36f,
                3138.91f, 3139.06f, 3139.13f, 3139.52f, 3139.57f, 3138.82f, 3138.17f, 3138.5f,
                3137.95f, 3138.55f, 3137.82f, 3138.25f, 3137.59f, 3137.75f, 3137.96f, 3138.37f,
                3137.82f, 3138.22f, 3138.17f, 3137.31f, 3137.96f, 3137.22f, 3137.82f, 3137.19f,
                3137.78f, 3137.93f, 3138.65f, 3138.70f, 3140.12f, 3140.35f, 3140.28f, 3140.46f,
                3140.22f, 3140.06f, 3138.75f, 3139.31f, 3138.73f, 3137.54f, 3137.13f, 3136.23f,
                3136.20f, 3136.53f, 3135.56f, 3135.71f, 3135.68f, 3135.89f, 3136.31f, 3135.81f,
                3135.82f, 3135.5f, 3136.18f, 3138.01f, 3137.89f, 3138.09f, 3138.21f, 3138.52f,
                3138.70f, 3138.55f, 3138.02f, 3137.73f, 3137.36f, 3137.59f, 3137.45f, 3137.89f,
                3138.29f, 3138.63f, 3138.54f, 3139.09f, 3140.09f, 3140.89f, 3141.19f, 3141.57f,
                3141.92f, 3142.10f, 3142.44f, 3143.38f, 3143.96f, 3144.77f, 3144.37f, 3148.02f,
                3149.62f, 3149.79f, 3149.5f, 3148.58f, 3148.39f, 3148.43f, 3148.5f, 3148.12f,
                3146.07f, 3144.87f, 3145.0f, 3144.67f, 3142.95f, 3143.63f, 3143.5f, 3144.13f,
                3145.08f, 3145.06f, 3144.96f, 3143.86f);

        List<PointValue> values = new ArrayList<>();
        for (Float value: floats) {
            values.add(new PointValue(value));
        }
        line = new LineDataSet(values);
    }

    @Override protected View buildView(@NonNull ViewGroup parent) {
        View rootView = super.buildView(parent);

        final LayoutLineChartBinding bd = (LayoutLineChartBinding) rootView.getTag();
        bd.lineChart.setCurrentViewport(new Viewport(0.5f, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX));
        bd.lineChart.setDoubleTapToZoom(true);
        bd.lineChart.addLine(line);

        bd.btMoveLeft.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                bd.lineChart.moveLeft();
            }
        });
        bd.btMoveRight.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                bd.lineChart.moveRight();
            }
        });

        return rootView;
    }

    @Override protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}
