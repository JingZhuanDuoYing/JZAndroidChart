package cn.jingzhuan.lib.chart3.data.dataset

import android.graphics.Rect
import cn.jingzhuan.lib.chart3.Viewport

/**
 * @since 2023-09-05
 * created by lei
 */
interface IDataSet {

    fun calcMinMax(viewport: Viewport)

    fun calcMinMax(viewport: Viewport, content: Rect, max: Float, min: Float)

    fun getEntryCount(): Int

}