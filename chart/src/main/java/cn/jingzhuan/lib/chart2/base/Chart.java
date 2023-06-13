package cn.jingzhuan.lib.chart2.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EdgeEffect;
import android.widget.OverScroller;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Zoomer;
import cn.jingzhuan.lib.chart.event.OnLoadMoreKlineListener;
import cn.jingzhuan.lib.chart.event.OnScaleListener;
import cn.jingzhuan.lib.chart.utils.ForceAlign.XForce;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.utils.ForceAlign;
import cn.jingzhuan.lib.source.JZScaleGestureDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Donglua on 17/7/17.
 */

public abstract class Chart extends BitmapCachedChart {

    protected AxisY mAxisLeft = new AxisY(AxisY.LEFT_INSIDE);
    protected AxisY mAxisRight = new AxisY(AxisY.RIGHT_INSIDE);
    protected AxisX mAxisTop = new AxisX(AxisX.TOP);
    protected AxisX mAxisBottom = new AxisX(AxisX.BOTTOM);

    private JZScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private OverScroller mScroller;
    private Zoomer mZoomer;
    private PointF mZoomFocalPoint = new PointF();
    private RectF mScrollerStartViewport = new RectF(); // Used only for zooms and flings.

    private boolean mScaleXEnable = true;
    private boolean mDraggingToMoveEnable = true;
    private boolean mIsMainChart = false;
    protected boolean mIsHighlight = false;
    private boolean mIsLongPress = false;
    private boolean mDoubleTapToZoom = false;
    private boolean mScaleGestureEnable = true;

    private boolean mHighlightDisable = false;
    private boolean mHighlightVolatile = true;

    protected List<OnTouchPointChangeListener> mTouchPointChangeListeners;
    protected List<OnTouchHighlightChangeListener> mTouchHighlightChangeListeners;
    private List<OnViewportChangeListener> mOnViewportChangeListeners;
    protected OnViewportChangeListener mInternalViewportChangeListener;
    protected OnLoadMoreKlineListener mOnLoadMoreKlineListener;

    protected OnScaleListener mScaleListener;

    /**
     * The scaling factor for a single zoom 'step'.
     *
     * @see #zoomIn()
     * @see #zoomOut()
     */
    private static final float ZOOM_AMOUNT = 0.2f;

    private final Point mSurfaceSizeBuffer = new Point();


    // Edge effect / overscroll tracking objects.
    private EdgeEffect mEdgeEffectLeft;
    private EdgeEffect mEdgeEffectRight;

    private boolean mEdgeEffectLeftActive;
    private boolean mEdgeEffectRightActive;

    private boolean isTouching = false;
    private boolean isShowRange = false;

    protected Highlight[] mHighlights;
    int mFocusIndex = -1;

    protected boolean canLoadMore = true;

    protected float mDistanceX = 0f;
    private float scaleSensitivity = 1f;
    private boolean canZoomIn = true;
    private boolean canZoomOut = true;

    /**
     * 背景颜色
     */
    private int bgColor = Color.TRANSPARENT;

    /**
     * 坐标轴刻度文本 是否画在底层
     */
    private boolean drawLabelsInBottom = false;

    public Chart(Context context) {
        this(context, null, 0);
    }

