package cn.jingzhuan.lib.chart.component;

/**
 * Created by donglua on 11/22/17.
 */

public interface HasValueOffset {

  float getMaxValueOffsetPercent();

  float getMinValueOffsetPercent();

  void setMinValueOffsetPercent(float minValueOffsetPercent);

  void setMaxValueOffsetPercent(float maxValueOffsetPercent);
}
