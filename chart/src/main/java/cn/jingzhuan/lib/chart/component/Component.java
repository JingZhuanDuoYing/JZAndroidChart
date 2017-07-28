package cn.jingzhuan.lib.chart.component;

abstract class Component {

  private boolean isEnable = true;

  public void setEnable(boolean enable) {
    isEnable = enable;
  }

  public boolean isEnable() {
    return isEnable;
  }
}
