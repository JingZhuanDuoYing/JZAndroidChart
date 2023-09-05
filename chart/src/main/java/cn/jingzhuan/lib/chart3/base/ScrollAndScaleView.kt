package cn.jingzhuan.lib.chart3.base

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import cn.jingzhuan.lib.chart.Viewport
import cn.jingzhuan.lib.chart3.state.HighlightState
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
     * 是否开启区间统计
     */
    var isOpenRange = false

    /**
     * 是否触发按下位置的回调
     */
    private var touchPointEnable = true

    /**
     * 十字光标状态
     */
    var highlightState = HighlightState.Initial

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

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
        onHighlightStateChange(e)
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
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
        velocityY: Float
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

    /**
     * 手指按下时 十字光标状态的更新
     * press、move、forever 都需要绘制十字光标
     */
    private fun onHighlightStateChange(event: MotionEvent) {
        if (highlightState == HighlightState.Initial) {
            // 按下前为initial 按下后设置为Press
            highlightState = HighlightState.Press
            onTouchPoint(event)
        } else if (highlightState == HighlightState.Press || highlightState == HighlightState.Move) {
            // 按下前为press || move 按下后设置为initial
            highlightState = HighlightState.Initial
        } else {
            onTouchPoint(event)
        }
    }

    /**
     * 滑动还未完成 强制停止
     */
    fun finishScroll() {
        if (mScroller?.isFinished == false)
            mScroller?.forceFinished(true)
    }

    /**
     * 按下位置的更新
     */
    abstract fun onTouchPoint(event: MotionEvent)

    companion object {
        private const val TAG = "ScrollAndScaleView"
    }
}
