package cn.jingzhuan.lib.chart2.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Zoomer;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart.event.OnLoadMoreKlineListener;
import cn.jingzhuan.lib.chart.event.OnScaleListener;
import cn.jingzhuan.lib.chart.event.OnSingleEntryClickListener;
import cn.jingzhuan.lib.chart.utils.ForceAlign.XForce;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Axis;
import cn.jingzhuan.lib.chart.component.AxisX;
import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart.utils.ForceAlign;
import cn.jingzhuan.lib.chart2.drawline.DrawLineTouchState;
import cn.jingzhuan.lib.chart2.drawline.DrawLineType;
import cn.jingzhuan.lib.chart2.drawline.OnDrawLineCompleteListener;
import cn.jingzhuan.lib.chart2.drawline.OnDrawLineTouchListener;
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
    private final PointF mZoomFocalPoint = new PointF();

    // Used only for zooms and flings.
    private final RectF mScrollerStartViewport = new RectF();

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

    protected OnSingleEntryClickListener onEntryClickListener;

    /**
     * The scaling factor for a single zoom 'step'.
     *
     * @see #zoomIn()
     * @see #zoomOut()
     */
    private static final float ZOOM_AMOUNT = 0.15f;

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
     * 背景颜色（不包含底部刻度文本）
     */
    private int bgColor = Color.TRANSPARENT;

    /**
     * 坐标轴刻度文本 是否画在底层
     */
    private boolean drawLabelsInBottom = false;

    /**
     * 坐标轴刻度文本 需要保留的小数位
     */
    private int decimalDigitsNumber = 2;

    /**
     * 是否需要展示水印
     */
    private boolean showWaterMark = false;

    /**
     * 是否黑夜模式
     */
    private boolean isNightMode = false;

    private boolean mMultipleTouch = false;

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

    public Chart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Chart, defStyleAttr, defStyleAttr);

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
            this.showWaterMark = a.getBoolean(R.styleable.Chart_showWaterMark, false);
            this.isNightMode = a.getBoolean(R.styleable.Chart_isNightMode, false);
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
        getBottomRect().set(
                getPaddingLeft() + (mAxisLeft.isInside() ? 0 : mAxisLeft.getLabelWidth()),
                getHeight() - getPaddingBottom() - mAxisBottom.getLabelHeight(),
                getWidth() - getPaddingRight() - (mAxisRight.isInside() ? 0 : mAxisRight.getLabelWidth()),
                getHeight()
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

        @Override
        public boolean onScaleBegin(JZScaleGestureDetector scaleGestureDetector) {
            Log.d("JZChart", "onScaleBegin");
            if (!isScaleGestureEnable()) return super.onScaleBegin(scaleGestureDetector);
            if(mScaleListener != null)  {
                mScaleListener.onScaleStart(mCurrentViewport);
            }
            return true;
        }

        @Override
        public void onScaleEnd(JZScaleGestureDetector detector) {
            Log.d("JZChart", "onScaleEnd");
            super.onScaleEnd(detector);
            if(mScaleListener != null)  {
                mScaleListener.onScaleEnd(mCurrentViewport);
            }
//            isScaling = false;
        }

        @Override
        public boolean onScale(JZScaleGestureDetector scaleGestureDetector) {
            Log.d("JZChart", "onScale");
            if (!isScaleXEnable()) return false;
            if (!isScaleGestureEnable()) return super.onScale(scaleGestureDetector);

            isScaling = true;

            float spanX = scaleGestureDetector.getCurrentSpan();
            float lastSpanX = scaleGestureDetector.getPreviousSpan();
            // 双指距离比上次大，为放大
            boolean zoomIn = spanX > lastSpanX;
            // 双指距离比上次小，为缩小
            boolean zoomOut = lastSpanX > spanX;

            boolean canZoom = Math.abs(Math.abs(lastSpanX) - Math.abs(spanX)) >= 5f;

            if (!canZoom) return false;

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

            float ratio = viewportFocus.x - newWidth * (focusX - mContentRect.left) / mContentRect.width();
            if (!canScroll()) {
                mCurrentViewport.left = Viewport.AXIS_X_MIN;
                mCurrentViewport.right = mCurrentViewport.right - ratio;
                float count = (int) ((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());
                if (count > getMaxVisibleEntryCount()) {
                    mCurrentViewport.right = getMaxVisibleEntryCount() / (float)getEntryCount() + mCurrentViewport.left;
                }
                if(mCurrentViewport.right < Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
            } else {
                // 不足一屏 并且缩放到一屏时 能继续缩小
                if (getEntryCount() < getMaxVisibleEntryCount() && !zoomIn && zoomOut && mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                    mCurrentViewport.left = Viewport.AXIS_X_MIN;
                    mCurrentViewport.right = mCurrentViewport.right - ratio;
                } else {
                    // 优先向右缩进
                    mCurrentViewport.left = ratio;
                    if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;

                    if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                        mCurrentViewport.right = mCurrentViewport.left + newWidth;
                        if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
                    }
                    if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;

                    float count = (int) ((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());
                    if (count > getMaxVisibleEntryCount()) {
                        mCurrentViewport.left = mCurrentViewport.right - getMaxVisibleEntryCount() / (float)getEntryCount();
                    }
                    if (count < getMinVisibleEntryCount()) {
                        mCurrentViewport.left = mCurrentViewport.right - getMinVisibleEntryCount() / (float)getEntryCount();
                    }
                }
            }
            currentVisibleEntryCount = Math.round((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());
            mCurrentViewport.constrainViewport();

            triggerViewportChange();
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
            Log.d("JZChart", "onDown");
            releaseEdgeEffects();
            mScrollerStartViewport.set(mCurrentViewport);
            mScroller.forceFinished(true);

            onPressDrawLine(e);
//            postInvalidateOnAnimation();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("JZChart", "onLongPress");
            if(getRangeEnable()) return;
            if (isDrawingLine()) return;
            mIsLongPress = true;
            onTouchPoint(e);
            e.setAction(MotionEvent.ACTION_UP);
            mGestureDetector.onTouchEvent(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("JZChart", "onSingleTapUp");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("JZChart", "onSingleTapConfirmed");
            if (getRangeEnable()) return false;
            if (isDrawingLine()) return false;
//            Log.d("Chart", "滑动 onSingleTapConfirmed isClickable:" + isClickable()+ ", hasOnClickListeners:" + hasOnClickListeners() + "; isHighlight:" + isHighlight());
            if (isClickable() && hasOnClickListeners()) {
                cleanHighlight();
                performClick();
            } else {
                int index = getEntryIndexByCoordinate(e.getX(), e.getY());
//                Log.v("Chart", "滑动 onSingleTapConfirmed index:" + index + ", mHighlightVolatile:" + mHighlightVolatile + ", mHighlights != null:" + (mHighlights != null) + ", mHighlightDisable:" + mHighlightDisable);
                if (index >= 0) {
                    if (onEntryClickListener != null) {
                        onEntryClickListener.onEntryClick(Chart.this, index);
                    }
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
            Log.d("JZChart", "onDoubleTap");
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
            if (isDrawingLine()) return false;
            if (!isMultipleTouch()) {
                Log.d("JZChart", "onScroll");
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
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isTouching()) {
                Log.d("JZChart", "onFling");
                boolean isRightSide = mCurrentViewport.right == Viewport.AXIS_X_MAX;
                if(isRightSide) return false;
                if (!isDraggingToMoveEnable()) return super.onFling(e1, e2, velocityX, velocityY);

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
                        (int) -velocityX,
                        0,
                        0, mSurfaceSizeBuffer.x - mContentRect.width(),
                        0, mSurfaceSizeBuffer.y - mContentRect.height(),
                        mContentRect.width() / 2,
                        0);
                postInvalidateOnAnimation();

                if (!isDraggingToMoveEnable()) {
                    Log.d("JZChart", "onFling-onTouchPoint1");
                    onTouchPoint(e2);
                }
            }

            return true;
        }
    };

    protected void triggerViewportChange() {

        if (mInternalViewportChangeListener != null) {
            synchronized (this) {
                try {
                    mInternalViewportChangeListener.onViewportChange(mCurrentViewport);
                } catch (Exception e) {
                    Log.d("JZChart", "onInternalViewportChange", e);
                }
            }
        }
        if (mOnViewportChangeListeners != null && !mOnViewportChangeListeners.isEmpty()) {
            synchronized (this) {
                for (OnViewportChangeListener mOnViewportChangeListener : mOnViewportChangeListeners) {
                    try {
                        mOnViewportChangeListener.onViewportChange(mCurrentViewport);
                    } catch (Exception e) {
                        Log.d("JZChart", "onViewportChange", e);
                    }
                }
            }
        }
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
            if (canLoadMore && currX <= 0 && canScroll()) {
                Log.w("JZChart", "加载更多");
                mScroller.forceFinished(true);
                if (mOnLoadMoreKlineListener != null) {
                    needsInvalidate = false;
                    mOnLoadMoreKlineListener.onLoadMoreKline(currX);
                }
            }
        }

        if (mZoomer.computeZoom()) {
            // Performs the zoom since a zoom is in progress (either programmatically or via
            // double-touch).

            float newWidth = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.width();
            float newHeight = (1f - mZoomer.getCurrZoom()) * mScrollerStartViewport.height();
            float pointWithinViewportY = (mZoomFocalPoint.y - mScrollerStartViewport.top)
                    / mScrollerStartViewport.height();

            float viewportTopY = mZoomFocalPoint.y - newHeight * pointWithinViewportY;
            float viewportBottomY = mZoomFocalPoint.y + newHeight * (1 - pointWithinViewportY);
            if (!canScroll()) {
                // 向左缩进
                mZoomFocalPoint.set(mCurrentViewport.left, (mCurrentViewport.bottom + mCurrentViewport.top) / 2);

                float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left) / mScrollerStartViewport.width();

                float viewportRightX = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);

                mCurrentViewport.set(Viewport.AXIS_X_MIN, viewportTopY, viewportRightX, viewportBottomY);
                float count = (int) ((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());
                if (count > getMaxVisibleEntryCount()) {
                    mCurrentViewport.right = getMaxVisibleEntryCount() / (float)getEntryCount() + mCurrentViewport.left;
                }

                if(mCurrentViewport.right < Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;

            } else {
                // 不足一屏 并且缩放到一屏时 能继续缩小
                if (getEntryCount() < getMaxVisibleEntryCount() && mZoomer.getCurrZoom() < 0f && mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                    // 向左缩进
                    mZoomFocalPoint.set(mCurrentViewport.left, (mCurrentViewport.bottom + mCurrentViewport.top) / 2);

                    float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left) / mScrollerStartViewport.width();

                    float viewportRightX = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);

                    mCurrentViewport.set(Viewport.AXIS_X_MIN, viewportTopY, viewportRightX, viewportBottomY);
                } else {
                    // 优先向右缩进
                    mZoomFocalPoint.set(mCurrentViewport.right, (mCurrentViewport.bottom + mCurrentViewport.top) / 2);
                    float pointWithinViewportX = (mZoomFocalPoint.x - mScrollerStartViewport.left) / mScrollerStartViewport.width();
                    float viewportLeftX = mZoomFocalPoint.x - newWidth * pointWithinViewportX;
                    float viewportRightX = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX);
                    mCurrentViewport.set(viewportLeftX, viewportTopY, viewportRightX, viewportBottomY);
                    if(mCurrentViewport.left < Viewport.AXIS_X_MIN) mCurrentViewport.left = Viewport.AXIS_X_MIN;

                    if(mCurrentViewport.left == Viewport.AXIS_X_MIN) {
                        mCurrentViewport.right = mCurrentViewport.left + newWidth;
                        if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;
                    }
                    if(mCurrentViewport.right > Viewport.AXIS_X_MAX) mCurrentViewport.right = Viewport.AXIS_X_MAX;

                    float count = (int) ((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());
                    if (count > getMaxVisibleEntryCount()) {
                        mCurrentViewport.left = mCurrentViewport.right - getMaxVisibleEntryCount() / (float)getEntryCount();
                    }
                    if (count < getMinVisibleEntryCount()) {
                        mCurrentViewport.left = mCurrentViewport.right - getMinVisibleEntryCount() / (float)getEntryCount();
                    }
                }
            }

            currentVisibleEntryCount = Math.round((mCurrentViewport.right - mCurrentViewport.left) * getEntryCount());

            mCurrentViewport.constrainViewport();

            if(mScaleListener != null) {
                mScaleListener.onScale(mCurrentViewport);
                mScaleListener.onScaleEnd(mCurrentViewport);
            }

            needsInvalidate = true;
        }

        if (needsInvalidate) {
            triggerViewportChange();
            Log.d("JZChart", "triggerViewportChange from= computeScroll");
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
                isTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                Log.d("JZChart", "onTouchEvent->ACTION_UP");
                mIsLongPress = false;
                isTouching = false;
                if (!isScaling) {
                    handleNoComputeScrollOffsetLoadMore();
                    postInvalidateOnAnimation();
                }
                isScaling = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d("JZChart", "onTouchEvent->ACTION_CANCEL");
                mIsLongPress = false;
                isTouching = false;
                isScaling = false;
                postInvalidateOnAnimation();
                break;
        }

        mMultipleTouch = event.getPointerCount() > 1;
        mScaleGestureDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void handleNoComputeScrollOffsetLoadMore() {
        if (!canScroll()) return;
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

    public void setOnEntryClickListener(OnSingleEntryClickListener onEntryClickListener) {
        this.onEntryClickListener = onEntryClickListener;
    }

    public OnSingleEntryClickListener getOnEntryClickListener() {
        return onEntryClickListener;
    }

    public void finishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }

    public boolean isScrolling() {
        return !mScroller.isFinished();
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

    public void setDecimalDigitsNumber(int decimalDigitsNumber) {
        this.decimalDigitsNumber = decimalDigitsNumber;
    }

    public int getDecimalDigitsNumber() {
        return this.decimalDigitsNumber;
    }

    public void setShowWaterMark(boolean showWaterMark) {
        this.showWaterMark = showWaterMark;
    }

    public boolean isShowWaterMark() {
        return this.showWaterMark;
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
    }

    public boolean isNightMode() {
        return this.isNightMode;
    }

    /**
     * 是否是多指触控
     * @return
     */
    public boolean isMultipleTouch() {
        return mMultipleTouch;
    }

    // <editor-fold desc="EntryCount 控制">    ----------------------------------------------------------
    private int maxVisibleEntryCount = 250;

    private int minVisibleEntryCount = 15;

    private int defaultVisibleEntryCount = -1;

    private int currentVisibleEntryCount = -1;

    private int entryCount = 0;

    public int getMaxVisibleEntryCount() {
        return maxVisibleEntryCount;
    }

    public void setMaxVisibleEntryCount(int maxVisibleEntryCount) {
        this.maxVisibleEntryCount = maxVisibleEntryCount;
    }

    public int getMinVisibleEntryCount() {
        return minVisibleEntryCount;
    }

    public void setMinVisibleEntryCount(int minVisibleEntryCount) {
        this.minVisibleEntryCount = minVisibleEntryCount;
    }

    public int getDefaultVisibleEntryCount() {
        return defaultVisibleEntryCount;
    }

    public void setDefaultVisibleEntryCount(int defaultVisibleEntryCount) {
        this.defaultVisibleEntryCount = defaultVisibleEntryCount;
        setCurrentVisibleEntryCount(defaultVisibleEntryCount);
    }

    public int getCurrentVisibleEntryCount() {
        return currentVisibleEntryCount;
    }

    public void setCurrentVisibleEntryCount(int currentVisibleEntryCount) {
        this.currentVisibleEntryCount = currentVisibleEntryCount;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(int entryCount) {
        this.entryCount = entryCount;
    }

    // </editor-fold desc="EntryCount 控制">    ----------------------------------------------------------

    // <editor-fold desc="十字光标 配置">    ----------------------------------------------------------

    // 隐藏水平交叉线 便于控制
    private boolean hideHorizontalHighlight = false;

    // 是否需要展示水平交叉线
    private boolean enableHorizontalHighlight = false;

    // 隐藏垂直交叉线 便于控制
    private boolean hideVerticalHighlight = false;

    // 是否需要展示垂直交叉线
    private boolean enableVerticalHighlight = false;

    // 是否需要展示水平交叉线左边文本
    private boolean enableHighlightLeftText = false;

    // 是否需要展示水平交叉线右边文本
    private boolean enableHighlightRightText = false;

    // 是否需要展示垂直交叉线底部文本
    private boolean enableHighlightBottomText = false;

    // 交叉线文本大小
    private float mHighlightTextSize;

    // 交叉线文本颜色
    private int mHighlightTextColor = Color.TRANSPARENT;

    // 交叉线文本背景
    private int mHighlightTextBgColor = Color.TRANSPARENT;

    // 交叉线文本背景高度
    private int mHighlightTextBgHeight;

    public boolean isHideHorizontalHighlight() {
        return hideHorizontalHighlight;
    }

    public void setHideHorizontalHighlight(boolean hideHorizontalHighlight) {
        this.hideHorizontalHighlight = hideHorizontalHighlight;
    }

    public boolean isEnableHorizontalHighlight() {
        return enableHorizontalHighlight;
    }

    public void setEnableHorizontalHighlight(boolean enableHorizontalHighlight) {
        this.enableHorizontalHighlight = enableHorizontalHighlight;
    }

    public boolean isHideVerticalHighlight() {
        return hideVerticalHighlight;
    }

    public void setHideVerticalHighlight(boolean hideVerticalHighlight) {
        this.hideVerticalHighlight = hideVerticalHighlight;
    }

    public boolean isEnableVerticalHighlight() {
        return enableVerticalHighlight;
    }

    public void setEnableVerticalHighlight(boolean enableVerticalHighlight) {
        this.enableVerticalHighlight = enableVerticalHighlight;
    }

    public boolean isEnableHighlightLeftText() {
        return enableHighlightLeftText;
    }

    public void setEnableHighlightLeftText(boolean enableHighlightLeftText) {
        this.enableHighlightLeftText = enableHighlightLeftText;
    }

    public boolean isEnableHighlightRightText() {
        return enableHighlightRightText;
    }

    public void setEnableHighlightRightText(boolean enableHighlightRightText) {
        this.enableHighlightRightText = enableHighlightRightText;
    }

    public boolean isEnableHighlightBottomText() {
        return enableHighlightBottomText;
    }

    public void setEnableHighlightBottomText(boolean enableHighlightBottomText) {
        this.enableHighlightBottomText = enableHighlightBottomText;
    }

    public void setHighlightTextSize(float textSize) {
        this.mHighlightTextSize = textSize;
    }

    public float getHighlightTextSize() {
        return this.mHighlightTextSize;
    }

    public int getHighlightTextColor() {
        return mHighlightTextColor;
    }

    public void setHighlightTextColor(int mHighlightTextColor) {
        this.mHighlightTextColor = mHighlightTextColor;
    }

    public int getHighlightTextBgColor() {
        return mHighlightTextBgColor;
    }

    public void setHighlightTextBgColor(int mHighlightTextBgColor) {
        this.mHighlightTextBgColor = mHighlightTextBgColor;
    }

    public int getHighlightTextBgHeight() {
        return mHighlightTextBgHeight;
    }

    public void setHighlightTextBgHeight(int mHighlightTextBgHeight) {
        this.mHighlightTextBgHeight = mHighlightTextBgHeight;
    }

    // </editor-fold desc="十字光标 配置">    ----------------------------------------------------------

    // <editor-fold desc="画线工具">    ----------------------------------------------------------
    /**
     * 是否开启画线
     */
    private boolean openDrawLine = false;

    /**
     * 画线状态
     */
    private DrawLineTouchState drawLineTouchState = DrawLineTouchState.none;

    private DrawLineDataSet preDrawLineDataSet = new DrawLineDataSet(DrawLineType.ltNone);

    private OnDrawLineTouchListener onDrawLineTouchListener;

    private OnDrawLineCompleteListener onDrawLineCompleteListener;

    public boolean isOpenDrawLine() {
        return openDrawLine;
    }

    public void setOpenDrawLine(boolean openDrawLine) {
        this.openDrawLine = openDrawLine;
    }

    public DrawLineTouchState getDrawLineTouchState() {
        if (!isOpenDrawLine()) return DrawLineTouchState.none;
        return drawLineTouchState;
    }

    public void setDrawLineTouchState(DrawLineTouchState drawLineTouchState) {
        if (!isOpenDrawLine()) return;
        this.drawLineTouchState = drawLineTouchState;
    }

    public boolean isDrawingLine() {
        return isOpenDrawLine() && (getDrawLineTouchState() == DrawLineTouchState.first || getDrawLineTouchState() == DrawLineTouchState.second);
    }

    public DrawLineDataSet getPreDrawLineDataSet() {
        return this.preDrawLineDataSet;
    }

    public void setPreDrawLineDataSet(DrawLineDataSet dataSet) {
        this.preDrawLineDataSet = dataSet;
    }

    public void setDrawLineComplete(PointF point1, PointF point2) {
        if (getOnDrawLineCompleteListener() != null) {
            getOnDrawLineCompleteListener().onComplete(point1, point2, preDrawLineDataSet.getLineType());
        }
    }

    public OnDrawLineTouchListener getDrawLineTouchListener() {
        return this.onDrawLineTouchListener;
    }

    public void setOnDrawLineTouchListener(OnDrawLineTouchListener mOnDrawLineTouchListener) {
        this.onDrawLineTouchListener = mOnDrawLineTouchListener;
    }

    public OnDrawLineCompleteListener getOnDrawLineCompleteListener() {
        return onDrawLineCompleteListener;
    }

    public void setOnDrawLineCompleteListener(OnDrawLineCompleteListener onDrawLineCompleteListener) {
        this.onDrawLineCompleteListener = onDrawLineCompleteListener;
    }

    private void onPressDrawLine(MotionEvent e) {
        // 当前 画线类型
        int type = getPreDrawLineDataSet().getLineType();
        // 没有设置类型/没有设置监听 不进行状态更新
        if (isOpenDrawLine() && type != 0 && getDrawLineTouchListener() != null) {
            PointF point = new PointF(e.getX(), e.getY());
            DrawLineTouchState state = getDrawLineTouchState();
            if (state == DrawLineTouchState.none) {
                // 第一步
                setDrawLineTouchState(DrawLineTouchState.first);
                getDrawLineTouchListener().onTouch(DrawLineTouchState.first, point, type);
            } else if (state == DrawLineTouchState.first) {
                // 第二步
                setDrawLineTouchState(DrawLineTouchState.second);
                getDrawLineTouchListener().onTouch(DrawLineTouchState.second, point, type);
            }else if (state == DrawLineTouchState.second) {
                // 完成
                setDrawLineTouchState(DrawLineTouchState.complete);
                getDrawLineTouchListener().onTouch(DrawLineTouchState.complete, point, type);
            }else if (state == DrawLineTouchState.complete) {
                setDrawLineTouchState(DrawLineTouchState.drag);
                getDrawLineTouchListener().onTouch(DrawLineTouchState.drag, point, type);
            }
        }
    }

    // </editor-fold desc="画线工具">    ----------------------------------------------------------

    protected boolean canScroll() {
        return getEntryCount() >= getCurrentVisibleEntryCount();
    }
}


