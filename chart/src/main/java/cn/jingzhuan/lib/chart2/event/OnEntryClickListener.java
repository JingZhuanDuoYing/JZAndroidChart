package cn.jingzhuan.lib.chart2.event;

import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * Created by donglua on 11/20/17.
 */
@FunctionalInterface
public interface OnEntryClickListener {

  void onEntryClick(Chart chart, int position);

}
