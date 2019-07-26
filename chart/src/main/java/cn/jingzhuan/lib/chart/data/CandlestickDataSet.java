package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Paint;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by donglua on 8/29/17.
 */

public class CandlestickDataSet extends AbstractDataSet<CandlestickValue> {

  private List<CandlestickValue> candlestickValues;
  private boolean mAutoWidth = true;
  private float mCandleWidth = -1;

  private int mIncreasingColor = 0xFFf84b4b;   // 阳线
  private int mDecreasingColor = 0xFF1deb5b; // 阴线
  private int mNeutralColor = Color.WHITE;    // 十字线
  private int mLimitUpColor = Color.TRANSPARENT;
  private Paint.Style mLimitUpPaintStyle = null;

  private float strokeThickness = 4;
  private Paint.Style mIncreasingPaintStyle = Paint.Style.FILL;
  private Paint.Style mDecreasingPaintStyle = Paint.Style.FILL;

  public CandlestickDataSet(List<CandlestickValue> candlestickValues) {
    this(candlestickValues, AxisY.DEPENDENCY_BOTH);
  }

  public CandlestickDataSet(List<CandlestickValue> candlestickValues, @AxisY.AxisDependency int axisDependency) {
    this.candlestickValues = candlestickValues;

    setAxisDependency(axisDependency);
  }

  @Override public void calcMinMax(Viewport viewport) {

    if (candlestickValues == null || candlestickValues.isEmpty())
      return;

    mViewportYMax = -Float.MAX_VALUE;
    mViewportYMin = Float.MAX_VALUE;

    for (CandlestickValue e : getVisiblePoints(viewport)) {
      calcViewportMinMax(e);
    }
  }

  private void calcViewportMinMax(CandlestickValue e) {

    if (Float.isNaN(e.getLow())) return;
    if (Float.isNaN(e.getHigh())) return;

    if (Float.isInfinite(e.getLow())) return;
    if (Float.isInfinite(e.getHigh())) return;

    if (e.getLow() < mViewportYMin)
      mViewportYMin = e.getLow();

    if (e.getHigh() > mViewportYMax)
      mViewportYMax = e.getHigh();
  }

  @Override public int getEntryCount() {
    if (candlestickValues == null) return 0;
    return Math.max(getMinValueCount(), candlestickValues.size());
  }

  @Override public void setValues(List<CandlestickValue> values) {
    this.candlestickValues = values;
  }

  @Override public List<CandlestickValue> getValues() {
    return candlestickValues;
  }

  @Override public boolean addEntry(CandlestickValue e) {
    if (e == null)
      return false;

    if (candlestickValues == null) {
      candlestickValues = new ArrayList<>();
    }

    calcViewportMinMax(e);

    // add the entry
    return candlestickValues.add(e);
  }

  @Override public boolean removeEntry(CandlestickValue e) {
    if (e == null) return false;

    calcViewportMinMax(e);

    return candlestickValues.remove(e);
  }

  @Override public int getEntryIndex(CandlestickValue e) {
    return candlestickValues.indexOf(e);
  }

  @Override public CandlestickValue getEntryForIndex(int index) {
    return candlestickValues.get(index);
  }

  public void setAutoWidth(boolean mAutoWidth) {
    this.mAutoWidth = mAutoWidth;
  }

  public boolean isAutoWidth() {
    return mAutoWidth;
  }

  public int getDecreasingColor() {
    return mDecreasingColor;
  }

  public void setDecreasingColor(int decreasingColor) {
    this.mDecreasingColor = decreasingColor;
  }

  public int getIncreasingColor() {
    return mIncreasingColor;
  }

  public void setIncreasingColor(int increasingColor) {
    this.mIncreasingColor = increasingColor;
  }

  public float getCandleWidth() {
    return mCandleWidth;
  }

  public void setCandleWidth(float mCandleWidth) {
    this.mCandleWidth = mCandleWidth;
  }

  public int getNeutralColor() {
    return mNeutralColor;
  }

  public void setNeutralColor(int mNeutralColor) {
    this.mNeutralColor = mNeutralColor;
  }

  public void setLimitUpColor(int mLimitUpColor) {
    this.mLimitUpColor = mLimitUpColor;
  }

  public int getLimitUpColor() {
    return mLimitUpColor;
  }

  public float getStrokeThickness() {
    return strokeThickness;
  }

  public void setStrokeThickness(float strokeThickness) {
    this.strokeThickness = strokeThickness;
  }

  public Paint.Style getIncreasingPaintStyle() {
    return mIncreasingPaintStyle;
  }

  public void setIncreasingPaintStyle(Paint.Style increasingPaintStyle) {
    this.mIncreasingPaintStyle = increasingPaintStyle;
  }

  public Paint.Style getDecreasingPaintStyle() {
    return mDecreasingPaintStyle;
  }


  public void setDecreasingPaintStyle(Paint.Style decreasingPaintStyle) {
    this.mDecreasingPaintStyle = decreasingPaintStyle;
  }

  public Paint.Style getLimitUpPaintStyle() {
    return mLimitUpPaintStyle;
  }

  public void setLimitUpPaintStyle(Paint.Style limitUpPaintStyle) {
    this.mLimitUpPaintStyle = limitUpPaintStyle;
  }

}
