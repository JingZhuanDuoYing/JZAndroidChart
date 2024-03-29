package cn.jingzhuan.lib.chart.data;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;

public class ScatterTextDataSet extends AbstractDataSet<ScatterTextValue>{

    public final static int ALIGN_TOP = 1;
    public final static int ALIGN_BOTTOM = 2;

    private List<ScatterTextValue> scatterTextValues;
    private int textBgColor;
    private int lineColor;
    private int frameColor;
    private String text;
    private int textColor;
    private int textSize = 11;
    private int textPadding = 10;
    private int lineDashHeight = 40;

    /**
     * 临时字段 只适合一个字的背景
     */
    private boolean bgCircle = false;
    private int align = ALIGN_TOP;

    private int mForceValueCount = -1;

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

    public int getFrameColor() {
        return frameColor;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
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
    public void calcMinMax(Viewport viewport) {
        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        List<ScatterTextValue> list = getVisiblePoints(viewport);

        if (list.size() == 1) {
            mViewportYMin = list.get(0).getLow();
            mViewportYMax = list.get(0).getHigh();
            float range = mViewportYMax - mViewportYMin;
            mViewportYMin = mViewportYMin - range * 0.2f;
            return;
        }

        for (ScatterTextValue e : list) {
            calcViewportMinMax(e);
        }
    }

    @Override
    public int getEntryCount() {
        if (getValues() == null) return 0;
        if (mForceValueCount > 0) return mForceValueCount;
        return Math.max(getMinValueCount(), getValues().size());
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * @return 获取文本边距
     */
    public int getTextPadding() {
        return this.textPadding;
    }

    /**
     * @param padding 文本边距
     */
    public void setTextPadding(int padding) {
        this.textPadding = padding;
    }

    /**
     * @return 获取虚线长度
     */
    public int getLineDashHeight() {
        return this.lineDashHeight;
    }

    /**
     * @param dashHeight 虚线长度
     */
    public void setLineDashLength(int dashHeight) {
        this.lineDashHeight = dashHeight;
    }

    public boolean isBgCircle() {
        return bgCircle;
    }

    public void setBgCircle(boolean bgCircle) {
        this.bgCircle = bgCircle;
    }

    public void setForceValueCount(int mForceValueCount) {
        this.mForceValueCount = mForceValueCount;
    }

    public int getForceValueCount() {
        return mForceValueCount;
    }
}
