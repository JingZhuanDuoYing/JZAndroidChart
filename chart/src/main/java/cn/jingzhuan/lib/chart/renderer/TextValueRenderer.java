package cn.jingzhuan.lib.chart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * DataSet Values Text Renderer
 *
 * Created by donglua on 11/23/17.
 */

public abstract class TextValueRenderer {

  private final Paint mValueTextPaint;

  public TextValueRenderer() {
    mValueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mValueTextPaint.setStyle(Paint.Style.FILL);
    mValueTextPaint.setColor(Color.WHITE);
    mValueTextPaint.setTextAlign(Paint.Align.CENTER);
  }

  public void render(Canvas canvas, int index, float x, float y) {
    this.render(canvas, mValueTextPaint, index, x, y);
  }

  public abstract void render(Canvas canvas, Paint textPaint, int index, float x, float y);

}
