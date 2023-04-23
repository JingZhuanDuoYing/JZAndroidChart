package cn.jingzhuan.lib.chart.component;

/**
 * Created by donglua on 11/22/17.
 */

public interface HasValueYOffset {

  float getMaxValueOffsetPercent();

  float getMinValueOffsetPercent();

  float getOffsetPercent();

  void setMinValueOffsetPercent(float minValueOffsetPercent);

  void setMaxValueOffsetPercent(float maxValueOffsetPercent);

  void setOffsetPercent(float offsetPercent);

}
