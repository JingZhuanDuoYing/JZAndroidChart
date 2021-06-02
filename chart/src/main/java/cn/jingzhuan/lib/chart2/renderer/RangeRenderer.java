package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
     * 绘制左侧的ico x的坐标
     */
    float mStartX = 0;
    /**
     * 绘制右侧ico 的x坐标
     */
    float mEndX = 0;
    /**
     * 绘制的ico
     */
    Bitmap icoBitmap;
    /**
     * 左侧的ico的矩形区域
     */
    RectF mStartRect;
    /**
     * 右侧ico的矩形区域
     */
    RectF mEndRect;
    /**
     * 用于划线
     */
    Paint paint = new Paint();
    /**
     * 中间阴影
     */
    Paint shadowPaint = new Paint();
    Chart chart;

    private OnRangeListener mOnRangeListener;
    private OnRangeKLineVisibleListener mOnRangeKLineVisibleListener;
    private CandlestickValue mStartCandlestickValue;
    private CandlestickValue mEndCandlestickValue;

    //线条颜色 默认蓝色
    private int mLineColor = Color.parseColor("#216FE1");
    //区间颜色
    private int mRangeColor = Color.parseColor("#66D8F2FD");

    //开始的index
    private int mStartIndex;
    //结束的index
    private int mEndIndex;


    public RangeRenderer(Chart chart) {
        super(chart);
        this.chart = chart;
        initPaint();

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

    public void initPaint() {
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

        //默认选中数据中最后10个K线作为统计范围
        //todo 缩放后是否该定位当前显示的最后10条K线
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
        mStartCandlestickValue = candlestickDataSet.getEntryForIndex(mStartIndex);
        mEndCandlestickValue = candlestickDataSet.getEntryForIndex(mEndIndex);

         /*
          获取区间统计开始的K线坐标
         */
        mStartX = getScaleCoordinateByIndex(mStartIndex);
        /*
         * 获取区间统计结束的K线坐标
         */
        mEndX = getScaleCoordinateByIndex(mEndIndex);

        float bitmapSpanX = icoBitmap.getWidth() / 2f;
        float bitmapSpanY = icoBitmap.getHeight() / 2f;
        RectF rect = new RectF(mStartX, 0, mEndX, chart.getContentRect().height());
//        System.out.println("9529 mStartX :" + mStartX + candlestickDataSet);

        //绘制区间统计的选择区域
        canvas.drawRect(rect, shadowPaint);

        /*
         * 根据K线数据的中点绘制线条
         */
        canvas.drawLine(mStartX, 0, mStartX, chart.getContentRect().height(), paint);
        canvas.drawLine(mEndX, 0, mEndX, chart.getContentRect().height(), paint);
//        System.out.println("9529 current drawLine " + currentViewport.left + "," + currentViewport.right);
//        System.out.println("9529 mContent left : " + mContentRect.left + " , right : " + mContentRect.right);

        /*
         * 绘制ico
         */
        if (icoBitmap != null){
            canvas.drawBitmap(icoBitmap, mStartX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, paint);
            canvas.drawBitmap(icoBitmap, mEndX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, paint);
        }

        /*
         *为了更好的能拖动区间 加大了触发拖动的范围
         */
        float defaultSpanX = 0;
        float defaultSpanY = 0;

        /*
         * 创建开始&结束矩阵 用于判断触摸点是否在该区域内
         * true -> 改变区间统计范围
         * false -> 不作反应
         */
        mStartRect = new RectF(
                mStartX - bitmapSpanX - defaultSpanX,
                chart.getContentRect().height() / 2f - bitmapSpanY - defaultSpanY,
                mStartX + bitmapSpanX + defaultSpanX,
                chart.getContentRect().height() / 2f + bitmapSpanY + defaultSpanY);
        mEndRect = new RectF(
                mEndX - bitmapSpanX - defaultSpanX,
                chart.getContentRect().height() / 2f - bitmapSpanY - defaultSpanY,
                mEndX + bitmapSpanX + defaultSpanX,
                chart.getContentRect().height() / 2f + bitmapSpanY + defaultSpanY);

        //回调区间X轴坐标的范围
        if (mOnRangeListener != null)
            mOnRangeListener.onRange(mStartX, mEndX);

        /*
         * 缩放限制
         * 当K线被缩放到屏幕外(不可见)的情况下 关闭缩放
         */
        if (mOnRangeKLineVisibleListener != null) {
            mOnRangeKLineVisibleListener.onRangeKLineVisible((mStartX > mContentRect.left + icoBitmap.getWidth() && mEndX < mContentRect.width() - icoBitmap.getWidth()));
        }
    }


    public void onTouchEvent(@NotNull MotionEvent event) {
        float currentX;
        float currentY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                currentX = event.getX();
                currentY = event.getY();
                //根据当前触摸点定位对应的K线

                //拖动ico 改变开始的K线
                if (mStartRect.contains(currentX, currentY)) {
                    mStartIndex = getEntryIndexByCoordinate(currentX, currentY);
                    chart.postInvalidate();
//                    System.out.println("9528 我点到了左边 " + mStartIndex);
                }

                //拖动ico 改变结束的K线
                if (mEndRect.contains(currentX, currentY)) {
                    mEndIndex = getEntryIndexByCoordinate(currentX, currentY);
                    if (mEndIndex - mStartIndex == 2){
                        mStartIndex--;
                        mEndIndex--;
                    }
                    chart.postInvalidate();
                }

        }
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
     * 设置区间统计ico图标
     *
     * @param ico 设置指定的Bitmap
     */
    public void setRangeIcoBitmap(Bitmap ico) {
        this.icoBitmap = ico;
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

}
