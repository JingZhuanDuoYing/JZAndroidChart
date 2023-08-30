package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.PointF;

/**
 * @since 2023-08-30
 */
public interface OnDrawLineCompleteListener {

    void onComplete(PointF point1, PointF point2, int type);

}
