package cn.jingzhuan.lib.chart3.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import java.util.List;

import cn.jingzhuan.lib.chart.AxisAutoValues;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.LabelColorSetter;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart3.base.AbstractChartView;

/**
 * @since 2023-09-04
 * created by lei
 */
public class AxisRenderer<V extends Value, T extends AbstractDataSet<V>> extends AbstractRenderer<V, T> {

    private final Axis mAxis;

    private Paint mAxisPaint;

    private Paint mGridPaint;

    private final char[] mLabelBuffer = new char[100];

    public AxisRenderer(AbstractChartView<V, T> chart, Axis axis) {
        super(chart);
        this.mAxis = axis;
        initPaints();
    }

    private void initPaints() {
        mAxisPaint = new Paint();
        mAxisPaint.setStyle(Paint.Style.STROKE);

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);

        mLabelTextPaint.setTextSize(mAxis.getLabelTextSize());
        mLabelTextPaint.setColor(mAxis.getLabelTextColor());
        mAxis.setLabelWidth((int) mLabelTextPaint.measureText("0000"));
        if (mAxis.getLabelTextSize() > 0) {
            mAxis.setLabelHeight((int) Math.abs(mLabelTextPaint.getFontMetrics().top));
        }
    }

    /**
     * 画坐标轴
     */
    @Override
    public void renderer(Canvas canvas) {
        if (mAxis.getLabels() == null) {
            if (mAxis instanceof AxisX) {
                computeAxisStopsX(mViewport.left, mViewport.right, (AxisX) mAxis, null);
            } else if (mAxis instanceof AxisY) {
                computeAxisStopsY((AxisY) mAxis);
            }
        }
        // Draws lib container
        drawAxisLine(canvas);
    }

    private void drawAxisLine(Canvas canvas) {
        float startX = 0f, startY = 0f, stopX = 0f, stopY = 0f;
        float halfThickness = mAxis.getAxisThickness() * .5f;
        switch (mAxis.getAxisPosition()) {
            case AxisX.TOP:
            case AxisX.TOP_INSIDE:
                startX = mContentRect.left;
                startY = mContentRect.top + halfThickness;
                stopX = mContentRect.right;
                stopY = startY;
                break;
            case AxisX.BOTTOM:
            case AxisX.BOTTOM_INSIDE:
                startX = mContentRect.left;
                startY = mContentRect.bottom - halfThickness;
                stopX = mContentRect.right;
                stopY = startY;
                break;
            case AxisY.LEFT_INSIDE:
            case AxisY.LEFT_OUTSIDE:
                startX = mContentRect.left + halfThickness;
                startY = mContentRect.top;
                stopX = startX;
                stopY = mContentRect.bottom;
                break;
            case AxisY.RIGHT_INSIDE:
            case AxisY.RIGHT_OUTSIDE:
                startX = mContentRect.right - halfThickness;
                startY = mContentRect.top;
                stopX = startX;
                stopY = mContentRect.bottom;
                break;
        }
        // Draw axis line
        if (mAxis.isEnable()) {
            mAxisPaint.setStrokeWidth(mAxis.getAxisThickness());
            mAxisPaint.setColor(mAxis.getAxisColor());
            canvas.drawLine(startX, startY, stopX, stopY, mAxisPaint);
        }
    }

    private void computeAxisStopsX(float start, float stop, AxisX axis, AxisAutoValues autoValues) {
        double range = stop - start;
        if (axis.getGridCount() == 0 || range <= 0) {
            return;
        }

        final int count = axis.getGridCount() + 1;
        double rawInterval = range / count;

        double interval = roundToOneSignificantFigure(rawInterval);
        double first = Math.ceil(start / interval) * interval;

        double f;
        int i;

        axis.mLabelEntries = new float[count + 1];
        for (f = first, i = 0; i < count + 1; f += interval, ++i) {
            axis.mLabelEntries[i] = (float) f;
        }

    }

    /**
     * Rounds the given number to the given number of significant digits. Based on an answer on
     * <a href="http://stackoverflow.com/questions/202302">Stack Overflow</a>.
     */
    private float roundToOneSignificantFigure(double num) {
        final float d = (float) Math.ceil((float) Math.log10(num < 0 ? -num : num));
        final int power = 1 - (int) d;
        final float magnitude = (float) Math.pow(10, power);
        final long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }

    private void computeAxisStopsY(AxisY axis) {
        double min = axis.getYMin();
        double max = axis.getYMax();
        int count = axis.getGridCount() + 1;

        double interval = (max - min) / count;

        axis.mLabelEntries = new float[count + 1];

        if (min < Float.MAX_VALUE && max > -Float.MAX_VALUE && min <= max) {
            double f = min;
            for (int j = 0; j < count + 1; f += interval, j++) {
                axis.mLabelEntries[j] = (float) f;
            }
        }
    }

    /**
     * 画坐标轴文本
     */
    public void drawAxisLabels(Canvas canvas) {
        if (!mAxis.isLabelEnable()) return;
        if (mAxis.getLabels() == null || mAxis.getLabels().isEmpty()) return;

        List<String> labels = mAxis.getLabels();

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

        if (mAxis instanceof AxisX) {
            // X轴
            final float width = mContentRect.width() / ((float) labels.size());
            for (int i = 0; i < labels.size(); i++) {
                char[] labelCharArray = labels.get(i).toCharArray();
                labelLength = labelCharArray.length;
                System.arraycopy(labelCharArray, 0, mLabelBuffer, mLabelBuffer.length - labelLength, labelLength);

                labelOffset = mLabelBuffer.length - labelLength;

                mLabelTextPaint.setTextAlign(Paint.Align.CENTER);

                LabelColorSetter colorSetter = mAxis.getLabelColorSetter();
                if (colorSetter != null) {
                    mLabelTextPaint.setColor(colorSetter.getColorByIndex(i));
                }

                canvas.drawText(mLabelBuffer, labelOffset, labelLength,
                        width * 0.5f + getDrawX(i / ((float) labels.size())),
                        y + mLabelTextPaint.getTextSize(), mLabelTextPaint);
            }
        } else {
            // Y轴
            final float height = mContentRect.height() / (mAxis.getLabels().size() - 1F);
            float separation = 0;
            for (int i = 0; i < mAxis.getLabels().size(); i++) {
                char[] labelCharArray = mAxis.getLabels().get(i).toCharArray();
                labelLength = labelCharArray.length;
                System.arraycopy(labelCharArray, 0, mLabelBuffer, mLabelBuffer.length - labelLength, labelLength);

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

                LabelColorSetter colorSetter = mAxis.getLabelColorSetter();
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

    /**
     * 画网格线
     */
    public void drawGridLines(Canvas canvas) {
        int count = mAxis.getGridCount() + 1;

        mGridPaint.setStrokeWidth(mAxis.getGridThickness());
        mGridPaint.setColor(mAxis.getGridColor());

        if (mAxis.getDashedGridIntervals() != null && mAxis.getDashedGridPhase() > 0) {
            mGridPaint.setPathEffect(new DashPathEffect(mAxis.getDashedGridIntervals(), mAxis.getDashedGridPhase()));
        }

        if (mAxis instanceof AxisX) {
            final float width = mContentRect.width() / ((float) count);
            for (int i = 1; i < count; i++) {
                if (mAxis.getGirdLineColorSetter() != null) {
                    mGridPaint.setColor(mAxis.getGirdLineColorSetter().getColorByIndex(mAxis.getGridColor(), i));
                }
                canvas.drawLine(
                        mContentRect.left + i * width,
                        mContentRect.top,
                        mContentRect.left + i * width,
                        mContentRect.bottom,
                        mGridPaint);
            }
        }
        if (mAxis instanceof AxisY) {
            final float height = mContentRect.height() / ((float) count);
            for (int i = 1; i < count; i++) {
                if (mAxis.getGirdLineColorSetter() != null) {
                    mGridPaint.setColor(mAxis.getGirdLineColorSetter().getColorByIndex(mAxis.getGridColor(), i));
                }
                canvas.drawLine(
                        mContentRect.left,
                        mContentRect.top + i * height,
                        mContentRect.right,
                        mContentRect.top + i * height,
                        mGridPaint);
            }
        }
    }

    public Axis getAxis() {
        return this.mAxis;
    }
}
