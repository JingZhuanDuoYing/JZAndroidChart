package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.utils.FloatUtils;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align;

/**
 * Created by donglua on 2/9/18.
 */

public class CandlestickDataSetArrowDecorator extends CandlestickDataSet {

  public final static String ARROW_LEFT = "←";
  public final static String ARROW_RIGHT = "→";

  private Paint mPaint;

  private final char[] mLabelBuffer = new char[20];

  private Rect mTextBounds = new Rect();

  private float maxData = -1f;
  private float minData = -1f;

  public CandlestickDataSetArrowDecorator(CandlestickDataSet candlestickDataSet) {
    super(candlestickDataSet.getValues(), candlestickDataSet.getAxisDependency());

    mPaint = new Paint(ANTI_ALIAS_FLAG);
    mPaint.setColor(0xffA1abbb);
    mPaint.setTextSize(30);
  }

  public Paint getPaint() {
    return mPaint;
  }

  public void setTextSize(int textSize) {
    mPaint.setTextSize(textSize);
  }

  public void draw(Canvas canvas, CandlestickValue candlestick, Rect contentRect, float candleWidth,
      float x, float highY, float lowY) {

    float lowValue = candlestick.getLow();
    float highValue = candlestick.getHigh();

    if (Float.compare(highValue, getViewportYMax()) == 0 && Float.compare(highValue, maxData) != 0) {

      int length = FloatUtils.formatFloatValue(mLabelBuffer, highValue, 2);

      if (x < contentRect.width() >> 1) {

        mPaint.setTextAlign(Align.LEFT);
        String text = ARROW_LEFT + String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);
        mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        canvas.drawText(text, x + candleWidth * 0.6f, highY + mTextBounds.height(), getPaint());

      } else {

        mPaint.setTextAlign(Align.RIGHT);
        String text = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length) + ARROW_RIGHT;
        mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        canvas.drawText(text, x + candleWidth * 0.4f, highY + mTextBounds.height(), getPaint());

      }

      maxData = highValue;
    }

    if (Float.compare(lowValue, getViewportYMin()) == 0 && Float.compare(lowValue, minData) != 0) {

      int length = FloatUtils.formatFloatValue(mLabelBuffer, lowValue, 2);

      if (x < contentRect.width() >> 1) {

        mPaint.setTextAlign(Align.LEFT);
        String text = ARROW_LEFT + String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length);
        canvas.drawText(text, x + candleWidth * 0.6f, lowY, getPaint());

      } else {

        mPaint.setTextAlign(Align.RIGHT);
        String text = String.valueOf(mLabelBuffer, mLabelBuffer.length - length, length) + ARROW_RIGHT;
        canvas.drawText(text, x + candleWidth * 0.4f, lowY, getPaint());

      }

      minData = lowValue;
    }

  }

  public void reset() {
    minData = -1f;
    maxData = -1f;
  }
}
