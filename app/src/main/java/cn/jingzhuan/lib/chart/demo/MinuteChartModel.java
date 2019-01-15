package cn.jingzhuan.lib.chart.demo;

import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;
import cn.jingzhuan.lib.chart.data.MinuteLine;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart.data.ValueFormatter;
import cn.jingzhuan.lib.chart.demo.databinding.LayoutMinuteChartBinding;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * Created by Donglua on 17/7/26.
 */

@EpoxyModelClass(layout = R.layout.layout_minute_chart)
public abstract class MinuteChartModel extends DataBindingEpoxyModel {

    private final MinuteLine line;
    private final float lastClose = 11.27f;

    @EpoxyAttribute(DoNotHash) View.OnClickListener onClickListener;
    @EpoxyAttribute(DoNotHash) HighlightStatusChangeListener highlightStatusChangeListener;

    public MinuteChartModel() {

        final List<Float> floats =
            Arrays.asList(11.17449f, 11.15434f, 11.16595f, 11.18753f, 11.25440f, 11.27073f,
                11.27655f, 11.28625f, 11.28847f, 11.29051f, 11.29548f, 11.29861f, 11.31060f,
                11.3284f, 11.37816f, 11.55310f, 11.630815f, 11.67925f, 11.69136f, 11.70062f,
                11.71826f, 11.7231f, 11.72815f, 11.73347f, 11.73745f, 11.73951f, 11.74206f,
                11.74301f, 11.74439f, 11.748f, 11.75036f, 11.75095f, 11.75179f, 11.75248f,
                11.75364f, 11.75408f, 11.75480f, 11.75523f, 11.75574f, 11.75635f, 11.75658f,
                11.75653f, 11.75634f, 11.75630f, 11.75648f, 11.75646f, 11.75623f, 11.75595f,
                11.75535f, 11.75532f, 11.75566f, 11.75635f, 11.7568f, 11.75707f, 11.75712f,
                11.75716f, 11.75725f, 11.75740f, 11.75747f, 11.75758f, 11.75761f, 11.75753f,
                11.75761f, 11.75843f, 11.75917f, 11.75963f, 11.75987f, 11.76059f, 11.76152f,
                11.76369f, 11.76426f, 11.76446f, 11.76476f, 11.76524f, 11.76556f, 11.76582f,
                11.76620f, 11.76672f, 11.76706f, 11.76722f, 11.76734f, 11.76747f, 11.76757f,
                11.76773f, 11.76784f, 11.76798f, 11.768167f, 11.76890f, 11.77048f, 11.77456f,
                11.77975f, 11.78389f, 11.78564f, 11.78687f, 11.78745f, 11.78922f, 11.79016f,
                11.79115f, 11.7921f, 11.79493f, 11.79727f, 11.80075f, 11.80353f, 11.80501f,
                11.80609f, 11.80723f, 11.80804f, 11.808804f, 11.80939f, 11.80992f, 11.81042f,
                11.810842f, 11.81131f, 11.81160f, 11.81220f, 11.81263f, 11.81307f, 11.81345f,
                11.81370f, 11.81391f, 11.81391f, 11.81438f, 11.815f, 11.8159f, 11.81694f, 11.81789f,
                11.81858f, 11.81979f, 11.82164f, 11.83589f, 11.85183f, 11.86026f, 11.86534f,
                11.869019f, 11.8747f, 11.87665f, 11.87817f, 11.879498f, 11.88067f, 11.88169f,
                11.88243f, 11.88314f, 11.88457f, 11.88600f, 11.88785f, 11.89003f, 11.89152f,
                11.89222f, 11.89326f, 11.89778f, 11.89976f, 11.90208f, 11.90356f, 11.90446f,
                11.905189f, 11.90743f, 11.91124f, 11.91410f, 11.91525f, 11.91636f, 11.919332f,
                11.9204f, 11.93025f, 11.97543f, 11.98504f, 11.98811f, 11.9973f, 11.99822f,
                11.99884f, 11.99967f, 12.01106f, 12.01314f, 12.01556f, 12.01905f, 12.02244f,
                12.02431f, 12.02863f, 12.03055f, 12.03342f, 12.03391f, 12.03451f, 12.03473f,
                12.03511f, 12.03528f, 12.035439f, 12.03549f, 12.0356f, 12.03586f, 12.03604f,
                12.03629f, 12.03680f, 12.03695f, 12.03699f, 12.03723f, 12.03739f, 12.03746f,
                12.03752f, 12.037683f, 12.03771f, 12.03792f, 12.03796f, 12.03856f, 12.03860f,
                12.03868f, 12.038836f, 12.03899f, 12.03905f, 12.03908f, 12.03914f, 12.03921f,
                12.03924f, 12.03937f, 12.03948f, 12.03954f, 12.03958f, 12.03964f, 12.03966f,
                12.0397f, 12.03979f, 12.03985f, 12.03988f, 12.03995f, 12.04000f, 12.04021f,
                12.04030f, 12.04048f, 12.04051f, 12.040565f, 12.04060f, 12.04163f, 12.04201f,
                12.04210f, 12.04233f, 12.04259f, 12.04264f, 12.04272f, 12.04276f, 12.04279f,
                12.04279f, 12.04307f);

        List<PointValue> values = new ArrayList<>();
        for (Float value: floats) {
            values.add(new PointValue(value));
        }

        line = new MinuteLine(values);
        line.setHighlightedVerticalEnable(true);
        line.setHighlightedHorizontalEnable(true);
        line.setLastClose(lastClose);
    }

