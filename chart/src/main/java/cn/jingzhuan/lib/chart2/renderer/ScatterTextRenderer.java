package cn.jingzhuan.lib.chart2.renderer;

import static cn.jingzhuan.lib.chart.data.ScatterTextDataSet.ALIGN_BOTTOM;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.List;

import cn.jingzhuan.lib.chart.base.AbstractChart;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.ScatterTextData;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextValue;

public class ScatterTextRenderer extends AbstractDataRenderer<ScatterTextDataSet> {
    // 顶部和底部给个边距
    public final static int margin = 2;

    ScatterTextData scatterTextData;

    private final Paint mTextPaint;

    public ScatterTextRenderer(AbstractChart chart) {
        super(chart);

        chart.setInternalViewportChangeListener(viewport -> {
            mViewport.set(viewport);
            calcDataSetMinMax();
        });
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<ScatterTextDataSet> chartData) {
        List<ScatterTextDataSet> dataSets = getDataSet();
        for (int i = dataSets.size() - 1; i >= 0; i--) {
            ScatterTextDataSet dataSet = dataSets.get(i);
            renderDataSet(canvas, chartData, dataSet);
        }
//        for (int i = dataSets.size() - 1; i >= 0; i--) {
//            ScatterTextDataSet dataSet = dataSets.get(i);
//            if (dataSet.isVisible()) {
//                cal(canvas, dataSet, i,
//                        chartData.getLeftMax(), chartData.getLeftMin(),
//                        chartData.getRightMax(), chartData.getRightMin());
//            }
//        }
//        for (int i = dataSets.size() - 1; i >= 0; i--) {
//            ScatterTextDataSet dataSet = dataSets.get(i);
//            if (dataSet.isVisible()) {
//                drawDataSet2(canvas, dataSet, i,
//                        chartData.getLeftMax(), chartData.getLeftMin(),
//                        chartData.getRightMax(), chartData.getRightMin());
//            }
//        }

    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<ScatterTextDataSet> chartData, ScatterTextDataSet dataSet) {
        List<ScatterTextDataSet> dataSets = getDataSet();
        int i = dataSets.indexOf(dataSet);
        if (dataSet.isVisible()) {
            drawDataSet(canvas, dataSet, i,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawDataSet(Canvas canvas, ScatterTextDataSet dataSet, int index, float leftMax, float leftMin, float rightMax, float rightMin) {
        mRenderPaint.setStrokeWidth(2);
        mRenderPaint.setColor(dataSet.getLineColor());
        mRenderPaint.setTextSize(dataSet.getTextSize());

        mTextPaint.setColor(dataSet.getTextColor());
        mTextPaint.setTextSize(dataSet.getTextSize());

        String text = dataSet.getText();
        int bgColor = dataSet.getTextBgColor();
        int lineColor = dataSet.getLineColor();
        int frameColor = dataSet.getFrameColor();
        int axisDependency = dataSet.getAxisDependency();
        int dashHeight = dataSet.getLineDashHeight();
        int textPadding = dataSet.getTextPadding();
        int dashLength;

        Rect textBound = new Rect();
        mRenderPaint.getTextBounds(text, 0, text.length(), textBound);

        int textRectHeight = textBound.height();
        int textRectWidth = textBound.width();

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
            if (!value.isVisible() || Float.isNaN(value.getHigh()) || Float.isNaN(value.getLow()))
                continue;

            int offset = calOffset(index, i, textPadding);
            dashLength = dashHeight + offset;
//            Log.d("ScatterTextRenderer", "------dashLength = BASE_DASH_LENGTH:" + BASE_DASH_LENGTH + " + offset:" + offset + " = " + dashLength);
//            Log.v("ScatterTextRenderer", "drawDataSet " + i + ", index_" + index + ":" + text + ", textRectHeight:" + textRectHeight + ", offset:" + offset + ", dashLength:" + dashLength);

            float xPosition = startX + step * (i + dataSet.getStartIndexOffset());
            float yHighPosition = (max - value.getHigh()) / (max - min) * mContentRect.height();
            float yLowPosition = (max - value.getLow()) / (max - min) * mContentRect.height();
            float anchor = yHighPosition;


            final float candlestickCenterX = (float) (xPosition + candleWidth * 0.5);
            value.setCoordinate(candlestickCenterX, anchor);
            float bottom = anchor - dashLength;
            float top = bottom - textRectHeight - textPadding * 2;
            float pathEnd = bottom;
            // 如果此时顶部位置小于最小边距 向下
            if(top < margin) {
                anchor = yLowPosition;
                bottom = anchor + dashLength + textRectHeight;
                top = bottom - textRectHeight - textPadding * 2;
                pathEnd = top;
            }

            RectF roundRect = new RectF();
            float right, left;
            switch (axisDependency) {
                case AxisY.DEPENDENCY_LEFT:
                    right = candlestickCenterX + textPadding;
                    left = candlestickCenterX - textRectWidth - textPadding;
                    break;
                case AxisY.DEPENDENCY_RIGHT:
                    right = candlestickCenterX + textRectWidth + textPadding;
                    left = candlestickCenterX - textPadding;
                    break;
                case AxisY.DEPENDENCY_BOTH:
                default:
                    right = candlestickCenterX + textRectWidth * 0.5f + textPadding;
                    left = candlestickCenterX - textRectWidth * 0.5f - textPadding;
                    break;
            }

            if (axisDependency == AxisY.DEPENDENCY_BOTH) {
                float maxRight = mContentRect.width() - mRenderPaint.getStrokeWidth();
                if (right > maxRight && candlestickCenterX < mContentRect.width()) {
                    right = maxRight;
                    left = maxRight - textRectWidth - textPadding * 2;
                }
                float minLeft = mRenderPaint.getStrokeWidth();
                if (left < minLeft && candlestickCenterX > 0) {
                    left = minLeft;
                    right = left + textRectWidth + textPadding * 2;
                }
            }
            if (dataSet.getAlign() == ALIGN_BOTTOM) {
                anchor = yLowPosition;
                bottom = anchor + dashLength + textRectHeight;
                top = bottom - textRectHeight - textPadding * 2;
                pathEnd = top;
                // 如果此时底部位置大于(mContentRect.height() - margin) 向上
                if(bottom > mContentRect.height() - margin) {
                    anchor = yHighPosition;
                    bottom = anchor - dashLength;
                    top = bottom - textRectHeight - textPadding * 2;
                    pathEnd = bottom;
                }
//                Log.w("ScatterTextRenderer", "drawDataSet " + i + ", index_" + index + ":" + text + ", h:" + (Math.abs(top - bottom)) + "(top:" + top + ", bottom:" + bottom + "), left:" + left + ", right:" + right);
            }

            roundRect.set(left, top, right, bottom);
            mRenderPaint.setPathEffect(null);
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(bgColor);
            canvas.drawRoundRect(roundRect, 2f, 2f, mRenderPaint);

            mRenderPaint.setStyle(Paint.Style.STROKE);
            mRenderPaint.setColor(frameColor);
            canvas.drawRoundRect(roundRect, 2f, 2f, mRenderPaint);

            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseline = roundRect.centerY() + distance;
            canvas.drawText(text, roundRect.centerX(), baseline, mTextPaint);

            Path path = new Path();
            path.moveTo(candlestickCenterX, anchor);
            path.lineTo(candlestickCenterX, pathEnd);

            mRenderPaint.setColor(lineColor);
            mRenderPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 0));
            canvas.drawPath(path, mRenderPaint);

        }

    }

