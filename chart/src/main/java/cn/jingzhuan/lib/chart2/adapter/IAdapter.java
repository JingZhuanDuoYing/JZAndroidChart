package cn.jingzhuan.lib.chart2.adapter;

import android.database.DataSetObserver;

import java.util.List;

import cn.jingzhuan.lib.chart.data.AbstractDataSet;

/**
 * @author YL
 * @since 2023-08-04
 */
public interface IAdapter {

    /**
     * 获取点的数目
     *
     * @return
     */
    int getCount();

    /**
     * 通过序号获取item
     */
    Object getItem(int position);

    List<AbstractDataSet> getDataSets();

    /**
     * 注册一个数据观察者
     *
     * @param observer 数据观察者
     */
    void registerDataSetObserver(DataSetObserver observer);

    /**
     * 移除一个数据观察者
     *
     * @param observer 数据观察者
     */
    void unregisterDataSetObserver(DataSetObserver observer);

    /**
     * 当数据发生变化时调用
     */
    void notifyDataSetChanged();

}
