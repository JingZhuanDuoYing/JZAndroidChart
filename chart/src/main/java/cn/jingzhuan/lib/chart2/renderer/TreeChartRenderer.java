package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Leaf;
import cn.jingzhuan.lib.chart.data.TreeData;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart.data.TreeValue;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * Created by guobosheng on 22/6/24.
 */

public class TreeChartRenderer extends AbstractDataRenderer<TreeDataSet> {

    private TreeData mTreeDataSets;
    private Chart mChart;

    public TreeChartRenderer(final Chart chart) {
        super(chart);
        mChart = chart;

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });

//        final Highlight highlight = new Highlight();
//        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
//            @Override
//            public void touch(float touchX, float touchY) {
//                if (chart.isHighlightDisable()) return;
//
//                for (TreeDataSet dataSet : getDataSet()) {
//                    if (dataSet.isHighlightedVerticalEnable()) {
//                        highlight.setTouchX(touchX);
//                        highlight.setTouchY(touchY);
//                        int index = getEntryIndexByCoordinate(touchX, touchY) - dataSet.getStartIndexOffset();
//                        if (index < dataSet.getValues().size()) {
//                            TreeValue treeValue = dataSet.getEntryForIndex(index);
//                            float yPosition = treeValue.getY();
//
//                            if (yPosition >= 0) {
//                                highlight.setX(touchX);
//                                highlight.setY(yPosition);
//                                highlight.setDataIndex(index);
//                                Log.d("筹码分布1", "TreeChartRenderer addOnTouchPointChangeListener highlight.x: " + highlight.getX()
//                                        + ", y: " + highlight.getX()
//                                        + ", touchX: " + highlight.getTouchX()
//                                        + ", touchY: " + highlight.getTouchY()
//                                        + ", dataIndex: " + highlight.getDataIndex()
//                                );
//                                chart.highlightValue(highlight);
//                            }
//                        }
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<TreeDataSet> chartData) {
        for (TreeDataSet dataSet : chartData.getDataSets()) {
            if (mChart.getFocusIndex() == -1) mChart.setFocusIndex(dataSet.getValues().size() - 1);
            if (dataSet.isVisible() && dataSet.getValues().size() > mChart.getFocusIndex()) {
                Log.d("筹码分布", "renderDataSet, mFocusIndex: " + mChart.getFocusIndex());
                drawTreeDataSet(canvas, dataSet, mChart.getFocusIndex(),
                        chartData.getLeftMax(), chartData.getLeftMin(),
                        chartData.getRightMax(), chartData.getRightMin());
            }
        }
    }

    private void drawTreeDataSet(Canvas canvas, TreeDataSet dataSet, int index,
                                 float lMax, float lMin, float rMax, float rMin) {
        mRenderPaint.setStrokeWidth(dataSet.getStrokeThickness());
        mRenderPaint.setStyle(Paint.Style.FILL);

        float min, max;
        switch (dataSet.getAxisDependency()) {
            case AxisY.DEPENDENCY_RIGHT:
                min = rMin;
                max = rMax;
                break;
            case AxisY.DEPENDENCY_BOTH:
            case AxisY.DEPENDENCY_LEFT:
            default:
                min = lMin;
                max = lMax;
                break;
        }

        // 只在图中间绘制指定的 TreeValue
        TreeValue treeValue = dataSet.getEntryForIndex(index);

        if (!treeValue.isEnable()) return;
        int leafCount = treeValue.getLeafCount();
        if (leafCount < 1) return;

        float zeroX = mContentRect.width() / 2f;
        float maxLeafSpace = mContentRect.width() / 4f;
        float maxLeafValue = treeValue.getMaxLeafValue();

        for (int i = 0; i < leafCount; i++) {
            Leaf leaf = treeValue.getLeafs().get(i);
            if (Float.isNaN(leaf.getHigh())) continue;

            float leftValue = leaf.getLeftValue();
            float rightValue = leaf.getRightValue();
            float high = leaf.getHigh() * mChartAnimator.getPhaseY();

            float y = calcHeight(high, max, min);
            treeValue.setCoordinate(mContentRect.width() / 2f, y);

            float left = zeroX - (leftValue / maxLeafValue) * maxLeafSpace;
            int positiveColor = ColorUtils.setAlphaComponent(dataSet.getPositiveColor(), dataSet.getColorAlpha());
            mRenderPaint.setColor(positiveColor);
            canvas.drawLine(left, y, zeroX, y, mRenderPaint);

            float right = zeroX + (rightValue / maxLeafValue) * maxLeafSpace;

            int negativeColor = ColorUtils.setAlphaComponent(dataSet.getNegativeColor(), dataSet.getColorAlpha());
            mRenderPaint.setColor(negativeColor);
            canvas.drawLine(zeroX, y, right, y, mRenderPaint);
        }
    }

    private float calcHeight(float value, float max, float min) {
        if (Float.compare(max, min) == 0) return 0;
        return (max - value) / (max - min) * mContentRect.height();
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {
//        mRenderPaint.setStyle(Paint.Style.FILL);
//        mRenderPaint.setStrokeWidth(1f);
//        mRenderPaint.setColor(getHighlightColor());
////        mRenderPaint.setColor(Color.RED);
//        for (Highlight highlight : highlights) {
//            if (highlight != null) {
//                canvas.drawLine(highlight.getX(),
//                        0,
//                        highlight.getX(),
//                        mContentRect.bottom,
//                        mRenderPaint);
//
//                // Horizontal
//                for (TreeDataSet treeDataSet : getDataSet()) {
//                    if (treeDataSet.isHighlightedHorizontalEnable()) {
//                        canvas.drawLine(0,
//                                highlight.getY(),
//                                mContentRect.right,
//                                highlight.getY(),
//                                mRenderPaint);
//                    }
//                }
//            }
//        }
//        mRenderPaint.setPathEffect(null);
    }

    @Override
    public void removeDataSet(TreeDataSet dataSet) {
        if (dataSet == null) return;
        mTreeDataSets.remove(dataSet);
        calcDataSetMinMax();

    }

    @Override
    public void clearDataSet() {
        mTreeDataSets.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<TreeDataSet> getDataSet() {
        return mTreeDataSets.getDataSets();
    }

    @Override
    public TreeData getChartData() {
        if (mTreeDataSets == null) mTreeDataSets = new TreeData();
        return mTreeDataSets;
    }
}
