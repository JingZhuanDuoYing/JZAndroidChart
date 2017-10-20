package cn.jingzhuan.lib.chart.data;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterValue extends Value {

  private float value;
  private boolean visible = true;

  public ScatterValue(float value) {
    this.value = value;
  }

  public ScatterValue(float value, boolean visible) {
    this.value = value;
    this.visible = visible;
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
}
