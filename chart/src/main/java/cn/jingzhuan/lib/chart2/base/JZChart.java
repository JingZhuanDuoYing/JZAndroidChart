package cn.jingzhuan.lib.chart2.base;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.jingzhuan.lib.chart2.adapter.IAdapter;

public abstract class JZChart extends Chart{
    private IAdapter mAdapter;

    private int mItemCount;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }

        @Override
        public void onInvalidated() {
            mItemCount = getAdapter().getCount();
            notifyChanged();
        }
    };

    public JZChart(Context context) {
        super(context);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 设置数据适配器
     */
    public void setAdapter(IAdapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(mDataSetObserver);
            mItemCount = mAdapter.getCount();
        } else {
            mItemCount = 0;
        }
        notifyChanged();
    }

    /**
     * 获取适配器
     * @return
     */
    public IAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 重新计算并刷新
     */
    public void notifyChanged() {
        if (mItemCount != 0) {

        } else {
//            setScrollX(0);
        }
        postInvalidate();
    }
}
