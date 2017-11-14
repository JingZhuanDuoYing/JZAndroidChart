package cn.jingzhuan.lib.chart.data;

import android.support.annotation.ColorInt;

/**
 * Created by donglua on 11/13/17.
 */

public interface GirdLineColorSetter {
  @ColorInt int getColorByIndex(int color, int position);

}
