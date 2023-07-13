package cn.jingzhuan.lib.chart.event;


import cn.jingzhuan.lib.chart2.base.Chart;

/**
 * @since 2023-07-13
 */
@FunctionalInterface
public interface OnSingleEntryClickListener {

  void onEntryClick(Chart chart, int position);

}
