package cn.jingzhuan.lib.chart.event;

import cn.jingzhuan.lib.chart.component.Highlight;

/**
 * Created by donglua on 8/23/17.
 */
@FunctionalInterface
public interface OnHighlightListener {

    void highlight(Highlight[] highlights);

}
