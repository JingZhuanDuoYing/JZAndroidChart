package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import java.util.List;

import cn.jingzhuan.lib.chart.R;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart2.base.Chart;

public class AIKLineDrawSelectRangeHelper {

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
  int mLeftX = 0;
  /**
   * 绘制右侧ico 的x坐标
   */
  int mRightX = 0;
  /**
   * 绘制左侧的ico y坐标
   */
  int mLeftY = 0;
  /**
   * 绘制右侧ico y坐标
   */
  int mRightY = 0;
  /**
   * 绘制的ico
   */
  Bitmap bitmap;
  /**
   * 左侧的ico的矩形区域
   */
  Rect leftRect;
  /**
   * 右侧ico的矩形区域
   */
  Rect rightRect;
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
  OnSelectRangerListener onSelectRangerListener;
  AIKLineCandlestickChartRenderer renderer;
  Chart chart;
  private int MIN_K_LINE = 6;

  public AIKLineDrawSelectRangeHelper(AIKLineCandlestickChartRenderer renderer, Chart chart) {
    this.renderer = renderer;
    this.chart = chart;

    bitmap = BitmapFactory.decodeResource(chart.getResources(), R.drawable.ico_red_dot);

    initPaint();

    initMeasure();
  }

  public AIKLineDrawSelectRangeHelper(AIKLineCandlestickChartRenderer renderer, Chart chart,int drawableId) {
    this.renderer = renderer;
    this.chart = chart;

    bitmap = BitmapFactory.decodeResource(chart.getResources(), drawableId);

    initPaint();

    initMeasure();
  }

  /**
   * 更新左右两侧选中想要的K线
   */
  public void updateAIKLineSelectRange(long startTime, long endTime) {
    specEndTime = endTime;
    specStarTime = startTime;
  }

  public void setOnSelectRangerListener(
      OnSelectRangerListener onSelectRangerListener) {
    this.onSelectRangerListener = onSelectRangerListener;
  }