    public Chart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Chart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.Chart, defStyleAttr, defStyleAttr);

        initListeners();

        mAxisTop.setGridLineEnable(false);
        mAxisTop.setLabelEnable(false);

        try {
            List<Axis> axisList = new ArrayList<>(4);
            axisList.add(mAxisLeft);
            axisList.add(mAxisRight);
            axisList.add(mAxisTop);
            axisList.add(mAxisBottom);

            this.bgColor = a.getColor(R.styleable.Chart_backgroundColor, Color.TRANSPARENT);
            this.drawLabelsInBottom = a.getBoolean(R.styleable.Chart_drawLabelsInBottom, false);
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

    private void setupInteractions(Context context) {
        mScaleGestureDetector = new JZScaleGestureDetector(context, mScaleGestureListener);

        mGestureDetector = new GestureDetector(context, mGestureListener);
        mGestureDetector.setIsLongpressEnabled(true);

        mScroller = new OverScroller(context);
        mZoomer = new Zoomer(context);
    }

    private void initListeners() {
        mTouchPointChangeListeners = Collections.synchronizedList(new ArrayList<>());
        mTouchHighlightChangeListeners = Collections.synchronizedList(new ArrayList<>());
        mOnViewportChangeListeners = Collections.synchronizedList(new ArrayList<>());
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

        dest.set(mCurrentViewport.left
                        + mCurrentViewport.width()
                        * (x - mContentRect.left) / mContentRect.width(),
                mCurrentViewport.top
                        + mCurrentViewport.height()
                        * (y - mContentRect.bottom) / -mContentRect.height());
        return true;
    }


    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final JZScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new JZScaleGestureDetector.SimpleOnScaleGestureListener() {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        private final PointF viewportFocus = new PointF();
//        private float lastSpanX;

        @Override
        public boolean onScaleBegin(JZScaleGestureDetector scaleGestureDetector) {
            if (!isScaleGestureEnable()) return super.onScaleBegin(scaleGestureDetector);
            if(mScaleListener != null)  {
                mScaleListener.onScaleStart(mCurrentViewport);
            }
//            lastSpanX = scaleGestureDetector.getCurrentSpanX();
            return true;
        }

        @Override
        public void onScaleEnd(JZScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            if(mScaleListener != null)  {
                mScaleListener.onScaleEnd(mCurrentViewport);
            }
            isScaling = false;
            Log.d("Chart", "onScaleEnd");
        }

        @Override
        public boolean onScale(JZScaleGestureDetector scaleGestureDetector) {
            Log.d("Chart", "onScale");
            if (!isScaleXEnable()) return false;
            if (!isScaleGestureEnable()) return super.onScale(scaleGestureDetector);

            isScaling = true;

            float spanX = scaleGestureDetector.getCurrentSpan();
            float lastSpanX = scaleGestureDetector.getPreviousSpan();
            boolean zoomIn = spanX > lastSpanX; // 双指距离比上次大，为放大
            boolean zoomOut = lastSpanX > spanX; // 双指距离比上次小，为缩小

            boolean canZoom = Math.abs(Math.abs(lastSpanX) - Math.abs(spanX)) >= 5f;

            if (zoomIn) {
                setCanZoomOut(true);
                if (!isCanZoomIn())
                    return false;
            }

            if (zoomOut) {
                setCanZoomIn(true);
                if (!isCanZoomOut())
                    return false;
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

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

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

//            if(getIfKlineFullRect()) {
//                // 优先向右缩进
//                mCurrentViewport.left = viewportFocus.x - newWidth * (focusX - mContentRect.left) / mContentRect.width();
//                if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;
//
//                if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
//                    mCurrentViewport.right = mCurrentViewport.left + newWidth;
//                    if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
//                }
//                if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.left = Viewport.AXIS_X_MAX;
//            } else {
//                mCurrentViewport.left = Viewport.AXIS_X_MIN;
//                mCurrentViewport.right = Viewport.AXIS_X_MAX;
//            }

            mCurrentViewport.constrainViewport();

            triggerViewportChange();
            Log.d("Chart", "triggerViewportChange from= onScale");
//            lastSpanX = spanX;
            if(mScaleListener != null)  {
                mScaleListener.onScale(mCurrentViewport);
            }
            return true;
        }
    };

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private final GestureDetector.SimpleOnGestureListener mGestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {

            releaseEdgeEffects();
            mScrollerStartViewport.set(mCurrentViewport);
            mScroller.forceFinished(true);

            postInvalidateOnAnimation();

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(getRangeEnable()) return;
            mIsLongPress = true;
            onTouchPoint(e);
            e.setAction(MotionEvent.ACTION_UP);
            mGestureDetector.onTouchEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d("Chart", "滑动 onSingleTapUp(" + e.getX() + ", " + e.getY() + "); isHighlight:" + isHighlight());
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(getRangeEnable()) return false;
//            Log.d("Chart", "滑动 onSingleTapConfirmed isClickable:" + isClickable()+ ", hasOnClickListeners:" + hasOnClickListeners() + "; isHighlight:" + isHighlight());
            if (isClickable() && hasOnClickListeners()) {
                cleanHighlight();
                performClick();
            } else {
                int index = getEntryIndexByCoordinate(e.getX(), e.getY());
//                Log.v("Chart", "滑动 onSingleTapConfirmed index:" + index + ", mHighlightVolatile:" + mHighlightVolatile + ", mHighlights != null:" + (mHighlights != null) + ", mHighlightDisable:" + mHighlightDisable);
                if (index >= 0) {
                    if (mHighlightVolatile && (mHighlights != null || mHighlightDisable)) {
                        if (isMainChart()) cleanHighlight();
                        else onTouchPoint(e);
                    } else {
                        onTouchPoint(e);
                    }
                } else {
                    cleanHighlight();
                    if (isClickable() && hasOnClickListeners()) {
                        performClick();
                    }
                }
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(getRangeEnable()) return false;
            if (mDoubleTapToZoom) {

                mZoomer.forceFinished(true);
                if (hitTest(e.getX(), e.getY(), mZoomFocalPoint)) {
                    mZoomer.startZoom(ZOOM_AMOUNT);
                }
                postInvalidateOnAnimation();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mDistanceX = distanceX;
//            Log.d("Chart", "滑动 onScroll(" + e2.getX() + ", " + e2.getY() + "), isDraggingToMoveEnable:" + isDraggingToMoveEnable() + ", isMainChart:" + isMainChart() + ", inHighlight:" + isHighlight());
            // 主图高亮时 不滚动，只触发点击
            if ((!isDraggingToMoveEnable() && isHighlight()) // 分时主图，高亮时
                    || (!isDraggingToMoveEnable() && !isMainChart()) // K线副图
                    || (isDraggingToMoveEnable() && isMainChart() && isHighlight() && isHighlightVolatile() || isLongPress()) // K线主图，高亮且能关闭高亮光标时
            ) {
//                Log.d("Chart", "滑动 onTouchPoint(" + e2.getX() + ", " + e2.getY() + ") isTouching:" + isTouching + ", isLongPress:" + isLongPress());
                if (isLongPress()) {
                    if (isMainChart()) onTouchPoint(e2); else onTouchHighlight(e2); // if (isTouching) {  }
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            if (mScaleGestureDetector.isInProgress()) {
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
            int scrolledX = (int) (mSurfaceSizeBuffer.x
                    * (mCurrentViewport.left + viewportOffsetX - Viewport.AXIS_X_MIN)
                    / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN));
            boolean canScrollX = mCurrentViewport.left > Viewport.AXIS_X_MIN
                    || mCurrentViewport.right < Viewport.AXIS_X_MAX;
            setViewportBottomLeft(
                    mCurrentViewport.left + viewportOffsetX
            );

//            if (canScrollX && scrolledX < 0) {
//                mEdgeEffectLeft.onPull(scrolledX / (float) mContentRect.width());
//                mEdgeEffectLeftActive = true;
//                if (canLoadMore && mOnLoadMoreKlineListener != null) {
//                    mOnLoadMoreKlineListener.onLoadMoreKline(scrolledX);
//                }
//                canLoadMore = false;
//            } else {
//                canLoadMore = true;
//            }

            boolean isRightSide = mCurrentViewport.right == Viewport.AXIS_X_MAX;

            if (canScrollX && scrolledX > mSurfaceSizeBuffer.x - mContentRect.width() && !isRightSide) {
                mEdgeEffectRight.onPull((scrolledX - mSurfaceSizeBuffer.x + mContentRect.width())
                        / (float) mContentRect.width());
                mEdgeEffectRightActive = true;
            }

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean isRightSide = mCurrentViewport.right == Viewport.AXIS_X_MAX;
            if(isRightSide) return false;
            if (!isDraggingToMoveEnable()) return super.onFling(e1, e2, velocityX, velocityY);

            fling((int) -velocityX);

            if (!isDraggingToMoveEnable()) {
                onTouchPoint(e2);
            }

            return true;
        }
    };

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

    private void fling(int velocityX) {
        if (!canLoadMore) return;

        releaseEdgeEffects();
        // Flings use math in pixels (as opposed to math based on the viewport).
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCurrentViewport);
        int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - Viewport.AXIS_X_MIN) / (
                Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN));
        mScroller.forceFinished(true);
        mScroller.fling(
                startX,
                0,
                velocityX,
                0,
                0, mSurfaceSizeBuffer.x - mContentRect.width(),
                0, mSurfaceSizeBuffer.y - mContentRect.height(),
                mContentRect.width() / 2,
                0);
        postInvalidateOnAnimation();
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
        mScrollerStartViewport.set(mCurrentViewport);
        mZoomer.forceFinished(true);
        mZoomer.startZoom(scalingFactor);
        mZoomFocalPoint.set(
                (mCurrentViewport.right + mCurrentViewport.left) / 2,
                (mCurrentViewport.bottom + mCurrentViewport.top) / 2);
        triggerViewportChange();
    }

    public void zoomOut(@XForce int forceAlignX) {
        mScrollerStartViewport.set(mCurrentViewport);
        mZoomer.forceFinished(true);
        mZoomer.startZoom(-ZOOM_AMOUNT);

        float forceX;
        switch (forceAlignX) {
            case ForceAlign.LEFT:
                forceX = mCurrentViewport.left;
                break;
            case ForceAlign.RIGHT:
                forceX = mCurrentViewport.right;
                break;
            case ForceAlign.CENTER:
            default:
                forceX = (mCurrentViewport.right + mCurrentViewport.left) / 2;
                break;
        }
        mZoomFocalPoint.set(forceX,
                (mCurrentViewport.bottom + mCurrentViewport.top) / 2);
        triggerViewportChange();
    }

    /**
     * Smoothly zooms the lib in one step.
     */
    public void zoomIn(@XForce int forceAlignX) {
        mScrollerStartViewport.set(mCurrentViewport);
        mZoomer.forceFinished(true);
        mZoomer.startZoom(ZOOM_AMOUNT);

        float forceX;
        switch (forceAlignX) {
            case ForceAlign.LEFT:
                forceX = mCurrentViewport.left;
                break;
            case ForceAlign.RIGHT:
                forceX = mCurrentViewport.right;
                break;
            case ForceAlign.CENTER:
            default:
                forceX = (mCurrentViewport.right + mCurrentViewport.left) / 2;
                break;
        }

        mZoomFocalPoint.set(forceX,
                (mCurrentViewport.bottom + mCurrentViewport.top) / 2);
        triggerViewportChange();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        boolean needsInvalidate = false;

        if (mScroller.computeScrollOffset()) {
            // The scroller isn't finished, meaning a fling or programmatic pan operation is
            // currently active.

            computeScrollSurfaceSize(mSurfaceSizeBuffer);
            int currX = mScroller.getCurrX();

            boolean canScrollX = (mCurrentViewport.left > Viewport.AXIS_X_MIN
                    || mCurrentViewport.right < Viewport.AXIS_X_MAX);

            if (canScrollX
                    && currX < 0
                    && mEdgeEffectLeft.isFinished()
                    && !mEdgeEffectLeftActive) {
                mEdgeEffectLeft.onAbsorb((int) mScroller.getCurrVelocity());
                mEdgeEffectLeftActive = true;
                needsInvalidate = true;
            } else if (canScrollX
                    && currX > (mSurfaceSizeBuffer.x - mContentRect.width())
                    && mEdgeEffectRight.isFinished()
                    && !mEdgeEffectRightActive) {
                mEdgeEffectRight.onAbsorb((int) mScroller.getCurrVelocity());
                mEdgeEffectRightActive = true;
                needsInvalidate = true;
            }

            float currXRange = Viewport.AXIS_X_MIN + (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)
                    * currX / mSurfaceSizeBuffer.x;
            setViewportBottomLeft(currXRange);
            if (canLoadMore && currX <= 0) {
                Log.w("Chart", "加载更多");
                mScroller.forceFinished(true);
                if (mOnLoadMoreKlineListener != null) {
                    mOnLoadMoreKlineListener.onLoadMoreKline(currX);
                }
            }
        }

        if (mZoomer.computeZoom()) {
            // Performs the zoom since a zoom is in progress (either programmatically or via
            // double-touch).
            float newWidth = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.width();
            float newHeight = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.height();
            float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left)
                    / mScrollerStartViewport.width();
            float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
                    / mScrollerStartViewport.height();

            // 优先向右缩进
            mCurrentViewport.set(
                    mZoomFocalPoint.x - newWidth * pointWithinViewportX,
                    mZoomFocalPoint.y - newHeight * pointWithinViewportY,
                    mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX),
                    mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY));
            if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;

            if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                mCurrentViewport.right = mCurrentViewport.left + newWidth;
                if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
            }
            if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.left = Viewport.AXIS_X_MAX;

