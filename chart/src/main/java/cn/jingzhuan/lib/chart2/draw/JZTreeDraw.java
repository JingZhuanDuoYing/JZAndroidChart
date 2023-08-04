package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.graphics.ColorUtils;

import java.util.List;

import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Leaf;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart.data.TreeValue;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZTreeDraw extends BaseDraw<TreeValue, TreeDataSet> {

    public JZTreeDraw(Chart chart) {
        super(chart);
    }

    @Override
    public void drawDataSet(Canvas canvas, ChartData<TreeDataSet> chartData, TreeDataSet dataSet) {
        if (mChart.getFocusIndex() == -1) mChart.setFocusIndex(dataSet.getValues().size() - 1);
        if (dataSet.isVisible() && dataSet.getValues().size() > mChart.getFocusIndex()) {
            drawTreeDataSet(canvas, dataSet, mChart.getFocusIndex(),
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawTreeDataSet(Canvas canvas, TreeDataSet dataSet, int index, float lMax, float lMin, float rMax, float rMin) {

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

        List<Leaf> leafList = treeValue.getLeafs();
        if (leafList == null) return;

        for (Leaf leaf : leafList) {

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
}
