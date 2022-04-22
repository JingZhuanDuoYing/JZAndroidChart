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
  private int shapeLevel = 0; // 正数向上扩展，负数向下扩展，0上下扩展一半
  private float drawOffsetX = 0f;
  private float drawOffsetY = 0f;
  private float shapeMinWidth = 0f;
  private float shapeMaxWidth = Float.NaN;
  private int shapeAlign = SHAPE_ALIGN_CENTER;

  protected float ORIGINAL_VIEWPORT_YMIN = Float.MAX_VALUE;
  protected float ORIGINAL_VIEWPORT_YMAX = -Float.MAX_VALUE;

  private Rect mContentRect;

  private boolean autoWidth = true;

  private List<TextValueRenderer> mTextValueRenderers;

  public ScatterDataSet(List<ScatterValue> scatterValues) {
    this.scatterValues = scatterValues;
  }

  @Override public void calcMinMax(Viewport viewport, Rect content, float max, float mix) {
    mContentRect = content;

    mViewportYMax = max;
    mViewportYMin = mix;
    ORIGINAL_VIEWPORT_YMAX = max;
    ORIGINAL_VIEWPORT_YMIN = mix;

    List<ScatterValue> visiblePoints = getVisiblePoints(viewport);
//    Log.e("ScatterDataSet", "calcMinMax_0 mViewportYMax:" + mViewportYMax
//            + ", mViewportYMin:" + mViewportYMin + ", visiblePoints:" + visiblePoints.size());
    for (int i = 0; i < visiblePoints.size(); i++) {
      ScatterValue e = visiblePoints.get(i);
      calcViewportMinMax(i, e, content);
    }
//    Log.e("ScatterDataSet", "calcMinMax_1 mViewportYMax:" + mViewportYMax
//            + ", mViewportYMin:" + mViewportYMin + ", visiblePoints:" + visiblePoints.size());

    float range = mViewportYMax - mViewportYMin;
    if (Float.compare(getMinValueOffsetPercent(), 0f) > 0f) {
      mViewportYMin = mViewportYMin - range * getMinValueOffsetPercent();
    }
    if (Float.compare(getMaxValueOffsetPercent(), 0f) > 0f) {
      mViewportYMax = mViewportYMax + range * getMaxValueOffsetPercent();
    }
  }

  private void calcViewportMinMax(int i, ScatterValue e, Rect content) {
    if (e == null || !e.isVisible()) return;
    if (Float.isNaN(e.getValue())) return;

    if (e.getValue() < mViewportYMin) {
      mViewportYMin = e.getValue();
//      Log.w("ScatterDataSet", "calcViewportMinMax:" + i
//              + ", e.value:" + e.getValue()
//              + ", mViewportYMax:" + mViewportYMax
//              + ", mViewportYMin:" + mViewportYMin);
    }

    if (e.getValue() > mViewportYMax) {
      mViewportYMax = e.getValue();
//      Log.w("ScatterDataSet", "calcViewportMinMax:" + i
//              + ", e.value:" + e.getValue()
//              + ", mViewportYMax:" + mViewportYMax
//              + ", mViewportYMin:" + mViewportYMin);
    }

    calcViewportMinMaxExpansion(i, e, content);
  }

  private void calcViewportMinMaxExpansion(int i, ScatterValue e, Rect content) {
    if (content == null) return;
    if (shape == null) return;

    float range = mViewportYMax - mViewportYMin;
//      float oldViewportYMax = mViewportYMax;
//      float oldViewportYMin = mViewportYMin;
//    if (mViewportYMax > -Float.MAX_VALUE || mViewportYMin < Float.MAX_VALUE) {
//      float shapeHeight = shape.getIntrinsicHeight() + 4f; // 间隙4px
//      Log.d("ScatterDataSet", "calcViewportMinMaxExpansion:" + i + ", range:" + range
//              + ", e.value:" + e.getValue() + ", mViewportYMax:" + mViewportYMax
//              + ", mViewportYMin:" + mViewportYMin + ", shapeHeight:" + shapeHeight
//              + ", contentRect.height():" + mContentRect.height());
//    }
    if (range <= 0f) return;

    float shapeHeight = shape.getIntrinsicHeight() + 4f; // 间隙4px
    float percent = shapeHeight / (float) content.height();
//    float expand = (float) Math.ceil(range * percent);
    float expand = range * percent;

    if (expand <= 0f) return;

    float anchor;
    if (shapeAlign == SHAPE_ALIGN_PARENT_TOP) {
      anchor = ORIGINAL_VIEWPORT_YMAX;
    } else if (shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {
      anchor = ORIGINAL_VIEWPORT_YMIN;
    } else {
      anchor = e.getValue();
    }

    float newValue;
    if (shapeAlign == SHAPE_ALIGN_TOP || shapeAlign == SHAPE_ALIGN_PARENT_TOP) {
      newValue = anchor + expand;
      mViewportYMax = Math.max(newValue, mViewportYMax);

    } else if (shapeAlign == SHAPE_ALIGN_BOTTOM || shapeAlign == SHAPE_ALIGN_PARENT_BOTTOM) {
      newValue = anchor - expand;
      mViewportYMin = Math.min(newValue, mViewportYMin);

    } else { // shapeAlign == SHAPE_ALIGN_CENTER
      float newMaxValue = anchor + (expand / 2);
      float newMinValue = anchor - (expand / 2);
      mViewportYMax = Math.max(newMaxValue, mViewportYMax);
      mViewportYMin = Math.min(newMinValue, mViewportYMin);
    }

//      if (oldViewportYMax != mViewportYMax) {
//        Log.w("ScatterDataSet", "calcViewportMinMaxExpansion:" + i
//                + ", e.value:" + e.getValue()
//                + ", range:" + range + ", shapeHeight:" + shapeHeight + ", contentRect.height:" + content.height()
//                + ", percent:" + percent
//                + ", (range * percent):" + (range * percent)
//                + ", expand:" + expand
//                + ", offset:" + offset
//                + ", mViewportYMax:" + mViewportYMax
//                + ", mViewportYMin:" + mViewportYMin);
//      }
//      if (oldViewportYMin != mViewportYMin) {
//        Log.w("ScatterDataSet", "calcViewportMinMaxExpansion:" + i
//                + ", e.value:" + e.getValue()
//                + ", range:" + range + ", shapeHeight:" + shapeHeight + ", contentRect.height:" + content.height()
//                + ", percent:" + percent
//                + ", (range * percent):" + (range * percent)
//                + ", expand:" + expand
//                + ", offset:" + offset
//                + ", mViewportYMax:" + mViewportYMax
//                + ", mViewportYMin:" + mViewportYMin);
//      }



//    float offset = shapeLevel * expand;
//    float newValue = anchor + offset;
//
//    if (offset > 0) {
//      mViewportYMax = Math.max(newValue, mViewportYMax);
//      return;
//    }
//
//    if (offset < 0) {
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
    calcViewportMinMax(scatterValues.size(), e, mContentRect);
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

  public int getShapeLevel() {
    return shapeLevel;
  }

  public void setShapeLevel(int shapeLevel) {
    this.shapeLevel = shapeLevel;
  }
}