    @Override protected View buildView(ViewGroup parent) {
        View rootView = super.buildView(parent);

        final LayoutMinuteChartBinding minuteBinding = (LayoutMinuteChartBinding) rootView.getTag();

        minuteBinding.minuteChart.getAxisLeft().enableGridDashPathEffect(new float[] {10, 10}, 10);
        minuteBinding.minuteChart.getAxisTop().enableGridDashPathEffect(new float[] {10, 10}, 10);
        minuteBinding.minuteChart.getAxisTop().setGridLineEnable(true);

        minuteBinding.minuteChart.getAxisRight().setLabelValueFormatter(new ValueFormatter() {
            @Override
            public String format(float value, int index) {
                return String.format(Locale.ENGLISH, "%.2f%%",
                    (value - lastClose) / lastClose * 100);
            }
        });

        minuteBinding.minuteChart.getAxisBottom().setGridCount(1);
        minuteBinding.minuteChart.getAxisTop().setGridCount(3);
        minuteBinding.minuteChart.getAxisRight().setGridCount(1);

        minuteBinding.minuteChart.getAxisBottom().setLabelValueFormatter(new ValueFormatter() {
            @Override
            public String format(float value, int index) {
                if (index == 0) {
                    return "9:30";
                }
                if (index == 1) {
                    return "11:30/13:00";
                }
                if (index == 2) {
                    return "15:00";
                }
                return "";
            }
        });

        minuteBinding.minuteChart.setOnHighlightStatusChangeListener(highlightStatusChangeListener);

        minuteBinding.minuteChart.setHighlightColor(Color.BLACK);

        minuteBinding.minuteChart.setScaleXEnable(false);

        line.setShader(new LinearGradient(0, 0, 0, 0, 0x10000000, 0x10000000, Shader.TileMode.REPEAT));

        minuteBinding.minuteChart.addLine(line);

        minuteBinding.minuteChart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                minuteBinding.minuteChart.animateX(1000);

                onClickListener.onClick(v);
            }
        });

        minuteBinding.minuteChart.enableHighlightDashPathEffect(new float[] {10, 10}, 10);

        return rootView;
    }

    @Override protected void setDataBindingVariables(ViewDataBinding binding) {
    }
}
