package cn.jingzhuan.lib.chart3.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.renderer.AxisRenderer;

/**
 * @since 2023-09-01
 * created by lei
 */
public abstract class BaseChartView extends ScrollAndScaleView implements IChartView{

    protected List<AxisRenderer> mAxisRenderers;

    protected AxisY mAxisLeft = new AxisY(AxisY.LEFT_INSIDE);

    protected AxisY mAxisRight = new AxisY(AxisY.RIGHT_INSIDE);

    protected AxisX mAxisTop = new AxisX(AxisX.TOP);

    protected AxisX mAxisBottom = new AxisX(AxisX.BOTTOM);

    protected Rect mContentRect = new Rect();

    protected Rect mBottomRect = new Rect();

    private int minChartWidth;

    private int minChartHeight;

    /**
     * 背景颜色
     */
    private int backgroundColor;

    /**
     * 坐标轴刻度文本 是否画在底层
     */
    private boolean drawLabelsInBottom;

    /**
     * 是否需要展示水印
     */
    private boolean showWaterMark;

    /**
     * 是否黑夜模式
     */
    private boolean isNightMode;

    public BaseChartView(Context context) {
        super(context);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init(AttributeSet attrs, int defStyleAttr) {
        super.init(attrs, defStyleAttr);
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.Chart, defStyleAttr, defStyleAttr);

        try {
            int minChartWidth = ta.getDimensionPixelSize(R.styleable.Chart_minChartWidth, 0);
            setMinChartWidth(minChartWidth);

            int minChartHeight = ta.getDimensionPixelSize(R.styleable.Chart_minChartHeight, 0);
            setMinChartHeight(minChartHeight);

            int backgroundColor = ta.getColor(R.styleable.Chart_backgroundColor, Color.TRANSPARENT);
            setBackgroundColor(backgroundColor);

            boolean drawLabelsInBottom = ta.getBoolean(R.styleable.Chart_drawLabelsInBottom, false);
            setDrawLabelsInBottom(drawLabelsInBottom);

            boolean showWaterMark = ta.getBoolean(R.styleable.Chart_showWaterMark, false);
            setShowWaterMark(showWaterMark);

            boolean isNightMode = ta.getBoolean(R.styleable.Chart_isNightMode, false);
            setNightMode(isNightMode);

            List<Axis> axisList = new ArrayList<>(4);
            axisList.add(mAxisLeft);
            axisList.add(mAxisRight);
            axisList.add(mAxisTop);
            axisList.add(mAxisBottom);

            float labelTextSize = ta.getDimension(R.styleable.Chart_labelTextSize, 28);

            float labelSeparation = ta.getDimensionPixelSize(R.styleable.Chart_labelSeparation, 10);

            float gridThickness = ta.getDimension(R.styleable.Chart_gridThickness, 2);

            float axisThickness = ta.getDimension(R.styleable.Chart_axisThickness, 2);

            int gridColor = ta.getColor(R.styleable.Chart_gridColor, Color.GRAY);

            int axisColor = ta.getColor(R.styleable.Chart_axisColor, Color.GRAY);

            int labelTextColor = ta.getColor(R.styleable.Chart_labelTextColor, Color.GRAY);

            for (Axis axis : axisList) {
                axis.setLabelTextSize(labelTextSize);
                axis.setLabelTextColor(labelTextColor);
                axis.setLabelSeparation(labelSeparation);
                axis.setGridColor(gridColor);
                axis.setGridThickness(gridThickness);
                axis.setAxisColor(axisColor);
                axis.setAxisThickness(axisThickness);
            }

        } catch (Exception e) {
            ta.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Clips the next few drawing operations to the content area
        int clipRestoreCount = canvas.save();

        // 剪切ContentRect区域
        canvas.clipRect(mContentRect);

        // 画背景
        if (getBackgroundColor() != Color.TRANSPARENT){
            canvas.drawColor(getBackgroundColor());
        }

        // 画水印
        if (isShowWaterMark()) {
            drawWaterMark(canvas);
        }

        // 画网格线
        drawGridLine(canvas);

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        int chartLeft = getPaddingLeft() + (mAxisLeft.isInside() ? 0 : mAxisLeft.getLabelWidth());
        int chartRight = getWidth() - getPaddingRight() - (mAxisRight.isInside() ? 0 : mAxisRight.getLabelWidth());
        int contentBottom = getHeight() - getPaddingBottom() - mAxisBottom.getLabelHeight();

        mContentRect.set(chartLeft, getPaddingTop(), chartRight,contentBottom);

        mBottomRect.set(chartLeft, contentBottom, chartRight, getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMinChartWidth() + getPaddingLeft() + (mAxisLeft.isInside() ? 0 : mAxisLeft.getLabelWidth()) + getPaddingRight();
        int height = getMinChartHeight() + (mAxisBottom.isInside() ? 0 : mAxisBottom.getLabelHeight()) + getPaddingBottom();
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(), resolveSize(width, widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(), resolveSize(height, heightMeasureSpec))
        );
    }

    @Override
    void onTouchPoint(MotionEvent event) {

    }

    @Override
    public void drawCrossWire() {

    }

    public int getMinChartWidth() {
        return minChartWidth;
    }

    public void setMinChartWidth(int minChartWidth) {
        this.minChartWidth = minChartWidth;
    }

    public int getMinChartHeight() {
        return minChartHeight;
    }

    public void setMinChartHeight(int minChartHeight) {
        this.minChartHeight = minChartHeight;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isDrawLabelsInBottom() {
        return drawLabelsInBottom;
    }

    public void setDrawLabelsInBottom(boolean drawLabelsInBottom) {
        this.drawLabelsInBottom = drawLabelsInBottom;
    }

    public boolean isShowWaterMark() {
        return showWaterMark;
    }

    public void setShowWaterMark(boolean showWaterMark) {
        this.showWaterMark = showWaterMark;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }
}
