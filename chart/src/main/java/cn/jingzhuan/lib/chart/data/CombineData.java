package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * Created by Donglua on 17/8/2.
 */
public class CombineData extends ChartData<AbstractDataSet<?>> {

    private final TreeData treeData;
    private final BarData barData;
    private final LineData lineData;
    private final CandlestickData candlestickData;
    private final ScatterData scatterData;
    private final PointLineData pointLineData;
    private final ScatterTextData scatterTextData;

    public CombineData() {
        treeData = new TreeData();
        barData = new BarData();
        lineData = new LineData();
        candlestickData = new CandlestickData();
        scatterData = new ScatterData();
        pointLineData = new PointLineData();
        scatterTextData = new ScatterTextData();
    }

    public List<BarDataSet> getBarData() {
        return barData.getDataSets();
    }

    public List<LineDataSet> getLineData() {
        return lineData.getDataSets();
    }

    public List<CandlestickDataSet> getCandlestickData() {
        return candlestickData.getDataSets();
    }

    public List<ScatterDataSet> getScatterData() {
        return scatterData.getDataSets();
    }

    public BarData getBarChartData() {
        return barData;
    }

    public LineData getLineChartData() {
        return lineData;
    }

    public List<PointLineDataSet> getPointLineData() {
        return pointLineData.getDataSets();
    }

    public PointLineData getPointLineChartData() {
        return pointLineData;
    }

    public CandlestickData getCandlestickChartData() {
        return candlestickData;
    }

    public ScatterData getScatterChartData() {
        return scatterData;
    }

    public List<ScatterTextDataSet> getScatterTextData() {
        return scatterTextData.getDataSets();
    }

    public ScatterTextData getScatterTextChartData() {
        return scatterTextData;
    }

    public List<TreeDataSet> getTreeData() {
        return treeData.getDataSets();
    }

    public TreeData getTreeChartData() {
        return treeData;
    }

    public boolean addDataSet(BarDataSet dataSet) {
        return barData.add(dataSet);
    }

    public boolean addDataSet(LineDataSet dataSet) {
        return lineData.add(dataSet);
    }

    public boolean addDataSet(CandlestickDataSet dataSet) {
        return candlestickData.add(dataSet);
    }

    public boolean addDataSet(ScatterDataSet dataSet) {
        return scatterData.add(dataSet);
    }

    public boolean addDataSet(PointLineDataSet dataSet) {
        return pointLineData.add(dataSet);
    }

    public boolean addDataSet(ScatterTextDataSet dataSet) {
        return scatterTextData.add(dataSet);
    }

    public boolean addDataSet(TreeDataSet dataSet) {
        return treeData.add(dataSet);
    }

    public void setCombineData(CombineData combineData) {
        this.leftMin = combineData.leftMin;
        this.rightMin = combineData.rightMin;
        this.leftMax = combineData.leftMax;
        this.rightMax = combineData.rightMax;

        treeData.setLeftMax(leftMax);
        barData.setLeftMax(leftMax);
        lineData.setLeftMax(leftMax);
        candlestickData.setLeftMax(leftMax);
        scatterData.setLeftMax(leftMax);
        pointLineData.setLeftMax(leftMax);
        scatterTextData.setLeftMax(leftMax);

        treeData.setLeftMin(leftMin);
        barData.setLeftMin(leftMin);
        lineData.setLeftMin(leftMin);
        candlestickData.setLeftMin(leftMin);
        scatterData.setLeftMin(leftMin);
        pointLineData.setLeftMin(leftMin);
        scatterTextData.setLeftMin(leftMin);

        treeData.setRightMax(rightMax);
        barData.setRightMax(rightMax);
        lineData.setRightMax(rightMax);
        candlestickData.setRightMax(rightMax);
        scatterData.setRightMax(rightMax);
        pointLineData.setRightMax(rightMax);
        scatterTextData.setRightMax(rightMax);

        treeData.setRightMin(rightMin);
        barData.setRightMin(rightMin);
        lineData.setRightMin(rightMin);
        candlestickData.setRightMin(rightMin);
        scatterData.setRightMin(rightMin);
        pointLineData.setRightMin(rightMin);
        scatterTextData.setRightMin(rightMin);

        treeData.getDataSets().addAll(combineData.getTreeData());
        barData.getDataSets().addAll(combineData.getBarData());
        lineData.getDataSets().addAll(combineData.getLineData());
        candlestickData.getDataSets().addAll(combineData.getCandlestickData());
        scatterData.getDataSets().addAll(combineData.getScatterData());
        pointLineData.getDataSets().addAll(combineData.getPointLineData());
        scatterTextData.getDataSets().addAll(combineData.getScatterTextData());
    }

