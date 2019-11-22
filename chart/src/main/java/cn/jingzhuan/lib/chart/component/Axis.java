package cn.jingzhuan.lib.chart.component;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import cn.jingzhuan.lib.chart.AxisAutoValues;
import cn.jingzhuan.lib.chart.data.GirdLineColorSetter;
import cn.jingzhuan.lib.chart.data.LabelColorSetter;
import cn.jingzhuan.lib.chart.data.ValueFormatter;
import java.util.List;

/**
 * Created by Donglua on 17/7/17.
 */

public class Axis extends AbstractComponent {

  private int mAxisPosition;

  private int mGridColor = Color.GRAY;
  private GirdLineColorSetter mGirdLineColorSetter = null;
  private float mGridThickness = 1;
  private int mGridCount = 3;

  private float mLabelTextSize;
  private float mLabelSeparation = 0;
  private int mLabelTextColor = Color.GREEN;
  private Paint mLabelTextPaint;
  private int mLabelWidth = 100;
  private int mLabelHeight = 0;
  private int mAxisColor = Color.GRAY;
  private float mAxisThickness = 2;
  private List<String> mLabels;

  private AxisAutoValues axisAutoValues = new AxisAutoValues();

  private ValueFormatter mLabelValueFormatter;

  public float[] mLabelEntries = new float[] {};
  private boolean gridLineEnable = true;
  private boolean labelEnable = true;

  private float mDashedGridIntervals[] = null;
  private float mDashedGridPhase = -1;

  private LabelColorSetter mLabelColorSetter;

  Axis(int axisPosition) {
    this.mAxisPosition = axisPosition;
  }

  public void setLabelTextSize(float mLabelTextSize) {
    this.mLabelTextSize = mLabelTextSize;
  }

  public void setLabelSeparation(float mLabelSeparation) {
    this.mLabelSeparation = mLabelSeparation;
  }

  public void setLabelTextColor(int mLabelTextColor) {
    this.mLabelTextColor = mLabelTextColor;
  }

  public void setLabelTextPaint(Paint mLabelTextPaint) {
    this.mLabelTextPaint = mLabelTextPaint;
  }

  public void setLabelWidth(int mLabelWidth) {
    this.mLabelWidth = mLabelWidth;
  }

  public void setLabelHeight(int mLabelHeight) {
    this.mLabelHeight = mLabelHeight;
  }

  public float getLabelTextSize() {
    return mLabelTextSize;
  }

  public int getLabelSeparation() {
    return Math.round(mLabelSeparation);
  }

  public int getLabelTextColor() {
    return mLabelTextColor;
  }

  public Paint getLabelTextPaint() {
    return mLabelTextPaint;
  }

  public int getLabelWidth() {
    if (isInside()) {
      return 0;
    }

    return mLabelWidth;
  }

  public boolean isInside() {
    switch (getAxisPosition()) {
      case AxisY.LEFT_INSIDE:
      case AxisY.RIGHT_INSIDE:
      case AxisX.BOTTOM_INSIDE:
      case AxisX.TOP_INSIDE:
        return true;
      default:
        return false;
    }
  }

  public int getLabelHeight() {
    return mLabelHeight;
  }

  public AxisAutoValues getAxisAutoValues() {
    return axisAutoValues;
  }

  public int getAxisPosition() {
    return mAxisPosition;
  }

  public void setAxisPosition(int mAxisPosition) {
    this.mAxisPosition = mAxisPosition;
  }

  public int getGridColor() {
    return mGridColor;
  }

  public void setGridColor(int mGridColor) {
    this.mGridColor = mGridColor;
  }

  public float getGridThickness() {
    return mGridThickness;
  }

  public void setGridThickness(float mGridThickness) {
    this.mGridThickness = mGridThickness;
  }

  public int getAxisColor() {
    return mAxisColor;
  }

  /**
   * 设置四周边框线的颜色
   */
  public void setAxisColor(int mAxisColor) {
    this.mAxisColor = mAxisColor;
  }

  public float getAxisThickness() {
    return mAxisThickness;
  }

  public void setAxisThickness(float mAxisThickness) {
    this.mAxisThickness = mAxisThickness;
  }

  public int getGridCount() {
    return mGridCount;
  }

  /**
   * 设置垂直或者水平方向分隔次数。如果是1分隔成两部分。
   * 如果是2那么被分割为3部分。
   */
  public void setGridCount(int mGridCount) {
    this.mGridCount = mGridCount;
  }

  /**
   * 设置是否绘制网格线
   */
  public void setGridLineEnable(boolean gridLineEnable) {
    this.gridLineEnable = gridLineEnable;
  }

  public boolean isGridLineEnable() {
    return gridLineEnable;
  }

  public boolean isLabelEnable() {
    return labelEnable;
  }

  public void setLabelEnable(boolean labelEnable) {
    this.labelEnable = labelEnable;
  }

  public ValueFormatter getLabelValueFormatter() {
    return mLabelValueFormatter;
  }

  /**
   * 自定义标签的显示内容
   * 当设置分隔数量的时候{@link #setGridCount} ，每个分隔线下面的标签内容
   * 如果设置{@link #setLabels} 那么此设置将失效
   */
  public void setLabelValueFormatter(ValueFormatter mValueFormatter) {
    this.mLabelValueFormatter = mValueFormatter;
  }

  public float getDashedGridPhase() {
    return mDashedGridPhase;
  }

  public float[] getDashedGridIntervals() {
    return mDashedGridIntervals;
  }

  /**
   * 设置网格线DashPathEffect 效果参数
   * 参数和系统自带的{@link DashPathEffect} 相同
   */
  public void enableGridDashPathEffect(float intervals[], float phase) {
    this.mDashedGridIntervals = intervals;
    this.mDashedGridPhase = phase;
  }

  /**
   * 设置每行或者每个竖直的网格线颜色
   * 下标从1开始
   */
  public void setGirdLineColorSetter(GirdLineColorSetter mGirdLineColorSetter) {
    this.mGirdLineColorSetter = mGirdLineColorSetter;
  }

  public GirdLineColorSetter getGirdLineColorSetter() {
    return mGirdLineColorSetter;
  }

  public List<String> getLabels() {
    return mLabels;
  }

  public void setLabels(List<String> mLabels) {
    this.mLabels = mLabels;
  }

  public LabelColorSetter getLabelColorSetter() {
    return mLabelColorSetter;
  }

  public void setLabelColorSetter(LabelColorSetter mLabelColorSetter) {
    this.mLabelColorSetter = mLabelColorSetter;
  }
}
