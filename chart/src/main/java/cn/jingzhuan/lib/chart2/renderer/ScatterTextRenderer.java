package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Timer;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.PointLineData;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextData;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextValue;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart2.base.Chart;

public class ScatterTextRenderer extends AbstractDataRenderer<ScatterTextDataSet> {

    ScatterTextData scatterTextData;
    private Paint mTextPaint;

    public ScatterTextRenderer(Chart chart) {
        super(chart);

        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<ScatterTextDataSet> chartData) {
        for (ScatterTextDataSet dataSet : getDataSet()) {
            if (dataSet.isVisible()) {
                drawDataSet(canvas, dataSet,
                        chartData.getLeftMax(), chartData.getLeftMin(),
                        chartData.getRightMax(), chartData.getRightMin());
            }
        }

    }

    private void drawDataSet(Canvas canvas, ScatterTextDataSet dataSet, float leftMax, float leftMin, float rightMax, float rightMin) {
        mRenderPaint.setStrokeWidth(2);
        mRenderPaint.setColor(dataSet.getLineColor());
        mRenderPaint.setTextSize(dataSet.getTextSize());

        mTextPaint.setColor(dataSet.getLineColor());
        mTextPaint.setTextSize(dataSet.getTextSize());

        String text = dataSet.getText();
        int bgColor = dataSet.getTextBgColor();
        int lineColor = dataSet.getLineColor();
        int dashLength = 60;

        Rect textBound = new Rect();
        mRenderPaint.getTextBounds(text, 0, text.length(), textBound);
        int padding = 10;

        int textRectHeight = textBound.height() ;
        int textRectWidth = textBound.width() ;

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

        final float scale = 1 / mViewport.width();
        final float step = mContentRect.width() * scale / valueCount;
        final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;
        final float visibleRange = dataSet.getVisibleRange(mViewport);
        int i = 0;
        int valuePhaseCount = (int) Math.floor(valueCount * mChartAnimator.getPhaseX());
        double candleWidth = mContentRect.width() / Math.max(visibleRange, dataSet.getMinValueCount());

        for (; i < valuePhaseCount && i < dataSet.getValues().size(); i++) {
            ScatterTextValue value = dataSet.getEntryForIndex(i);
            if (!value.isVisible() || Float.isNaN(value.getHigh())|| Float.isNaN(value.getLow()))
                continue;

            float xPosition = startX + step * (i + dataSet.getStartIndexOffset());
            float yPosition = (max - value.getHigh()) / (max - min) * mContentRect.height();
            float antiYPosition = (max - value.getLow()) / (max - min) * mContentRect.height();


            final float candlestickCenterX = (float) (xPosition + candleWidth * 0.5);
            value.setCoordinate(candlestickCenterX, yPosition);


            RectF roundRect = new RectF();
            float right = candlestickCenterX + textRectWidth * 0.5f + padding;
            float left = candlestickCenterX - textRectWidth * 0.5f - padding;
            float top = yPosition - dashLength - textRectHeight - padding;
            float bottom = yPosition - dashLength + padding;
            float pathEnd  = bottom;

            if (right > mContentRect.width() && xPosition < mContentRect.width()) {
                right = mContentRect.width() - padding;
                left = mContentRect.width() - textRectWidth - padding * 3;
            }

            if (top < 0){
                top = antiYPosition + dashLength - padding;
                bottom = top + textRectHeight + padding * 2;
                yPosition = antiYPosition;
                pathEnd = top;
            }

            roundRect.set(left, top, right, bottom);
            mRenderPaint.setPathEffect(null);
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(bgColor);
            canvas.drawRoundRect(roundRect, 2f, 2f, mRenderPaint);

            mRenderPaint.setStyle(Paint.Style.STROKE);
            mRenderPaint.setColor(lineColor);
            canvas.drawRoundRect(roundRect, 2f, 2f, mRenderPaint);

            Paint.FontMetrics fontMetrics=mTextPaint.getFontMetrics();
            float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
            float baseline=roundRect.centerY()+distance;
            canvas.drawText(text, roundRect.centerX(), baseline , mTextPaint);

            Path path = new Path();
            path.moveTo(candlestickCenterX, yPosition);
            path.lineTo(candlestickCenterX, pathEnd);

            mRenderPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 0));
            canvas.drawPath(path, mRenderPaint);

        }

    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull Highlight[] highlights) {

    }

    @Override
    public void removeDataSet(ScatterTextDataSet dataSet) {
        scatterTextData.remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        scatterTextData.clear();
        calcDataSetMinMax();
    }

    @Override
    protected List<ScatterTextDataSet> getDataSet() {
        return scatterTextData.getDataSets();
    }

    @Override
    public ChartData<ScatterTextDataSet> getChartData() {
        if (scatterTextData == null) scatterTextData = new ScatterTextData();
        return scatterTextData;
    }
}
