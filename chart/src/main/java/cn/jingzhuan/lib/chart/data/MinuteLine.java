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
            float maxDiff = Math.max(Math.abs(mYMin - mLastClose), Math.abs(mYMax - mLastClose));
            maxDiff = Math.max(mLastClose * 0.01f, maxDiff);
            mYMin = mLastClose - maxDiff;
            mYMax = mLastClose + maxDiff;
        }
    }

    @Override
    public void calcViewportY(Viewport viewport) {
        mViewportYMax = -Float.MAX_VALUE;
        mViewportYMin = Float.MAX_VALUE;

        for (PointValue e : getVisiblePoints(viewport)) {
            calcViewportMinMax(e);
        }

        if (mLastClose > 0) {
            float maxDiff = Math.max(Math.abs(mViewportYMin - mLastClose), Math.abs(mViewportYMax - mLastClose));
            maxDiff = Math.max(mLastClose * 0.01f, maxDiff);
            mViewportYMin = mLastClose - maxDiff;
            mViewportYMax = mLastClose + maxDiff;
        }

        //setAxisViewportY(mAxisLeft, mViewportYMin, mViewportYMax);
        //setAxisViewportY(mAxisRight, mViewportYMin, mViewportYMax);
    }
}



