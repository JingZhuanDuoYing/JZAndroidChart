package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterValue extends Value {

  private float value;
  private boolean visible = true;
  private int color = Color.TRANSPARENT;

  public ScatterValue(float value) {
    this.value = value;
  }

  public ScatterValue(float value, boolean visible) {
    this.value = value;
    this.visible = visible;
  }

  public ScatterValue(float value, boolean visible, int color) {
    this.value = value;
    this.visible = visible;
    this.color = color;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getColor() {
    return color;
  }
}