  private void initMeasure() {

    chart.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            chart.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            // 测量完初始化数据
            mLeftY = mRightY = chart.getContentRect().centerY();
            mRightX = (int) (chart.getContentRect().right - chart.getContentRect().width() * 0.1);
            mLeftX = (int) (chart.getContentRect().left + chart.getContentRect().width() * 0.1);
            minLeft = chart.getContentRect().left;
            maxRight = chart.getContentRect().right;
          }
        });
  }

  public void initPaint() {
    paint.setAntiAlias(true);
    paint.setColor(Color.RED);
    paint.setSubpixelText(true);
    paint.setStrokeWidth(1);
    shadowPaint = new Paint(paint);
    shadowPaint.setStyle(Paint.Style.FILL);
    shadowPaint.setColor(Color.argb(0x30, 0xff, 0x00, 0x00));
  }

  public boolean onTouchEvent(MotionEvent event) {
    specEndTime = -1;
    specStarTime = -1;

    if (leftRect == null || rightRect == null) {
      return false;
    }
    final CandlestickDataSet candlestickDataSet = dataVaild();
    if (candlestickDataSet == null) {
      return false;
    }
    switch (event.getAction()) {

      case MotionEvent.ACTION_DOWN: {
        leftPress = false;
        rightPress = false;
        float currentX = event.getX();
        float currentY = event.getY();
        beforeX = (int) currentX;
        beforeY = (int) currentY;
        if (leftRect.contains((int) currentX, (int) currentY)) {
          leftPress = true;
          return true;
        } else if (rightRect.contains((int) currentX, (int) currentY)) {
          rightPress = true;
          return true;
        } else {
          return false;
        }
      }
      case MotionEvent.ACTION_MOVE: {
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        try {
          if (leftPress || rightPress) {

          } else {
            return false;
          }

          //左侧按下
          if (leftPress) {
            int deltaX = currentX - beforeX;
            if (deltaX + mLeftX < minLeft) {
              deltaX = minLeft - mLeftX;
            }
            int tempLeftX = mLeftX + deltaX;

            if (tempLeftX >= mRightX) {
              return true;
            }

            int leftIndex =
                renderer.getEntryIndexByCoordinate(tempLeftX, mLeftY);

            int rightIndex =
                renderer.getEntryIndexByCoordinate(mRightX, mRightY);

            if (rightIndex - leftIndex <= MAX_DIFF_ENTRY - 1 && tempLeftX > mLeftX) {

              int needIndex = rightIndex - 5;

              if (candlestickDataSet.getValues().size() > needIndex && needIndex >= 0) {
                //进行增量右侧移动
                //下次滑动到最大数值直接将他滑到中间
                CandlestickValue value = candlestickDataSet.getEntryForIndex(needIndex);
                mLeftX = (int) value.getX() - renderer.getItemWidth() / 2;
                chart.invalidate();
              }
            } else {
              mLeftX = tempLeftX;
              chart.invalidate();
            }
          } else {
            int deltaX = currentX - beforeX;
            if (deltaX + mRightX > maxRight) {
              deltaX = maxRight - mRightX;
            }
            int tempRightX = deltaX + mRightX;
            if (tempRightX <= mLeftX + bitmap.getWidth()) {
              return true;
            }
            int leftIndex =
                renderer.getEntryIndexByCoordinate(mLeftX, mLeftY);

            int rightIndex =
                renderer.getEntryIndexByCoordinate(tempRightX, mRightY);

            if (rightIndex - leftIndex <= MAX_DIFF_ENTRY - 1 && tempRightX < mRightX) {

              int needIndex = rightIndex + 5;
              if (candlestickDataSet.getValues().size() > needIndex && needIndex >= 0) {
                CandlestickValue valueLeft = candlestickDataSet.getEntryForIndex(leftIndex + 5);
                mRightX = (int) valueLeft.getX() + renderer.getItemWidth() / 2;
                chart.invalidate();
              }
            } else {
              mRightX = tempRightX;
              chart.invalidate();
            }
          }
        } finally {
          beforeX = currentX;
          beforeY = currentY;
        }

        return true;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        rightPress = false;
        leftPress = false;

        break;
    }

    return false;
  }

  /**
   * 数据有效且数据量大于6那么返回对应的数据集合，否则不返回
   */
  public CandlestickDataSet dataVaild() {
    List<CandlestickDataSet> dataSet = renderer.getDataSet();

    if (renderer.getChartData() == null
        || dataSet == null
        || dataSet.size() <= 0) {
      return null;
    } else {
      CandlestickDataSet candlestickDataSet = dataSet.get(0);
      if (candlestickDataSet.getValues().size() < MIN_K_LINE) {
        return null;
      } else {
        return dataSet.get(0);
      }
    }
  }

  public void drawCanvas(Canvas canvas) {

    CandlestickDataSet candlestickDataSet = dataVaild();

    if (candlestickDataSet == null) {
      return;
    }

    //检测是否手动设置范围
    checkSpec();
    int leftIndex = renderer.getEntryIndexByCoordinate(mLeftX, mLeftY);

    List<CandlestickValue> values = candlestickDataSet.getValues();

    if (values.isEmpty()) {
      return;
    }
    if (leftIndex < 0 || leftIndex >= values.size()) {
      return;
    }
    CandlestickValue leftValue = values.get(leftIndex);

    int rightIndex = renderer.getEntryIndexByCoordinate(mRightX, mRightY);

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
    int drawBitLeft = mLeftX - bitmap.getWidth() / 2;

    /**
     * 让右侧红线紧贴某个entry的右侧
     */
    int drawBitRight = mRightX - bitmap.getWidth() / 2;

    /**
     * 两根红线之间至少相差六根k线
     */
//    paint.setColor(Color.parseColor("#ffffff"));
//    shadowPaint.setColor(Color.parseColor("#ffffff"));
    canvas.drawLine(mLeftX, 0, mLeftX, chart.getContentRect().height(), paint);
    canvas.drawLine(mRightX, 0, mRightX, chart.getContentRect().height(), paint);

    Rect rect = new Rect(mLeftX, 0, mRightX, chart.getContentRect().height());
    canvas.drawRect(rect, shadowPaint);

    if (specEndTime == -1 || specStarTime == -1) {
      canvas.drawBitmap(bitmap, drawBitLeft, mLeftY, paint);
      canvas.drawBitmap(bitmap, drawBitRight, mRightY, paint);
    }

    leftRect =
        new Rect(drawBitLeft, mLeftY, drawBitLeft + bitmap.getWidth(), mLeftY + bitmap.getHeight());
    rightRect = new Rect(drawBitRight, mRightY, drawBitRight + bitmap.getWidth(),
        mRightY + bitmap.getHeight());
  }

  private void checkSpec() {
    CandlestickDataSet candlestickDataSet = dataVaild();

    if (candlestickDataSet == null) {
      return;
    }
    if (specEndTime == -1 || specStarTime == -1) {
      return;
    }
    int size = renderer.getDataSet().size();
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
      mLeftX = (int) startValue.getX() - renderer.getItemWidth() / 2;
      //右侧最大值
      if (endValue.getX() == -1) {
        mRightX = maxRight;
      } else {
        mRightX = (int) endValue.getX() + renderer.getItemWidth() / 2;
      }
    }
  }

  public interface OnSelectRangerListener {
    void onSelect(CandlestickValue start, CandlestickValue end);
  }
}
