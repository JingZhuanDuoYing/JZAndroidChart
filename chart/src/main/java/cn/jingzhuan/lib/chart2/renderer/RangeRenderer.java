package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
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
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * 区间统计Renderer
 */
public class RangeRenderer extends AbstractDataRenderer<CandlestickDataSet> {

    AIKLineDrawSelectRangeHelper.OnSelectRangerListener onSelectRangerListener;
    private CandlestickData chartData;
    /**
     * 左侧和右侧最小间隔的k线数量
     */
    final int MAX_DIFF_ENTRY = 6;
    /**
     * 选中的左右两侧的时间
     */
    long specStarTime = -1L;
    /**
     * 选中的左右两侧的时间
     */
    long specEndTime = -1L;
    /**
     * 绘制左侧的ico x的坐标
     */
    float mStartX = 0;
    /**
     * 绘制右侧ico 的x坐标
     */
    float mEndX = 0;
    /**
     * 绘制左侧的ico y坐标
     */
    float mStartY = 0;
    /**
     * 绘制右侧ico y坐标
     */
    float mEndY = 0;
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
     * 当前手指按下的是左侧的ico
     */
    boolean leftPress = false;
    /**
     * 当前手指按下的是右侧的ico
     */
    boolean rightPress = false;
    /**
     * 用于划线等
     */
    Paint paint = new Paint();
    /**
     * 中间红色阴影
     */
    Paint shadowPaint = new Paint();
    /**
     * 上一次触摸的x坐标
     */
    int beforeX = 0;
    /**
     * 上一次触摸的y坐标
     */
    int beforeY = 0;
    /**
     * 左侧最小的滑动间距
     */
    int minLeft = 0;
    /**
     * 右侧最大的滑动间距
     */
    int maxRight = 0;
    Chart chart;

    private CandlestickValue mStartCandlestickValue;
    private CandlestickValue mEndCandlestickValue;

    //线条颜色 默认蓝色
    private int mLineColor = Color.parseColor("#216FE1");


    //区间颜色
    private int mRangeColor = Color.parseColor("#66D8F2FD");

    public RangeRenderer(Chart chart) {
        super(chart);
        this.chart = chart;
        initMeasure();
        initPaint();
        System.out.println("init RangeRenderer");
    }

    private void initData() {
        if (dataVaild().getValues() != null) {
            mStartCandlestickValue = getCandlestickValue(dataVaild().getValues().size() - 10);
            mEndCandlestickValue = getCandlestickValue(dataVaild().getValues().size() - 1);
        }

    }

    public void setRangeIcoBitmap(Bitmap ico) {
        this.icoBitmap = ico;
    }


    @Override
    protected void renderDataSet(Canvas canvas, ChartData<CandlestickDataSet> chartData) {
        drawCanvas(canvas);
    }

    private void initMeasure() {

    }

    public void initPaint() {
        paint.setAntiAlias(true);
//    paint.setColor(setRangeLineColor("#216FE1"));
        paint.setColor(getLineColor());
        paint.setSubpixelText(true);
        paint.setStrokeWidth(3);
        shadowPaint = new Paint(paint);
        shadowPaint.setStyle(Paint.Style.FILL);
//    shadowPaint.setColor(setRangeLineColor("#66D8F2FD"));
        shadowPaint.setColor(getRangeColor());
    }

    private int setRangeLineColor(String s) {
        return Color.parseColor(s);
    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull @NotNull Highlight[] highlights) {

    }

    private CandlestickValue getCandlestickValue(int index) {
        return dataVaild().getVisiblePoints(mViewport).get(index);
    }

