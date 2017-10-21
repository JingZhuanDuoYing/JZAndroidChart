package cn.jingzhuan.lib.chart.data;

import android.graphics.drawable.Drawable;
import cn.jingzhuan.lib.chart.Viewport;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterDataSet extends AbstractDataSet<ScatterValue> {

  private List<ScatterValue> scatterValues;

  private Drawable shape;
  private float drawOffsetX = 0f;
  private float drawOffsetY = 0f;

  private boolean autoWidth = true;

  public ScatterDataSet(List<ScatterValue> scatterValues) {
    this.scatterValues = scatterValues;
  }

  @Override public void calcMinMax(Viewport viewport) {

    mViewportYMax = -Float.MAX_VALUE;
    mViewportYMin = Float.MAX_VALUE;

    for (ScatterValue e : getVisiblePoints(viewport)) {
      calcViewportMinMax(e);
    }
  }

  private void calcViewportMinMax(ScatterValue e) {
    if (e.getValue() < mViewportYMin)
      mViewportYMin = e.getValue();

    if (e.getValue() > mViewportYMax)
      mViewportYMax = e.getValue();
  }

  @Override public int getEntryCount() {
    return getValues().size();
  }

  @Override public void setValues(List<ScatterValue> values) {
    this.scatterValues = values;
  }

  @Override public List<ScatterValue> getValues() {
    return scatterValues;
  }

  @Override public boolean addEntry(ScatterValue e) {
    calcViewportMinMax(e);
    return scatterValues.add(e);
  }

  @Override public boolean removeEntry(ScatterValue e) {
    return scatterValues.remove(e);
  }

  @Override public int getEntryIndex(ScatterValue e) {
    return scatterValues.indexOf(e);
  }

  @Override public ScatterValue getEntryForIndex(int index) {
    return scatterValues.get(index);
  }

  public void setShape(Drawable shape) {
    this.shape = shape;
  }

  public Drawable getShape() {
    return shape;
  }

  public float getDrawOffsetX() {
    return drawOffsetX;
  }

  public void setDrawOffsetX(float drawOffsetX) {
    this.drawOffsetX = drawOffsetX;
  }

  public float getDrawOffsetY() {
    return drawOffsetY;
  }

  public void setDrawOffsetY(float drawOffsetY) {
    this.drawOffsetY = drawOffsetY;
  }

  public void setAutoWidth(boolean autoWidth) {
    this.autoWidth = autoWidth;
  }

  public boolean isAutoWidth() {
    return autoWidth;
  }
}
