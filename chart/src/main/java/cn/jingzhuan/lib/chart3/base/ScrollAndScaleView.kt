package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import cn.jingzhuan.lib.chart.utils.ForceAlign
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.event.OnLoadMoreListener
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.event.OnViewportChangeListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_MOVE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_PRESS
import cn.jingzhuan.lib.source.JZScaleGestureDetector
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * @since 2023-09-01
 * created by lei
 * 可以滑动和放大的ChartView
 */
abstract class ScrollAndScaleView : View, GestureDetector.OnGestureListener,
    JZScaleGestureDetector.OnScaleGestureListener {

    private var mDistanceX = 0

    private lateinit var mScroller: OverScroller

    private lateinit var mDetector: GestureDetectorCompat

    private lateinit var mScaleDetector: JZScaleGestureDetector

    private var touchPointListener: OnTouchPointListener? = null

    private var viewportChangeListener: OnViewportChangeListener? = null

    private var loadMoreListener: OnLoadMoreListener? = null

    private val mSurfacePoint = Point()

    /**
     * 当前Viewport
     */
    var currentViewport = Viewport()

    /**
     * 内容区域
     */
    var contentRect = Rect()

    /**
     * 底部label区域
     */
    var bottomRect = Rect()

    /**
     * 仅用于 zooms and flings.
     */
    private val scrollerStartViewport = RectF()

    /**
     * 是否在触摸中
     */
    var isTouching = false

    /**
     * 双击放大
     */
    var isDoubleTapToZoom = false

    /**
     * 是否长按
     */
    var isLongPress = false

    /**
     * 是否能够缩放
     */
    var isScaleEnable = true

    /**
     * 是否多指操作
     */
    var isMultipleTouch = false

    /**
     * 光标水平线是否跟随手指
     */
    var isFollowFingerY = true

    /**
     * 是否能够滑动
     */
    var isScrollEnable = true

    /**
     * 是否能够显示光标
     */
    var isHighlightEnable = true

    /**
     * 是否能够放大
     */
    var canZoomIn = true

    /**
     * 是否能够缩小
     */
    var canZoomOut = true

    /**
     * 是否开启区间统计
     */
    var isOpenRange = false

    /**
     * 是否触发按下位置的回调
     */
    var touchPointEnable = true

    /**
     * 十字光标状态
     */
    var highlightState = HIGHLIGHT_STATUS_INITIAL

    /**
     * 最大可见数量
     */
    var maxVisibleEntryCount = 250

    /**
     * 最小可见数量
     */
    var minVisibleEntryCount = 15

    /**
     * 当前可见数量
     */
    var currentVisibleEntryCount = -1

    /**
     * 总数量
     */
    var totalEntryCount = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    protected open fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        setWillNotDraw(false)
        mDetector = GestureDetectorCompat(context, this)
        mScaleDetector = JZScaleGestureDetector(context, this)
        mScroller = OverScroller(context)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG, "onDown")
        scrollerStartViewport.set(currentViewport)
        finishScroll()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG, "onShowPress")
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG, "onLongPress")
        if (isOpenRange) return
        if (!isLongPress) isLongPress = true
        if (highlightState != HIGHLIGHT_STATUS_MOVE){
            highlightState = HIGHLIGHT_STATUS_MOVE
        }
        onTouchPoint(e)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG, "onSingleTapUp")
        if (isDoubleTapToZoom) return false
        if (isOpenRange) return false

        if (isClickable && hasOnClickListeners()) {
            // 光标清除
            highlightState = HIGHLIGHT_STATUS_INITIAL
            onHighlightClean()
            performClick()
        } else {
            // 当前按下的位置有效
            if (getEntryIndex(e.x) > 0) {
                when (highlightState) {
                    HIGHLIGHT_STATUS_INITIAL -> {
                        highlightState = HIGHLIGHT_STATUS_PRESS
                        onTouchPoint(e)
                    }
                    HIGHLIGHT_STATUS_FOREVER -> {
                        onTouchPoint(e)
                    }
                    else -> {
                        highlightState = HIGHLIGHT_STATUS_INITIAL
                        onHighlightClean()
                    }
                }
            } else {
                highlightState = HIGHLIGHT_STATUS_INITIAL
                onHighlightClean()
                performClick()
            }
        }

        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (!isScrollEnable) {
            mScroller.forceFinished(true)
            return false
        }

        if (!isLongPress && !isMultipleTouch) {
            Log.i(TAG, "onScroll")

            mDistanceX = distanceX.roundToInt()

            /**
             * Pixel offset is the offset in screen pixels, while viewport offset is the
             * offset within the current viewport. For additional information on surface sizes
             * and pixel offsets, see the docs for []. For
             * additional information about the viewport, see the comments for
             * [currentViewport].
             */
            val viewportOffsetX: Float = mDistanceX * currentViewport.width() / contentRect.width()

            computeScrollSurfaceSize(mSurfacePoint)

            setViewportBottomLeft(currentViewport.left + viewportOffsetX)
            return true
        }
        return false
    }

    override fun computeScroll() {
        super.computeScroll()

        var needsInvalidate = false

        if (mScroller.computeScrollOffset()) {

            // The scroller isn't finished, meaning a fling or programmatic pan operation is currently active.
            computeScrollSurfaceSize(mSurfacePoint)

            val currX = mScroller.currX

            Log.i(TAG, "computeScroll-> currX=${mScroller.currX} mScroller.isFinished=${mScroller.isFinished}")

            val leftSide = currentViewport.left <= Viewport.AXIS_X_MIN
            val rightSide = currentViewport.right >= Viewport.AXIS_X_MAX

            val canScrollX = !leftSide || !rightSide

            if (canScrollX && currX < 0) {
                needsInvalidate = true
            } else if (canScrollX && currX > mSurfacePoint.x - contentRect.width()) {
                needsInvalidate = true
            }
            val currXRange = Viewport.AXIS_X_MIN + (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN) * currX / mSurfacePoint.x
            setViewportBottomLeft(currXRange)

            if (currX <= 0 && leftSide) {
                Log.w(TAG, "加载更多")
                needsInvalidate = false
                mScroller.forceFinished(true)
                if (loadMoreListener != null) {
                    loadMoreListener?.onLoadMore()
                }
            }
        }

        if (needsInvalidate) {
            Log.i(TAG, "computeScroll1")
            triggerViewportChange()
        }
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        if (!isTouching && isScrollEnable) {
            Log.i(TAG, "onFling")
            val rightSide = currentViewport.right == Viewport.AXIS_X_MAX
            if (rightSide) return false

            // Flings use math in pixels (as opposed to math based on the viewport).
            computeScrollSurfaceSize(mSurfacePoint)

            Log.i(TAG, "onFling->mSurfacePoint(x,y)=${mSurfacePoint.x},${mSurfacePoint.y}")

            scrollerStartViewport.set(currentViewport)

            val startX: Int = (mSurfacePoint.x * (scrollerStartViewport.left - Viewport.AXIS_X_MIN) / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)).toInt()

//            mScroller.forceFinished(true)

            mScroller.fling(
                startX,
                0, -velocityX.roundToInt(),
                0,
                0, mSurfacePoint.x - contentRect.width(),
                0, mSurfacePoint.y - contentRect.height(),
                contentRect.width() / 2,
                0
            )

            postInvalidateOnAnimation()
        }
        return true
    }

    override fun onScale(detector: JZScaleGestureDetector): Boolean {
        Log.i(TAG, "onScale")
        return false
    }

    override fun onScaleBegin(detector: JZScaleGestureDetector): Boolean {
        Log.i(TAG, "onScaleBegin")
        return false
    }

    override fun onScaleEnd(detector: JZScaleGestureDetector) {
        Log.i(TAG, "onScaleEnd")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i(TAG, "onTouchEvent")

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "onTouchEvent->ACTION_DOWN")
                isTouching = true
            }

            MotionEvent.ACTION_MOVE ->{
                Log.i(TAG, "onTouchEvent->ACTION_MOVE")
                //长按之后移动
                if (isLongPress) onLongPress(event)
            }

            MotionEvent.ACTION_POINTER_UP ->{
                Log.i(TAG, "onTouchEvent->ACTION_POINTER_UP")
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent->ACTION_UP")
                isTouching = false
                if (isLongPress) {
                    isLongPress = false
                    // 之前是长按 抬起时直接return 不回调 onSingleTapUp
                    return false
                }
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "onTouchEvent->ACTION_CANCEL")
                isLongPress = false
                isTouching = false
                invalidate()
            }

            else -> {}
        }
        isMultipleTouch = event.pointerCount > 1
        mDetector.onTouchEvent(event)
        mScaleDetector.onTouchEvent(event)
        return true
    }

    fun setCurrentViewport(viewport: RectF) {
        currentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom)
        currentViewport.constrainViewport()
        triggerViewportChange()
    }

    protected open fun triggerViewportChange() {
        if (viewportChangeListener != null) {
            synchronized(this) {
                try {
                    viewportChangeListener?.onViewportChange(currentViewport)
                } catch (e: Exception) {
                    Log.d(TAG, "OnViewportChangeListener", e)
                }
            }
        }
        postInvalidateOnAnimation()
    }

    /**
     * Sets the current viewport (defined by [currentViewport]) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position, and thus
     * the bottom of the [currentViewport] rectangle. For more details on why top and
     * bottom are flipped, see [currentViewport].
     */
    private fun setViewportBottomLeft(x: Float) {
        /**
         * Constrains within the scroll range. The scroll range is simply the viewport extremes
         * (AXIS_X_MAX, etc.) minus the viewport size. For example, if the extrema were 0 and 10,
         * and the viewport size was 2, the scroll range would be 0 to 8.
         */

        val curWidth: Float = currentViewport.width()
        val left = max(Viewport.AXIS_X_MIN, min(x, Viewport.AXIS_X_MAX - curWidth))

        if (currentViewport.left == left && currentViewport.right == left + curWidth) {
            return
        }

        val leftSide = currentViewport.left <= Viewport.AXIS_X_MIN
        val rightSide = currentViewport.right >= Viewport.AXIS_X_MAX

        if (leftSide && currentViewport.left == left) {
            return
        }

        if (rightSide && currentViewport.right == left + curWidth) {
            return
        }

        currentViewport.left = left
        currentViewport.right = left + curWidth

        currentViewport.constrainViewport()

        Log.i(TAG, "setViewportBottomLeft.left=${currentViewport.left}, currentViewport.right=${currentViewport.right}")
        triggerViewportChange()
    }

    /**
     * Computes the current scrollable surface size, in pixels. For example, if the entire lib
     * area is visible, this is simply the current size of [contentRect]. If the lib
     * is zoomed in 200% in both directions, the returned size will be twice as large horizontally
     * and vertically.
     */
    private fun computeScrollSurfaceSize(out: Point) {
        out.x = (contentRect.width() * (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)
                / currentViewport.width()).toInt()
        out.y = (contentRect.height() * (Viewport.AXIS_Y_MAX - Viewport.AXIS_Y_MIN)
                / currentViewport.height()).toInt()
    }

    /**
     * 滑动还未完成 强制结束
     */
    fun finishScroll() {
        if (!mScroller.isFinished)
            mScroller.forceFinished(true)
    }

    /**
     * 按下位置的更新
     */
    private fun onTouchPoint(event: MotionEvent) {
        if (touchPointListener != null) {
            if (!isMultipleTouch && touchPointEnable) {
                touchPointListener?.touch(event.x, event.y)
            }
        }
    }

    open fun isCanZoomIn(): Boolean {
        return canZoomIn
    }

    open fun isCanZoomOut(): Boolean {
        return canZoomOut
    }

    open fun zoomIn(@ForceAlign.XForce forceAlignX: Int) {

    }

    open fun zoomOut(@ForceAlign.XForce forceAlignX: Int) {

    }

    fun addOnTouchPointListener(listener: OnTouchPointListener) {
        this.touchPointListener = listener
    }

    fun setViewportChangeListener(listener: OnViewportChangeListener) {
        this.viewportChangeListener = listener
    }

    open fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.loadMoreListener = listener
    }

    /**
     * 获取下标
     */
    abstract fun getEntryIndex(x: Float): Int

    /**
     * 获取X
     */
    abstract fun getEntryX(index: Int): Float

    /**
     * 清除十字光标
     */
    abstract fun onHighlightClean()

    companion object {
        private const val TAG = "ScrollAndScaleView"
    }
}