    private int calOffset(int index, int i, int textPadding) {
        List<ScatterTextDataSet> dataSets = getDataSet();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        Rect textBound = new Rect();
        int offset = 0;
        int invalidTime = 0;
        for (int j = 0; j < index; j++) {
            ScatterTextDataSet dataSet = dataSets.get(j);
            if (dataSet.getAlign() != dataSets.get(index).getAlign()) {
                invalidTime++;
                continue;
            }

            ScatterTextValue value = dataSet.getEntryForIndex(i);
            if (!value.isVisible() || Float.isNaN(value.getHigh()) || Float.isNaN(value.getLow())) {
                invalidTime++;
                continue;
            }

            String text = dataSet.getText();
            paint.setTextSize(dataSet.getTextSize());
            paint.getTextBounds(text, 0, text.length(), textBound);

            int textHeight = textBound.height();
            int oldOffset = offset;
            offset += textHeight + textPadding * 2;
//            Log.d("ScatterTextRenderer", "------calOffset " + i + " index_" + j + ":" + text + ", textHeight:" + textHeight + ", offset = " + oldOffset + " + textHeight:" + textHeight + " + PADDING * 2 = " + offset);
        }
        int oldOffset = offset;
        if (offset > 0) {
            offset += textPadding * (index - invalidTime);
//            Log.d("ScatterTextRenderer", "------------offset = " + oldOffset + " + PADDING * " + (index - invalidTime) + " = " + offset);
        } else {
//            Log.d("ScatterTextRenderer", "------------offset = 0");
        }
        return offset;
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
