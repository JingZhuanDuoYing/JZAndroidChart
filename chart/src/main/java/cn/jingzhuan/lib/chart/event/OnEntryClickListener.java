package cn.jingzhuan.lib.chart.event;

import cn.jingzhuan.lib.chart.base.Chart;

/**
 * Created by donglua on 11/20/17.
 */
@FunctionalInterface
public interface OnEntryClickListener {

  void onEntryClick(Chart chart, int position);

}
