package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.CandlestickData;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.event.OnViewportChangeListener;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * 区间统计Renderer
 */
public class RangeRenderer extends AbstractDataRenderer<CandlestickDataSet> {

    private CandlestickData chartData;

    /**
     * 最小周期
     */
    final int MAX_DIFF_ENTRY = 1;

    /**
     * 左、右两边icon x的坐标
     */
    float mStartX, mEndX = 0;

    /**
     * 当前chart的左右边界
     */
    int chartLeft, chartRight = 0;

    /**
     * 左、右两边touch bitmap
     */
    Bitmap leftTouchBitmap,rightTouchBitmap;

    /**
     * 左、右两边 touch的矩形区域
     */
    RectF leftTouchRect, rightTouchRect;

    /**
     * 当前手指按下的是左边 还是右边
     */
    boolean leftPress, rightPress = false;

    /**
     * 上一次触摸的x、y坐标
     */
    float lastPreX, lastPreY = 0;

    /**
     * 左、右两边touch的矩形区域中间 线的颜色
     * 默认为 #FD263F的 %8透明度
     */
    private int mLineColor = Color.parseColor("#1AFD263F");

    /**
     * 区间的颜色
     * 默认为 #FD263F的 %8透明度
     */
    private int mRangeColor = Color.parseColor("#1AFD263F");

    /**
     * 用于划线
     */
    Paint paint = new Paint();

    /**
     * 用于画bitmap
     */
    Paint btPaint = new Paint();

    /**
     * 中间阴影
     */
    Paint shadowPaint = new Paint();

    Chart chart;

    private OnRangeListener mOnRangeListener;
    private OnRangeKLineVisibleListener mOnRangeKLineVisibleListener;
    private OnRangeKLineListener mOnRangeKLineListener;
    private CandlestickValue mStartCandlestickValue;
    private CandlestickValue mEndCandlestickValue;

    //开始的index
    private int mStartIndex;
    //结束的index
    private int mEndIndex;

    public RangeRenderer(Chart chart) {
        super(chart);
        this.chart = chart;
        initPaint();
        initMeasure();
        chart.setInternalViewportChangeListener(new OnViewportChangeListener() {
            @Override
            public void onViewportChange(Viewport viewport) {
                mViewport.set(viewport);
                calcDataSetMinMax();
            }
        });
    }


