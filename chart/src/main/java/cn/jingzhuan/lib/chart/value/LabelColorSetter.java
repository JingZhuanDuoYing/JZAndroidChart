package cn.jingzhuan.lib.chart.value;

import android.support.annotation.ColorInt;

/**
 * Created by Donglua on 17/7/30.
 */

public interface LabelColorSetter {

    @ColorInt int getColorByIndex(int position);
}
