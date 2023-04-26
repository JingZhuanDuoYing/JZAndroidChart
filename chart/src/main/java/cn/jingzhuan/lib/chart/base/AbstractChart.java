package cn.jingzhuan.lib.chart.base;


import static cn.jingzhuan.lib.chart.config.JZChartConfig.ZOOM_AMOUNT;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.Zoomer;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.event.HighlightStatusChangeListener;
import cn.jingzhuan.lib.chart.event.OnHighlightListener;
import cn.jingzhuan.lib.chart.event.OnLoadMoreKlineListener;
import cn.jingzhuan.lib.chart.event.OnScaleListener;
import cn.jingzhuan.lib.chart.event.OnTouchHighlightChangeListener;
import cn.jingzhuan.lib.chart.event.OnTouchPointChangeListener;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.utils.ForceAlign;
import cn.jingzhuan.lib.source.JZScaleGestureDetector;

/**
 * @author yilei
 * @since 2023-03-22
 */
public abstract class AbstractChart extends BitmapCacheChart {

    private GestureDetector mDetector;
    private JZScaleGestureDetector mScaleDetector;
    private OverScroller mScroller;

    // Edge effect / overscroll tracking objects.
    private EdgeEffect mEdgeEffectLeft, mEdgeEffectRight;

    private boolean mEdgeEffectLeftActive, mEdgeEffectRightActive;

    /**
     * Used only for zooms and flings.
     */
    private final RectF mScrollerStartViewport = new RectF();

    protected AxisY mAxisLeft = new AxisY(AxisY.LEFT_INSIDE);

    protected AxisY mAxisRight = new AxisY(AxisY.RIGHT_INSIDE);

    protected AxisX mAxisTop = new AxisX(AxisX.TOP);

    protected AxisX mAxisBottom = new AxisX(AxisX.BOTTOM);

    protected List<OnTouchPointChangeListener> mTouchPointChangeListeners
            = Collections.synchronizedList(new ArrayList<>());

    protected List<OnTouchHighlightChangeListener> mTouchHighlightChangeListeners =
            Collections.synchronizedList(new ArrayList<>());

    protected List<OnViewportChangeListener> mOnViewportChangeListeners =
            Collections.synchronizedList(new ArrayList<>());

    protected OnViewportChangeListener mInternalViewportChangeListener;

    protected HighlightStatusChangeListener mHighlightStatusChangeListener;

    protected OnHighlightListener mHighlightListener;

    protected OnLoadMoreKlineListener mOnLoadMoreKlineListener;

    /**
     * 手指触摸缩放回调
     */
    protected OnScaleListener mScaleListener;

    /**
     * 是否主图
     */
    private boolean mIsMainChart = false;

    /**
     * 是否触摸状态
     */
    private boolean mIsTouching = false;

    /**
     * 是否长按状态
     */
    private boolean mIsLongPress = false;

    /**
     * 是否光标高亮状态
     */
    protected boolean mIsHighlight = false;

    /**
     * 是否展示区间统计 - 默认否
     */
    private boolean mRangeEnable = false;

    /**
     * 双击放大
     */
    private boolean mDoubleTapToZoom = false;

    /**
     * 是否能够放大
     */
    private boolean canZoomIn = true;

    /**
     * 是否能够缩小
     */
    private boolean canZoomOut = true;

    /**
     * 是否允许伸缩
     */
    private boolean enableScaleGesture = true;

    private boolean enableScaleX = true;

    /**
     * 是否允许滑动
     */
    private boolean enableDraggingToMove = true;

    /**
     * 是否允许显示十字光标
     */
    private boolean enableHighlight = true;

    /**
     * 总是显示十字光标
     */
    private boolean alwaysHighlight = false;

    /**
     * 伸缩灵敏度
     */
    private float scaleSensitivity = 1f;

    /**
     * 是否允许加载更多
     */
    private boolean enableLoadMore = true;

    private Zoomer zoomer;

    private final PointF mZoomFocalPoint = new PointF();

    private final Point mSurfaceSizeBuffer = new Point();

    private int mFocusIndex = -1;

    protected float mHighlightX, mHighlightY = Float.NaN;

    protected int mHighlightIndex = -1;

    // 这个属性没用 只是为了兼容代码而已
    private boolean mHighlightDisable = false;

