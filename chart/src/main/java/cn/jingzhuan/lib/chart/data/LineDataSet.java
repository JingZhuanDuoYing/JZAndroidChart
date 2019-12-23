package cn.jingzhuan.lib.chart.data;

import android.graphics.Shader;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

import static cn.jingzhuan.lib.chart.component.AxisY.*;

/**
 * Created by Donglua on 17/7/19.
 */

public class LineDataSet extends AbstractDataSet<PointValue> {

  private int mLineThickness = 2;

  protected List<PointValue> mPointValues;

  private int mForceValueCount = -1;
  private Shader shader;
  private Shader mShaderTop;
  private Shader mShaderBottom;
  private float mShaderBaseValue = Float.NaN;

  private boolean isLineVisible = true;

  public LineDataSet(List<PointValue> pointValues) {
    this(pointValues, DEPENDENCY_BOTH);
  }

  /**
   *
   * @param pointValues
   * @param depsAxis 存在缺陷 todo fix
   */
  public LineDataSet(List<PointValue> pointValues, @AxisDependency int depsAxis) {
    mPointValues = pointValues;
    if (mPointValues == null) {
      mPointValues = new ArrayList<>();
    }

    setAxisDependency(depsAxis);
  }

  public int getLineThickness() {
    return mLineThickness;
  }

  //设置中间折线图粗细
  public void setLineThickness(int mLineThickness) {
    this.mLineThickness = mLineThickness;
  }

  public List<PointValue> getLines() {
    return mPointValues;
  }

  /**
   * 得到数据集的数量,
   * 如果设置setForceValueCount那么将返回setForceValueCount所设置的数量
   * @return
   */
  @Override
  public int getEntryCount() {
    if (mPointValues == null) return 0;
    if (mForceValueCount > 0) return mForceValueCount;

    int entryCount = mPointValues.size();
    if (getMinValueCount() > entryCount) {
      entryCount = getMinValueCount();
    }
    return entryCount;
  }

  @Override
  public void calcMinMax(Viewport viewport) {

    if (mPointValues == null || mPointValues.isEmpty()) {
      return;
    }

    calcViewportY(viewport);
  }

  private void calcViewportY(Viewport viewport) {

    mViewportYMax = -Float.MAX_VALUE;
    mViewportYMin = Float.MAX_VALUE;

    for (PointValue e : getVisiblePoints(viewport)) {
      calcViewportMinMax(e);
    }
  }

  @Override
  public void setValues(List<PointValue> values) {
    this.mPointValues = values;
  }

  /**
   * 得到数据集
   * @return
   */
  @Override
  public List<PointValue> getValues() {
    return mPointValues;
  }

  private void calcViewportMinMax(PointValue e) {

    if (Float.isNaN(e.getValue()) || Float.isInfinite(e.getValue())) return;

    if (e.getValue() < mViewportYMin) {
      mViewportYMin = e.getValue();
    }

    if (e.getValue() > mViewportYMax) {
      mViewportYMax = e.getValue();
    }
  }

  @Override
  public boolean addEntry(PointValue e) {

    if (e == null) {
      return false;
    }

    if (mPointValues == null) {
      mPointValues = new ArrayList<>();
    }

    calcViewportMinMax(e);

    // add the entry
    return mPointValues.add(e);
  }

  @Override
  public boolean removeEntry(PointValue e) {

    if (e == null) {
      return false;
    }

    if (mPointValues == null) {
      return false;
    }

    // remove the entry
    boolean removed = mPointValues.remove(e);

    //if (removed) {
    //    setMinMax();
    //}

    return removed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getEntryIndex(PointValue e) {
    return mPointValues.indexOf(e);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public PointValue getEntryForIndex(int index) {
    return mPointValues.get(index);
  }

  /**
   * 设置这个LineDataSet对象在图表库中有多少个item数量，如果你setForceValueCount传入了100，现在数据只有50个数据项，
   * 那么你的50个数据项将会绘制在图表库View的x轴起始点到x轴中点。
   *
   * 例子1：
   * 股票分时线数据会在9:15-3:00之间
   * 那你假设你的分时线一天是240根，你现在是12点只有120根数据。
   * 但是如果你不设置setForceValueCount 那么图表库
   * 直接将120根数据平分到view中进行绘制，假设你需要120根数据只绘制在
   */
  public void setForceValueCount(int mForceValueCount) {
    this.mForceValueCount = mForceValueCount;
  }

  public int getForceValueCount() {
    return mForceValueCount;
  }

  public Shader getShader() {
    return shader;
  }

  public void setShader(Shader shader) {
    this.shader = shader;
  }

  public float getShaderBaseValue() {
    return mShaderBaseValue;
  }

  public void setShaderBaseValue(float shaderBaseValue, Shader shaderTop, Shader shaderBottom) {
    this.mShaderBaseValue = shaderBaseValue;
    this.mShaderTop = shaderTop;
    this.mShaderBottom = shaderBottom;
  }

  public Shader getShaderTop() {
    return mShaderTop;
  }

  public Shader getShaderBottom() {
    return mShaderBottom;
  }

  public void setLineVisible(boolean lineVisible) {
    isLineVisible = lineVisible;
  }

  public boolean isLineVisible() {
    return isLineVisible;
  }
}
