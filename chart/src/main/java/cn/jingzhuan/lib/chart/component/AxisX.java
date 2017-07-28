package cn.jingzhuan.lib.chart.component;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Donglua on 17/7/17.
 */

public class AxisX extends Axis {

    public final static int TOP = 101;
    public final static int BOTTOM = 102;
    public final static int TOP_INSIDE = 103;
    public final static int BOTTOM_INSIDE = 104;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP, BOTTOM, TOP_INSIDE, BOTTOM_INSIDE})
    @interface AxisXPosition {}

    public AxisX(@AxisXPosition int axisPosition) {
        super(axisPosition);
    }



}
