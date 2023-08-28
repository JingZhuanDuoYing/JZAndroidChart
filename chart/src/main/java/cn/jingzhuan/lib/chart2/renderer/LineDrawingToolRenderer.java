package cn.jingzhuan.lib.chart2.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import cn.jingzhuan.lib.chart.component.Highlight;
import cn.jingzhuan.lib.chart.data.ChartData;
import cn.jingzhuan.lib.chart.data.LineToolData;
import cn.jingzhuan.lib.chart.data.LineToolDataSet;
import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @since 2023-08-28
 * 画线工具Renderer
 */
public class LineDrawingToolRenderer extends AbstractDataRenderer<LineToolDataSet> {

    private LineToolData chartData;

    private final Paint linePaint = new Paint();

    private final Paint bgPaint = new Paint();

    private final Paint textPaint = new Paint();

    /**
     * 当前chart
     */
    private final Chart chart;

    public LineDrawingToolRenderer(Chart chart) {
        super(chart);
        this.chart = chart;
        initPaint();
    }

    public void initPaint() {
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);

        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAlpha(20);

        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<LineToolDataSet> chartData) {
        for (LineToolDataSet dataSet : chartData.getDataSets()) {
            renderDataSet(canvas, chartData, dataSet);
        }
    }

    @Override
    protected void renderDataSet(Canvas canvas, ChartData<LineToolDataSet> chartData, LineToolDataSet dataSet) {
        if (dataSet.isVisible()) {
            linePaint.setColor(dataSet.getLineColor());
            linePaint.setStrokeWidth(dataSet.getLineSize());

            bgPaint.setColor(dataSet.getLineColor());

            textPaint.setTextSize(dataSet.getFontSize());
            textPaint.setColor(dataSet.getLineColor());

            drawDataSet(canvas, dataSet,
                    chartData.getLeftMax(), chartData.getLeftMin(),
                    chartData.getRightMax(), chartData.getRightMin());
        }
    }

    private void drawDataSet(Canvas canvas, LineToolDataSet dataSet, float lMax, float lMin, float rMax, float rMin) {

    }

    @Override
    public void renderHighlighted(Canvas canvas, @NonNull @NotNull Highlight[] highlights) {
    }

    @Override
    public void removeDataSet(LineToolDataSet dataSet) {
        getChartData().remove(dataSet);
        calcDataSetMinMax();
    }

    @Override
    public void clearDataSet() {
        getChartData().clear();
        getChartData().calcMaxMin(mViewport, mContentRect);
    }

    @Override
    protected List<LineToolDataSet> getDataSet() {
        return chartData.getDataSets();
    }

    @Override
    public ChartData<LineToolDataSet> getChartData() {
        if (chartData == null)
            chartData = new LineToolData();
        return chartData;
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        return false;
    }

}