    public void drawCanvas(Canvas canvas) {
//        chart.setDraggingToMoveEnable(false);
        CandlestickDataSet candlestickDataSet = dataVaild();

        if (candlestickDataSet == null || icoBitmap == null) {
            return;
        }

        if (mStartCandlestickValue == null || mEndCandlestickValue == null) {
            System.out.println("9528 onDraw 获取数据");
            List<CandlestickValue> values = candlestickDataSet.getVisiblePoints(mViewport);
            mStartCandlestickValue = getCandlestickValue(values.size() - 10);
            mEndCandlestickValue = getCandlestickValue(values.size() - 1);
        }


        //检测是否手动设置范围
//    checkSpec();
        System.out.println("9527 : mLeftx : " + mStartX + " , mLeftY :" + mStartY);
//    CandlestickValue mStartCandlestickValue = candlestickDataSet.getValues().get(candlestickDataSet.getValues().size() - 10);
//    CandlestickValue mEndCandlestickValue = candlestickDataSet.getValues().get(candlestickDataSet.getValues().size() - 1);
        System.out.println("candlestickValue -10 x :" + mStartCandlestickValue.getX() + " , " + mStartCandlestickValue.getY());
        int leftIndex = getEntryIndexByCoordinate(mStartX, mStartY);

        List<CandlestickValue> values = candlestickDataSet.getValues();

        if (values.isEmpty()) {
            return;
        }
        if (leftIndex < 0 || leftIndex >= values.size()) {
            return;
        }
        CandlestickValue leftValue = values.get(leftIndex);

        int rightIndex = getEntryIndexByCoordinate(mEndX, mEndY);

        if (rightIndex < 0 || rightIndex >= values.size()) {
            return;
        }

        CandlestickValue rightValue = values.get(rightIndex);

        if (onSelectRangerListener != null) {
            onSelectRangerListener.onSelect(leftValue, rightValue);
        }

        /**
         * 让左侧红线紧贴某个entry的左侧
         */
        float drawBitLeft = mStartX - icoBitmap.getWidth() / 2f;

        /**
         * 让右侧红线紧贴某个entry的右侧
         */
        float drawBitRight = mEndX - icoBitmap.getWidth() / 2f;

        /**
         * 两根红线之间至少相差六根k线
         */
//    canvas.drawLine(mLeftX, 0, mLeftX, chart.getContentRect().height(), paint);

        mStartX = mStartCandlestickValue.getX();
        mStartY = mStartCandlestickValue.getY();
        mEndX = mEndCandlestickValue.getX();
        mEndY = mEndCandlestickValue.getY();
        float bitmapSpanX = icoBitmap.getWidth() / 2f;
        float bitmapSpanY = icoBitmap.getHeight() / 2f;
        System.out.println("9529 mStartX :" + mStartX);
        RectF rect = new RectF(mStartX, 0, mEndX, chart.getContentRect().height());
        canvas.drawRect(rect, shadowPaint);
        canvas.drawLine(mStartX , 0, mStartX, chart.getContentRect().height(), paint);
//    canvas.drawLine(mRightX, 0, mRightX, chart.getContentRect().height(), paint);
        canvas.drawLine(mEndX, 0, mEndX, chart.getContentRect().height(), paint);
        Viewport currentViewport = chart.getCurrentViewport();
        System.out.println("9529 current drawLine " + currentViewport.left + "," + currentViewport.right);
        System.out.println("9529 mContent left : " + mContentRect.left + " , right : " + mContentRect.right);

        if (specEndTime == -1 || specStarTime == -1) {
            System.out.println("9527 drawBitmap");
            canvas.drawBitmap(icoBitmap, mStartX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, paint);
            canvas.drawBitmap(icoBitmap, mEndX - bitmapSpanX, chart.getContentRect().height() / 2f - bitmapSpanY, paint);
        }
        float defaultSpanX = 50;
        float defaultSpanY = 20;
        mStartRect = new RectF(mStartX - bitmapSpanX - defaultSpanX, chart.getContentRect().height() / 2f - bitmapSpanY - defaultSpanY,
                mStartX + bitmapSpanX + defaultSpanX, chart.getContentRect().height() / 2f + bitmapSpanY + defaultSpanY);
        mEndRect = new RectF(mEndX - bitmapSpanX - defaultSpanX,
                chart.getContentRect().height() / 2f - bitmapSpanY, mEndX + bitmapSpanX, chart.getContentRect().height() / 2f + bitmapSpanY);
//        canvas.drawRect(mStartRect, paint);
    }


