package cn.jingzhuan.lib.chart2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import cn.jingzhuan.lib.chart2.base.JZBaseChart;
import cn.jingzhuan.lib.chart2.renderer.JZCombineChartRenderer;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZCombineChart extends JZBaseChart {

    public JZCombineChart(Context context) {
        super(context);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZCombineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initChart() {
        super.initChart();
        mRenderer = new JZCombineChartRenderer(this);
    }

    /**
     * 把触摸事件传到RangeRenderer 用于处理拖动区间统计范围
     */
    public boolean onTouchEvent(MotionEvent event) {
        JZCombineChartRenderer renderer = (JZCombineChartRenderer) this.mRenderer;
        if (getRangeEnable() && renderer.rangeRenderer != null) {
            if (renderer.rangeRenderer.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }


}
