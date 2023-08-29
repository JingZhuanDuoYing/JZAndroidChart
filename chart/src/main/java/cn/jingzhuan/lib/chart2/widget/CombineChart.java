package cn.jingzhuan.lib.chart2.widget;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.DrawLineDataSet;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.TreeDataSet;
import cn.jingzhuan.lib.chart2.base.BaseChart;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart2.renderer.CombineChartRenderer;
import java.util.List;

/**
 * Created by Donglua on 17/8/2.
 */

public class CombineChart extends BaseChart {

    public CombineChart(Context context) {
        super(context);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();
        mRenderer = new CombineChartRenderer(this);
    }

    public void addDataSet(AbstractDataSet abstractDataSet) {
        getRenderer().addDataSet(abstractDataSet);
    }

    public void setDataSet(AbstractDataSet dataSet) {
        cleanAllDataSet();

        addDataSet(dataSet);
    }

    public <T extends AbstractDataSet> void addAll(List<T> dataSets) {
        for (AbstractDataSet dataSet : dataSets) {
            addDataSet(dataSet);
        }
    }

    public void setCombineData(final CombineData combineData) {
        cleanAllDataSet();
        int entryCount = 0;
        for (TreeDataSet treeDataSet : combineData.getTreeData()) {
            addDataSet(treeDataSet);
            entryCount = Math.max(entryCount, treeDataSet.getValues().size());
        }
        for (LineDataSet lineDataSet : combineData.getLineData()) {
            addDataSet(lineDataSet);
            entryCount = Math.max(entryCount, lineDataSet.getValues().size());
        }
        for (BarDataSet barDataSet : combineData.getBarData()) {
            addDataSet(barDataSet);
            entryCount = Math.max(entryCount, barDataSet.getValues().size());
        }
        for (CandlestickDataSet candlestickDataSet : combineData.getCandlestickData()) {
            addDataSet(candlestickDataSet);
            entryCount = Math.max(entryCount, candlestickDataSet.getValues().size());
        }
        for (ScatterDataSet scatterDataSet : combineData.getScatterData()) {
            addDataSet(scatterDataSet);
            entryCount = Math.max(entryCount, scatterDataSet.getValues().size());
        }
        for (PointLineDataSet pointLineDataSet : combineData.getPointLineData()) {
            addDataSet(pointLineDataSet);
            entryCount = Math.max(entryCount, pointLineDataSet.getValues().size());
        }
        for (ScatterTextDataSet scatterTextDataSet : combineData.getScatterTextData()) {
            addDataSet(scatterTextDataSet);
            entryCount = Math.max(entryCount, scatterTextDataSet.getValues().size());
        }
        for (DrawLineDataSet drawLineDataSet : combineData.getDrawLineData()){
            addDataSet(drawLineDataSet);
        }
        setEntryCount(entryCount);

        if (!mCurrentViewport.initialized() && entryCount > 0) {
            // 移动到最新的K线
            Viewport newViewport = mCurrentViewport.moveToEnd();
            int visibleSize = getCurrentVisibleEntryCount();


            if (visibleSize > 0) {
                float viewportWidth = (float) visibleSize / entryCount;
                if (entryCount >= visibleSize) {
                    newViewport.left = newViewport.right - viewportWidth;
                } else {
                    newViewport.left = 0f;
                    newViewport.right = viewportWidth;
                }
            }
            setCurrentViewport(newViewport);
        }
    }

    public <T extends AbstractDataSet> void setData(List<T> data) {
        cleanAllDataSet();

        for (T datum : data) {
            addDataSet(datum);
        }
    }

    public List<LineDataSet> getLineDataSet() {
        return getRenderer().getChartData().getLineData();
    }

    public List<BarDataSet> getBarDataSet() {
        return getRenderer().getChartData().getBarData();
    }

    public List<CandlestickDataSet> getCandlestickDataSet() {
        return getRenderer().getChartData().getCandlestickData();
    }

    public List<PointLineDataSet> getPointLineDataSet() {
        return getRenderer().getChartData().getPointLineData();
    }

    public List<ScatterDataSet> getScatterDataSet() {
        return getRenderer().getChartData().getScatterData();
    }

    public CombineChartRenderer getRenderer() {
        return (CombineChartRenderer) mRenderer;
    }

    @Override
    public void setTypeface(Typeface tf) {
        mRenderer.setTypeface(tf);
        super.setTypeface(tf);
    }

    public void cleanLineDataSet() {
        getRenderer().cleanLineDataSet();
    }

    public void cleanBarDataSet() {
        getRenderer().cleanBarDataSet();
    }

    public void cleanCandlestickDataSet() {
        getRenderer().cleanCandlestickDataSet();
    }

    public void cleanScatterDataSet() {
        getRenderer().cleanScatterDataSet();
    }

    public void cleanRangeDataSet() {
        getRenderer().cleanRangeDataSet();
    }

    public void cleanAllDataSet() {
        getRenderer().clearDataSet();
    }

    public void addLine(LineDataSet lineDataSet) {
        mRenderer.addDataSet(lineDataSet);
    }

    public void setLine(LineDataSet lineDataSet) {
        mRenderer.clearDataSet();
        addLine(lineDataSet);
    }

    /**
     * 把触摸事件传到Renderer
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (getRangeEnable() && getRenderer().rangeRenderer != null) {
            if (getRenderer().rangeRenderer.onTouchEvent(event)) {
                return true;
            }
        }
//        // 开启了画线模式
//        if (isOpenDrawLine() && getRenderer().getDrawLineRenderer() != null) {
//            if (getRenderer().getDrawLineRenderer().onTouchEvent(event)) {
//                return true;
//            }
//        }
        return super.onTouchEvent(event);
    }


}
