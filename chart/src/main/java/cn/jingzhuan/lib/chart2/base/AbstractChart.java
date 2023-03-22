package cn.jingzhuan.lib.chart2.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Zoomer;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.event.OnTouchHighlightChangeListener;
import cn.jingzhuan.lib.chart.event.OnTouchPointChangeListener;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;

/**
 * @author yilei
 * @since 2023-03-22
 */
public abstract class AbstractChart extends BitmapCachedChart implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector mScaleDetector;
    private OverScroller mScroller;

    private Zoomer zoomer;
    /**
     * 矩形边界 - 左
     */
    protected AxisY mAxisLeft = new AxisY(AxisY.LEFT_INSIDE);
    /**
     * 矩形边界 - 右
     */
    protected AxisY mAxisRight = new AxisY(AxisY.RIGHT_INSIDE);
    /**
     * 矩形边界 - 上
     */
    protected AxisX mAxisTop = new AxisX(AxisX.TOP);
    /**
     * 矩形边界 - 下
     */
    protected AxisX mAxisBottom = new AxisX(AxisX.BOTTOM);

    protected List<OnTouchPointChangeListener> mTouchPointChangeListeners;
    protected List<OnTouchHighlightChangeListener> mTouchHighlightChangeListeners;
    protected List<OnViewportChangeListener> mOnViewportChangeListeners;

    // Edge effect / overscroll tracking objects.
    private EdgeEffect mEdgeEffectLeft, mEdgeEffectRight;

    /**
     * 是否触摸状态
     */
    private boolean mIsTouching = false;

    /**
     * 是否展示区间统计 - 默认否
     */
    private boolean mEnableRange = false;

    /**
     * 双击放大
     */
    private boolean mDoubleTapToZoom = false;

    /**
     * The scaling factor for a single zoom 'step'.
     *
     * @see #zoomIn()
     * @see #zoomOut()
     */
    private static final float ZOOM_AMOUNT = 0.2f;

    public AbstractChart(Context context) {
        this(context, null);
    }

    public AbstractChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public AbstractChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Chart, defStyleAttr, defStyleAttr);

        mAxisTop.setGridLineEnable(false);
        mAxisTop.setLabelEnable(false);

        try {
            List<Axis> axisList = new ArrayList<>(4);
            axisList.add(mAxisLeft);
            axisList.add(mAxisRight);
            axisList.add(mAxisTop);
            axisList.add(mAxisBottom);

            float labelTextSize = a.getDimension(R.styleable.Chart_labelTextSize, 28);
            float labelSeparation = a.getDimensionPixelSize(R.styleable.Chart_labelSeparation, 10);
            float gridThickness = a.getDimension(R.styleable.Chart_gridThickness, 2);
            float axisThickness = a.getDimension(R.styleable.Chart_axisThickness, 2);
            int gridColor = a.getColor(R.styleable.Chart_gridColor, Color.GRAY);
            int axisColor = a.getColor(R.styleable.Chart_axisColor, Color.GRAY);
            int labelTextColor = a.getColor(R.styleable.Chart_labelTextColor, Color.GRAY);

            for (Axis axis : axisList) {
                axis.setLabelTextSize(labelTextSize);
                axis.setLabelTextColor(labelTextColor);
                axis.setLabelSeparation(labelSeparation);
                axis.setGridColor(gridColor);
                axis.setGridThickness(gridThickness);
                axis.setAxisColor(axisColor);
                axis.setAxisThickness(axisThickness);
            }

        } finally {
            a.recycle();
        }

        initChart();

        initListeners();

        setupInteractions(context);

        setupEdgeEffect(context);
    }

    /**
     * Sets up edge effects
     */
    protected void setupEdgeEffect(Context context) {
        mEdgeEffectLeft = new EdgeEffect(context);
        mEdgeEffectRight = new EdgeEffect(context);
    }

    private void setupInteractions(Context context) {
        setWillNotDraw(false);
        mDetector = new GestureDetectorCompat(context, this);
        mScaleDetector = new ScaleGestureDetector(context, this);
        mScroller = new OverScroller(context);
        zoomer = new Zoomer(context);
    }

    private void initListeners() {
        mTouchPointChangeListeners = Collections.synchronizedList(new ArrayList<>());
        mTouchHighlightChangeListeners = Collections.synchronizedList(new ArrayList<>());
        mOnViewportChangeListeners = Collections.synchronizedList(new ArrayList<>());
    }

    public abstract void initChart();

    public void addOnTouchPointChangeListener(OnTouchPointChangeListener listener) {
        synchronized (this) {
            this.mTouchPointChangeListeners.add(listener);
        }
    }

    public void removeOnTouchPointChangeListener(OnTouchPointChangeListener listener) {
        synchronized (this) {
            this.mTouchPointChangeListeners.remove(listener);
        }
    }

    /**
     * 获取触摸状态
     */
    public boolean isTouching() {
        return this.mIsTouching;
    }

    /**
     * 设置区间统计
     */
    public void setRangeEnable(boolean enableRange) {
        this.mEnableRange = enableRange;
    }

    public boolean getRangeEnable() {
        return this.mEnableRange;
    }

    /**
     * 设置双击放大
     */
    public void setDoubleTapToZoom(boolean doubleTapToZoom) {
        this.mDoubleTapToZoom = doubleTapToZoom;
    }

    public boolean DoubleTapToZoom() {
        return this.mDoubleTapToZoom;
    }

    /**
     * Smoothly zooms the lib in one step.
     */
    public void zoomIn() {
        zoom(ZOOM_AMOUNT);
    }

    /**
     * Smoothly zooms the lib out one step.
     */
    public void zoomOut() {
        zoom(-ZOOM_AMOUNT);
    }

    public void zoom(float scalingFactor) {

    }

    /**
     * 手指在触摸屏幕双击的时候回调
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(mEnableRange) return false;

        if (mDoubleTapToZoom) {
            zoomer.forceFinished(true);
//            if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
//                zoomer.startZoom(ZOOM_AMOUNT);
//            }
            postInvalidateOnAnimation();
        }
        return true;
    }

    /**
     * 手指在触摸屏幕滑动的时候回调
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        return true;
    }

}
