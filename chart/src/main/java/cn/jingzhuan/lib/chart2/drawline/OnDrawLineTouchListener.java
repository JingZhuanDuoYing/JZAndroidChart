package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.PointF;

/**
 * @since 2023-08-30
 */
public interface OnDrawLineTouchListener {

    void onTouch(DrawLineTouchState state, PointF point, int type);

}
