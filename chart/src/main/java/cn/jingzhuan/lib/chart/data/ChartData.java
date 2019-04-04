package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import cn.jingzhuan.lib.chart.base.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Donglua on 17/8/2.
 */

public class ChartData<T extends IDataSet> {

  private List<T> chartData;

  protected float leftMin = Float.MAX_VALUE;
  protected float leftMax = -Float.MAX_VALUE;
  protected float rightMin = Float.MAX_VALUE;
  protected float rightMax = -Float.MAX_VALUE;

  private int entryCount = 0;

  protected AxisY leftAxis;
  protected AxisY rightAxis;

  protected int maxVisibleEntryCount = 500;
  protected int minVisibleEntryCount = 20;
  protected int defaultVisibleEntryCount = -1;

  private int minValueCount = -1;

  public ChartData() {
    this.chartData = Collections.synchronizedList(new ArrayList<T>());
  }

  public List<T> getDataSets() {
    if (chartData == null) {
      chartData = Collections.synchronizedList(new ArrayList<T>());
    }
    return chartData;
  }

  public boolean add(T e) {
    if (e == null) return false;
    e.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
    e.setMinVisibleEntryCount(minVisibleEntryCount);
    e.setMaxVisibleEntryCount(maxVisibleEntryCount);
    synchronized (this) {
      return getDataSets().add(e);
    }
  }

  public boolean remove(T e) {
    synchronized (this) {
      return e != null && getDataSets().remove(e);
    }
  }

  public void clear() {
    synchronized (this) {
      getDataSets().clear();
    }

    leftMin = Float.MAX_VALUE;
    leftMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;
  }

  public void setMinMax() {
    if (leftAxis != null && leftMin != Float.MAX_VALUE) {
      leftAxis.setYMin(leftMin);
      leftAxis.setYMax(leftMax);
    }
    if (rightAxis != null && rightMin != Float.MAX_VALUE) {
      rightAxis.setYMin(rightMin);
      rightAxis.setYMax(rightMax);
    }
  }

  public void calcMaxMin(Viewport viewport, Rect content) {
    leftMin = Float.MAX_VALUE;
    leftMax = -Float.MAX_VALUE;
    rightMin = Float.MAX_VALUE;
    rightMax = -Float.MAX_VALUE;

    entryCount = 0;

    if (!getDataSets().isEmpty()) {
      synchronized (this) {
        for (T t : getDataSets()) {

          if (!t.isEnable()) continue;

          t.calcMinMax(viewport);
          if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_LEFT) {
            leftMax = Math.max(leftMax, t.getViewportYMax());
            leftMin = Math.min(leftMin, t.getViewportYMin());
          }
          if (t.getAxisDependency() == AxisY.DEPENDENCY_BOTH || t.getAxisDependency() == AxisY.DEPENDENCY_RIGHT) {
            rightMax = Math.max(rightMax, t.getViewportYMax());
            rightMin = Math.min(rightMin, t.getViewportYMin());
          }
          if (t.getEntryCount() > entryCount) {
            entryCount = t.getEntryCount();
          }
        }
      }
      setMinMax();
    }
  }

  public float getLeftMin() {
    return leftMin;
  }

  public float getRightMax() {
    return rightMax;
  }

  public float getRightMin() {
    return rightMin;
  }

  public float getLeftMax() {
    return leftMax;
  }

  public void setLeftMin(float leftMin) {
    this.leftMin = leftMin;
  }

  public void setLeftMax(float leftMax) {
    this.leftMax = leftMax;
  }

  public void setRightMin(float rightMin) {
    this.rightMin = rightMin;
  }

  public void setRightMax(float rightMax) {
    this.rightMax = rightMax;
  }

  public int getEntryCount() {
    return entryCount;
  }

  public void setChart(Chart chart) {
    this.leftAxis = chart.getAxisLeft();
    this.rightAxis = chart.getAxisRight();
  }

  public void setChart(cn.jingzhuan.lib.chart2.base.Chart chart) {
    this.leftAxis = chart.getAxisLeft();
    this.rightAxis = chart.getAxisRight();
  }

  public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
    this.maxVisibleEntryCount = maxVisibleEntryCount;
    synchronized (getDataSets()) {
      for (T t : getDataSets()) {
        t.setMaxVisibleEntryCount(maxVisibleEntryCount);
      }
    }
  }

  public void setMinVisibleEntryCount(int minVisibleEntryCount) {
    this.minVisibleEntryCount = minVisibleEntryCount;
    synchronized (getDataSets()) {
      for (T t : getDataSets()) {
        t.setMinVisibleEntryCount(minVisibleEntryCount);
      }
    }
  }

  public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
    this.defaultVisibleEntryCount = defaultVisibleEntryCount;
    synchronized (getDataSets()) {
      for (T t : getDataSets()) {
        t.setDefaultVisibleEntryCount(defaultVisibleEntryCount);
      }
    }
  }

  public int getMinValueCount() {
    return minValueCount;
  }

  public void setMinValueCount(int minValueCount) {
    this.minValueCount = minValueCount;

    synchronized (getDataSets()) {
      for (T t : getDataSets()) {
        t.setMinValueCount(minValueCount);
      }
    }
  }

}
