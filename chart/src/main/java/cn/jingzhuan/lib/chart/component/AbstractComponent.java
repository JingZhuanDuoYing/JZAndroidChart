package cn.jingzhuan.lib.chart.component;

public abstract class AbstractComponent implements Component {

  private boolean isEnable = true;

  @Override
  public void setEnable(boolean enable) {
    isEnable = enable;
  }

  @Override
  public boolean isEnable() {
    return isEnable;
  }
}
