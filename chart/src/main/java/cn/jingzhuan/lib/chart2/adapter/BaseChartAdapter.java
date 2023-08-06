package cn.jingzhuan.lib.chart2.adapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

import cn.jingzhuan.lib.chart.data.IDataSet;

/**
 * @author YL
 * @since 2023-08-04
 * 数据适配器
 */
public abstract class BaseChartAdapter<T extends IDataSet> implements IAdapter<T> {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public void notifyDataSetChanged() {
        if (getCount() > 0) {
            mDataSetObservable.notifyChanged();
        } else {
            mDataSetObservable.notifyInvalidated();
        }
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }
}
