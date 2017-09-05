package cn.jingzhuan.lib.chart.utils;

import android.support.annotation.IntDef;

/**
 * Created by donglua on 9/4/17.
 */

public interface ForceAlign {

  int LEFT = 21;
  int RIGHT = 22;
  int CENTER = 23;

  @IntDef({ LEFT, RIGHT, CENTER })
  @interface XForce {}

}
