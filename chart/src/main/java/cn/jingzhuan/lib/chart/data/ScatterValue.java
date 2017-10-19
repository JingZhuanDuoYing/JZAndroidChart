package cn.jingzhuan.lib.chart.data;

import android.graphics.drawable.Drawable;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterValue extends Value {

  private float value;

  public ScatterValue(float value) {
    this.value = value;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }
}
