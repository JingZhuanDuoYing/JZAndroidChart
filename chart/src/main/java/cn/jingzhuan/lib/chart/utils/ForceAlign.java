package cn.jingzhuan.lib.chart.utils;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by donglua on 9/4/17.
 */

public interface ForceAlign {

  int LEFT = 21;
  int RIGHT = 22;
  int CENTER = 23;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ LEFT, RIGHT, CENTER })
  @interface XForce {}

}
