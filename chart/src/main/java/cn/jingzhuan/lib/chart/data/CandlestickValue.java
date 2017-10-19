package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by donglua on 8/29/17.
 */

public class CandlestickValue extends Value {

  public final static int COLOR_NONE = Color.TRANSPARENT;

  private float high = 0f;
  private float low = 0f;
  private float open = 0f;
  private float close = 0f;

  private long time = -1;

  private Paint.Style mPaintStyle = null;
  private int color = COLOR_NONE;

  public CandlestickValue(float high, float low, float open, float close) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
  }
  public CandlestickValue(float high, float low, float open, float close, long time) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.time = time;
  }

  public CandlestickValue(float high, float low, float open, float close, int color) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.color = color;
  }

  public CandlestickValue(float high, float low, float open, float close, long time, int color) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.color = color;
    this.time = time;
  }

  public CandlestickValue(float high, float low, float open, float close, Paint.Style mPaintStyle,
      int color) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.mPaintStyle = mPaintStyle;
    this.color = color;
  }

  public CandlestickValue(float high, float low, float open, float close, Paint.Style mPaintStyle) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.mPaintStyle = mPaintStyle;
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

  public void setPaintStyle(Paint.Style mPaintStyle) {
    this.mPaintStyle = mPaintStyle;
  }

  public Paint.Style getPaintStyle() {
    return mPaintStyle;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }
}
