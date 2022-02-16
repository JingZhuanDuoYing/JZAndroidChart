package cn.jingzhuan.lib.chart.data;

import java.util.List;

import cn.jingzhuan.lib.chart.Viewport;
import cn.jingzhuan.lib.chart.component.AxisY;

/**
 * Created by Donglua on 17/7/26.
 */

public class MinuteLine extends LineDataSet {

    private float mLastClose = -1;

    public MinuteLine(List<PointValue> pointValues) {
        super(pointValues);
    }

    public MinuteLine(List<PointValue> pointValues, @AxisY.AxisDependency int axisDependency) {
        super(pointValues, axisDependency);
    }

    public float getLastClose() {
        return mLastClose;
    }

    public void setLastClose(float lastClose) {
        this.mLastClose = lastClose;
    }

    @Override
    public void calcMinMax(Viewport viewport) {
        super.calcMinMax(viewport);

        if (mLastClose > 0) {
            if (getValues() != null && getValues().size() > 0) {
                float maxDiff = Math.max(Math.abs(mViewportYMin - mLastClose), Math.abs(mViewportYMax - mLastClose));
                maxDiff = Math.max(mLastClose * 0.01f, maxDiff);
                mViewportYMin = mLastClose - maxDiff;
                mViewportYMax = mLastClose + maxDiff;
            } else {
                mViewportYMin = mLastClose * 0.99f;
                mViewportYMax = mLastClose * 1.01f;
            }
        }
    }

}



