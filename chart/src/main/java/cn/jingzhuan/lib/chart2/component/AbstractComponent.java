package cn.jingzhuan.lib.chart2.component;

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
