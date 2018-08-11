package cn.jingzhuan.lib.chart;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Donglua on 17/7/17.
 */

public class Viewport extends RectF {

    public static final float AXIS_X_MIN = 0f;
    public static final float AXIS_X_MAX = 1f;
    public static final float AXIS_Y_MIN = -1f;
    public static final float AXIS_Y_MAX = 1f;

    public Viewport() {
        this(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);
    }

    public Viewport(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public Viewport(RectF r) {
        super(r);
    }

    public Viewport(Rect r) {
        super(r);
    }

    /**
     * Ensures that current viewport is inside the viewport extremes defined by {@link #AXIS_X_MIN},
     * {@link #AXIS_X_MAX}, {@link #AXIS_Y_MIN} and {@link #AXIS_Y_MAX}.
     */
    public void constrainViewport() {
        left = Math.max(AXIS_X_MIN, left);
        right = Math.max(Math.nextUp(left), Math.min(AXIS_X_MAX, right));
    }

}
