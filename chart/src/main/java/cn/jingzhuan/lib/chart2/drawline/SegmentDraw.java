package cn.jingzhuan.lib.chart2.drawline;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Map<String, Float> widthSets;

    private final Map<String, Float> heightSets;

    public SegmentDraw(final Chart chart) {
        super(chart);
        widthSets = new ConcurrentHashMap<>();
        heightSets = new ConcurrentHashMap<>();
    }

    @Override
    public void onDraw(Canvas canvas, DrawLineDataSet dataSet, CandlestickDataSet candlestickDataSet, float lMax, float lMin) {
        super.onDraw(canvas, dataSet, candlestickDataSet, lMax, lMin);
        List<DrawLineValue> values =  dataSet.getValues();
        if (values.size() != 2) return;
        DrawLineValue startValue = values.get(0);
        DrawLineValue endValue = values.get(1);
        final List<CandlestickValue> visibleValues = candlestickDataSet.getVisiblePoints(mViewport);

        String key = dataSet.getLineKey();

        Path linePath = new Path();

        float startX = -1;
        float endX = -1;
        for (CandlestickValue candlestickValue : visibleValues) {
            if (candlestickValue.getTime() == startValue.getTime()) {
                startX = candlestickValue.getX();
            }
            if (candlestickValue.getTime() == endValue.getTime()) {
                endX = candlestickValue.getX();
            }
        }

        float startY = getScaleY(startValue.getValue());
        float endY = getScaleY(endValue.getValue());

        if (startX != -1f && endX != -1f) {
            float width = Math.abs(endX - startX);
            float height = Math.abs(endY - startY);
            widthSets.put(key, width);
            heightSets.put(key, height);
        }

        if (startX == -1f && endX == -1f) return;

        if (endX == -1f) {
            // 根据平行截割定理 得(rightY - startY) / (endY - startY) = (rightX - startX) / (endX - startX)
            float width = 0f;
            float height = 0f;
            if (widthSets.containsKey(key)) {
                width = widthSets.get(key);
            }
            if (heightSets.containsKey(key)) {
                height = heightSets.get(key);
            }
            float rightX = mContentRect.right;
            float rightY = (rightX - startX) / width * height + startY;
            Log.d("onDraw", "width=" + width + "height="+ height +
                    "startY=" + startY + "rightY=" + rightY + "endY=" + endY);
            linePath.moveTo(startX, startY);
            linePath.lineTo(rightX, rightY);
        } else {
            linePath.moveTo(startX, startY);
            linePath.lineTo(endX, endY);
        }
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(linePath, linePaint);
    }

    @Override
    public void drawTypeShape(Canvas canvas) {
        // 当前形状是线段 先画线段 再画背景
        linePaint.setStyle(Paint.Style.STROKE);
        Path linePath = new Path();
        linePath.moveTo(touchPointStart.x, touchPointStart.y);
        linePath.lineTo(touchPointEnd.x, touchPointEnd.y);
        canvas.drawPath(linePath, linePaint);
    }
}
