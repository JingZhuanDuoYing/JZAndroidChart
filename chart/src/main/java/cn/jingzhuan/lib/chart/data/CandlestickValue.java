package cn.jingzhuan.lib.chart.data;

/**
 * Created by donglua on 8/29/17.
 */

public class CandlestickValue implements Value {

  private float high = 0f;
  private float low = 0f;
  private float open = 0f;
  private float close = 0f;

  public CandlestickValue(float high, float low, float open, float close) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
  }

  public float getHigh() {
    return high;
  }

  public void setHigh(float high) {
    this.high = high;
  }

  public float getLow() {
    return low;
  }

  public void setLow(float low) {
    this.low = low;
  }

  public float getOpen() {
    return open;
  }

  public void setOpen(float open) {
    this.open = open;
  }

  public float getClose() {
    return close;
  }

  public void setClose(float close) {
    this.close = close;
  }
}
