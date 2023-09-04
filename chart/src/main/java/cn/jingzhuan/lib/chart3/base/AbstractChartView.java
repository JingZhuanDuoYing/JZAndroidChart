package cn.jingzhuan.lib.chart3.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.Value;
import cn.jingzhuan.lib.chart3.renderer.AxisRenderer;
import cn.jingzhuan.lib.chart3.renderer.HighlightRenderer;
import cn.jingzhuan.lib.chart3.state.HighlightState;

/**
 * @since 2023-09-01
 * created by lei
 */
public abstract class AbstractChartView<V extends Value, T extends AbstractDataSet<V>> extends ScrollAndScaleView implements IChartView{

    protected WeakReference<Bitmap> mDrawBitmap;

    protected Canvas mBitmapCanvas;

    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    protected AxisRenderer<V, T> mAxisLeftRenderer;

    protected AxisRenderer<V, T> mAxisRightRenderer;

    protected AxisRenderer<V, T> mAxisTopRenderer;

    protected AxisRenderer<V, T> mAxisBottomRenderer;

    protected HighlightRenderer<V, T> mHighlightRenderer;

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

    public AbstractChartView(Context context) {
        super(context);
    }

    public AbstractChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AbstractChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

            initAxisRenderers(ta);

            mHighlightRenderer = new HighlightRenderer<>(this);

