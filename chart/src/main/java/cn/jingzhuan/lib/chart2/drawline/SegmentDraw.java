package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

import java.util.List;

import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickValue;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineValue;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @since 2023-08-29
 * 画线段
 */
public class SegmentDraw extends BaseDraw {
    float width = 0f;
    float height = 0f;
    public SegmentDraw(final Chart chart) {
        super(chart);
    }

    @Override
    public void onDraw(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin) {
        super.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
        List<DrawLineValue> values =  dataSet.getValues();
        if (values.size() != 2) return;
        DrawLineValue startValue = values.get(0);
        DrawLineValue endValue = values.get(1);
        final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);

        Path linePath = new Path();

        float startX = -1;
        float endX = -1;
        for (CandlestickValue candlestickValue: visibleValues) {
            if (candlestickValue.getTime() == startValue.getTime()) {
               startX = candlestickValue.getX();
            }
            if (candlestickValue.getTime() == endValue.getTime()) {
               endX = candlestickValue.getX();
            }
        }

        float startY = getScaleY(startValue.getValue(), lMax, lMin);
        float endY = getScaleY(endValue.getValue(), lMax, lMin);

        if (startX != -1f && endX != -1f) {
            width = Math.abs(endX - startX);
            height = Math.abs(endY - startY);
        }

        if (startX == -1f && endX == -1f) return;

        if (endX == -1f) {
            for (CandlestickValue candlestickValue: candlestickDataSet.getValues()) {
                if (candlestickValue.getTime() == endValue.getTime()) {
                    endX = candlestickValue.getX();
                    break;
                }
            }
            // 根据平行截割定理 得(rightY - startY) / (endY - startY) = (rightX - startX) / (endX - startX)
            float rightX = mContentRect.right;
            Log.d("onDraw", "startX="+ startX + "rightX=" + rightX + "endX="+endX + "endY="+endY + "startY="+startY);
            float rightY = (rightX - startX) / width * height + startY;
            linePath.moveTo(startX, startY);
            linePath.lineTo(rightX, rightY);
        } else {
            linePath.moveTo(startX, startY);
            linePath.lineTo(endX, endY);
        }

        canvas.drawPath(linePath, linePaint);

        if (chart.getDrawLineStartPoint() != null) {
            // 画起点
            canvas.drawCircle(chart.getDrawLineStartPoint().x, chart.getDrawLineStartPoint().y, radiusIn, linePaint);
            canvas.drawCircle(chart.getDrawLineStartPoint().x, chart.getDrawLineStartPoint().y, radiusOut, bgPaint);
        }

        if (chart.getDrawLineEndPoint() != null) {
            // 画起点
            canvas.drawCircle(chart.getDrawLineEndPoint().x, chart.getDrawLineEndPoint().y, radiusIn, linePaint);
            canvas.drawCircle(chart.getDrawLineEndPoint().x, chart.getDrawLineEndPoint().y, radiusOut, bgPaint);
        }
    }
}
