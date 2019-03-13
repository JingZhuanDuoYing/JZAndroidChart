package cn.jingzhuan.lib.chart2.event;

import cn.jingzhuan.lib.chart2.component.Highlight;

/**
 * Created by donglua on 8/22/17.
 */

public interface HighlightStatusChangeListener {

    void onHighlightShow(Highlight[] highlights);

    void onHighlightHide();

}
