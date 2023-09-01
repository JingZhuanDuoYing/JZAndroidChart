package cn.jingzhuan.lib.chart3.base;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * @since 2023-09-01
 * created by lei
 */
public abstract class BaseChartView extends View implements IChartView{

    protected Rect mContentRect = new Rect();

    public BaseChartView(Context context) {
        super(context);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