            ta.recycle();
        } catch (Exception e) {
            ta.recycle();
        }

        initChart();

    }

    private void initAxisRenderers(TypedArray ta) {
        AxisY axisLeft = new AxisY(AxisY.LEFT_INSIDE);
        mAxisLeftRenderer = new AxisRenderer<>(this, axisLeft);

        AxisY axisRight = new AxisY(AxisY.RIGHT_INSIDE);
        mAxisRightRenderer = new AxisRenderer<>(this, axisRight);

        AxisX axisTop = new AxisX(AxisX.TOP);
        mAxisTopRenderer = new AxisRenderer<>(this, axisTop);

        AxisX axisBottom = new AxisX(AxisX.BOTTOM);
        mAxisBottomRenderer = new AxisRenderer<>(this, axisBottom);

        List<AxisRenderer<V, T>> mAxisRenderers = new ArrayList<>(4);
        mAxisRenderers.add(mAxisLeftRenderer);
        mAxisRenderers.add(mAxisRightRenderer);
        mAxisRenderers.add(mAxisTopRenderer);
        mAxisRenderers.add(mAxisBottomRenderer);

        float labelTextSize = ta.getDimension(R.styleable.Chart_labelTextSize, 28);

        float labelSeparation = ta.getDimensionPixelSize(R.styleable.Chart_labelSeparation, 10);

        int labelTextColor = ta.getColor(R.styleable.Chart_labelTextColor, Color.GRAY);

        float gridThickness = ta.getDimension(R.styleable.Chart_gridThickness, 2);

        float axisThickness = ta.getDimension(R.styleable.Chart_axisThickness, 2);

        int gridColor = ta.getColor(R.styleable.Chart_gridColor, Color.GRAY);

        int axisColor = ta.getColor(R.styleable.Chart_axisColor, Color.GRAY);

        for (AxisRenderer<V, T> axisRenderer : mAxisRenderers) {
            Axis axis = axisRenderer.getAxis();
            axis.setLabelTextSize(labelTextSize);
            axis.setLabelTextColor(labelTextColor);
            axis.setLabelSeparation(labelSeparation);
            axis.setGridColor(gridColor);
            axis.setGridThickness(gridThickness);
            axis.setAxisColor(axisColor);
            axis.setAxisThickness(axisThickness);
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

        // 画坐标轴
        drawAxis(canvas);

        // 画网格线
        drawGridLine(canvas);

        // 画坐标轴文本 (左、右、上)
        if (isDrawLabelsInBottom()) {
            drawAxisLabels(canvas);
        }

        createBitmapCache();

        if (getBitmapCanvas() != null && getRenderPaint() != null) {

            drawChart(getBitmapCanvas());

            canvas.drawBitmap(getDrawBitmap(), 0, 0, getRenderPaint());
        }

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        if (!isDrawLabelsInBottom()) {
            drawAxisLabels(canvas);
        }

        if (mAxisBottomRenderer.getAxis().getLabelHeight() != 0) {
            drawBottomLabels(canvas);
        }

        // 画十字光标
        if (getHighlightState() != HighlightState.initial) {
            drawHighlight(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        Axis axisLeft = getAxisLeftRenderer().getAxis();
        int chartLeft = getPaddingLeft() + (axisLeft.isInside() ? 0 : axisLeft.getLabelWidth());

        Axis axisRight = getAxisRightRenderer().getAxis();
        int chartRight = getWidth() - getPaddingRight() - (axisRight.isInside() ? 0 : axisRight.getLabelWidth());

        Axis axisBottom = getAxisBottomRenderer().getAxis();
        int contentBottom = getHeight() - getPaddingBottom() - axisBottom.getLabelHeight();

        mContentRect.set(chartLeft, getPaddingTop(), chartRight,contentBottom);

        mBottomRect.set(chartLeft, contentBottom, chartRight, getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Axis axisLeft = getAxisLeftRenderer().getAxis();
        int width = getMinChartWidth() + getPaddingLeft() + (axisLeft.isInside() ? 0 : axisLeft.getLabelWidth()) + getPaddingRight();

        Axis axisBottom = getAxisBottomRenderer().getAxis();
        int height = getMinChartHeight() + (axisBottom.isInside() ? 0 : axisBottom.getLabelHeight()) + getPaddingBottom();
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(), resolveSize(width, widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(), resolveSize(height, heightMeasureSpec))
        );
    }

    public void setTypeface(Typeface tf) {
        mAxisLeftRenderer.setTypeface(tf);
        mAxisLeftRenderer.setTypeface(tf);
        mAxisTopRenderer.setTypeface(tf);
        mAxisBottomRenderer.setTypeface(tf);
    }

    protected void createBitmapCache() {
        int width = getContentRect().width() + getContentRect().left;
        int height = getContentRect().height();

        if (mDrawBitmap == null
                || (mDrawBitmap.get() == null)
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {
                mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(getResources().getDisplayMetrics(), width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);
    }

    protected Bitmap getDrawBitmap() {
        return mDrawBitmap.get();
    }

    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            if (mDrawBitmap.get() != null) mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseBitmap();
    }

    public Canvas getBitmapCanvas() {
        return mBitmapCanvas;
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

    // <editor-fold desc="十字光标 配置">    ----------------------------------------------------------

    /**
     * 是否需要展示水平交叉线-默认展示
     */
    private boolean enableHorizontalHighlight = true;

    public boolean isEnableHorizontalHighlight() {
        return enableHorizontalHighlight;
    }

    public void setEnableHorizontalHighlight(boolean enableHorizontalHighlight) {
        this.enableHorizontalHighlight = enableHorizontalHighlight;
    }

    /**
     * 是否需要展示垂直交叉线-默认展示
     */
    private boolean enableVerticalHighlight = true;

    public boolean isEnableVerticalHighlight() {
        return enableVerticalHighlight;
    }

    public void setEnableVerticalHighlight(boolean enableVerticalHighlight) {
        this.enableVerticalHighlight = enableVerticalHighlight;
    }

    /**
     * 是否需要展示水平交叉线左边文本-默认展示
     */
    private boolean enableHighlightLeftText = true;

    public boolean isEnableHighlightLeftText() {
        return enableHighlightLeftText;
    }

    public void setEnableHighlightLeftText(boolean enableHighlightLeftText) {
        this.enableHighlightLeftText = enableHighlightLeftText;
    }

    /**
     * 是否需要展示水平交叉线右边文本-默认不展示
     */
    private boolean enableHighlightRightText = false;

    public boolean isEnableHighlightRightText() {
        return enableHighlightRightText;
    }

    public void setEnableHighlightRightText(boolean enableHighlightRightText) {
        this.enableHighlightRightText = enableHighlightRightText;
    }

    /**
     * 是否需要展示垂直交叉线底部文本-默认展示
     */
    private boolean enableHighlightBottomText = true;

    public boolean isEnableHighlightBottomText() {
        return enableHighlightBottomText;
    }

    public void setEnableHighlightBottomText(boolean enableHighlightBottomText) {
        this.enableHighlightBottomText = enableHighlightBottomText;
    }

    /**
     * 交叉线文本大小
     */
    private float mHighlightTextSize;

    public void setHighlightTextSize(float textSize) {
        this.mHighlightTextSize = textSize;
    }

    public float getHighlightTextSize() {
        return this.mHighlightTextSize;
    }

    /**
     * 交叉线文本颜色
     */
    private int mHighlightTextColor = Color.TRANSPARENT;

    public int getHighlightTextColor() {
        return mHighlightTextColor;
    }

    public void setHighlightTextColor(int mHighlightTextColor) {
        this.mHighlightTextColor = mHighlightTextColor;
    }

    /**
     * 交叉线文本背景颜色
     */
    private int mHighlightTextBgColor = Color.TRANSPARENT;

    public int getHighlightTextBgColor() {
        return mHighlightTextBgColor;
    }

    public void setHighlightTextBgColor(int mHighlightTextBgColor) {
        this.mHighlightTextBgColor = mHighlightTextBgColor;
    }

    /**
     * 交叉线文本背景高度
     */
    private int mHighlightTextBgHeight;

    public int getHighlightTextBgHeight() {
        return mHighlightTextBgHeight;
    }

    public void setHighlightTextBgHeight(int mHighlightTextBgHeight) {
        this.mHighlightTextBgHeight = mHighlightTextBgHeight;
    }

    /**
     * 交叉线厚度
     */
    private float mHighlightThickness = 3;

    public float getHighlightThickness() {
        return mHighlightThickness;
    }

    public void setHighlightThickness(float highlightThickness) {
        this.mHighlightThickness = highlightThickness;
    }

    // </editor-fold desc="十字光标 配置">    ----------------------------------------------------------

    public AxisRenderer<V, T> getAxisLeftRenderer() {
        return mAxisLeftRenderer;
    }

    public AxisRenderer<V, T> getAxisRightRenderer() {
        return mAxisRightRenderer;
    }

    public AxisRenderer<V, T> getAxisTopRenderer() {
        return mAxisTopRenderer;
    }

    public AxisRenderer<V, T> getAxisBottomRenderer() {
        return mAxisBottomRenderer;
    }

    public abstract ChartData<T> getChartData();

    abstract Paint getRenderPaint();

    /**
     * 十字光标选中
     */
   abstract void highlightValue(Highlight highlight);

    /**
     * 清除十字光标
     */
    abstract void cleanHighlight();
}