//            if(getIfKlineFullRect()) {
//                // 优先向右缩进
//                mCurrentViewport.set(
//                        mZoomFocalPoint.x - newWidth * pointWithinViewportX,
//                        mZoomFocalPoint.y - newHeight * pointWithinViewportY,
//                        mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX),
//                        mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY));
//                if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;
//
//                if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
//                    mCurrentViewport.right = mCurrentViewport.left + newWidth;
//                    if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
//                }
//                if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.left = Viewport.AXIS_X_MAX;
//
//            } else {
//                mCurrentViewport.left = Viewport.AXIS_X_MIN;
//                mCurrentViewport.right = Viewport.AXIS_X_MAX;
//            }

            mCurrentViewport.constrainViewport();

            if(mScaleListener != null) {
                mScaleListener.onScale(mCurrentViewport);
                mScaleListener.onScaleEnd(mCurrentViewport);
            }

            needsInvalidate = true;
        }

        if (needsInvalidate) {
            triggerViewportChange();
            Log.d("Chart", "triggerViewportChange from= computeScroll");
        }
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
        triggerViewportChange();

        Log.d("Chart", "triggerViewportChange from= setViewportBottomLeft");
    }

    /**
     * Sets the lib's current viewport.
     *
     * @see #getCurrentViewport()
     */
    public void setCurrentViewport(RectF viewport) {
        mCurrentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom);
        mCurrentViewport.constrainViewport();
        triggerViewportChange();
    }

    private boolean isScaling = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouching = true;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                postInvalidateOnAnimation();
                break;
            case MotionEvent.ACTION_UP:
                mIsLongPress = false;
                isTouching = false;
                postInvalidateOnAnimation();
                handleNoComputeScrollOffsetLoadMore();
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsLongPress = false;
                isTouching = false;
                isScaling = false;
                postInvalidateOnAnimation();
                break;
        }
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private void handleNoComputeScrollOffsetLoadMore() {
        float viewportOffsetX = mDistanceX * mCurrentViewport.width() / mContentRect.width();
        int scrolledX = (int) (mSurfaceSizeBuffer.x
                * (mCurrentViewport.left + viewportOffsetX - Viewport.AXIS_X_MIN)
                / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN));
        boolean isRightSide = mCurrentViewport.right == Viewport.AXIS_X_MAX;

        if(isRightSide && scrolledX < 0 && canLoadMore) {
//            Log.w("Chart", "加载更多->handleNoComputeScrollOffsetLoadMore");
            if (mOnLoadMoreKlineListener != null) {
                mOnLoadMoreKlineListener.onLoadMoreKline(scrolledX);
            }
        }
        mDistanceX = 0f;
    }

    protected void setupEdgeEffect(Context context) {

        // Sets up edge effects
        mEdgeEffectLeft = new EdgeEffect(context);
        mEdgeEffectRight = new EdgeEffect(context);
    }

    /**
     * Draws the overscroll "glow" at the four edges of the lib region, if necessary. The edges
     * of the lib region are stored in {@link #mContentRect}.
     *
     * @see EdgeEffect
     */
    protected void drawEdgeEffectsUnclipped(Canvas canvas) {
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
        mEdgeEffectLeftActive
                = mEdgeEffectRightActive
                = false;
        mEdgeEffectLeft.onRelease();
        mEdgeEffectRight.onRelease();
    }

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

    public void addOnViewportChangeListener(OnViewportChangeListener onViewportChangeListener) {
        synchronized (this) {
            this.mOnViewportChangeListeners.add(onViewportChangeListener);
        }
    }

    public boolean isScaleXEnable() {
        return mScaleXEnable;
    }

    public void setScaleXEnable(boolean scaleXEnable) {
        this.mScaleXEnable = scaleXEnable;
    }

    public void setDoubleTapToZoom(boolean doubleTapToZoom) {
        this.mDoubleTapToZoom = doubleTapToZoom;
    }

    public float getScaleSensitivity() {
        return scaleSensitivity;
    }

    public void setScaleSensitivity(float scaleSensitivity) {
        this.scaleSensitivity = scaleSensitivity;
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

    public interface OnTouchPointChangeListener {
        void touch(float x, float y);
    }

    public void addOnTouchPointChangeListener(OnTouchPointChangeListener touchPointChangeListener) {
        synchronized (this) {
            this.mTouchPointChangeListeners.add(touchPointChangeListener);
        }
    }

    public void removeOnTouchPointChangeListener(OnTouchPointChangeListener touchPointChangeListener) {
        synchronized (this) {
            this.mTouchPointChangeListeners.remove(touchPointChangeListener);
        }
    }

    public interface OnTouchHighlightChangeListener {
        void highlight(float x, float y);
    }

    public void addOnTouchHighlightChangeListener(OnTouchHighlightChangeListener touchHighlightChangeListener) {
        synchronized (this) {
            this.mTouchHighlightChangeListeners.add(touchHighlightChangeListener);
        }
    }

    public void removeOnTouchHighlightChangeListener(OnTouchHighlightChangeListener touchHighlightChangeListener) {
        synchronized (this) {
            this.mTouchHighlightChangeListeners.remove(touchHighlightChangeListener);
        }
    }

    public void setDraggingToMoveEnable(boolean draggingToMoveEnable) {
        this.mDraggingToMoveEnable = draggingToMoveEnable;
    }

    public boolean isDraggingToMoveEnable() {
        return mDraggingToMoveEnable;
    }

    public boolean isMainChart() {
        return mIsMainChart;
    }

    public void setIsMainChart(boolean isMainChart) {
        this.mIsMainChart = isMainChart;
    }

    public boolean isHighlight() {
        return mIsHighlight;
    }

    public boolean isLongPress() {
        return mIsLongPress;
    }

    public void setIsLongPress(boolean isLongPress) {
        this.mIsLongPress = isLongPress;
    }

    public void moveLeft() {
        moveLeft(0.2f);
    }

    public void moveRight() {
        moveRight(0.2f);
    }

    public void moveLeft(@FloatRange(from = 0f, to = 1.0f) float percent) {
        releaseEdgeEffects();
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCurrentViewport);

        float moveDistance = mContentRect.width() * percent;
        int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - Viewport.AXIS_X_MIN)
                / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN));
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(startX, 0, (int) -moveDistance, 0, 300);
        postInvalidateOnAnimation();
    }

    public void moveRight(@FloatRange(from = 0f, to = 1.0f) float percent) {
//        releaseEdgeEffects();
        computeScrollSurfaceSize(mSurfaceSizeBuffer);
        mScrollerStartViewport.set(mCurrentViewport);

        float moveDistance = mContentRect.width() * percent;
        int startX = (int) (mSurfaceSizeBuffer.x * (mScrollerStartViewport.left - Viewport.AXIS_X_MIN)
                / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN));
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(startX, 0, (int) moveDistance, 0, 300);
        postInvalidateOnAnimation();
    }

    public void setInternalViewportChangeListener(
            OnViewportChangeListener mInternalViewportChangeListener) {
        this.mInternalViewportChangeListener = mInternalViewportChangeListener;
    }

    public void setScaleGestureEnable(boolean mScaleGestureEnable) {
        this.mScaleGestureEnable = mScaleGestureEnable;
    }

    public int getFocusIndex() {
        return mFocusIndex;
    }

    public void setFocusIndex(int focusIndex) {
        this.mFocusIndex = focusIndex;
    }

    public boolean isScaleGestureEnable() {
        return mScaleGestureEnable;
    }

    public boolean isHighlightDisable() {
        return mHighlightDisable;
    }

    public void setHighlightDisable(boolean highlightDisable) {
        this.mHighlightDisable = highlightDisable;
    }

    public boolean isHighlightVolatile() {
        return mHighlightVolatile;
    }

    public void setHighlightVolatile(boolean highlightVolatile) {
        this.mHighlightVolatile = highlightVolatile;
    }

    public Zoomer getZoomer() {
        return mZoomer;
    }

    public boolean isTouching() {
        return isTouching;
    }

    /**
     * 是否是伸缩状态
     */
    public boolean isScaling() {
        return isScaling;
    }

    public boolean getRangeEnable() {
        return isShowRange;
    }

    public void setRangeEnable(boolean showRange) {
        isShowRange = showRange;
    }

    public void setOnLoadMoreKlineListener(OnLoadMoreKlineListener onLoadMoreKlineListener) {
        this.mOnLoadMoreKlineListener = onLoadMoreKlineListener;
    }

    public void setOnScaleListener(OnScaleListener onScaleListener) {
        this.mScaleListener = onScaleListener;
    }

    public void finishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }

    public void setBackgroundColor(int bgColor) {
        this.bgColor = bgColor;
    }

    @Override
    public int getBackgroundColor() {
        return this.bgColor;
    }

    public void setDrawLabelsInBottom(boolean drawLabelsInBottom) {
        this.drawLabelsInBottom = drawLabelsInBottom;
    }

    @Override
    public boolean getDrawLabelsInBottom() {
        return this.drawLabelsInBottom;
    }
}


