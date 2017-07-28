package cn.jingzhuan.lib.chart.component;

import android.graphics.Paint;

/**
 * Created by Donglua on 17/7/17.
 */

public class Labels extends Component {

    private float mLabelTextSize;
    private int mLabelSeparation;
    private int mLabelTextColor;
    private Paint mLabelTextPaint;
    private int mMaxLabelWidth;
    private int mLabelHeight;

    public void setLabelTextSize(float mLabelTextSize) {
        this.mLabelTextSize = mLabelTextSize;
    }

    public void setLabelSeparation(int mLabelSeparation) {
        this.mLabelSeparation = mLabelSeparation;
    }

    public void setLabelTextColor(int mLabelTextColor) {
        this.mLabelTextColor = mLabelTextColor;
    }

    public void setLabelTextPaint(Paint mLabelTextPaint) {
        this.mLabelTextPaint = mLabelTextPaint;
    }

    public void setMaxLabelWidth(int mMaxLabelWidth) {
        this.mMaxLabelWidth = mMaxLabelWidth;
    }

    public void setLabelHeight(int mLabelHeight) {
        this.mLabelHeight = mLabelHeight;
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public int getLabelSeparation() {
        return mLabelSeparation;
    }

    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    public Paint getLabelTextPaint() {
        return mLabelTextPaint;
    }

    public int getMaxLabelWidth() {
        return mMaxLabelWidth;
    }

    public int getLabelHeight() {
        return mLabelHeight;
    }
}
