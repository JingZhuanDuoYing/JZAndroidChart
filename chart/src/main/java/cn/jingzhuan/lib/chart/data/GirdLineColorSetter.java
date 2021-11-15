package cn.jingzhuan.lib.chart.data;

import androidx.annotation.ColorInt;

/**
 * Created by donglua on 11/13/17.
 */

public interface GirdLineColorSetter {
  @ColorInt int getColorByIndex(int color, int position);

}
