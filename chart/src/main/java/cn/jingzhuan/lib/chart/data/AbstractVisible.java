package cn.jingzhuan.lib.chart.data;

/**
 * Created by Donglua on 17/8/2.
 */

abstract class AbstractVisible {

    private boolean isVisible = true;

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

}
