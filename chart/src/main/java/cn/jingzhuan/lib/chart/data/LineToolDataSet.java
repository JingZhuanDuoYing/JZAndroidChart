package cn.jingzhuan.lib.chart.data;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

/**
 * @since 2023-08-28
 */

public class LineToolDataSet extends AbstractDataSet<LineToolValue> {

    private List<LineToolValue> lineToolValues;

    /**
     * 自绘线类型
     */
    private int lineType = 0;

    /**
     * 自绘线颜色
     */
    private int lineColor;

    /**
     * 自绘线宽度
     */
    private float lineSize;

    /**
     * 字体大小
     */
    private int fontSize;

    /**
     * 字体类型
     */
    private String fontName;

    /**
     * 文本内容
     */
    private String text;

    /**
     * 线形
     */
    private String dash;

    /**
     * 是否除权
     */
    private String bcap;


    public LineToolDataSet(List<LineToolValue> lineToolValues) {
        this(lineToolValues, AxisY.DEPENDENCY_BOTH);
    }

    public LineToolDataSet(List<LineToolValue> lineToolValues, @AxisY.AxisDependency int axisDependency) {
        this.lineToolValues = lineToolValues;
        setAxisDependency(axisDependency);
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        if (lineToolValues == null || lineToolValues.isEmpty()) return;

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        List<LineToolValue> visiblePoints = getVisiblePoints(viewport);

        for (int i = 0; i < visiblePoints.size(); i++) {
            LineToolValue e = visiblePoints.get(i);
            calcViewportMinMax(e);
        }

        float range = mViewportYMax - mViewportYMin;
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMin = mViewportYMin - range * getOffsetPercent();
        }
        if (Float.compare(getOffsetPercent(), 0f) > 0f) {
            mViewportYMax = mViewportYMax + range * getOffsetPercent();
        }
    }


    private void calcViewportMinMax(LineToolValue e) {
        if (e == null || !e.isVisible()) return;

        if (Float.isNaN(e.getValue())) return;

        if (Float.isInfinite(e.getValue())) return;

        if (e.getValue() < mViewportYMin) {
            mViewportYMin = e.getValue();
        }

        if (e.getValue() > mViewportYMax) {
            mViewportYMax = e.getValue();
        }
    }

    @Override
    public int getEntryCount() {
        if (lineToolValues == null) return 0;
        return Math.max(getMinValueCount(), lineToolValues.size());
    }

    @Override
    public void setValues(List<LineToolValue> values) {
        this.lineToolValues = values;
    }

    @Override
    public List<LineToolValue> getValues() {
        return this.lineToolValues;
    }

    @Override
    public boolean addEntry(LineToolValue value) {
        if (value == null)
            return false;

        if (lineToolValues == null) {
            lineToolValues = new ArrayList<>();
        }

        calcViewportMinMax(value);

        return lineToolValues.add(value);
    }

    @Override
    public boolean removeEntry(LineToolValue value) {
        if (value == null) return false;

        calcViewportMinMax(value);

        return lineToolValues.remove(value);
    }

    @Override
    public int getEntryIndex(LineToolValue e) {
        return lineToolValues.indexOf(e);
    }

    @Override
    public LineToolValue getEntryForIndex(int index) {
        return lineToolValues.get(index);
    }

    public int getLineType() {
        return lineType;
    }

    public void setLineType(int lineType) {
        this.lineType = lineType;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getLineSize() {
        return lineSize;
    }

    public void setLineSize(float lineSize) {
        this.lineSize = lineSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDash() {
        return dash;
    }

    public void setDash(String dash) {
        this.dash = dash;
    }

    public String getBcap() {
        return bcap;
    }

    public void setBcap(String bcap) {
        this.bcap = bcap;
    }
}
