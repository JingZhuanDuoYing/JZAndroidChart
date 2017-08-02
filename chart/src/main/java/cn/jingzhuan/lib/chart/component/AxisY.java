package cn.jingzhuan.lib.chart.component;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.jingzhuan.lib.chart.data.LabelColorSetter;

/**
 * Created by Donglua on 17/7/17.
 */
public class AxisY extends Axis {

    public final static int LEFT_OUTSIDE = 111;
    public final static int LEFT_INSIDE = 112;
    public final static int RIGHT_OUTSIDE = 113;
    public final static int RIGHT_INSIDE = 114;

    public final static int DEPENDENCY_LEFT = 23;
    public final static int DEPENDENCY_RIGHT = 24;
    public final static int DEPENDENCY_BOTH = 25;
    private float mYMin;
    private float mYMax;

    private LabelColorSetter mLabelColorSetter;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DEPENDENCY_LEFT, DEPENDENCY_RIGHT, DEPENDENCY_BOTH})
    public @interface AxisDependency{}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEFT_OUTSIDE, LEFT_INSIDE, RIGHT_OUTSIDE, RIGHT_INSIDE})
    public @interface AxisYPosition {}

    public AxisY(int axisPosition) {
        super(axisPosition);
    }

    public void setYMin(float yMin) {
        this.mYMin = yMin;
    }

    public void setYMax(float yMax) {
        this.mYMax = yMax;
    }

    public float getYMin() {
        return mYMin;
    }

    public float getYMax() {
        return mYMax;
    }

    public LabelColorSetter getLabelColorSetter() {
        return mLabelColorSetter;
    }

    public void setLabelColorSetter(LabelColorSetter mLabelColorSetter) {
        this.mLabelColorSetter = mLabelColorSetter;
    }
}