    public void onTouchEvent(MotionEvent event) {
        System.out.println("9528 我拿到了MotionEvent ");
        float currentX;
        float currentY;
        float lastX;
        float lastY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                int entryIndexByCoordinate = getEntryIndexByCoordinate(currentX, currentY);
//        System.out.println("9528 listSize"+dataVaild().getValues().size());
//        System.out.println("9528 我点到了左边" + entryIndexByCoordinate);
                CandlestickDataSet candlestickDataSet = dataVaild();
                if (candlestickDataSet != null && mStartRect.contains(event.getX(), event.getY())) {
                    System.out.println("9528 我点到了左边 + entryIndexByCoordinate");
                    mStartCandlestickValue = candlestickDataSet.getValues().get(entryIndexByCoordinate);
                    chart.postInvalidate();
                }
                if (candlestickDataSet != null && mEndRect.contains(event.getX(), event.getY())) {
                    System.out.println("9528 我点到了右边 + entryIndexByCoordinate");
                    mEndCandlestickValue = candlestickDataSet.getValues().get(entryIndexByCoordinate);
                    chart.postInvalidate();
                }

//        if (mStartRect.contains(event.getX(),event.getY())){
//          int entryIndexByCoordinate = getEntryIndexByCoordinate(currentX, currentY);
//          System.out.println("9528 listSize"+dataVaild().getValues().size());
//          System.out.println("9528 我点到了左边" + entryIndexByCoordinate);
//
//        }
//        if (mEndRect.contains(event.getX(),event.getY())){
//          System.out.println("9528 右边");
//        }

        }
    }

    private void checkSpec() {
        CandlestickDataSet candlestickDataSet = dataVaild();

        if (candlestickDataSet == null) {
            return;
        }
        if (specEndTime == -1 || specStarTime == -1) {
            return;
        }
        int size = getDataSet().size();
        CandlestickValue startValue = null;
        CandlestickValue endValue = null;
        if (size > 0) {

            for (CandlestickValue value : candlestickDataSet.getValues()) {

                if (value.getTime() == specStarTime) {

                    startValue = value;
                }
                if (value.getTime() == specEndTime) {
                    endValue = value;
                }
            }
        }
        if (startValue != null && endValue != null) {
            mStartX = (int) startValue.getX() - getItemWidth() / 2f;
            //右侧最大值
            if (endValue.getX() == -1) {
                mEndX = maxRight;
            } else {
                mEndX = (int) endValue.getX() + getItemWidth() / 2f;
            }
        }
    }

    private int getItemWidth() {
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

        return (int) candleWidth;
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

//  public void setOnSelectRangerListener(
//      AIKLineDrawSelectRangeHelper.OnSelectRangerListener onSelectRangerListener) {
//    this.onSelectRangerListener = onSelectRangerListener;
//    ((AIKLineCandlestickChartRenderer) candlestickChartRenderer).getDrawSelectRangeHelper()
//        .setOnSelectRangerListener(onSelectRangerListener);
//  }
//
//  @Override
//  protected @NotNull CandlestickChartRenderer initCandlestickChartRenderer(Chart chart) {
//    return new AIKLineCandlestickChartRenderer(chart);
//  }
//
//  public AIKLineDrawSelectRangeHelper getDrawSelectRangeHelper() {
//    return ((AIKLineCandlestickChartRenderer) candlestickChartRenderer).getDrawSelectRangeHelper();
//  }
//  /**
//   * 更新左右两侧选中想要的K线
//   */
//  public void updateAIKLineSelectRange(long startTime, long endTime) {
//    ((AIKLineCandlestickChartRenderer) candlestickChartRenderer).updateAIKLineSelectRange( startTime,  endTime);
//  }

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
}
