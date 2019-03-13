package cn.jingzhuan.lib.chart2.event;

import cn.jingzhuan.lib.chart2.component.Highlight;

/**
 * Created by donglua on 8/23/17.
 */
@FunctionalInterface
public interface OnHighlightListener {

    void highlight(Highlight[] highlights);

}