    @Override
    protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData) {
        drawCanvas(canvas);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData, CandlestickDataSet dataSet) {
        drawCanvas(canvas);
    }

    private void initMeasure() {
        chart.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        chart.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        chartLeft = chart.getContentRect().left;
                        chartRight = chart.getContentRect().right;
                    }
                });
    }

    public void initPaint() {
        btPaint.setAntiAlias(true);
        paint.setAntiAlias(true);
        paint.setColor(getLineColor());
        paint.setSubpixelText(true);
        paint.setStrokeWidth(3);
        shadowPaint = new Paint(paint);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(getRangeColor());
    }


    @Override
    public void renderHighlighted(Canvas canvas, @NonNull @NotNull Highlight[] highlights) {

    }

    public void drawCanvas(Canvas canvas) {
        CandlestickDataSet candlestickDataSet = dataVaild();
        if (candlestickDataSet == null) return;

        //默认选中当前屏幕内全部
        if (mStartCandlestickValue == null || mEndCandlestickValue == null) {
            List<CandlestickValue> visiblePoints = candlestickDataSet.getVisiblePoints(mViewport);

            CandlestickValue startVisible = visiblePoints.get(visiblePoints.size() - 10);
            CandlestickValue endVisible = visiblePoints.get(visiblePoints.size() - 1);
            float startVisibleX = startVisible.getX();
            float startVisibleY = startVisible.getY();
            float endVisibleX = endVisible.getX();
            float endVisibleY = endVisible.getY();
            mStartIndex = getEntryIndexByCoordinate(startVisibleX, startVisibleY);
            mEndIndex = getEntryIndexByCoordinate(endVisibleX, endVisibleY);
        }

        if (mStartIndex >= candlestickDataSet.getValues().size() || mEndIndex >= candlestickDataSet.getValues().size())
            return;
        mStartCandlestickValue = candlestickDataSet.getEntryForIndex(mStartIndex);
        mEndCandlestickValue = candlestickDataSet.getEntryForIndex(mEndIndex);

        // 获取区间统计开始的K线坐标
        mStartX = getScaleCoordinateByIndex(mStartIndex);

        // 获取区间统计结束的K线坐标
        mEndX = getScaleCoordinateByIndex(mEndIndex);

        RectF rect = new RectF(mStartX, 0, mEndX, chart.getContentRect().height());

        //绘制区间统计的选择区域
        canvas.drawRect(rect, shadowPaint);

        /*
         * 根据K线数据的中点绘制线条
         */
        canvas.drawLine(mStartX, 0, mStartX, chart.getContentRect().height(), paint);
        canvas.drawLine(mEndX, 0, mEndX, chart.getContentRect().height(), paint);

        // 如果leftTouchBitmap和rightTouchBitmap 没有设置 用默认的
        if(leftTouchBitmap == null)
            leftTouchBitmap = BitmapFactory.decodeResource(this.chart.getResources(), R.drawable.ico_range_touch_left);
        if(rightTouchBitmap == null)
            rightTouchBitmap = BitmapFactory.decodeResource(this.chart.getResources(), R.drawable.ico_range_touch_right);

        // 绘制左右touch icon
        float bitmapSpanX = leftTouchBitmap.getWidth() / 2f;
        float bitmapSpanY = leftTouchBitmap.getHeight() / 2f;
        if(leftTouchBitmap != null)
            canvas.drawBitmap(leftTouchBitmap, mStartX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, btPaint);
        if(rightTouchBitmap != null)
            canvas.drawBitmap(rightTouchBitmap, mEndX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, btPaint);

        /*
         * 创建开始&结束矩阵 用于判断触摸点是否在该区域内
         * true -> 改变区间统计范围
         * false -> 不作反应
         */
        leftTouchRect = new RectF(
                mStartX - bitmapSpanX * 2f , chart.getContentRect().top,
                mStartX + bitmapSpanX * 2f,
                chart.getContentRect().bottom);
        rightTouchRect = new RectF(
                mEndX - bitmapSpanX * 2f,
                chart.getContentRect().top,
                mEndX + bitmapSpanX * 2f,
                chart.getContentRect().bottom);

        //回调区间X轴坐标的范围
        if (mOnRangeListener != null)
            mOnRangeListener.onRange(mStartX, mEndX);

        /*
         * 缩放限制
         * 当K线被缩放到屏幕外(不可见)的情况下 关闭缩放
         */
        if (mOnRangeKLineVisibleListener != null) {
            mOnRangeKLineVisibleListener.onRangeKLineVisible((mStartX > (mContentRect.left + leftTouchBitmap.getWidth()) && mEndX < (mContentRect.width() - leftTouchBitmap.getWidth())));
        }

        if (mOnRangeKLineListener!=null)
            mOnRangeKLineListener.onRangeKLine(mStartIndex,mEndIndex);
    }

    private boolean moveToLeft(float deltaX) {
        final CandlestickDataSet candlestickDataSet = dataVaild();
        if (candlestickDataSet == null) {
            return false;
        }
        if (deltaX + mStartX < chartLeft) {
            deltaX = chartLeft - mStartX;
        }
        float tempLeftX = mStartX + deltaX;

        if (tempLeftX >= chartRight) {
            return true;
        }
        int leftIndex = getEntryIndexByCoordinate(tempLeftX, 0);
        int rightIndex = getEntryIndexByCoordinate(mEndX, 0);
        mStartIndex = leftIndex;
        mEndIndex = rightIndex;

        if (rightIndex - leftIndex <= MAX_DIFF_ENTRY && tempLeftX > mStartX) {
            int needIndex = rightIndex - MAX_DIFF_ENTRY;
            if (candlestickDataSet.getValues().size() > needIndex && needIndex >= 0) {
                //进行增量右侧移动
                //下次滑动到最大数值直接将他滑到中间
                CandlestickValue value = candlestickDataSet.getEntryForIndex(needIndex);
                mStartX = value.getX() - getItemWidth() / 2;
                chart.invalidate();
            }
        } else {
            mStartX = tempLeftX;
            chart.invalidate();
        }
        return true;
    }

    private boolean moveToRight(float deltaX) {
        final CandlestickDataSet candlestickDataSet = dataVaild();
        if (candlestickDataSet == null) {
            return false;
        }

        if (deltaX + mEndX > chartRight) {
            deltaX = chartRight - mEndX;
        }
        float tempRightX = deltaX + mEndX;
        if (tempRightX <= mStartX + rightTouchBitmap.getWidth()) {
            return true;
        }

        int leftIndex = getEntryIndexByCoordinate(mStartX, 0);
        int rightIndex = getEntryIndexByCoordinate(tempRightX, 0);
        mStartIndex = leftIndex;
        mEndIndex = rightIndex;

        if (rightIndex - leftIndex <= MAX_DIFF_ENTRY && tempRightX < mEndX) {
            int needIndex = rightIndex + MAX_DIFF_ENTRY;
            if (candlestickDataSet.getValues().size() > needIndex && needIndex >= 0) {
                CandlestickValue valueLeft = candlestickDataSet.getEntryForIndex(leftIndex + 5);
                mEndX = (int) valueLeft.getX() + getItemWidth() / 2;
                chart.invalidate();
            }
        } else {
            mEndX = tempRightX;
            chart.invalidate();
        }
        return true;
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                leftPress = false;
                rightPress = false;
                float currentX = event.getX();
                float currentY = event.getY();
                lastPreX = currentX;
                lastPreY = currentY;
                if (leftTouchRect.contains(currentX, currentY)) {
                    leftPress = true;
                    return true;
                } else if (rightTouchRect.contains(currentX, currentY)) {
                    rightPress = true;
                    return true;
                } else {
                    return false;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                float currentX = event.getX();
                float currentY = event.getY();
                float deltaX = currentX - lastPreX;
                try {
                    if (leftPress && rightPress) {
                        if(deltaX < 0) {
                            // 向左
                            moveToLeft(deltaX);
                        }else {
                            //向右
                            moveToRight(deltaX);
                        }
                    } else if (leftPress) {
                        moveToLeft(deltaX);
                    } else if(rightPress){
                        moveToRight(deltaX);
                    } else {
                        return false;
                    }
                } finally {
                    lastPreX = currentX;
                    lastPreY = currentY;
                }

                return true;
            }
            case MotionEvent.ACTION_UP:
                rightPress = false;
                leftPress = false;
                break;
            case MotionEvent.ACTION_CANCEL:
        }
        return false;
    }

    /**
     * 根据子当前Kline在数据集合的index获得缩放后对应的X轴坐标
     * @param index 数据集合的index
     * @return X轴坐标
     */
    public float getScaleCoordinateByIndex(int index) {
        CandlestickDataSet candlestickDataSet = dataVaild();
        int valueCount = candlestickDataSet.getEntryCount();
        final float scale = 1.0f / mViewport.width();
        float candleWidth = candlestickDataSet.getCandleWidth();
        final float visibleRange = candlestickDataSet.getVisibleRange(mViewport);
        if (candlestickDataSet.isAutoWidth()) {
            candleWidth = mContentRect.width() / Math.max(visibleRange, candlestickDataSet.getMinValueCount());
        }
        final float step = mContentRect.width() * scale / valueCount;
        final float startX = mContentRect.left - mViewport.left * mContentRect.width() * scale;
        float startXPosition = startX + step * (index + candlestickDataSet.getStartIndexOffset());
        return startXPosition + candleWidth * 0.5f;
    }

    private float getItemWidth() {
        if (getDataSet() == null || getDataSet().size() == 0) {
            return 0;
        }
        CandlestickDataSet candlestickDataSet = getDataSet().get(0);
        float candleWidth = candlestickDataSet.getCandleWidth();
        final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);
        if (candlestickDataSet.isAutoWidth()) {
            candleWidth = (mContentRect.width() + 0f) / Math.max(visibleValues.size(),
                    candlestickDataSet.getMinValueCount());
        }

        return candleWidth;
    }


    /**
     * 数据有效且数据量大于6那么返回对应的数据集合，否则不返回
     */
    public CandlestickDataSet dataVaild() {
        List<CandlestickDataSet> dataSet = getDataSet();

        if (getChartData() == null
                || dataSet == null
                || dataSet.size() <= 0) {
            return null;
        } else {
            CandlestickDataSet candlestickDataSet = dataSet.get(0);
            if (candlestickDataSet.getValues().size() < 6) {
                return null;
            } else {
                return dataSet.get(0);
            }
        }
    }

    @Override
    public void removeDataSet(CandlestickDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    @Override
    protected List<CandlestickDataSet> getDataSet() {
        return chartData.getDataSets();
    }

    @Override
    public ChartData<CandlestickDataSet> getChartData() {
        if (chartData == null)
            chartData = new CandlestickData();
        return chartData;
    }

    /**
     * 重置数据
     */
    public void resetData() {
        if (mStartCandlestickValue != null && mEndCandlestickValue != null) {
            mStartCandlestickValue = null;
            mEndCandlestickValue = null;
        }
    }


    /**
     * @return 线条颜色
     */
    public int getLineColor() {
        return mLineColor;
    }

    /**
     * @param mLineColor 设置线条指定的颜色
     */
    public void setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    /**
     * @return 区间统计颜色
     */
    public int getRangeColor() {
        return mRangeColor;
    }

    /**
     * @param mRangeColor 设置区间统计指定的颜色
     */
    public void setRangeColor(int mRangeColor) {
        this.mRangeColor = mRangeColor;
    }

    /**
     * 设置区间统计左边touch icon图标
     * @param icon 设置指定的Bitmap
     */
    public void setRangeLeftBitmap(Bitmap icon) {
        this.leftTouchBitmap = icon;
    }

    /**
     * 设置区间统计右边touch icon图标
     * @param icon 设置指定的Bitmap
     */
    public void setRangeRightBitmap(Bitmap icon) {
        this.rightTouchBitmap = icon;
    }

    /**
     * 区间统计监听器
     */
    public interface OnRangeListener {

        /**
         * 区间统计坐标
         *
         * @param startX 开始的X坐标
         * @param endX   结束的X坐标
         */
        void onRange(float startX, float endX);
    }

    /**
     * 设置区间统计范围的监听器 用于更新关闭按钮的所在位置(X轴坐标)
     *
     * @param listener 监听器
     */
    public void setOnRangeListener(OnRangeListener listener) {
        this.mOnRangeListener = listener;
    }

    /**
     * 监听K线是否可见
     */
    public interface OnRangeKLineVisibleListener {


        /**
         * @param visible true为可见
         */
        void onRangeKLineVisible(boolean visible);
    }

    /**
     * 设置区间统计可见KLine监听器
     *
     * @param listener 监听器
     */
    public void setOnRangeKLineVisibleListener(OnRangeKLineVisibleListener listener) {
        this.mOnRangeKLineVisibleListener = listener;
    }

    /**
     * 监听K线是否可见
     */
    public interface OnRangeKLineListener {


        /**
         *
         * @param startIndex 开始的index
         * @param endIndex   结束的index
         */
        void onRangeKLine(int startIndex , int endIndex);
    }


    /**
     * 设置区间统计可见KLine监听器
     *
     * @param listener 监听器
     */
    public void setOnRangeKLineListener(OnRangeKLineListener listener) {
        this.mOnRangeKLineListener = listener;
    }
}