    public AbstractChart(Context context) {
        this(context, null, 0);
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
        mDetector = new GestureDetector(context, mGestureListener);
        mScaleDetector = new JZScaleGestureDetector(context, mScaleGestureListener);
        mScroller = new OverScroller(context);
        zoomer = new Zoomer(context);
    }

    /**
     * Draws the overscroll "glow" at the four edges of the lib region, if necessary. The edges
     * of the lib region are stored in {@link #mContentRect}.
     *
     * @see EdgeEffect
     */
    protected void drawEdgeEffectsUnClipped(Canvas canvas) {
        // The methods below rotate and translate the canvas as needed before drawing the glow,
        // since EdgeEffectCompat always draws a top-glow at 0,0.

        boolean needsInvalidate = false;

        if (!mEdgeEffectLeft.isFinished()) {
            final int restoreCount = canvas.save();
            canvas.translate(mContentRect.left, mContentRect.bottom);
            canvas.rotate(-90, 0, 0);
            mEdgeEffectLeft.setSize(mContentRect.height(), mContentRect.width());
            if (mEdgeEffectLeft.draw(canvas)) {
                needsInvalidate = true;
            }
            canvas.restoreToCount(restoreCount);
        }

        if (!mEdgeEffectRight.isFinished()) {
            final int restoreCount = canvas.save();
            canvas.translate(mContentRect.right, mContentRect.top);
            canvas.rotate(90, 0, 0);
            mEdgeEffectRight.setSize(mContentRect.height(), mContentRect.width());
            if (mEdgeEffectRight.draw(canvas)) {
                needsInvalidate = true;
            }
            canvas.restoreToCount(restoreCount);
        }

        if (needsInvalidate) {
            triggerViewportChange();
        }
    }

    private void releaseEdgeEffects() {
        mEdgeEffectLeftActive = mEdgeEffectRightActive = false;
        mEdgeEffectLeft.onRelease();
        mEdgeEffectRight.onRelease();
    }

    /**
     * Finds the lib point (i.e. within the lib's domain and range) represented by the
     * given pixel coordinates, if that pixel is within the lib region described by
     * {@link #mContentRect}. If the point is found, the "dest" argument is set to the point and
     * this function returns true. Otherwise, this function returns false and "dest" is unchanged.
     */
    private boolean hitTest(float x, float y, PointF dest) {
        if (!mContentRect.contains((int) x, (int) y)) {
            return false;
        }

        dest.set(mCurrentViewport.left + mCurrentViewport.width() * (x - mContentRect.left) / mContentRect.width(),
                mCurrentViewport.top + mCurrentViewport.height() * (y - mContentRect.bottom) / -mContentRect.height());
        return true;
    }

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

    public void addOnViewportChangeListener(OnViewportChangeListener onViewportChangeListener) {
        synchronized (this) {
            this.mOnViewportChangeListeners.add(onViewportChangeListener);
        }
    }

    /**
     * Smoothly zooms the lib in one step.
     */
    public void zoomIn() {
        zoom(ZOOM_AMOUNT, -1);
    }

    public void zoomIn(@ForceAlign.XForce int forceAlignX) {
        zoom(ZOOM_AMOUNT, forceAlignX);
    }

    /**
     * Smoothly zooms the lib out one step.
     */
    public void zoomOut() {
        zoom(-ZOOM_AMOUNT, -1);
    }

    public void zoomOut(@ForceAlign.XForce int forceAlignX) {
        zoom(-ZOOM_AMOUNT, forceAlignX);
    }

    public void zoom(float scalingFactor, int forceAlignX) {
        mScrollerStartViewport.set(mCurrentViewport);
        zoomer.forceFinished(true);
        zoomer.startZoom(scalingFactor);
        float pointFX;
        if (forceAlignX == -1) {
            pointFX = (mCurrentViewport.right + mCurrentViewport.left) / 2;
        } else {
            switch (forceAlignX) {
                case ForceAlign.LEFT:
                    pointFX = mCurrentViewport.left;
                    break;
                case ForceAlign.RIGHT:
                    pointFX = mCurrentViewport.right;
                    break;
                case ForceAlign.CENTER:
                default:
                    pointFX = (mCurrentViewport.right + mCurrentViewport.left) / 2;
                    break;
            }
        }

        mZoomFocalPoint.set(pointFX, (mCurrentViewport.bottom + mCurrentViewport.top) / 2);
        triggerViewportChange();
        if (alwaysHighlight) {
            onAlwaysHighlight();
        }
        if (mScaleListener != null) {
            mScaleListener.onScale(mCurrentViewport);
        }
    }

