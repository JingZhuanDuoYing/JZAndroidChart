package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/8/1.
 */

public abstract class Value {

  private float x = -1;
  private float y = -1;

  public void setCoordinate(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }
}
