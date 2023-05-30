package cn.jingzhuan.lib.chart.event;

import cn.jingzhuan.lib.chart.Viewport;

/**
 * Created by Donglua on 17/7/24.
 */
@FunctionalInterface
public interface OnViewportChangeListener {

    void onViewportChange(Viewport viewport);

}
