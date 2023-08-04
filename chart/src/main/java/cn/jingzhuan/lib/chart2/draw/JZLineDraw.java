package cn.jingzhuan.lib.chart2.draw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.component.AxisY;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PartLineData;
import cn.jingzhuan.lib.chart.data.PointValue;
import cn.jingzhuan.lib.chart2.TimeUtil;
import cn.jingzhuan.lib.chart2.base.Chart;
import cn.jingzhuan.lib.chart2.base.JZBaseChart;
import cn.jingzhuan.lib.chart2.widget.LineChart;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZLineDraw extends BaseDraw<PointValue, LineDataSet> {

    private final List<PartLineData> partLineList;

    private final List<Path> shaderPaths;

    private final List<Shader> shaderPathColors;

    private final List<Path> linePaths;

    private Path shaderPath;

    private boolean onlyLines = false;

    private boolean isDrawHighLight = false;

    private Paint mTextPaint;

    public JZLineDraw(Chart chart) {
        super(chart);
        linePaths = new ArrayList<>();
        shaderPath = new Path();
        shaderPaths = new ArrayList<>();
        shaderPathColors = new ArrayList<>();
        partLineList = new ArrayList<>();

        if (chart instanceof LineChart) {
            onlyLines = true;
        }

        initPaint();

        addOnTouchPointChangeListener();
    }

    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(8);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(25f);
    }

    @Override
    public void drawDataSet(Canvas canvas, ChartData<LineDataSet> chartData, LineDataSet dataSet) {
        if (dataSet.isVisible()) {
            dataSetList = chartData.getDataSets();
            drawLineDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawLineDataSet(Canvas canvas, final LineDataSet lineDataSet, float lMax, float lMin, float rMax, float rMin) {
        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(lineDataSet.getLineThickness());
        mRenderPaint.setColor(lineDataSet.getColor());

        int valueCount = lineDataSet.getEntryCount();

        shaderPath.reset();
        shaderPaths.clear();
        shaderPathColors.clear();
        linePaths.clear();
        partLineList.clear();

        boolean isFirst = true;

        float min, max;
        switch (lineDataSet.getAxisDependency()) {
            case AxisY.DEPENDENCY_RIGHT:
                min = rMin;
                max = rMax;
                break;
            case AxisY.DEPENDENCY_BOTH:
            case AxisY.DEPENDENCY_LEFT:
            default:
                min = lMin;
                max = lMax;
                break;
        }

        final float count = lineDataSet.getVisibleRange(mViewport);
        final float width = count > 0 ? (mContentRect.width() / count) : 0;

        final int offset = lineDataSet.getStartIndexOffset();

        final float scale = 1 / mViewport.width();
        final float step = (valueCount > 1 && onlyLines) ?
                (mContentRect.width() * scale / (valueCount - 1)) : (mContentRect.width() * scale / valueCount);
        final float startX = mContentRect.left + (onlyLines ? 0f : step * 0.5f) - mViewport.left * mContentRect.width() * scale;

        PointValue prevValue = null;

        boolean shaderSplit = !Float.isNaN(lineDataSet.getShaderBaseValue()) &&
                lineDataSet.getShaderBaseValue() < max &&
                lineDataSet.getShaderBaseValue() > min;

        int lastIndex = 0;
        if (mChartAnimator.getPhaseX() > 0) {
            lastIndex = (int) (Math.floor(lineDataSet.getValues().size() * mChartAnimator.getPhaseX()) - 1);
        }

        if (lastIndex >= valueCount) lastIndex = valueCount - 1;

        int valuePhaseCount = (int) Math.floor(valueCount * mChartAnimator.getPhaseX());

        Path linePath = new Path();

        int i = 0;
        float preBaseX = Float.NaN;

        if (lineDataSet.isDrawBand()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    // 这里有潜在的崩溃问题，先catch，后续重写
                    drawBand(canvas, lineDataSet, valuePhaseCount, startX, step, offset, max, min);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        if (lineDataSet.isHorizontalLine()) {
            drawHorizontalLine(canvas, lineDataSet, linePath, max, min);
            return;
        }

        int lineThickness = lineDataSet.getLineThickness();

        PointValue headPoint = lineDataSet.getHeadPoint();
        if (headPoint != null && !headPoint.isValueNaN()) {
            // 垂直方向绘制范围收缩至能容下线条的宽度
            float headYPosition = (max - headPoint.getValue()) / (max - min) * (mContentRect.height() - 2 * lineThickness) + lineThickness * 0.5f;
            linePath.moveTo(startX, headYPosition);


            PointValue firstPoint = null;
            float firstXPosition = startX;
            for (; i < valuePhaseCount && i < lineDataSet.getValues().size(); i++) {
                PointValue point = lineDataSet.getEntryForIndex(i);

                if (!point.isValueNaN()) {
                    firstPoint = point;
                    firstXPosition = startX + step * (i + offset);
                    break;
                }
            }

            if (firstPoint != null) {
                float firstYPosition = (max - firstPoint.getValue()) / (max - min) * (mContentRect.height() - 2 * lineThickness) + lineThickness * 0.5f;
                linePath.lineTo(firstXPosition, firstYPosition);
            }
        }

        for (; i < valuePhaseCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            if (point.isValueNaN()) {
                continue;
            }

            float xPosition = startX + step * (i + offset);
            float yPosition = (max - point.getValue()) / (max - min) * (mContentRect.height() - 2 * lineThickness) + lineThickness * 0.5f;

            point.setCoordinate(xPosition, yPosition);

            //分段线条
            if (i > 1 && lineDataSet.isPartLine()) {
                PointValue lastPoint = lineDataSet.getEntryForIndex(i - 1);
                boolean split = point.getPathColor() != lastPoint.getPathColor();
                if (split) {
                    partLineList.add(new PartLineData(linePath, lastPoint.getPathColor()));
                    linePath = new Path();
                    linePath.moveTo(lastPoint.getX(), lastPoint.getY());
                }
                if (i == lineDataSet.getValues().size() - 1) {
                    partLineList.add(new PartLineData(linePath, point.getPathColor()));
                }
            }

            //普通线条
            if (isFirst) {
                if (!point.isPathEnd()) {
                    isFirst = false;
                    linePath.moveTo(xPosition, yPosition);
                }
            } else {
                linePath.lineTo(xPosition, yPosition);
                if (point.isPathEnd()) {
                    linePaths.add(linePath);
                    linePath = new Path();
                    isFirst = true;
                }
            }

            //阴影
            if (shaderSplit) {
                float baseValue = lineDataSet.getShaderBaseValue();
                float baseValueY = mContentRect.height() / (max - min) * (max - baseValue);

                if (prevValue == null) {
                    preBaseX = point.getX();
                    shaderPath.moveTo(preBaseX, yPosition);
                } else if (prevValue.getValue() > lineDataSet.getShaderBaseValue()) {
                    if (point.getValue() <= lineDataSet.getShaderBaseValue()) { // 跨越颜色区域

                        float nextBaseX = getBaseX(prevValue, point, baseValueY);
                        shaderPath.lineTo(nextBaseX, baseValueY);
                        shaderPath.lineTo(preBaseX, baseValueY);
                        shaderPath.close();

                        shaderPaths.add(new Path(shaderPath));
                        shaderPathColors.add(lineDataSet.getShaderTop());

                        shaderPath.reset();
                        shaderPath.moveTo(nextBaseX, baseValueY);
                        shaderPath.lineTo(xPosition, yPosition);

                        preBaseX = nextBaseX;
                    } else {
                        shaderPath.lineTo(xPosition, yPosition); // 当前值坐标
                    }
                } else if (point.getValue() > lineDataSet.getShaderBaseValue()) {

                    float nextBaseX = getBaseX(prevValue, point, baseValueY);
                    shaderPath.lineTo(nextBaseX, baseValueY);
                    shaderPath.lineTo(preBaseX, baseValueY);
                    shaderPath.close();

                    shaderPaths.add(new Path(shaderPath));
                    shaderPathColors.add(lineDataSet.getShaderBottom());

                    shaderPath.reset();
                    shaderPath.moveTo(nextBaseX, baseValueY);
                    shaderPath.lineTo(xPosition, yPosition);

                    preBaseX = nextBaseX;

                } else {
                    shaderPath.lineTo(xPosition, yPosition); // 当前值坐标
                }

                prevValue = point;

                if (lastIndex == i) {
                    shaderPath.lineTo(xPosition, baseValueY);
                    shaderPath.lineTo(preBaseX, baseValueY);
                    shaderPath.close();
                    shaderPaths.add(new Path(shaderPath));
                    if (prevValue.getValue() > baseValue) {
                        shaderPathColors.add(lineDataSet.getShaderTop());
                    } else {
                        shaderPathColors.add(lineDataSet.getShaderBottom());
                    }
                    shaderPath.reset();
                }
            }
        } // end for.

        if (!isFirst) {
            linePaths.add(linePath);
        }

        if (!shaderSplit) { // 不区分颜色分段

            // draw shader area
            if (i > 0 && lineDataSet.getShader() != null && lineDataSet.getValues().size() > 0) {
                mRenderPaint.setStyle(Paint.Style.FILL);

                if (shaderPath == null) {
                    shaderPath = new Path(linePath);
                } else {
                    shaderPath.set(linePath);
                }

                PointValue pointValue = lineDataSet.getEntryForIndex(i - 1);

                if (pointValue != null) {
                    shaderPath.lineTo(pointValue.getX(), mContentRect.bottom);
                    shaderPath.lineTo(startX + offset * width, mContentRect.bottom);
                    shaderPath.lineTo(startX + offset * width, lineDataSet.getValues().get(0).getY());
                    shaderPath.close();
                    mRenderPaint.setShader(lineDataSet.getShader());
                    canvas.drawPath(shaderPath, mRenderPaint);
                    mRenderPaint.setShader(null);
                    mRenderPaint.setStyle(Paint.Style.STROKE);
                }
            }
        } else {
            mRenderPaint.setStyle(Paint.Style.FILL);

            for (i = 0; i < shaderPaths.size(); i++) {
                Path path = shaderPaths.get(i);

                Shader shader = shaderPathColors.get(i);
                mRenderPaint.setShader(shader);
                canvas.drawPath(path, mRenderPaint);
                mRenderPaint.setShader(null);
            }
            mRenderPaint.setStyle(Paint.Style.STROKE);
        }

        if (lineDataSet.isLineVisible()) {
            if (lineDataSet.isPartLine()) {
                for (PartLineData partLineData : partLineList) {
                    mRenderPaint.setColor(partLineData.getColor());
                    canvas.drawPath(partLineData.getPath(), mRenderPaint);
                }
            } else {
                for (Path path : linePaths) {
                    canvas.drawPath(path, mRenderPaint);
                }
            }
        }
    }

    private void drawHorizontalLine(Canvas canvas, LineDataSet lineDataSet, Path linePath, float max, float min) {
        if (mChart instanceof JZBaseChart) {
            isDrawHighLight = ((JZBaseChart) mChart).getHighlights() != null;
        }
        mRenderPaint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 0));
        mRenderPaint.setColor(lineDataSet.getColor());

        PointValue value = lineDataSet.getEntryForIndex(lineDataSet.getValues().size() - 1);
        float yPosition = (max - value.getValue()) / (max - min) * mContentRect.height();
        linePath.moveTo(0, yPosition);
        linePath.lineTo(mContentRect.width(), yPosition);
        canvas.drawPath(linePath, mRenderPaint);

        mRenderPaint.setPathEffect(null);
        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setStrokeWidth(2f);
        mRenderPaint.setTextSize(25f);

        String text;
        if (isDrawHighLight){
            text = value.getValue() + "";
        } else {
            text = lineDataSet.getTag();
        }

        Rect textBound = new Rect();

        mRenderPaint.getTextBounds(text, 0, text.length(), textBound);

        int padding = 10;
        Rect textRect = new Rect();
        textRect.left = 0;
        textRect.top = (int) (yPosition - textBound.height());
        textRect.right = textBound.width() + padding * 2;
        textRect.bottom = (int) (yPosition + textBound.height());

        if (TimeUtil.isInTime()) {
            textRect.left = mContentRect.width() - textBound.width() - padding * 2;
            textRect.right = mContentRect.width();
        }

        if (textRect.top < 0 && yPosition >= 0) {
            int height = textRect.height();
            textRect.top = 0;
            textRect.bottom = height;
        }

        if (textRect.bottom > mContentRect.height() && yPosition <= mContentRect.height()) {
            int height = textRect.height();
            textRect.bottom = mContentRect.height();
            textRect.top = mContentRect.height() - height;
        }


        if (value.getValue() <= max && value.getValue() >= min) {
            canvas.drawRect(textRect, mRenderPaint);
        }


        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = textRect.centerY() + distance;


        if (value.getValue() <= max && value.getValue() >= min) {
            canvas.drawText(text, textRect.centerX(), baseline, mTextPaint);
        }
    }

    /**
     * 绘制带状线
     */
    @Deprecated
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawBand(Canvas canvas, LineDataSet lineDataSet, int valuePhaseCount, float startX, float step, int offset, float max, float min) {
        int i = 0;
        Path cache1 = new Path();
        Path cache2 = new Path();
        //当数据1 > 数据2 将cache1连接控件底部形成不规则封闭图形 cache2连接控件顶部 取交集部分绘制
        boolean isCloseToBottom;
        boolean preStatus = false;
        boolean newPath = true;
        float pathStartX = 0;

        ArrayList<PartLineData> paths = new ArrayList<>();


        for (; i < valuePhaseCount && i < lineDataSet.getValues().size(); i++) {
            PointValue point = lineDataSet.getEntryForIndex(i);

            if (point.isValueNaN()) {
                continue;
            }

            isCloseToBottom = point.getValue() > point.getSecondValue();
//


            float xPosition = startX + step * (i + offset);
            float yPosition = (max - point.getValue()) / (max - min) * mContentRect.height();
            float secondYPosition = (max - point.getSecondValue()) / (max - min) * mContentRect.height();

            point.setCoordinate(xPosition, yPosition);
            point.setSecondY(secondYPosition);


            if (newPath) {
                cache1.moveTo(xPosition, yPosition);
                cache2.moveTo(xPosition, secondYPosition);
                newPath = false;
                pathStartX = xPosition;
            } else {
                cache1.lineTo(xPosition, yPosition);
                cache2.lineTo(xPosition, secondYPosition);
            }

            if (i > 0 && i == lineDataSet.getValues().size() - 1) { //结尾部分

                //找到上一个点 做为起点
                PointValue prePoint = lineDataSet.getEntryForIndex(i - 1);
                boolean special = prePoint.getValue() > prePoint.getSecondValue();
                if (special != isCloseToBottom) {
                    if (special) {
                        cache1.lineTo(xPosition, mContentRect.height());
                        cache1.lineTo(pathStartX, mContentRect.height());
                        cache2.lineTo(xPosition, 0);
                        cache2.lineTo(pathStartX, 0);
                    } else {
                        cache2.lineTo(xPosition, mContentRect.height());
                        cache2.lineTo(pathStartX, mContentRect.height());
                        cache1.lineTo(xPosition, 0);
                        cache1.lineTo(pathStartX, 0);
                    }
                    cache1.close();
                    cache2.close();
                    cache1.op(cache2, Path.Op.INTERSECT);
                    paths.add(new PartLineData(cache1, prePoint.getPathColor()));

                    cache1 = new Path();
                    cache2 = new Path();

                    pathStartX = prePoint.getX();

                    //相交代表 本来数据比较高的线变成低 低的线变成高


                    cache1.moveTo(prePoint.getX(), prePoint.getY());
                    cache1.lineTo(xPosition, yPosition);

                    cache2.moveTo(prePoint.getX(), prePoint.getSecondY());
                    cache2.lineTo(xPosition, secondYPosition);

                }

                if (isCloseToBottom) {
                    cache1.lineTo(xPosition, mContentRect.height());
                    cache1.lineTo(pathStartX, mContentRect.height());
                    cache2.lineTo(xPosition, 0);
                    cache2.lineTo(pathStartX, 0);
                } else {
                    cache2.lineTo(xPosition, mContentRect.height());
                    cache2.lineTo(pathStartX, mContentRect.height());
                    cache1.lineTo(xPosition, 0);
                    cache1.lineTo(pathStartX, 0);
                }


                cache1.close();
                cache2.close();

                cache1.op(cache2, Path.Op.INTERSECT);
                paths.add(new PartLineData(cache1, point.getPathColor()));

            } else {
                if (preStatus != isCloseToBottom && i > 0) { //相交  两个值 高的连接底部 低的连接顶部
                    if (isCloseToBottom) {
                        cache2.lineTo(xPosition, mContentRect.height());
                        cache2.lineTo(pathStartX, mContentRect.height());
                        cache1.lineTo(xPosition, 0);
                        cache1.lineTo(pathStartX, 0);
                    } else {
                        cache1.lineTo(xPosition, mContentRect.height());
                        cache1.lineTo(pathStartX, mContentRect.height());
                        cache2.lineTo(xPosition, 0);
                        cache2.lineTo(pathStartX, 0);
                    }
                    cache1.close();
                    cache2.close();
                    cache1.op(cache2, Path.Op.INTERSECT);


                    //找到上一个点 做为起点
                    PointValue prePoint = lineDataSet.getEntryForIndex(i - 1);

                    paths.add(new PartLineData(cache1, prePoint.getPathColor()));
                    cache1 = new Path();
                    cache2 = new Path();

                    pathStartX = prePoint.getX();

                    //相交代表 本来数据比较高的线变成低 低的线变成高


                    cache1.moveTo(prePoint.getX(), prePoint.getY());
                    cache1.lineTo(xPosition, yPosition);

                    cache2.moveTo(prePoint.getX(), prePoint.getSecondY());
                    cache2.lineTo(xPosition, secondYPosition);


                }

            }

            preStatus = isCloseToBottom;
        }

        if (!paths.isEmpty()) {
            for (PartLineData pathData : paths) {
                mRenderPaint.setColor(pathData.getColor());
                mRenderPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(pathData.getPath(), mRenderPaint);
            }
        }

    }

    private float getBaseX(PointValue p1, PointValue p2, float baseY) {
        float x1 = p1.getX();
        float x2 = p2.getX();
        float y1 = Math.abs(p1.getY() - baseY);
        float y2 = Math.abs(p2.getY() - baseY);
        return (y1 * x2 + x1 * y2) / (y2 + y1);
    }
}