    @Override
    public void calcMaxMin(Viewport viewport, Rect content) {
        leftMin = Float.MAX_VALUE;
        leftMax = -Float.MAX_VALUE;
        rightMin = Float.MAX_VALUE;
        rightMax = -Float.MAX_VALUE;

        if (!treeData.getDataSets().isEmpty()) {
            treeData.calcMaxMin(viewport, content);
            leftMin = Math.min(treeData.leftMin, leftMin);
            leftMax = Math.max(treeData.leftMax, leftMax);
            rightMin = Math.min(treeData.rightMin, rightMin);
            rightMax = Math.max(treeData.rightMax, rightMax);
        }

        if (!candlestickData.getDataSets().isEmpty()) {
            candlestickData.calcMaxMin(viewport, content);
            leftMin = Math.min(candlestickData.leftMin, leftMin);
            leftMax = Math.max(candlestickData.leftMax, leftMax);
            rightMin = Math.min(candlestickData.rightMin, rightMin);
            rightMax = Math.max(candlestickData.rightMax, rightMax);
        }

        if (!lineData.getDataSets().isEmpty()) {
            lineData.calcMaxMin(viewport, content);
            leftMin = Math.min(lineData.leftMin, leftMin);
            leftMax = Math.max(lineData.leftMax, leftMax);
            rightMin = Math.min(lineData.rightMin, rightMin);
            rightMax = Math.max(lineData.rightMax, rightMax);
        }

        if (!barData.getDataSets().isEmpty()) {
            barData.calcMaxMin(viewport, content);
            leftMin = Math.min(barData.leftMin, leftMin);
            leftMax = Math.max(barData.leftMax, leftMax);
            rightMin = Math.min(barData.rightMin, rightMin);
            rightMax = Math.max(barData.rightMax, rightMax);
        }

        if (!pointLineData.getDataSets().isEmpty()) {
            pointLineData.calcMaxMin(viewport, content);
            leftMin = Math.min(pointLineData.leftMin, leftMin);
            leftMax = Math.max(pointLineData.leftMax, leftMax);
            rightMin = Math.min(pointLineData.rightMin, rightMin);
            rightMax = Math.max(pointLineData.rightMax, rightMax);
        }

        if (!scatterTextData.getDataSets().isEmpty()) {
            scatterTextData.calcMaxMin(viewport, content);
            leftMin = Math.min(scatterTextData.leftMin, leftMin);
            leftMax = Math.max(scatterTextData.leftMax, leftMax);
            rightMin = Math.min(scatterTextData.rightMin, rightMin);
            rightMax = Math.max(scatterTextData.rightMax, rightMax);
        }

//        Log.d("CombineData", "calcMaxMin_0 leftMax:" + leftMax
//                + ", leftMin:" + leftMin + ", rightMax:" + rightMax + ", rightMin:" + rightMin);
        /**
         * ScatterData的calcMaxMin放在其它Data后面调用，利用其它Data计算好的leftMax, leftMin, rightMax, rightMin，计算要扩展的数值。
         * **/
        if (!scatterData.getDataSets().isEmpty()) {
            scatterData.calcMaxMin(viewport, content, leftMax, leftMin, rightMax, rightMin);
            leftMin = Math.min(scatterData.leftMin, leftMin);
            leftMax = Math.max(scatterData.leftMax, leftMax);
            rightMin = Math.min(scatterData.rightMin, rightMin);
            rightMax = Math.max(scatterData.rightMax, rightMax);
        }
//        Log.d("CombineData", "calcMaxMin_1 leftMax:" + leftMax
//                + ", leftMin:" + leftMin + ", rightMax:" + rightMax + ", rightMin:" + rightMin);

        treeData.setLeftMax(leftMax);
        barData.setLeftMax(leftMax);
        lineData.setLeftMax(leftMax);
        candlestickData.setLeftMax(leftMax);
        pointLineData.setLeftMax(leftMax);
        scatterTextData.setLeftMax(leftMax);
        scatterData.setLeftMax(leftMax);

        treeData.setLeftMin(leftMin);
        barData.setLeftMin(leftMin);
        lineData.setLeftMin(leftMin);
        candlestickData.setLeftMin(leftMin);
        pointLineData.setLeftMin(leftMin);
        scatterTextData.setLeftMin(leftMin);
        scatterData.setLeftMin(leftMin);

        treeData.setRightMax(rightMax);
        barData.setRightMax(rightMax);
        lineData.setRightMax(rightMax);
        candlestickData.setRightMax(rightMax);
        pointLineData.setRightMax(rightMax);
        scatterTextData.setRightMax(rightMax);
        scatterData.setRightMax(rightMax);

        treeData.setRightMin(rightMin);
        barData.setRightMin(rightMin);
        lineData.setRightMin(rightMin);
        candlestickData.setRightMin(rightMin);
        pointLineData.setRightMin(rightMin);
        scatterTextData.setRightMin(rightMin);
        scatterData.setRightMin(rightMin);

        setMinMax();
    }

