package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.BarValue;
import cn.jingzhuan.lib.chart.Viewport;;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.BarData;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.ValueFormatter;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.utils.FloatUtils;
import java.util.List;

/**
 * Created by Donglua on 17/8/1.
 */

public class BarChartRenderer extends AbstractDataRenderer<BarDataSet> {

    private BarData mBarDataSets;
    private final char[] mLabelBuffer = new char[100];

    private Paint mValueTextPaint;

    public BarChartRenderer(final Chart chart) {
        super(chart);

        mValueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValueTextPaint.setStyle(Paint.Style.FILL);

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });

        final Highlight highlight = new Highlight();
        chart.addOnTouchPointChangeListener(new Chart.OnTouchPointChangeListener() {
            @Override
            public void touch(float x, float y) {
                if (chart.isHighlightDisable()) return;

                for (BarDataSet dataSet : getDataSet()) {
                    if (dataSet.isHighlightedVerticalEnable()) {
                        highlight.setTouchX(x);
                        highlight.setTouchY(y);
                        int index = getEntryIndexByCoordinate(x, y) - dataSet.getStartIndexOffset();
                        if (index < dataSet.getValues().size()) {
                            BarValue barValue = dataSet.getEntryForIndex(index);
                            float xPosition = barValue.getX();
                            float yPosition = barValue.getY();

                            if (xPosition >= 0 && yPosition >= 0) {
                                highlight.setX(xPosition);
                                highlight.setY(yPosition);
                                highlight.setDataIndex(index);
                                chart.highlightValue(highlight);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override protected void renderDataSet(Canvas canvas, ChartData<BarDataSet> chartData) {
        for (BarDataSet dataSet : chartData.getDataSets()) {
            if (dataSet.isVisible()) {
                drawBarDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
            }
        }
    }

    private void drawBarDataSet(Canvas canvas, BarDataSet barDataSet,
        float lMax, float lMin, float rMax, float rMin) {

        mRenderPaint.setStrokeWidth(barDataSet.getStrokeThickness());
        mRenderPaint.setStyle(Paint.Style.FILL);

        mValueTextPaint.setColor(barDataSet.getValueColor());
        mValueTextPaint.setTextSize(barDataSet.getValueTextSize());

        int valueCount = barDataSet.getEntryCount();

        float min, max;
        switch (barDataSet.getAxisDependency()) {
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

        float width = barDataSet.getBarWidth();
        float visibleRange = barDataSet.getVisibleRange(mViewport);
        if (barDataSet.isAutoBarWidth() && visibleRange > 0) {
            width = mContentRect.width() / visibleRange;
        }
        final float percent = barDataSet.getBarWidthPercent();

        final float scale = 1 / mViewport.width();
        final float step = mContentRect.width() * scale / valueCount;
        final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;

        for (int i = 0; i < valueCount && i < barDataSet.getValues().size(); i++) {
            BarValue barValue = barDataSet.getEntryForIndex(i);

            if (!barValue.isEnable()) continue;
            if (barValue.getValues().length < 1 || Float.isNaN(barValue.getValues()[0])) continue;

            if (barValue.getColor() != -2) {
                mRenderPaint.setColor(barValue.getColor());
            } else {
                mRenderPaint.setColor(barDataSet.getColor());
            }

            float x = startX + step * (i + barDataSet.getStartIndexOffset());

            float top;
            float bottom = calcHeight(0, max, min);

            if (barValue.getValueCount() > 0) {
                float value = barValue.getValues()[0] * mChartAnimator.getPhaseY();

                top = calcHeight(value, max, min) ;
                if (barValue.getValueCount() > 1) bottom = calcHeight(barValue.getValues()[1], max, min);

                barValue.setCoordinate(x + width * 0.5f, top);

                mRenderPaint.setStyle(barValue.getPaintStyle());

                float left = x + width * (1 - percent) * 0.5f;
                float right = left + width * percent;

                if (Math.abs(top - bottom) < 0.0001) {
                    canvas.drawLine(left, top, right, bottom, mRenderPaint);
                } else {
                    if (barValue.getGradientColors() != null && barValue.getGradientColors().length > 1) {
                        float centerX = (left + right) * 0.5f;
                        mRenderPaint.setShader(
                            new LinearGradient(centerX, top, centerX, bottom,
                                barValue.getGradientColors()[0],
                                barValue.getGradientColors()[1], Shader.TileMode.MIRROR));
                    }
                    canvas.drawRect(left, top, right, bottom, mRenderPaint);
                }

                int labelLength;
                int labelOffset;
                if (barDataSet.isDrawValueEnable()) {
                    ValueFormatter valueFormatter;
                    valueFormatter = barDataSet.getValueFormatter();
                    if (valueFormatter == null) {
                        labelLength = FloatUtils.formatFloatValue(mLabelBuffer, value, 2);
                    } else {
                        char[] labelCharArray = valueFormatter.format(barValue.getValues()[0], i).toCharArray();
                        labelLength = labelCharArray.length;
                        System.arraycopy(labelCharArray,
                            0,
                            mLabelBuffer,
                            mLabelBuffer.length - labelLength,
                            labelLength);
                    }
                    labelOffset = mLabelBuffer.length - labelLength;

                    mValueTextPaint.setTextAlign(Paint.Align.CENTER);

                    canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                        x + width * 0.5f,
                        top - 10, mValueTextPaint);
                }
            }
            mRenderPaint.setShader(null);
        }
        mRenderPaint.setStyle(Paint.Style.FILL);
    }

    private float calcHeight(float value, float max, float min) {
        if (Float.compare(max, min) == 0) return 0;
        return (max - value) / (max - min) * mContentRect.height();
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

        mRenderPaint.setColor(getHighlightColor());
        mRenderPaint.setStrokeWidth(1);
        mRenderPaint.setStyle(Paint.Style.STROKE);
        if (mHighlightedDashPathEffect != null) {
            mRenderPaint.setPathEffect(mHighlightedDashPathEffect);
        }

        for (Highlight highlight : highlights) {

            canvas.drawLine(
                    highlight.getX(),
                    0,
                    highlight.getX(),
                    mContentRect.bottom,
                    mRenderPaint);
        }

        mRenderPaint.setPathEffect(null);
    }

    @Override public void removeDataSet(BarDataSet dataSet) {
        if (dataSet == null) return;
        mBarDataSets.remove(dataSet);
        calcDataSetMinMax();

    }

    @Override public void clearDataSet() {
        mBarDataSets.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<BarDataSet> getDataSet() {
        return mBarDataSets.getDataSets();
    }

    @Override public BarData getChartData() {
        if (mBarDataSets == null) mBarDataSets = new BarData();
        return mBarDataSets;
    }

    @Override public void setTypeface(Typeface tf) {
        mValueTextPaint.setTypeface(tf);
    }
}
