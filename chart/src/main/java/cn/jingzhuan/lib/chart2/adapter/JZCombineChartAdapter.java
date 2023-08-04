package cn.jingzhuan.lib.chart2.adapter;

import java.util.List;

import cn.jingzhuan.lib.chart.data.AbstractDataSet;
import cn.jingzhuan.lib.chart.data.BarDataSet;
import cn.jingzhuan.lib.chart.data.CandlestickDataSet;
import cn.jingzhuan.lib.chart.data.CombineData;
import cn.jingzhuan.lib.chart.data.LineDataSet;
import cn.jingzhuan.lib.chart.data.PointLineDataSet;
import cn.jingzhuan.lib.chart.data.ScatterDataSet;
import cn.jingzhuan.lib.chart.data.ScatterTextDataSet;
import cn.jingzhuan.lib.chart.data.TreeDataSet;

/**
 * @author YL
 * @since 2023-08-04
 */
public class JZCombineChartAdapter extends BaseChartAdapter {
    private CombineData combineData = new CombineData();


    public JZCombineChartAdapter() {
    }

    public void setData(CombineData data) {
        combineData.clear();
        combineData.setCombineData(data);
    }

    public void addData(CombineData data) {
        combineData.addAll(data.getDataSets());
    }

    public CombineData getData() {
        return this.combineData;
    }

    @Override
    public List<AbstractDataSet> getDataSets() {
        return combineData.getDataSets();
    }

    @Override
    public int getCount() {
        return combineData.getEntryCount();
    }

    @Override
    public Object getItem(int position) {
        synchronized (combineData.getDataSets()) {
            for (Object t : combineData.getDataSets()) {
                if (t instanceof TreeDataSet) {
                    ((TreeDataSet) t).getValues().get(position);
                }
                if (t instanceof LineDataSet) {
                    ((LineDataSet) t).getValues().get(position);
                }
                if (t instanceof BarDataSet) {
                    ((BarDataSet) t).getValues().get(position);
                }
                if (t instanceof CandlestickDataSet) {
                    ((CandlestickDataSet) t).getValues().get(position);
                }
                if (t instanceof ScatterDataSet) {
                    ((ScatterDataSet) t).getValues().get(position);
                }
                if (t instanceof PointLineDataSet) {
                    ((PointLineDataSet) t).getValues().get(position);
                }
                if (t instanceof ScatterTextDataSet) {
                    ((ScatterTextDataSet) t).getValues().get(position);
                }
            }
        }
        return null;
    }
}
