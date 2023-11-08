package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.annotation.FloatRange
import androidx.core.view.GestureDetectorCompat
import cn.jingzhuan.lib.chart.Zoomer
import cn.jingzhuan.lib.chart.utils.ForceAlign
import cn.jingzhuan.lib.chart3.Viewport
import cn.jingzhuan.lib.chart3.event.OnBottomAreaClickListener
import cn.jingzhuan.lib.chart3.event.OnDrawLineListener
import cn.jingzhuan.lib.chart3.event.OnFlagClickListener
import cn.jingzhuan.lib.chart3.event.OnLoadMoreListener
import cn.jingzhuan.lib.chart3.event.OnRangeChangeListener
import cn.jingzhuan.lib.chart3.event.OnScaleListener
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.event.OnViewportChangeListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_MOVE
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_PRESS
import cn.jingzhuan.lib.chart3.utils.ChartConstant.ZOOM_AMOUNT
import cn.jingzhuan.lib.source.JZScaleGestureDetector
import kotlin.math.abs
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

    private lateinit var mScroller: OverScroller

    private lateinit var mDetector: GestureDetectorCompat

    private lateinit var mScaleDetector: JZScaleGestureDetector

    private var touchPointListener: OnTouchPointListener? = null

    private var internalViewportChangeListener: OnViewportChangeListener? = null

    private var viewportChangeListener: OnViewportChangeListener? = null

    private var loadMoreListener: OnLoadMoreListener? = null

    private var scaleListener: OnScaleListener? = null

    private var bottomFlagsClickListener: OnBottomAreaClickListener? = null

    private var flagClickListener: OnFlagClickListener? = null

    protected var rangeChangeListener: OnRangeChangeListener? = null

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
     * 缩放器
     */
    private lateinit var mZoomer: Zoomer

    private val mZoomFocalPoint = PointF()

    /**
     * 是否在触摸中
     */
    var isTouching = false

    /**
     * 是否正在缩放
     */
    var isScaling = false

    private var stopScale = false

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
     * 不是主动的 光标状态不变
     */
    var isStatic = false

    /**
     * 最大可见数量
     */
    var maxVisibleEntryCount = 250

    /**
     * 最小可见数量
     */
    var minVisibleEntryCount = 15

    /**
     * 当前可见数量 （可滑动可缩放）
     */
    var currentVisibleEntryCount = -1

    /**
     * 总数量
     */
    var totalEntryCount = 0

    /**
     * 缩放乘数
     */
    var scaleSensitivity = 1.05f

    /**
     * 是否显示底部标签组
     */
    var showBottomFlags = false

    var pointWidth = 0f

    // <editor-fold desc="画线工具">    ----------------------------------------------------------
    /**
     * 是否开启画线
     */
    var isOpenDrawLine: Boolean = false

    /**
     * 是否开启画线自动吸附功能 (蜡烛的开高收低)
     */
    var isDrawLineAdsorb: Boolean = true

    var drawLineListener: OnDrawLineListener? = null

    // </editor-fold desc="画线工具">    ---------------------------------------------------------

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    protected open fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        setWillNotDraw(false)
        mDetector = GestureDetectorCompat(context, this)
        mScaleDetector = JZScaleGestureDetector(context, this)
        mScroller = OverScroller(context)
        mZoomer = Zoomer(context)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG, "onDown")
        scrollerStartViewport.set(currentViewport)

        finishScroll()

        val isBottom = bottomRect.contains(e.x.roundToInt(), e.y.roundToInt())

        if (showBottomFlags && bottomFlagsClickListener != null && isBottom && highlightState != HIGHLIGHT_STATUS_INITIAL) {
            bottomFlagsClickListener?.onClick(e.x, e.y)
        }
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG, "onShowPress")
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG, "onLongPress")
        if (isOpenRange) return
        if (isDrawingLine()) return

        if (!isLongPress) isLongPress = true
        if (highlightState != HIGHLIGHT_STATUS_MOVE && highlightState != HIGHLIGHT_STATUS_FOREVER && !isStatic){
            highlightState = HIGHLIGHT_STATUS_MOVE
        }
        onTouchPoint(e)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG, "onSingleTapUp")
        if (isDoubleTapToZoom) return false
        if (isOpenRange) return false
        if (isDrawingLine()) return false

        if (bottomRect.contains(e.x.roundToInt(), e.y.roundToInt())) {
            return false
        }

        if (isClickable && hasOnClickListeners()) {
            // 光标清除
            highlightState = HIGHLIGHT_STATUS_INITIAL
            onHighlightClean()
            performClick()
        } else {
            // 当前按下的位置有效
            if (getEntryIndex(e.x) >= 0) {
                if (isStatic) {
                    onTouchPoint(e)
                } else {
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
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (isDrawingLine()) return false

        if (!isScrollEnable || !canScroll() || isLongPress || isMultipleTouch || isScaling) {
            finishScroll()
            return false
        }

        if (highlightState != HIGHLIGHT_STATUS_FOREVER) {
            onHighlightClean()
        }

        Log.i(TAG, "onScroll")

        /**
         * Pixel offset is the offset in screen pixels, while viewport offset is the
         * offset within the current viewport. For additional information on surface sizes
         * and pixel offsets, see the docs for []. For
         * additional information about the viewport, see the comments for
         * [currentViewport].
         */
        val viewportOffsetX = distanceX.roundToInt() * currentViewport.width() / contentRect.width()

        computeScrollSurfaceSize(mSurfacePoint)

        setViewportBottomLeft(currentViewport.left + viewportOffsetX)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()

        var needsInvalidate = false

        if (mScroller.computeScrollOffset()) {

            // The scroller isn't finished, meaning a fling or programmatic pan operation is currently active.
            computeScrollSurfaceSize(mSurfacePoint)

            val currX = mScroller.currX

//            Log.i(TAG, "computeScroll-> currX=${mScroller.currX} mScroller.isFinished=${mScroller.isFinished}")

            val leftSide = currentViewport.left <= Viewport.AXIS_X_MIN
//            val rightSide = currentViewport.right >= Viewport.AXIS_X_MAX

//            val canScrollX = !leftSide || !rightSide

//            if (canScrollX && currX < 0) {
//                needsInvalidate = true
//            } else if (canScrollX && currX > mSurfacePoint.x - contentRect.width()) {
//                needsInvalidate = true
//            }
            val currXRange = Viewport.AXIS_X_MIN + (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN) * currX / mSurfacePoint.x
            Log.i(TAG, "computeScroll-> currXRange=${currXRange} mScroller.isFinished=${mScroller.isFinished}")
            setViewportBottomLeft(currXRange)

            if (currX < pointWidth * 0.5f && leftSide) {
                Log.w(TAG, "加载更多")
                needsInvalidate = false
                finishScroll()
                if (loadMoreListener != null) {
                    loadMoreListener?.onLoadMore()
                }
            }
        }

        if (mZoomer.computeZoom()) {
            // Performs the zoom since a zoom is in progress (either programmatically or via double-touch).

            val newWidth = (1f - mZoomer.currZoom) * scrollerStartViewport.width()
            val newHeight = (1f - mZoomer.currZoom) * scrollerStartViewport.height()
            val pointWithinViewportY = ((mZoomFocalPoint.y - scrollerStartViewport.top) / scrollerStartViewport.height())

            val viewportTopY = mZoomFocalPoint.y - newHeight * pointWithinViewportY
            val viewportBottomY = mZoomFocalPoint.y + newHeight * (1f - pointWithinViewportY)

            if (!canScroll()) {
                // 向左缩进
                mZoomFocalPoint[currentViewport.left] = (currentViewport.bottom + currentViewport.top) / 2f
                val pointWithinViewportX = (mZoomFocalPoint.x - scrollerStartViewport.left) / scrollerStartViewport.width()
                val viewportRightX = mZoomFocalPoint.x + newWidth * (1f - pointWithinViewportX)
                currentViewport.set(Viewport.AXIS_X_MIN, viewportTopY, viewportRightX, viewportBottomY)

                val count = ((currentViewport.right - currentViewport.left) * totalEntryCount.toFloat()).roundToInt()

                if (count > maxVisibleEntryCount) {
                    currentViewport.right = maxVisibleEntryCount.toFloat() / totalEntryCount.toFloat() + currentViewport.left
                }

                if (currentViewport.right < Viewport.AXIS_X_MAX) currentViewport.right = Viewport.AXIS_X_MAX
            } else {
                // 不足一屏 并且缩放到一屏时 能继续缩小
                if (totalEntryCount < maxVisibleEntryCount && mZoomer.currZoom < 0f && currentViewport.left == Viewport.AXIS_X_MIN) {
                    // 向左缩进
                    mZoomFocalPoint[currentViewport.left] = (currentViewport.bottom + currentViewport.top) / 2f
                    val pointWithinViewportX = (mZoomFocalPoint.x - scrollerStartViewport.left) / scrollerStartViewport.width()
                    val viewportRightX = mZoomFocalPoint.x + newWidth * (1f - pointWithinViewportX)
                    currentViewport.set(Viewport.AXIS_X_MIN, viewportTopY, viewportRightX, viewportBottomY)
                } else {
                    // 优先向右缩进
                    mZoomFocalPoint[currentViewport.right] = (currentViewport.bottom + currentViewport.top) / 2f
                    val pointWithinViewportX = (mZoomFocalPoint.x - scrollerStartViewport.left) / scrollerStartViewport.width()
                    val viewportLeftX = mZoomFocalPoint.x - newWidth * pointWithinViewportX
                    val viewportRightX = mZoomFocalPoint.x + newWidth * (1 - pointWithinViewportX)
                    currentViewport.set(viewportLeftX, viewportTopY, viewportRightX, viewportBottomY)

                    if (currentViewport.left < Viewport.AXIS_X_MIN) currentViewport.left = Viewport.AXIS_X_MIN

                    if (currentViewport.left == Viewport.AXIS_X_MIN) {
                        currentViewport.right = currentViewport.left + newWidth
                        if (currentViewport.right > Viewport.AXIS_X_MAX) currentViewport.right = Viewport.AXIS_X_MAX
                    }

                    if (currentViewport.right > Viewport.AXIS_X_MAX) currentViewport.right = Viewport.AXIS_X_MAX

                    val count = ((currentViewport.right - currentViewport.left) * totalEntryCount).roundToInt()
                    if (count > maxVisibleEntryCount) {
                        currentViewport.left = currentViewport.right - maxVisibleEntryCount.toFloat() / totalEntryCount.toFloat()
                    }

                    if (count < minVisibleEntryCount) {
                        currentViewport.left = currentViewport.right - minVisibleEntryCount.toFloat() / totalEntryCount.toFloat()
                    }
                }
            }

            triggerScale()

            if (scaleListener != null) scaleListener?.onScaleEnd(currentViewport)

            needsInvalidate = true
        }

        if (needsInvalidate) {
            Log.i(TAG, "computeScroll-> needsInvalidate")
            triggerViewportChange()
        }
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        if (!isTouching && isScrollEnable && canScroll() && !isMultipleTouch && !isScaling) {
            Log.i(TAG, "onFling")
            val leftSide = currentViewport.left == Viewport.AXIS_X_MIN
            val rightSide = currentViewport.right == Viewport.AXIS_X_MAX

            if (leftSide && rightSide) {
                if (loadMoreListener != null) {
                    loadMoreListener?.onLoadMore()
                }
                return false
            }

            if (rightSide) return false

            // Flings use math in pixels (as opposed to math based on the viewport).
            computeScrollSurfaceSize(mSurfacePoint)

            Log.i(TAG, "onFling->mSurfacePoint(x,y)=${mSurfacePoint.x},${mSurfacePoint.y}")

            scrollerStartViewport.set(currentViewport)

            val startX: Int = (mSurfacePoint.x * (scrollerStartViewport.left - Viewport.AXIS_X_MIN) / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)).toInt()

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
        if (!isScaleEnable) return false
        if (isLongPress) return false
        if (stopScale) return false
        Log.i(TAG, "onScale")
        isScaling = true

        if (highlightState != HIGHLIGHT_STATUS_FOREVER) {
            onHighlightClean()
        }

        val spanX: Float = mScaleDetector.currentSpan
        var lastSpanX: Float = mScaleDetector.previousSpan

        // 双指距离比上次大，为放大
        val zoomIn = spanX > lastSpanX

        // 双指距离比上次小，为缩小
        val zoomOut = lastSpanX > spanX

        val canZoom = abs(abs(lastSpanX) - abs(spanX)) > 0f

        if (!canZoom) return false

        if (zoomIn) {
            canZoomOut = true
            if (!isCanZoomIn()) return false
        }

        if (zoomOut) {
            canZoomIn = true
            if (!isCanZoomOut()) return false
        }

        var scaleSpanX = lastSpanX

        if (zoomIn) {
            scaleSpanX = spanX * scaleSensitivity
        } else if (zoomOut) {
            lastSpanX *= scaleSensitivity
        }

        val newWidth = if (zoomIn) {
            lastSpanX / scaleSpanX * currentViewport.width()
        } else {
            lastSpanX / spanX * currentViewport.width()
        }

        if (newWidth < currentViewport.width() && currentViewport.width() < 0.001f) {
            return true
        }

        var focusX: Float = mScaleDetector.focusX

        if (zoomOut) focusX *= scaleSensitivity else if (zoomIn) focusX /= scaleSensitivity

        val viewportFocusX = (currentViewport.left + currentViewport.width() * (focusX - contentRect.left) / contentRect.width())

        val ratio = viewportFocusX - newWidth * (focusX - contentRect.left) / contentRect.width()

        if (!canScroll()) {
            currentViewport.left = Viewport.AXIS_X_MIN
            currentViewport.right = currentViewport.right - ratio
            val count: Float = ((currentViewport.right - currentViewport.left) * totalEntryCount).toInt().toFloat()

            if (count > maxVisibleEntryCount) {
                currentViewport.right = maxVisibleEntryCount / totalEntryCount.toFloat() + currentViewport.left
            }

            if (currentViewport.right < Viewport.AXIS_X_MAX)
                currentViewport.right = Viewport.AXIS_X_MAX
        } else {
            // 不足一屏 并且缩放到一屏时 能继续缩小
            if (totalEntryCount < maxVisibleEntryCount && !zoomIn && zoomOut && currentViewport.left == Viewport.AXIS_X_MIN) {
                currentViewport.left = Viewport.AXIS_X_MIN
                currentViewport.right = currentViewport.right - ratio
            } else {
                // 优先向右缩进
                currentViewport.left = ratio
                if (currentViewport.left < Viewport.AXIS_X_MIN) currentViewport.left = Viewport.AXIS_X_MIN

                if (currentViewport.left == Viewport.AXIS_X_MIN) {
                    currentViewport.right = currentViewport.left + newWidth
                    if (currentViewport.right > Viewport.AXIS_X_MAX) currentViewport.right = Viewport.AXIS_X_MAX
                }

                if (currentViewport.right > Viewport.AXIS_X_MAX) currentViewport.right = Viewport.AXIS_X_MAX

                val count: Float = ((currentViewport.right - currentViewport.left) * totalEntryCount).toInt().toFloat()
                if (count > maxVisibleEntryCount) {
                    currentViewport.left = currentViewport.right - maxVisibleEntryCount / totalEntryCount.toFloat()
                }

                if (count < minVisibleEntryCount) {
                    currentViewport.left = currentViewport.right - minVisibleEntryCount / totalEntryCount.toFloat()
                }
            }
        }

        triggerScale()
        triggerViewportChange()

        return true
    }

    override fun onScaleBegin(detector: JZScaleGestureDetector): Boolean {
        if (!isScaleEnable) return false
        if (isLongPress) return false
        if (stopScale) return false
        Log.i(TAG, "onScaleBegin")
        if (scaleListener != null) {
            scaleListener?.onScaleStart(currentViewport)
        }
        return true
    }

    override fun onScaleEnd(detector: JZScaleGestureDetector) {
        Log.i(TAG, "onScaleEnd")
        if (stopScale) return
        if (scaleListener != null) {
            scaleListener?.onScaleEnd(currentViewport)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.i(TAG, "onTouchEvent->ACTION_DOWN")
                isTouching = true
            }

            MotionEvent.ACTION_MOVE -> {
                Log.i(TAG, "onTouchEvent->ACTION_MOVE ")
                //长按之后移动
                if (isLongPress) onLongPress(event)
            }

            MotionEvent.ACTION_UP -> {
                Log.i(TAG, "onTouchEvent->ACTION_UP")
                if (isTouching) isTouching = false
                stopScale = false

                if (isScaling) {
                    isScaling = false
                    return false
                }

                if (isLongPress) {
                    isLongPress = false
                    // 之前是长按 抬起时直接return 不回调 onSingleTapUp
                    return false
                }
                if (isDrawingLine()) {
                    return false
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i(TAG, "onTouchEvent->ACTION_CANCEL")
                if (isScaling) isScaling = false
                if (isLongPress) isLongPress = false
                if (isTouching) isTouching = false
                if (isDrawingLine()) return false
                invalidate()
            }

            else -> {}
        }
        isMultipleTouch = event.pointerCount > 1
        mDetector.onTouchEvent(event)
        if (!stopScale) {
            mScaleDetector.onTouchEvent(event)
        }
        return true
    }

    fun setCurrentViewport(viewport: RectF) {
        currentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom)
        currentViewport.constrainViewport()
        triggerViewportChange()
    }

    fun setStaticCurrentViewport(viewport: RectF) {
        currentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom)
        currentViewport.constrainViewport()
    }

    private fun triggerViewportChange() {
        if (internalViewportChangeListener != null) {
            synchronized(this) {
                try {
                    internalViewportChangeListener?.onViewportChange(currentViewport)
                } catch (e: Exception) {
                    Log.d(TAG, "OnViewportChangeListener", e)
                }
            }
        }
        if (viewportChangeListener != null) {
            synchronized(this) {
                try {
                    viewportChangeListener?.onViewportChange(currentViewport)
                } catch (e: Exception) {
                    Log.d(TAG, "OnViewportChangeListener", e)
                }
            }
        }

        if (isOpenRange) {
            onRangeViewPortChange()
        }

        if (highlightState == HIGHLIGHT_STATUS_FOREVER) {
            onHighlightForever()
        }

        invalidate()
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
    open fun finishScroll() {
        if (!mScroller.isFinished)
            mScroller.forceFinished(true)
    }

    open fun isScrolling(): Boolean {
        return !mScroller.isFinished
    }

    open fun canScroll(): Boolean {
        return totalEntryCount >= currentVisibleEntryCount
    }

    open fun forceStopScale() {
        if (mScaleDetector.isInProgress) {
            stopScale = true
            isScaling = false
        }
    }

    private fun triggerScale() {
        currentVisibleEntryCount = ((currentViewport.right - currentViewport.left) * totalEntryCount).roundToInt()

        currentViewport.constrainViewport()

        if (scaleListener != null) {
            scaleListener?.onScale(currentViewport)
        }
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
        return currentVisibleEntryCount >= minVisibleEntryCount && canZoomIn
    }

    open fun isCanZoomOut(): Boolean {
        return currentVisibleEntryCount <= maxVisibleEntryCount && canZoomOut
    }

    /**
     * Smoothly zooms the lib in one step.
     */
    open fun zoomIn() {
        if (currentVisibleEntryCount <= minVisibleEntryCount) return
        zoom(ZOOM_AMOUNT, ForceAlign.CENTER)
    }

    /**
     * Smoothly zooms the lib out one step.
     */
    open fun zoomOut() {
        if (currentVisibleEntryCount >= maxVisibleEntryCount) return
        zoom(-ZOOM_AMOUNT, ForceAlign.CENTER)
    }

    open fun zoom(scalingFactor: Float, @ForceAlign.XForce forceAlignX: Int) {
        scrollerStartViewport.set(currentViewport)
        mZoomer.forceFinished(true)
        mZoomer.startZoom(scalingFactor)
        val forceX = when (forceAlignX) {
            ForceAlign.LEFT -> currentViewport.left
            ForceAlign.RIGHT -> currentViewport.right
            ForceAlign.CENTER -> (currentViewport.right + currentViewport.left) / 2
            else -> (currentViewport.right + currentViewport.left) / 2
        }
        mZoomFocalPoint.set(
            forceX,
            (currentViewport.bottom + currentViewport.top) / 2
        )
        triggerViewportChange()
    }

    open fun zoomIn(@ForceAlign.XForce forceAlignX: Int) {
        if (currentVisibleEntryCount <= minVisibleEntryCount) return
        zoom(ZOOM_AMOUNT, forceAlignX)
    }

    open fun zoomOut(@ForceAlign.XForce forceAlignX: Int) {
        if (currentVisibleEntryCount >= maxVisibleEntryCount) return
        zoom(-ZOOM_AMOUNT, forceAlignX)
    }

    fun computeZoom(): Boolean {
        return mZoomer.computeZoom()
    }

    open fun moveLeft() {
        moveLeft(0.2f)
    }

    open fun moveRight() {
        moveRight(0.2f)
    }

    open fun moveLeft(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        computeScrollSurfaceSize(mSurfacePoint)
        scrollerStartViewport.set(currentViewport)
        val moveDistance = contentRect.width() * percent
        val startX =
            (mSurfacePoint.x * (scrollerStartViewport.left - Viewport.AXIS_X_MIN) / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)).toInt()
        if (!mScroller.isFinished) {
            mScroller.forceFinished(true)
        }
        mScroller.startScroll(startX, 0, -moveDistance.toInt(), 0, 300)
        postInvalidateOnAnimation()
    }

    open fun moveRight(@FloatRange(from = 0.0, to = 1.0) percent: Float) {
        computeScrollSurfaceSize(mSurfacePoint)
        scrollerStartViewport.set(currentViewport)
        val moveDistance: Float = contentRect.width() * percent
        val startX =
            (mSurfacePoint.x * (scrollerStartViewport.left - Viewport.AXIS_X_MIN) / (Viewport.AXIS_X_MAX - Viewport.AXIS_X_MIN)).toInt()
        if (!mScroller.isFinished) {
            mScroller.forceFinished(true)
        }
        mScroller.startScroll(startX, 0, moveDistance.toInt(), 0, 300)
        postInvalidateOnAnimation()
    }

    open fun isDrawingLine(): Boolean {
        return isOpenDrawLine
    }


    fun addOnTouchPointListener(listener: OnTouchPointListener) {
        this.touchPointListener = listener
    }

    fun setViewportChangeListener(listener: OnViewportChangeListener) {
        this.viewportChangeListener = listener
    }

    internal fun addInternalViewportChangeListener(listener: OnViewportChangeListener) {
        this.internalViewportChangeListener = listener
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener) {
        this.loadMoreListener = listener
    }

    fun setOnScaleListener(listener: OnScaleListener) {
        this.scaleListener = listener
    }

    fun setOnBottomFlagsClickListener(listener: OnBottomAreaClickListener) {
        this.bottomFlagsClickListener = listener
    }

    fun addOnFlagClickListener(listener: OnFlagClickListener) {
        this.flagClickListener = listener
    }

    fun setOnDrawLineListener(listener: OnDrawLineListener) {
        this.drawLineListener = listener
    }

    fun onFlagCallback(type: Int, index: Int) {
        if (flagClickListener != null) {
            flagClickListener?.onClick(type, index)
        }
    }

    fun setOnRangeChangeListener(listener: OnRangeChangeListener) {
        this.rangeChangeListener = listener
    }

    fun loadMore() {
        if (loadMoreListener != null) {
            loadMoreListener?.onLoadMore()
        }
    }

    /**
     * viewport改变 光标状态为forever时 光标位置对应更新
     */
    abstract fun onHighlightForever()

    /**
     * viewport改变 区间统计位置对应更新
     */
    abstract fun onRangeViewPortChange()

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
