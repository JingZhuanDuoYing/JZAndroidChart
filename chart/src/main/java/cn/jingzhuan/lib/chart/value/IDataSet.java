package cn.jingzhuan.lib.chart.value;

/**
 * Created by Donglua on 17/7/19.
 */

public interface IDataSet {

   boolean isVisible();
   void setVisible(boolean visible);

   void calcMinMax();

   int getEntryCount();

}
