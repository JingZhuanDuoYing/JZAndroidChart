package cn.jingzhuan.lib.chart3.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart3.state.HighlightState;
import cn.jingzhuan.lib.source.JZScaleGestureDetector;

/**
 * @since 2023-09-01
 * created by lei
 * 可以滑动和放大的ChartView
 */
public abstract class ScrollAndScaleView extends View implements GestureDetector.OnGestureListener,
        JZScaleGestureDetector.OnScaleGestureListener {

    private final static String TAG = "ScrollAndScaleView";

    protected int mScrollX = 0;

    private OverScroller mScroller;

    protected GestureDetectorCompat mDetector;

    protected JZScaleGestureDetector mScaleDetector;

    protected Viewport mCurrentViewport = new Viewport();


    protected Rect mContentRect = new Rect();

    protected Rect mBottomRect = new Rect();

    private boolean mIsTouching = false;

    /**
     * 双击放大
     */
    private boolean mDoubleTapToZoom = false;

    /**
     * 是否长按
     */
    protected boolean mIsLongPress = false;

    /**
     * 是否能够缩放
     */
    private boolean mScaleEnable = true;

    /**
     * 是否多指操作
     */
    private boolean mMultipleTouch = false;

    /**
     * 是否能够滑动
     */
    private boolean mScrollEnable = true;

    /**
     * 是否开启区间统计
     */
    private boolean mIsOpenRange = false;

    /**
     * 是否触发按下位置的回调
     */
    private boolean mTouchPointEnable = true;

    /**
     * 十字光标状态
     */
    private HighlightState mHighlightState = HighlightState.initial;

    public ScrollAndScaleView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScrollAndScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScrollAndScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public ScrollAndScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    protected void init(AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);
        mDetector = new GestureDetectorCompat(getContext(), this);
        mScaleDetector = new JZScaleGestureDetector(getContext(), this);
        mScroller = new OverScroller(getContext());
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown");
        finishScroll();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(TAG, "onSingleTapUp");
        onHighlightStateChange(e);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(TAG, "onScroll");
        if (!mIsLongPress && !isMultipleTouch()) {
            scrollBy(Math.round(distanceX), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG, "onLongPress");
        mIsLongPress = true;
        onTouchPoint(e);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX - Math.round(x), 0);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!isScrollEnable()) {
            mScroller.forceFinished(true);
            return;
        }
        int oldX = mScrollX;
        mScrollX = x;
        onScrollChanged(mScrollX, 0, oldX, 0);
        invalidate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i(TAG, "onFling");
        if (!isTouching() && isScrollEnable()) {
        }
        return true;
    }

    @Override
    public boolean onScale(JZScaleGestureDetector detector) {
        Log.i(TAG, "onScale");
        return false;
    }

    @Override
    public boolean onScaleBegin(JZScaleGestureDetector detector) {
        Log.i(TAG, "onScaleBegin");
        return false;
    }

    @Override
    public void onScaleEnd(JZScaleGestureDetector detector) {
        Log.i(TAG, "onScaleEnd");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");

        if (event.getPointerCount() > 1) mIsLongPress = false;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mIsTouching = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //长按之后移动
                if (mIsLongPress) onLongPress(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsTouching = false;
                if (mIsLongPress) {
                    mIsLongPress = false;
                    // 之前是长按 抬起时直接return 不回调 onSingleTapUp
                    return false;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsLongPress = false;
                mIsTouching = false;
                invalidate();
                break;
            default:
                break;
        }
        mMultipleTouch = event.getPointerCount() > 1;
        this.mDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 手指按下时 十字光标状态的更新
     * press、move、forever 都需要绘制十字光标
     */
    private void onHighlightStateChange(MotionEvent event) {
        if (mHighlightState == HighlightState.initial) {
            // 按下前为initial 按下后设置为Press
            setHighlightState(HighlightState.press);
            onTouchPoint(event);
        } else if (mHighlightState == HighlightState.press || mHighlightState == HighlightState.move) {
            // 按下前为press || move 按下后设置为initial
            setHighlightState(HighlightState.initial);
        } else {
            onTouchPoint(event);
        }
    }

    /**
     * 设置十字光标状态
     */
    public void setHighlightState(HighlightState highlightState) {
        mHighlightState = highlightState;
    }

    public HighlightState getHighlightState() {
        return mHighlightState;
    }

    /**
     * 设置是否触发按下位置的回调
     */
    public void setTouchPointEnable(boolean touchPointEnable) {
        mTouchPointEnable = touchPointEnable;
    }

    /**
     * 是否开启区间统计
     */
    public void setIsOpenRange(boolean openRange) {
        mIsOpenRange = openRange;
    }

    public boolean isIsOpenRange() {
        return mIsOpenRange;
    }

    /**
     * 设置是否能双击放大
     */
    public void setDoubleTapToZoom(boolean mDoubleTapToZoom) {
        this.mDoubleTapToZoom = mDoubleTapToZoom;
    }

    public boolean isDoubleTapToZoom() {
        return mDoubleTapToZoom;
    }


    /**
     * 设置是否可以滑动
     */
    public void setScrollEnable(boolean scrollEnable) {
        mScrollEnable = scrollEnable;
    }

    public boolean isScrollEnable() {
        return mScrollEnable;
    }

    /**
     * 设置是否可以缩放
     */
    public void setScaleEnable(boolean scaleEnable) {
        mScaleEnable = scaleEnable;
    }

    public boolean isScaleEnable() {
        return mScaleEnable;
    }

    /**
     * 是否是多指触控
     */
    public boolean isMultipleTouch() {
        return mMultipleTouch;
    }

    /**
     * 设置是否长按状态
     */
    public void setIsLongPress(boolean isLongPress) {
        mIsLongPress = isLongPress;
    }

    /**
     * 是否在触摸中
     */
    public boolean isTouching() {
        return mIsTouching;
    }

    /**
     * 滑动还未完成 强制停止
     */
    public void finishScroll() {
        if (!mScroller.isFinished()) mScroller.forceFinished(true);
    }

    /**
     * 按下位置的更新
     */
    abstract void onTouchPoint(MotionEvent event);

    /**
     * 当前Viewport
     */
    public Viewport getCurrentViewport() {
        return mCurrentViewport;
    }

    public Rect getContentRect() {
        return mContentRect;
    }

    public Rect getBottomRect() {
        return mBottomRect;
    }

}
