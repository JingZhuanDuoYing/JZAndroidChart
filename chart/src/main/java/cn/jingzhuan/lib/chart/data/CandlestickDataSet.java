package cn.jingzhuan.lib.chart.data;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
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

  private float mViewportWidth = 1f;
  private int maxVisibleEntry = 100;

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

    //mYMax = -Float.MAX_VALUE;
    //mYMin = Float.MAX_VALUE;
    //mXMax = -Float.MAX_VALUE;
    //mXMin = Float.MAX_VALUE;

    mViewportYMax = -Float.MAX_VALUE;
    mViewportYMin = Float.MAX_VALUE;

    for (CandlestickValue e : getVisiblePoints(viewport)) {
      calcViewportMinMax(e);
    }
  }

  private void calcViewportMinMax(CandlestickValue e) {
    if (e.getLow() < mViewportYMin)
      mViewportYMin = e.getLow();

    if (e.getHigh() > mViewportYMax)
      mViewportYMax = e.getHigh();
  }

  public int getVisibleCount(Viewport viewport) {
    return getVisiblePoints(viewport).size();
  }

  protected List<CandlestickValue> getVisiblePoints(Viewport viewport) {
    int from = (int) (viewport.left * candlestickValues.size());
    int to  = (int) (viewport.right * candlestickValues.size());

    if (maxVisibleEntry > 0 && to - from > maxVisibleEntry) {
      from = to - maxVisibleEntry;
      viewport.left = from / (float) candlestickValues.size();
    }
    return candlestickValues.subList(from, to);
  }

  public void calcMinMaxY(CandlestickValue e) {
    if (e.getLow() < mYMin)
      mYMin = e.getLow();

    if (e.getHigh() > mYMax)
      mYMax = e.getHigh();
  }

  @Override public int getEntryCount() {
    return candlestickValues.size();
  }

  @Override public void setValues(List<CandlestickValue> values) {
    this.candlestickValues = values;
    //setMinMax();
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

    calcMinMaxY(e);

    // add the entry
    return candlestickValues.add(e);
  }

  @Override public boolean removeEntry(CandlestickValue e) {
    if (e == null) return false;

    calcMinMaxY(e);

    return candlestickValues.remove(e);
  }

  @Override public int getEntryIndex(CandlestickValue e) {
    return candlestickValues.indexOf(e);
  }

  @Override public CandlestickValue getEntryForIndex(int index) {
    return candlestickValues.get(index);
  }

  public void onViewportChange(Viewport viewport, Rect content) {

    //mViewport = viewport;

    boolean needCalcCandleWidth = Float.compare(viewport.width(), mViewportWidth) != 0;

    calcMinMax(viewport);

    if (needCalcCandleWidth) {
      mCandleWidth = content.width() / (getVisibleCount(viewport) + 1);
    }
    mViewportWidth = viewport.width();
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

  public void setLimitUpColor(int mLimitUpColor) {
    this.mLimitUpColor = mLimitUpColor;
  }

  public int getLimitUpColor() {
    return mLimitUpColor;
  }

  public int getMaxVisibleEntry() {
    return maxVisibleEntry;
  }

  public void setMaxVisibleEntry(int maxVisibleEntry) {
    this.maxVisibleEntry = maxVisibleEntry;
  }
}
