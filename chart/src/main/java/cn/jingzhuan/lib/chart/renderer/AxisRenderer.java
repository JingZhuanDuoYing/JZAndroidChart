package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.AxisAutoValues;
import cn.jingzhuan.lib.chart.Chart;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.utils.FloatUtils;
import cn.jingzhuan.lib.chart.value.LabelColorSetter;

/**
 * Created by Donglua on 17/7/17.
 */

public class AxisRenderer implements Renderer {

    private Viewport mCurrentViewport;
    private Rect mContentRect;
    private Axis mAxis;

    // Buffers used during drawing. These are defined as fields to avoid allocation during
    // draw calls.
//    private float[] mAxisPositionsBuffer = new float[]{};
    private final char[] mLabelBuffer = new char[100];
//    private AxisAutoValues mStopsBuffer = new AxisAutoValues();

    private Paint mGridPaint;
    private Paint mLabelTextPaint;
    private Paint mAxisPaint;

    private static final int POW10[] = {1, 10, 100, 1000, 10000, 100000, 1000000};

    public AxisRenderer(Chart chart, Axis axis) {
        this.mCurrentViewport = chart.getCurrentViewport();
        this.mContentRect = chart.getContentRect();
        this.mAxis = axis;

        initPaints();
    }

    public void initPaints() {
        mGridPaint = new Paint();
        mGridPaint.setStrokeWidth(mAxis.getGridThickness());
        mGridPaint.setColor(mAxis.getGridColor());
        mGridPaint.setStyle(Paint.Style.STROKE);

        mLabelTextPaint = new Paint();
        mLabelTextPaint.setAntiAlias(true);
        mLabelTextPaint.setTextSize(mAxis.getLabelTextSize());
        mLabelTextPaint.setColor(mAxis.getLabelTextColor());
        mAxis.setMaxLabelWidth((int) mLabelTextPaint.measureText("0000"));
        mAxis.setLabelHeight((int) Math.abs(mLabelTextPaint.getFontMetrics().top));

        mAxisPaint = new Paint();
        mAxisPaint.setStrokeWidth(mAxis.getAxisThickness());
        mAxisPaint.setColor(mAxis.getAxisColor());
        mAxisPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void renderer(Canvas canvas) {
        int i;

        if (mAxis instanceof AxisX) {

            computeAxisStopsX(
                    mCurrentViewport.left,
                    mCurrentViewport.right,
                    (AxisX) mAxis,
                    null);

        } else if (mAxis instanceof AxisY) {

            computeAxisStopsY((AxisY) mAxis);

        }

        // Draws lib container
        drawAxisLine(canvas);

        if (mAxis.isGridLineEnable()) {
            drawGridLines(canvas);
        }

        if (mAxis.isLabelEnable()) {
            drawLabels(canvas);
        }
    }

    private static void computeAxisStopsY(AxisY axis) {

        double min = axis.getYMin();
        double max = axis.getYMax();
        int count = axis.getGridCount() - 1;

        double interval = (max - min) / count;

        axis.mLabelEntries = new float[axis.getGridCount()];
        double f = min;
        for (int j = 0; j < axis.getGridCount(); f += interval, j++) {
            axis.mLabelEntries[j] = (float) f;
        }
    }

    // 坐标轴
    private void drawAxisLine(Canvas canvas) {
        float startX = 0f, startY = 0f, stopX = 0f, stopY = 0f;
        switch (mAxis.getAxisPosition()) {
            case AxisX.TOP:
            case AxisX.TOP_INSIDE:
                startX = mContentRect.left;
                startY = mContentRect.top;
                stopX  = mContentRect.right;
                stopY  = startY;
                break;
            case AxisX.BOTTOM:
            case AxisX.BOTTOM_INSIDE:
                startX = mContentRect.left;
                startY = mContentRect.bottom;
                stopX  = mContentRect.right;
                stopY  = startY;
                break;
            case AxisY.LEFT_INSIDE:
            case AxisY.LEFT_OUTSIDE:
                startX = mContentRect.left;
                startY = mContentRect.top;
                stopX  = startX;
                stopY  = mContentRect.bottom;
                break;
            case AxisY.RIGHT_INSIDE:
            case AxisY.RIGHT_OUTSIDE:
                startX = mContentRect.right;
                startY = mContentRect.top;
                stopX  = startX;
                stopY  = mContentRect.bottom;
                break;
        }
        // Draw axis line
        if (mAxis.isEnable()) {
            canvas.drawLine(startX, startY, stopX, stopY, mAxisPaint);
        }
    }

    private static void computeAxisStopsX(float start, float stop, AxisX axis, AxisAutoValues autoValues) {
        double range = stop - start;
        if (axis.getGridCount() == 0 || range <= 0) {
//            autoValues.values = new float[]{};
//            autoValues.number = 0;
            return;
        }

        final int count = axis.getGridCount();
//        autoValues.number = axis.getGridCount();
//        if (autoValues.values.length < autoValues.number) {
//            // Ensure values contains at least number elements.
//            autoValues.values = new float[autoValues.number];
//        }
        double rawInterval = range / count;

        double interval = roundToOneSignificantFigure(rawInterval);
        double first = Math.ceil(start / interval) * interval;

        double f;
        int i;

        axis.mLabelEntries = new float[count + 1];
        for (f = first, i = 0; i < count + 1; f += interval, ++i) {
//            autoValues.values[i] = (float) f;
            axis.mLabelEntries[i] = (float) f;
        }

//        if (interval < 1) {
//            autoValues.decimals = (int) Math.ceil(-Math.log10(interval));
//        } else {
//            autoValues.decimals = 0;
//        }
    }

    /**
     * Rounds the given number to the given number of significant digits. Based on an answer on
     * <a href="http://stackoverflow.com/questions/202302">Stack Overflow</a>.
     */
    private static float roundToOneSignificantFigure(double num) {
        final float d = (float) Math.ceil((float) Math.log10(num < 0 ? -num : num));
        final int power = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    /**
     * Computes the pixel offset for the given X lib value. This may be outside the view bounds.
     */
    private float getDrawX(float x) {
        return mContentRect.left
                + mContentRect.width()
                * (x - mCurrentViewport.left) / mCurrentViewport.width();
    }

    /**
     * Computes the pixel offset for the given Y lib value. This may be outside the view bounds.
     */
    private float getDrawY(float y) {
        return mContentRect.bottom
                - mContentRect.height()
                * (y - mCurrentViewport.top) / mCurrentViewport.height();
    }

    /**
     * Formats a float value to the given number of decimals. Returns the length of the string.
     * The string begins at out.length - [return value].
     */
    private static int formatFloat(final char[] out, float val, int digits) {
        boolean negative = false;
        if (val == 0) {
            out[out.length - 1] = '0';
            return 1;
        }
        if (val < 0) {
            negative = true;
            val = -val;
        }
        if (digits > POW10.length) {
            digits = POW10.length - 1;
        }
        val *= POW10[digits];
        long lval = Math.round(val);
        int index = out.length - 1;
        int charCount = 0;
        while (lval != 0 || charCount < (digits + 1)) {
            int digit = (int) (lval % 10);
            lval = lval / 10;
            out[index--] = (char) (digit + '0');
            charCount++;
            if (charCount == digits) {
                out[index--] = '.';
                charCount++;
            }
        }
        if (negative) {
            out[index--] = '-';
            charCount++;
        }
        return charCount;
    }


    // 网絡线
    private void drawGridLines(Canvas canvas) {
        if (mAxis.isEnable()) {
            int count = mAxis.getGridCount();
            if (mAxis instanceof AxisX) {
                final float width = mContentRect.width() / count;
                for (int i = 1; i < count; i++) {
                    canvas.drawLine(
                            mContentRect.left + i * width,
                            mContentRect.top,
                            mContentRect.left + i * width,
                            mContentRect.bottom,
                            mGridPaint);
                }
            }
            if (mAxis instanceof AxisY) {
                final float height = mContentRect.height() / (count - 1);
                for (int i = 1; i < count; i++) {
                    canvas.drawLine(
                            mContentRect.left,
                            mContentRect.top + i * height,
                            mContentRect.right,
                            mContentRect.top + i * height,
                            mGridPaint);
                }
            }
        }
    }


    private void drawLabels(Canvas canvas) {

        float[] labels = mAxis.mLabelEntries;
        if (labels == null || labels.length < 1) {
            return;
        }

        mLabelTextPaint.setColor(mAxis.getLabelTextColor());
        mLabelTextPaint.setTextSize(mAxis.getLabelTextSize());

        float x = 0f, y = 0f;
        switch (mAxis.getAxisPosition()) {
            case AxisX.TOP:
            case AxisX.TOP_INSIDE:
                x = mContentRect.left;
                y = mContentRect.top;
                break;
            case AxisX.BOTTOM:
            case AxisX.BOTTOM_INSIDE:
                x = mContentRect.left;
                y = mContentRect.top + mContentRect.height();
                break;
            case AxisY.LEFT_INSIDE:
            case AxisY.LEFT_OUTSIDE:
                x = mContentRect.left;
                y = mContentRect.bottom;
                break;
            case AxisY.RIGHT_INSIDE:
            case AxisY.RIGHT_OUTSIDE:
                x = mContentRect.right;
                y = mContentRect.bottom;
                break;
        }
        int labelOffset;
        int labelLength;

        if (mAxis instanceof AxisX) { // X轴
            final float width = mContentRect.width() / (labels.length - 1);
            for (int i = 0; i < labels.length; i++) {
                labelLength = FloatUtils.formatFloatValue(mLabelBuffer, labels[i], 2);
                labelOffset = mLabelBuffer.length - labelLength;
//                    final float textWidth = mLabelTextPaint.measureText(mLabelBuffer, labelOffset, labelLength);

                if (i == 0) {
                    mLabelTextPaint.setTextAlign(Paint.Align.LEFT);
                } else if (i == labels.length - 1) {
                    mLabelTextPaint.setTextAlign(Paint.Align.RIGHT);
                } else {
                    mLabelTextPaint.setTextAlign(Paint.Align.CENTER);
                }

                canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                        x + i * width, // - textWidth * 0.5f,
                        y + mLabelTextPaint.getTextSize(),
                        mLabelTextPaint);
            }
        } else { // Y轴

            final float height = mContentRect.height() / (labels.length - 1);
            float separation = 0;
            for (int i = 0; i < labels.length; i++) {
                labelLength = FloatUtils.formatFloatValue(mLabelBuffer, labels[i], 2);
                labelOffset = mLabelBuffer.length - labelLength;
                switch (mAxis.getAxisPosition()) {
                    case AxisY.LEFT_OUTSIDE:
                    case AxisY.RIGHT_INSIDE:
                        mLabelTextPaint.setTextAlign(Paint.Align.RIGHT);
                        separation = -mAxis.getLabelSeparation();
                        break;
                    case AxisY.LEFT_INSIDE:
                    case AxisY.RIGHT_OUTSIDE:
                        mLabelTextPaint.setTextAlign(Paint.Align.LEFT);
                        separation = mAxis.getLabelSeparation();
                        break;
                }

                float textHeightOffset = (mLabelTextPaint.descent() + mLabelTextPaint.ascent()) / 2;
                if (i == 0) { // Bottom
                    textHeightOffset = mAxis.getLabelSeparation();
                } else if (i == labels.length - 1) { // Top
                    textHeightOffset += textHeightOffset - mAxis.getLabelSeparation();
                }

                LabelColorSetter colorSetter = ((AxisY) mAxis).getLabelColorSetter();
                if (colorSetter != null) {
                    mLabelTextPaint.setColor(colorSetter.getColorByIndex(i));
                }

                canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                        x + separation,
                        y - i * height - textHeightOffset,
                        mLabelTextPaint);

            }
        }
    }
}
