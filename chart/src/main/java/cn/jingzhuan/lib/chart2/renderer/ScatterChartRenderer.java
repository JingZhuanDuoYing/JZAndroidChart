package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterValue;
import cn.jingzhuan.lib.chart.renderer.TextValueRenderer;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.ScatterData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Scatter Chart Renderer
 * <p>
 * Created by donglua on 10/19/17.
 */

public class ScatterChartRenderer extends AbstractDataRenderer<ScatterDataSet> {

    private ScatterData scatterData;

    private final Map<String, Float> alignTopHeights = new HashMap<>();

    private final Map<String, Float> alignBottomHeights = new HashMap<>();

    private final Map<String, Float> alignParentTopHeights = new HashMap<>();

    private final Map<String, Float> alignParentBottomHeights = new HashMap<>();

    public ScatterChartRenderer(Chart chart) {
        super(chart);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<ScatterDataSet> chartData) {
        clearTemporaryData();
        for (ScatterDataSet dataSet : getDataSet()) {
            renderDataSet(canvas, chartData, dataSet);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<ScatterDataSet> chartData, ScatterDataSet dataSet) {
        clearTemporaryData();
        for (ScatterDataSet scatterDataSet : getDataSet()) {
            drawDataSet(canvas, scatterDataSet, chartData.getLeftMax(), chartData.getLeftMin(), chartData.getRightMax(), chartData.getRightMin());
        }
//    if (dataSet.isVisible()) {
//      drawDataSet(canvas, dataSet,
//              chartData.getLeftMax(), chartData.getLeftMin(),
//              chartData.getRightMax(), chartData.getRightMin());
//    }
    }

    private void drawDataSet(Canvas canvas, final ScatterDataSet dataSet, float leftMax, float leftMin, float rightMax, float rightMin) {

        mRenderPaint.setStrokeWidth(1);
        mRenderPaint.setColor(dataSet.getColor());

        int valueCount = dataSet.getEntryCount();

        float min, max;
        switch (dataSet.getAxisDependency()) {
            case AxisY.DEPENDENCY_RIGHT:
                min = rightMin;
                max = rightMax;
                break;
            case AxisY.DEPENDENCY_BOTH:
            case AxisY.DEPENDENCY_LEFT:
            default:
                min = leftMin;
                max = leftMax;
                break;
        }

        final float width = (mContentRect.width() - dataSet.getStartXOffset() - dataSet.getEndXOffset())
                / dataSet.getVisibleRange(mViewport) + 1;

        float shapeWidth = dataSet.getShape().getIntrinsicWidth();
        float shapeHeight = dataSet.getShape().getIntrinsicHeight();
        if (dataSet.isAutoWidth()) {
            shapeWidth = Math.max(width * 0.8f, dataSet.getShapeMinWidth());
            if (!Float.isNaN(dataSet.getShapeMaxWidth())) {
                shapeWidth = Math.min(shapeWidth, dataSet.getShapeMaxWidth());
            }
            shapeHeight = shapeWidth * shapeHeight / ((float) dataSet.getShape().getIntrinsicWidth());
        }

        float yOffset;
        if (dataSet.getShapeAlign() == ScatterDataSet.SHAPE_ALIGN_CENTER) {
            yOffset = shapeHeight * 0.5f;
        } else {
            yOffset = 0f;
        }
        Drawable shape = dataSet.getShape();

        for (int i = 0; i < valueCount && i < dataSet.getValues().size() && shape != null; i++) {
            ScatterValue point = dataSet.getEntryForIndex(i);

            if (!point.isVisible()) continue;

            float xPosition = dataSet.getStartXOffset() + width * 0.5f
                    + getDrawX((i + dataSet.getStartIndexOffset()) / ((float) valueCount)) - shapeWidth * 0.5f;

            float yPosition;
            String heightIndexKey = String.valueOf(i);
            if (dataSet.getShapeAlign() == ScatterDataSet.SHAPE_ALIGN_PARENT_BOTTOM) {
                float offset = shapeHeight;
                if(!Float.isNaN(point.getValue())) {
                    float lastOffset = alignParentBottomHeights.get(heightIndexKey) == null ? 0f : alignParentBottomHeights.get(heightIndexKey);
                    offset = offset + lastOffset;
                    alignParentBottomHeights.put(heightIndexKey, offset);
                }
                yPosition = mContentRect.height() - offset;
            } else if (dataSet.getShapeAlign() == ScatterDataSet.SHAPE_ALIGN_PARENT_TOP) {
                float offset = alignParentTopHeights.get(heightIndexKey) == null ? 0f : alignParentTopHeights.get(heightIndexKey);
                yPosition = mContentRect.top + offset;
                if(!Float.isNaN(point.getValue())) {
                    alignParentTopHeights.put(heightIndexKey, offset + shapeHeight);
                }

            } else if (dataSet.getShapeAlign() == ScatterDataSet.SHAPE_ALIGN_BOTTOM) {
                float offset = shapeHeight;
                if(!Float.isNaN(point.getValue())) {
                    float lastOffset = alignBottomHeights.get(heightIndexKey) == null ? 0f : alignBottomHeights.get(heightIndexKey);
                    offset = offset + lastOffset;
                    alignBottomHeights.put(heightIndexKey, offset);
                }
                yPosition = (max - point.getValue()) / (max - min) * mContentRect.height() - offset;
            } else if (dataSet.getShapeAlign() == ScatterDataSet.SHAPE_ALIGN_TOP) {
                float offset = alignTopHeights.get(heightIndexKey) == null ? 0f : alignTopHeights.get(heightIndexKey);
                yPosition = (max - point.getValue()) / (max - min) * mContentRect.height() + offset;
                if(!Float.isNaN(point.getValue())) {
                    alignTopHeights.put(heightIndexKey, offset + shapeHeight);
                }
            } else {
                yPosition = (max - point.getValue()) / (max - min) * mContentRect.height() - yOffset;
            }

            shape = dataSet.getShape();
            if (point.getShape() != null) {
                shape = point.getShape();
            }

            point.setCoordinate(xPosition, yPosition);

            int x = (int) (xPosition + dataSet.getDrawOffsetX());
            int y = (int) (yPosition + dataSet.getDrawOffsetY());
            if (point.getColor() != Color.TRANSPARENT) {
                shape.setColorFilter(point.getColor(), PorterDuff.Mode.SRC_OVER);
            }
            shape.setBounds(x,
                    y,
                    (int) (x + shapeWidth),
                    (int) (y + shapeHeight));
            int saveId = canvas.save();
            shape.draw(canvas);
            canvas.restoreToCount(saveId);

            if (dataSet.getTextValueRenderers() != null) {
                for (TextValueRenderer textValueRenderer : dataSet.getTextValueRenderers()) {
                    textValueRenderer.render(canvas, i,
                            x + shapeWidth * 0.5f, y + shapeHeight * 0.5f);
                }
            }
        }

    }

    @Override
    public int getEntryIndexByCoordinate(float x, float y) {
        int index = -1;
        if (scatterData.getDataSets().size() > 0) {
            ScatterDataSet dataSet = scatterData.getDataSets().get(0);
            RectF rect = new RectF();
            Drawable shape = dataSet.getShape();
            float shapeWidth = shape.getIntrinsicWidth();
            float shapeHeight = shape.getIntrinsicHeight();
            for (int i = 0; i < dataSet.getValues().size(); i++) {
                final ScatterValue value = dataSet.getEntryForIndex(i);
                float pX = value.getX();
                float pY = value.getY();
                rect.set(pX, pY, pX + shapeWidth, pY + shapeHeight);
                if (rect.contains(x, y)) {
                    index = i;
                    break;
                }
            }
            return index;
        }
        return super.getEntryIndexByCoordinate(x, y);
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
    }

    @Override
    public void removeDataSet(ScatterDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();
        clearTemporaryData();
    }

    @Override
    protected List<ScatterDataSet> getDataSet() {
        return getChartData().getDataSets();
    }

    @Override
    public ChartData<ScatterDataSet> getChartData() {
        if (scatterData == null) {
            scatterData = new ScatterData();
        }
        return scatterData;
    }

    private void clearTemporaryData() {
        alignTopHeights.clear();
        alignBottomHeights.clear();
        alignParentTopHeights.clear();
        alignParentBottomHeights.clear();
    }

}
