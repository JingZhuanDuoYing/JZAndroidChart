package cn.jingzhuan.lib.chart.data;

import cn.jingzhuan.lib.chart.component.XYCoordinate;

/**
 * Created by Donglua on 17/8/1.
 */

public abstract class Value {

  private XYCoordinate coordinate;

  public XYCoordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(XYCoordinate coordinate) {
    this.coordinate = coordinate;
  }
}