    protected void triggerViewportChange() {
        postInvalidateOnAnimation();
        if (mInternalViewportChangeListener != null) {
            mInternalViewportChangeListener.onViewportChange(mCurrentViewport);
        }
        if (mOnViewportChangeListeners != null && !mOnViewportChangeListeners.isEmpty()) {
            synchronized (this) {
                for (OnViewportChangeListener mOnViewportChangeListener : mOnViewportChangeListeners) {
                    try {
                        mOnViewportChangeListener.onViewportChange(mCurrentViewport);
                    } catch (Exception e) {
                        Log.d("Chart", "onViewportChange", e);
                    }
                }
            }
        }
    }

    public void setInternalViewportChangeListener(
            OnViewportChangeListener mInternalViewportChangeListener) {
        this.mInternalViewportChangeListener = mInternalViewportChangeListener;
    }

    public void setOnHighlightStatusChangeListener(HighlightStatusChangeListener mHighlightStatusChangeListener) {
        this.mHighlightStatusChangeListener = mHighlightStatusChangeListener;
    }

    public HighlightStatusChangeListener getOnHighlightStatusChangeListener() {
        return mHighlightStatusChangeListener;
    }

    public void setOnHighlightListener(OnHighlightListener highlightListener) {
        this.mHighlightListener = highlightListener;
    }

    public void setOnLoadMoreKlineListener(OnLoadMoreKlineListener onLoadMoreKlineListener) {
        this.mOnLoadMoreKlineListener = onLoadMoreKlineListener;
    }

    /**
     * 设置触摸伸缩回调监听
     */
    public void setOnScaleListener(OnScaleListener onScaleListener) {
        this.mScaleListener = onScaleListener;
    }

