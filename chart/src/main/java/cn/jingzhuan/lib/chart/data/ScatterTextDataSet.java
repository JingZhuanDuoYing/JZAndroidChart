package cn.jingzhuan.lib.chart.data;

import android.graphics.Rect;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

public class ScatterTextDataSet extends AbstractDataSet<ScatterTextValue>{

    private List<ScatterTextValue> scatterTextValues;
    private int textBgColor;
    private int lineColor;
    private String text;
    private int textSize = 11;

    public int getTextBgColor() {
        return textBgColor;
    }

    public void setTextBgColor(int textBgColor) {
        this.textBgColor = textBgColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ScatterTextDataSet(List<ScatterTextValue> scatterTextValues) {
        this.scatterTextValues = scatterTextValues;
    }


    @Override
    public List<ScatterTextValue> getValues() {
        return scatterTextValues;
    }

    @Override
    public void setValues(List<ScatterTextValue> values) {
        this.scatterTextValues = values;
    }
    private void calcViewportMinMax(ScatterTextValue e) {
//        if (Float.isNaN(e.getHigh())) return;
//        if (e.getHigh() < mViewportYMin)
//            mViewportYMin = e.getHigh();
//
//        if (e.getHigh() > mViewportYMax)
//            mViewportYMax = e.getHigh();
    }
    @Override
    public boolean addEntry(ScatterTextValue e) {
        calcViewportMinMax(e);
        return scatterTextValues.add(e);
    }

    @Override
    public boolean removeEntry(ScatterTextValue e) {
        return scatterTextValues.remove(e);
    }

    @Override
    public int getEntryIndex(ScatterTextValue e) {
        return scatterTextValues.indexOf(e);
    }

    @Override
    public ScatterTextValue getEntryForIndex(int index) {
        return scatterTextValues.get(index);
    }

    @Override
    public void calcMinMax(Viewport viewport, Rect content) {
        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        for (ScatterTextValue e : getVisiblePoints(viewport)) {
            calcViewportMinMax(e);
        }
    }

    @Override
    public int getEntryCount() {
        if (getValues() == null) return 0;
        int entryCount = getValues().size();
        return getMinValueCount() > entryCount ? getMinValueCount() : entryCount;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

}
