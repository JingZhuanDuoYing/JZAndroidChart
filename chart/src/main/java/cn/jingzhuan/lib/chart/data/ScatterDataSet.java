package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.HasValueXOffset;
import cn.jingzhuan.lib.chart.component.HasValueYOffset;
import cn.jingzhuan.lib.chart.renderer.TextValueRenderer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by donglua on 10/19/17.
 */

public class ScatterDataSet extends AbstractDataSet<ScatterValue> implements HasValueYOffset,
    HasValueXOffset {

  public final static int SHAPE_ALIGN_CENTER = 1;
  public final static int SHAPE_ALIGN_TOP = 2;
  public final static int SHAPE_ALIGN_BOTTOM = 3;
  public final static int SHAPE_ALIGN_PARENT_TOP = 4;
  public final static int SHAPE_ALIGN_PARENT_BOTTOM = 5;

  private List<ScatterValue> scatterValues;

  private Drawable shape;
  private int shapeOrder = 0; // 正数从value向上排，负数向下排，0不变
  private float drawOffsetX = 0f;
  private float drawOffsetY = 0f;
  private float shapeMinWidth = 0f;
  private float shapeMaxWidth = Float.NaN;
  private int shapeAlign = SHAPE_ALIGN_CENTER;

  private Rect mContentRect;

  private boolean autoWidth = true;

  private List<TextValueRenderer> mTextValueRenderers;

  public ScatterDataSet(List<ScatterValue> scatterValues) {
    this.scatterValues = scatterValues;
  }

  @Override public void calcMinMax(Viewport viewport, Rect content) {
    mContentRect = content;

    mViewportYMax = -Float.MAX_VALUE;
    mViewportYMin = Float.MAX_VALUE;

    if (shape != null && shapeOrder == 0) {
      if (drawOffsetY > 0) {
        shapeOrder = - 1;
      } else {
        shapeOrder = 1;
      }
    }

    List<ScatterValue> visiblePoints = getVisiblePoints(viewport);
    for (ScatterValue e : visiblePoints) {
      calcViewportMinMax(e, content);
    }

    float range = mViewportYMax - mViewportYMin;
    if (Float.compare(getMinValueOffsetPercent(), 0f) > 0f) {
      mViewportYMin = mViewportYMin - range * getMinValueOffsetPercent();
    }
    if (Float.compare(getMaxValueOffsetPercent(), 0f) > 0f) {
      mViewportYMax = mViewportYMax + range * getMaxValueOffsetPercent();
    }
  }

  private void calcViewportMinMax(ScatterValue e, Rect content) {
    if (e.getValue() < mViewportYMin)
      mViewportYMin = e.getValue();

    if (e.getValue() > mViewportYMax)
      mViewportYMax = e.getValue();

//    if (content == null) return;
//    if (shape == null) return;
//    if (!e.isVisible()) return;
//
//    float range = mViewportYMax - mViewportYMin;
//    float shapeHeight = shape.getIntrinsicHeight() + 4f; // 间隙4px
//    float percent = shapeHeight / (float) content.height();
//    float expand = (float) Math.ceil(range * percent);
//
//    if (expand <= 0f) return;
//
//    float offset = shapeOrder * expand;
//    float newValue = e.getValue() + offset;
//
//    if (shapeOrder > 0) {
//      mViewportYMax = Math.max(newValue, mViewportYMax);
//      return;
//    }
//
//    if (shapeOrder < 0) {
//      mViewportYMin = Math.min(newValue, mViewportYMin);
//    }

  }

  @Override public int getEntryCount() {
    if (getValues() == null) return 0;
    int entryCount = getValues().size();
    return getMinValueCount() > entryCount ? getMinValueCount() : entryCount;
  }

  @Override public void setValues(List<ScatterValue> values) {
    this.scatterValues = values;
  }

  @Override public List<ScatterValue> getValues() {
    return scatterValues;
  }

  @Override public boolean addEntry(ScatterValue e) {
    calcViewportMinMax(e, mContentRect);
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

  @Override public float getMaxValueOffsetPercent() {
    return maxValueOffsetPercent;
  }

  @Override public float getMinValueOffsetPercent() {
    return minValueOffsetPercent;
  }

  @Override
  public void setMinValueOffsetPercent(float minValueOffsetPercent) {
    this.minValueOffsetPercent = minValueOffsetPercent;
  }

  @Override
  public void setMaxValueOffsetPercent(float maxValueOffsetPercent) {
    this.maxValueOffsetPercent = maxValueOffsetPercent;
  }

  public float getStartXOffset() {
    return startXOffset;
  }

  public void setStartXOffset(float startXOffset) {
    this.startXOffset = startXOffset;
  }

  public float getEndXOffset() {
    return endXOffset;
  }

  public void setEndXOffset(float endXOffset) {
    this.endXOffset = endXOffset;
  }

  public void addTextValueRenderer(TextValueRenderer textValueRenderer) {
    if (mTextValueRenderers == null) {
      mTextValueRenderers = Collections.synchronizedList(new ArrayList<TextValueRenderer>());
    }
    this.mTextValueRenderers.add(textValueRenderer);
  }

  public float getShapeMinWidth() {
    return shapeMinWidth;
  }

  public float getShapeMaxWidth() {
    return shapeMaxWidth;
  }

  public void setShapeMinWidth(float shapeMinWidth) {
    this.shapeMinWidth = shapeMinWidth;
  }

  public void setShapeMaxWidth(float shapeMaxWidth) {
    this.shapeMaxWidth = shapeMaxWidth;
  }

  public List<TextValueRenderer> getTextValueRenderers() {
    return mTextValueRenderers;
  }

  public int getShapeAlign() {
    return shapeAlign;
  }

  public void setShapeAlign(int shapeAlign) {
    this.shapeAlign = shapeAlign;
  }

  public int getShapeOrder() {
    return shapeOrder;
  }

  public void setShapeOrder(int shapeOrder) {
    this.shapeOrder = shapeOrder;
  }
}