    private final JZScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new JZScaleGestureDetector.SimpleOnScaleGestureListener() {

        /**
         * 多指触摸屏幕开始伸缩的时候回调
         */
        @Override
        public boolean onScaleBegin(JZScaleGestureDetector scaleGestureDetector) {
            if (!enableScaleGesture || !enableScaleX) return false;
            Log.d("Chart", "onScaleBegin");
            if (mScaleListener != null) {
                mScaleListener.onScaleStart(mCurrentViewport);
            }
            return true;
        }

        /**
         * 多指触摸屏幕结束伸缩的时候回调
         */
        @Override
        public void onScaleEnd(JZScaleGestureDetector detector) {
            if (!enableScaleGesture || !enableScaleX) return;
            Log.d("Chart", "onScaleEnd");
            if (mScaleListener != null) {
                mScaleListener.onScaleEnd(mCurrentViewport);
            }
        }

        /**
         * 多指触摸屏幕伸缩的时候回调
         */
        @Override
        public boolean onScale(JZScaleGestureDetector detector) {
            Log.d("Chart", "onScale");
            if (!enableScaleGesture || !enableScaleX) return false;
            PointF viewportFocus = new PointF();

            float spanX = mScaleDetector.getCurrentSpan();
            float lastSpanX = mScaleDetector.getPreviousSpan();
            // 双指距离比上次大，为放大
            boolean zoomIn = spanX > lastSpanX;
            // 双指距离比上次小，为缩小
            boolean zoomOut = lastSpanX > spanX;

            boolean canZoom = Math.abs(Math.abs(lastSpanX) - Math.abs(spanX)) >= 1f;

            // 如果当前是放大 则能够缩小
            if (zoomIn) {
                setCanZoomOut(true);
                // 不能放大时 return
                if (!isCanZoomIn()) return false;
            }

            // 如果当前是缩小 则能够放大
            if (zoomOut) {
                setCanZoomIn(true);
                // 不能缩小时 return
                if (!isCanZoomOut()) return false;
            }

            float scaleSpanX = lastSpanX;
            if (canZoom) {
                if (zoomIn) {
                    scaleSpanX = spanX * scaleSensitivity;
                } else if (zoomOut) {
                    lastSpanX = lastSpanX * scaleSensitivity;
                }
            }

            float newWidth;
            if (canZoom) {
                if (zoomIn) {
                    newWidth = lastSpanX / scaleSpanX * mCurrentViewport.width();
                } else {
                    newWidth = lastSpanX / spanX * mCurrentViewport.width();
                }
            } else {
                newWidth = lastSpanX / spanX * mCurrentViewport.width();
            }

            if (newWidth < mCurrentViewport.width() && mCurrentViewport.width() < 0.001) {
                return true;
            }

            float focusX = mScaleDetector.getFocusX();
            float focusY = mScaleDetector.getFocusY();

            if (canZoom) {
                if (zoomOut)
                    focusX *= scaleSensitivity;
                else if (zoomIn)
                    focusX /= scaleSensitivity;
            }

            hitTest(focusX, focusY, viewportFocus);

            // 优先向右缩进
            mCurrentViewport.left = viewportFocus.x - newWidth * (focusX - mContentRect.left) / mContentRect.width();
            if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;
            if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                mCurrentViewport.right = mCurrentViewport.left + newWidth;
                if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
            }

            if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.left = Viewport.AXIS_X_MAX;

            Log.d("Chart", "onScale->" + mCurrentViewport.left + "--" + mCurrentViewport.right);

            mCurrentViewport.constrainViewport();

            triggerViewportChange();
            if (alwaysHighlight) {
                onAlwaysHighlight();
            }
            if (mScaleListener != null) {
                mScaleListener.onScale(mCurrentViewport);
            }
            return true;
        }
    };

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        /**
         * 手指在触摸屏幕按下时回调
         */
        @Override
        public boolean onDown(MotionEvent e) {
            releaseEdgeEffects();
            mScrollerStartViewport.set(mCurrentViewport);
            mScroller.forceFinished(true);
            return true;
        }

        /**
         * 手指在屏幕长按时回调
         */
        @Override
        public void onLongPress(MotionEvent e) {
            if (getRangeEnable()) return;
            mIsLongPress = true;
            onTouchPoint(e);
            e.setAction(MotionEvent.ACTION_UP);
            mDetector.onTouchEvent(e);
        }

        /**
         * 手指在屏幕单击的时候回调
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (getRangeEnable()) return false;
//            Log.d("Chart", "单击 onSingleTapConfirmed isClickable:" + isClickable()+ ", hasOnClickListeners:" + hasOnClickListeners() + "; isHighlight:" + isHighlight());
            if (isClickable() && hasOnClickListeners()) {
                if (getHighlights() != null) cleanHighlight();
                performClick();
            } else {
                int index = getEntryIndexByCoordinate(e.getX(), e.getY());
//                Log.v("Chart", "滑动 onSingleTapConfirmed index:" + index + ", mHighlightVolatile:" + mHighlightVolatile + ", mHighlights != null:" + (mHighlights != null) + ", mHighlightDisable:" + mHighlightDisable);
                if (index >= 0) {
                    if (isMainChart()) {
                        // 如果不是一直显示光标 并且 光标数组当前不为null 清除光标
                        if (!alwaysHighlight && getHighlights() != null) {
                            cleanHighlight();
                        } else {
                            mHighlightY = e.getY();
                            onTouchPoint(e);
                        }
                    } else {
                        if (getHighlights() != null) cleanHighlight();
                    }
                } else {
                    if (getHighlights() != null) cleanHighlight();
                    if (isClickable() && hasOnClickListeners()) {
                        performClick();
                    }
                }
            }

            return true;
        }

        /**
         * 手指在屏幕双击的时候回调
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 区间统计打开时 双击无效
            if (mRangeEnable) return false;

            if (mDoubleTapToZoom) {
                zoomer.forceFinished(true);
                if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
                    zoomer.startZoom(ZOOM_AMOUNT);
                }
                postInvalidateOnAnimation();
            }
            return true;
        }

        /**
         * 手指在触摸屏幕滑动的时候回调
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!enableDraggingToMove || isScaling()) {
                mScroller.forceFinished(true);
                return false;
            }

            // 主图高亮时 不滚动，只触发点击
            if ((!isDraggingToMoveEnable() && isHighlight()) // 分时主图，高亮时
                    || (!isDraggingToMoveEnable() && !isMainChart()) // K线副图
                    || (isDraggingToMoveEnable() && isMainChart() && isHighlight() && !alwaysHighlight || isLongPress()) // K线主图，高亮且能关闭高亮光标时
            ) {
                if (mIsLongPress) {
                    if (isMainChart()) onTouchPoint(e2);
                    else onTouchHighlight(e2);
                } else {
                    // 不是长按 滑动时如果光标正在显示 清掉光标
                    if (mIsHighlight && !alwaysHighlight) cleanHighlight();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            if (mScaleDetector.isInProgress()) {
                return false;
            }

            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) {
                return true;
            }
            Log.w("Chart", "onScroll");

            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            /**
             * Pixel offset is the offset in screen pixels, while viewport offset is the
             * offset within the current viewport. For additional information on surface sizes
             * and pixel offsets, see the docs for {@link computeScrollSurfaceSize()}. For
             * additional information about the viewport, see the comments for
             * {@link mCurrentViewport}.
             */

            float viewportOffsetX = distanceX * mCurrentViewport.width() / mContentRect.width();
            // float viewportOffsetY = -distanceY * mCurrentViewport.height() / mContentRect.height();
            computeScrollSurfaceSize(mSurfaceSizeBuffer);
            float viewPortDiff = Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN;
            int scrolledX = (int) (mSurfaceSizeBuffer.x * (mCurrentViewport.left + viewportOffsetX - Viewport.AXIS_X_MIN) / viewPortDiff);
            boolean canScrollX = mCurrentViewport.left > Viewport.AXIS_X_MIN
                    || mCurrentViewport.right < Viewport.AXIS_X_MAX;
            setViewportBottomLeft(mCurrentViewport.left + viewportOffsetX);

            if (canScrollX && scrolledX < 0) {
                mEdgeEffectLeft.onPull(scrolledX / (float) mContentRect.width());
                mEdgeEffectLeftActive = true;
            }

            if (canScrollX && scrolledX > mSurfaceSizeBuffer.x - mContentRect.width()) {
                mEdgeEffectRight.onPull((scrolledX - mSurfaceSizeBuffer.x + mContentRect.width())
                        / (float) mContentRect.width());
                mEdgeEffectRightActive = true;
            }

            return true;
        }

        /**
         * 手指迅速移动并松开的时候回调
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (enableDraggingToMove && (!mIsHighlight || alwaysHighlight)) {
                Log.w("Chart", "onFling");
                releaseEdgeEffects();
                // Flings use math in pixels (as opposed to math based on the viewport).
                computeScrollSurfaceSize(mSurfaceSizeBuffer);
                mScrollerStartViewport.set(mCurrentViewport);

                float viewPortDiff = Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN;
                int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - Viewport.AXIS_X_MIN) / viewPortDiff);
                mScroller.forceFinished(true);
                mScroller.fling(
                        startX,
                        0,
                        (int) -velocityX,
                        0,
                        0, mSurfaceSizeBuffer.x - mContentRect.width(),
                        0, mSurfaceSizeBuffer.y - mContentRect.height(),
                        mContentRect.width() / 2,
                        0);
                postInvalidateOnAnimation();
            }
            return true;
        }
    };

    private void onAlwaysHighlight() {
        if (getHighlights() == null) return;
        float highlightX = getEntryCoordinateByIndex(mHighlightIndex);
        Log.w("onAlwaysHighlight", highlightX + "-" + mHighlightY + "-" + mHighlightIndex);
        for (OnTouchPointChangeListener touchPointChangeListener : mTouchPointChangeListeners) {
            touchPointChangeListener.touch(highlightX, mHighlightY);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        getContentRect().set(
                getPaddingLeft() + (mAxisLeft.isInside() ? 0 : mAxisLeft.getLabelWidth()),
                getPaddingTop(),
                getWidth() - getPaddingRight() - (mAxisRight.isInside() ? 0 : mAxisRight.getLabelWidth()),
                getHeight() - getPaddingBottom() - mAxisBottom.getLabelHeight()
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minChartSize = getResources().getDimensionPixelSize(R.dimen.jz_chart_min_size);
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(minChartSize + getPaddingLeft()
                                        + (mAxisLeft.isInside() ? 0 : mAxisLeft.getLabelWidth())
                                        + getPaddingRight(),
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(minChartSize + getPaddingTop()
                                        + (mAxisBottom.isInside() ? 0 : mAxisBottom.getLabelHeight())
                                        + getPaddingBottom(),
                                heightMeasureSpec)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                postInvalidateOnAnimation();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsLongPress = false;
                mIsTouching = false;
                postInvalidateOnAnimation();
                break;
        }
        mDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 由父级调用以请求子级在必要时更新其mScrollX和mScrollY的值。
     * 如果子类正在使用Scroller对象设置滚动动画，则通常会执行此操作。
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            // The scroller isn't finished, meaning a fling or programmatic pan operation is
            // currently active.
            if (!mIsHighlight || alwaysHighlight) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            } else {
                mScroller.forceFinished(true);
            }
        }

        if (zoomer.computeZoom()) {
            // Performs the zoom since a zoom is in progress (either programmatically or via
            // double-touch).
            float newWidth = (1f - zoomer.getCurrZoom()) * mScrollerStartViewport.width();
            float newHeight = (1f - zoomer.getCurrZoom()) * mScrollerStartViewport.height();
            float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
                    / mScrollerStartViewport.width();
            float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
                    / mScrollerStartViewport.height();
            mCurrentViewport.set(
                    mZoomFocalPoint.x - newWidth * pointWithinViewportX,
                    mZoomFocalPoint.y - newHeight * pointWithinViewportY,
                    mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX),
                    mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY));
            mCurrentViewport.constrainViewport();
            triggerViewportChange();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!isDraggingToMoveEnable()) {
            mScroller.forceFinished(true);
            return;
        }

        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        int currX = mScroller.getCurrX();

        boolean canScrollX = (mCurrentViewport.left > Viewport.AXIS_X_MIN
                || mCurrentViewport.right < Viewport.AXIS_X_MAX);

        Log.w("Chart", "currX=" + currX + "----" + "canScrollX" + canScrollX);

        if (canScrollX
                && currX < 0
                && mEdgeEffectLeft.isFinished()
                && !mEdgeEffectLeftActive) {
            mEdgeEffectLeft.onAbsorb((int) mScroller.getCurrVelocity());
            mEdgeEffectLeftActive = true;
        } else if (canScrollX
                && currX > (mSurfaceSizeBuffer.x - mContentRect.width())
                && mEdgeEffectRight.isFinished()
                && !mEdgeEffectRightActive) {
            mEdgeEffectRight.onAbsorb((int) mScroller.getCurrVelocity());
            mEdgeEffectRightActive = true;
        }

        float currXRange = Viewport.AXIS_X_MIN + (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)
                * currX / mSurfaceSizeBuffer.x;
        setViewportBottomLeft(currXRange);

        if (enableLoadMore && currX <= 0) {
            Log.w("Chart", "加载更多");
            mScroller.forceFinished(true);
            if (mOnLoadMoreKlineListener != null) {
                mOnLoadMoreKlineListener.onLoadMoreKline(currX);
            }
        }

    }

    /**
     * Computes the current scrollable surface size, in pixels. For example, if the entire lib
     * area is visible, this is simply the current size of {@link #mContentRect}. If the lib
     * is zoomed in 200% in both directions, the returned size will be twice as large horizontally
     * and vertically.
     */
    private void computeScrollSurfaceSize(Point out) {
        out.set((int) (mContentRect.width() * (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)
                        / mCurrentViewport.width()),
                (int) (mContentRect.height() * (Viewport.AXIS_Y_MAX - Viewport.AXIS_Y_MIN)
                        / mCurrentViewport.height()));
    }

    public void moveLeft() {
        moveLeft(0.2f);
    }

    public void moveRight() {
        moveRight(0.2f);
    }

    public void moveLeft(@FloatRange(from = 0f, to = 1.0f) float percent) {
        moveTo(percent, -1);
    }

    public void moveRight(@FloatRange(from = 0f, to = 1.0f) float percent) {
        moveTo(percent, 1);
    }

    /**
     * @param percent   比例
     * @param direction 左移 -1 右移 1
     */
    private void moveTo(@FloatRange(from = 0f, to = 1.0f) float percent, int direction) {
        releaseEdgeEffects();
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCurrentViewport);

        float moveDistance = mContentRect.width() * percent * direction;
        float viewPortDiff = Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN;
        int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - Viewport.AXIS_X_MIN) / viewPortDiff);
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(startX, 0, (int) moveDistance, 0, 300);
        postInvalidateOnAnimation();
    }

    /**
     * Sets the current viewport (defined by {@link #mCurrentViewport}) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position, and thus
     * the bottom of the {@link #mCurrentViewport} rectangle. For more details on why top and
     * bottom are flipped, see {@link #mCurrentViewport}.
     */
    private void setViewportBottomLeft(float x) {
        /**
         * Constrains within the scroll range. The scroll range is simply the viewport extremes
         * (AXIS_X_MAX, etc.) minus the viewport size. For example, if the extrema were 0 and 10,
         * and the viewport size was 2, the scroll range would be 0 to 8.
         */

        float curWidth = mCurrentViewport.width();
        x = Math.max(Viewport.AXIS_X_MIN, Math.min(x, Viewport.AXIS_X_MAX - curWidth));

        mCurrentViewport.left = x;
        mCurrentViewport.right = x + curWidth;
        mCurrentViewport.constrainViewport();

        if (alwaysHighlight) {
            onAlwaysHighlight();
        }
        triggerViewportChange();
    }

    public void setCurrentViewport(RectF viewport) {
        mCurrentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom);
        mCurrentViewport.constrainViewport();
        triggerViewportChange();
    }

    // <editor-fold desc="generate set and get">    ----------------------------------------------------------
    public AxisY getAxisLeft() {
        return mAxisLeft;
    }

    public AxisY getAxisRight() {
        return mAxisRight;
    }

    public AxisX getAxisTop() {
        return mAxisTop;
    }

    public AxisX getAxisBottom() {
        return mAxisBottom;
    }

    /**
     * 是否伸缩状态
     */
    public boolean isScaling() {
        return mScaleDetector.isInProgress();
    }

    /**
     * 获取触摸状态
     */
    public boolean isTouching() {
        return this.mIsTouching;
    }

    /**
     * 获取光标高亮状态
     */
    public boolean isHighlight() {
        return mIsHighlight;
    }

    public boolean isLongPress() {
        return this.mIsLongPress;
    }

    public void setIsLongPress(boolean isLongPress) {
        this.mIsLongPress = isLongPress;
    }

    public boolean isEnableHighlight() {
        return this.enableHighlight;
    }

    public void setEnableHighlight(boolean enableHighlight) {
        this.enableHighlight = enableHighlight;
    }

    public boolean isAlwaysHighlight() {
        return alwaysHighlight;
    }

    public void setAlwaysHighlight(boolean alwaysHighlight) {
        this.alwaysHighlight = alwaysHighlight;
    }

    public void setRangeEnable(boolean enableRange) {
        this.mRangeEnable = enableRange;
    }

    public boolean getRangeEnable() {
        return this.mRangeEnable;
    }

    public void setDoubleTapToZoom(boolean doubleTapToZoom) {
        this.mDoubleTapToZoom = doubleTapToZoom;
    }

    public boolean getDoubleTapToZoom() {
        return this.mDoubleTapToZoom;
    }

    public boolean isCanZoomIn() {
        return canZoomIn;
    }

    public void setCanZoomIn(boolean canZoomIn) {
        this.canZoomIn = canZoomIn;
    }

    public boolean isCanZoomOut() {
        return canZoomOut;
    }

    public void setCanZoomOut(boolean canZoomOut) {
        this.canZoomOut = canZoomOut;
    }

    public boolean isScaleGestureEnable() {
        return this.enableScaleGesture;
    }

    public void setScaleGestureEnable(boolean enable) {
        this.enableScaleGesture = enable;
    }

    public boolean isScaleXEnable() {
        return enableScaleX;
    }

    public void setScaleXEnable(boolean enable) {
        this.enableScaleX = enable;
    }

    public void setDraggingToMoveEnable(boolean enableDraggingToMove) {
        this.enableDraggingToMove = enableDraggingToMove;
    }

    public boolean isDraggingToMoveEnable() {
        return this.enableDraggingToMove;
    }

    public float getScaleSensitivity() {
        return this.scaleSensitivity;
    }

    public void setScaleSensitivity(float scaleSensitivity) {
        this.scaleSensitivity = scaleSensitivity;
    }

    public boolean isEnableLoadMore() {
        return this.enableLoadMore;
    }

    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
    }

    public int getFocusIndex() {
        return mFocusIndex;
    }

    public void setFocusIndex(int focusIndex) {
        this.mFocusIndex = focusIndex;
    }

    public boolean isMainChart() {
        return mIsMainChart;
    }

    public void setIsMainChart(boolean isMainChart) {
        this.mIsMainChart = isMainChart;
    }

    public boolean isHighlightDisable() {
        return mHighlightDisable;
    }

    public void setHighlightDisable(boolean highlightDisable) {
        this.mHighlightDisable = highlightDisable;
    }

    // </editor-fold desc="generate set and get">    ---------------------------------------------------------
}
