package cn.jingzhuan.lib.chart.event;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * @since 2023-02-26
 */
public interface OnScaleListener {

    void onScaleStart(Viewport viewport);

    void onScale(Viewport viewport);

    void onScaleEnd(Viewport viewport);

}