    @Override
    public boolean add(AbstractDataSet<?> e) {
        if (e instanceof TreeDataSet) {
            return addDataSet((TreeDataSet) e);
        }
        if (e instanceof CandlestickDataSet) {
            return addDataSet((CandlestickDataSet) e);
        }
        if (e instanceof LineDataSet) {
            return addDataSet((LineDataSet) e);
        }
        if (e instanceof BarDataSet) {
            return addDataSet((BarDataSet) e);
        }
        if (e instanceof ScatterDataSet) {
            return addDataSet((ScatterDataSet) e);
        }
        if (e instanceof PointLineDataSet) {
            return addDataSet((PointLineDataSet) e);
        }
        if (e instanceof ScatterTextDataSet) {
            return addDataSet((ScatterTextDataSet) e);
        }
        return super.add(e);
    }

    @Override
    public void setMinMax() {
        if (leftAxis != null) {
            leftAxis.setYMin(leftMin);
            leftAxis.setYMax(leftMax);
        }
        if (rightAxis != null) {
            rightAxis.setYMin(rightMin);
            rightAxis.setYMax(rightMax);
        }
    }


    public <T extends AbstractDataSet<?>> void addAll(List<T> dataSets) {
        for (AbstractDataSet<?> dataSet : dataSets) {
            add(dataSet);
        }
    }

    public List<AbstractDataSet<?>> getAllDataSet() {
        List<AbstractDataSet<?>> allDataSet = Collections.synchronizedList(new ArrayList<>());
        // 按分类顺序添加，当drawIndex是默认值-1时，按下列顺序绘制
        allDataSet.addAll(treeData.getDataSets());
        allDataSet.addAll(barData.getDataSets());
        allDataSet.addAll(candlestickData.getDataSets());
        allDataSet.addAll(lineData.getDataSets());
        allDataSet.addAll(scatterData.getDataSets());
        allDataSet.addAll(pointLineData.getDataSets());
        allDataSet.addAll(scatterTextData.getDataSets());

        Collections.sort(allDataSet, new Comparator<AbstractDataSet>() {
            @Override
            public int compare(AbstractDataSet dataSet1, AbstractDataSet dataSet2) {
                return dataSet1.getDrawIndex() - dataSet2.getDrawIndex();
            }
        });
        return allDataSet;
    }
}
