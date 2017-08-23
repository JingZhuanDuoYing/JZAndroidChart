package cn.jingzhuan.lib.chart.event;

import android.graphics.Canvas;

import cn.jingzhuan.lib.chart.component.Highlight;

/**
 * Created by donglua on 8/23/17.
 */

public interface OnHighlightRenderListener {

    void highlight(Canvas canvas, Highlight[] highlights);

}
