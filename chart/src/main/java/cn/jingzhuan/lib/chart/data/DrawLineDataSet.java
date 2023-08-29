package cn.jingzhuan.lib.chart.data;

import java.util.ArrayList;
import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

/**
 * @since 2023-08-28
 */

public class DrawLineDataSet extends AbstractDataSet<DrawLineValue> {

    private List<DrawLineValue> drawLineValues;

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


    public DrawLineDataSet(List<DrawLineValue> drawLineValues) {
        this(drawLineValues, AxisY.DEPENDENCY_BOTH);
    }

    public DrawLineDataSet(List<DrawLineValue> drawLineValues, @AxisY.AxisDependency int axisDependency) {
        this.drawLineValues = drawLineValues;
        setAxisDependency(axisDependency);
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        if (drawLineValues == null || drawLineValues.isEmpty()) return;

        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        List<DrawLineValue> visiblePoints = getVisiblePoints(viewport);

        for (int i = 0; i < visiblePoints.size(); i++) {
            DrawLineValue e = visiblePoints.get(i);
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


    private void calcViewportMinMax(DrawLineValue e) {
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
        if (drawLineValues == null) return 0;
        return Math.max(getMinValueCount(), drawLineValues.size());
    }

    @Override
    public void setValues(List<DrawLineValue> values) {
        this.drawLineValues = values;
    }

    @Override
    public List<DrawLineValue> getValues() {
        return this.drawLineValues;
    }

    @Override
    public boolean addEntry(DrawLineValue value) {
        if (value == null)
            return false;

        if (drawLineValues == null) {
            drawLineValues = new ArrayList<>();
        }

        calcViewportMinMax(value);

        return drawLineValues.add(value);
    }

    @Override
    public boolean removeEntry(DrawLineValue value) {
        if (value == null) return false;

        calcViewportMinMax(value);

        return drawLineValues.remove(value);
    }

    @Override
    public int getEntryIndex(DrawLineValue e) {
        return drawLineValues.indexOf(e);
    }

    @Override
    public DrawLineValue getEntryForIndex(int index) {
        return drawLineValues.get(index);
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
