package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.animation.ChartAnimator;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.base.JZBaseChart;

/**
 * @author YL
 * @since 2023-08-04
 */
public abstract class BaseDraw<E extends Value, T extends AbstractDataSet<E>> implements IDraw<E, T>{

    protected final Chart mChart;

    protected Paint mRenderPaint;

    protected Viewport mViewport;

    protected Rect mContentRect;

    protected ChartAnimator mChartAnimator;

    protected List<T> dataSetList;

    public BaseDraw(final Chart chart) {
        this.mChart = chart;
        this.mViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();
        this.mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mRenderPaint.setStyle(Paint.Style.STROKE);
        this.dataSetList = new ArrayList<>();
        if (chart instanceof JZBaseChart) {
            this.mChartAnimator = ((JZBaseChart) chart).getChartAnimator();
        } else {
            this.mChartAnimator = new ChartAnimator(animation -> this.mChart.postInvalidate());
        }
    }

    protected void addOnTouchPointChangeListener() {
        final Highlight highlight = new Highlight();
        mChart.addOnTouchPointChangeListener((x, y) -> {

            if (mChart.isHighlightDisable()) return;

            synchronized (mChart) {
                for (T dataSet : getDataSets()) {
                    if (dataSet.isHighlightedVerticalEnable() && !dataSet.getValues().isEmpty()) {
                        highlight.setTouchX(x);
                        highlight.setTouchY(y);
                        int offset = dataSet.getStartIndexOffset();
                        int index = mChart.getEntryIndexByCoordinate(x, y) - offset;
                        index = Math.max(index, 0);
                        index = Math.min(index, dataSet.getValues().size() - 1);

                        final E value = dataSet.getEntryForIndex(index);
                        float xPosition = value.getX();
                        float yPosition = value.getY();

                        if (xPosition >= 0 && yPosition >= 0) {
                            highlight.setX(xPosition);
                            highlight.setY(yPosition);
                            highlight.setDataIndex(index);
                            mChart.highlightValue(highlight);
                        }
                    }
                }
            }
        });
    }

    protected List<T> getDataSets() {
        return dataSetList;
    }

    protected float getDrawX(float x) {
        return mContentRect.left + mContentRect.width() * (x - mViewport.left) / mViewport.width();
    }

}
