package cn.jingzhuan.lib.chart.event;

/**
 * @since 2023-05-06
 */
public interface OnScaleStateListener {

    /**
     * 已经缩放到最小
     */
    void onScaleMinimum(boolean minimum);

    /**
     * 已经放大到最大
     */
    void onScaleMaximum(boolean maximum);

}
