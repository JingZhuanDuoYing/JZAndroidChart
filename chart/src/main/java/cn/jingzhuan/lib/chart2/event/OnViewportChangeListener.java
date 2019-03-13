package cn.jingzhuan.lib.chart2.event;

import cn.jingzhuan.lib.chart2.Viewport;

/**
 * Created by Donglua on 17/7/24.
 */
@FunctionalInterface
public interface OnViewportChangeListener {

    void onViewportChange(Viewport viewport);

}
