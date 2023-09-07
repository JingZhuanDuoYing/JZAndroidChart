package cn.jingzhuan.lib.chart3.base

import android.content.Context
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
import cn.jingzhuan.lib.chart3.event.OnTouchPointListener
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_FOREVER
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_INITIAL
import cn.jingzhuan.lib.chart3.utils.ChartConstant.HIGHLIGHT_STATUS_PRESS
import cn.jingzhuan.lib.source.JZScaleGestureDetector
import kotlin.math.roundToInt

/**
 * @since 2023-09-01
 * created by lei
 * 可以滑动和放大的ChartView
 */
abstract class ScrollAndScaleView : View, GestureDetector.OnGestureListener,
    JZScaleGestureDetector.OnScaleGestureListener {

    protected var mScrollX = 0

    private var mScroller: OverScroller? = null

    private var mDetector: GestureDetectorCompat? = null

    private var mScaleDetector: JZScaleGestureDetector? = null

    protected var touchPointListener: OnTouchPointListener? = null

    /**
     * 当前Viewport
     */
    var currentViewport = Viewport()

    var contentRect = Rect()

    var bottomRect = Rect()

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
        finishScroll()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG, "onShowPress")
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
        Log.i(TAG, "onScroll")
        if (!isLongPress && !isMultipleTouch) {
            scrollBy(distanceX.roundToInt(), 0)
            return true
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG, "onLongPress")
        isLongPress = true
        onTouchPoint(e)
    }

    override fun computeScroll() {
        super.computeScroll()
    }

    override fun scrollBy(x: Int, y: Int) {
        scrollTo(mScrollX - x.toFloat().roundToInt(), 0)
    }

    override fun scrollTo(x: Int, y: Int) {
        if (!isScrollEnable) {
            mScroller!!.forceFinished(true)
            return
        }
        val oldX = mScrollX
        mScrollX = x
        onScrollChanged(mScrollX, 0, oldX, 0)
        invalidate()
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        Log.i(TAG, "onFling")
        if (!isTouching && isScrollEnable) {
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

        if (event.pointerCount > 1) isLongPress = false

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isTouching = true
            }

            MotionEvent.ACTION_MOVE ->{
                //长按之后移动
                if (isLongPress) onLongPress(event)
            }

            MotionEvent.ACTION_POINTER_UP ->{
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isTouching = false
                if (isLongPress) {
                    isLongPress = false
                    // 之前是长按 抬起时直接return 不回调 onSingleTapUp
                    return false
                }
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                isLongPress = false
                isTouching = false
                invalidate()
            }

            else -> {}
        }
        isMultipleTouch = event.pointerCount > 1
        mDetector?.onTouchEvent(event)
        mScaleDetector?.onTouchEvent(event)
        return true
    }

    fun setCurrentViewport(viewport: RectF) {
        currentViewport.set(viewport.left, viewport.top, viewport.right, viewport.bottom)
        currentViewport.constrainViewport()
//        triggerViewportChange()
    }

    /**
     * 滑动还未完成 强制结束
     */
    fun finishScroll() {
        if (mScroller?.isFinished == false)
            mScroller?.forceFinished(true)
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
