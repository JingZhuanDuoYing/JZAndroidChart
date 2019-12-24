package cn.jingzhuan.lib.chart2.demo

import android.databinding.ViewDataBinding
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import cn.jingzhuan.lib.chart.data.BarDataSet
import cn.jingzhuan.lib.chart.data.BarValue
import cn.jingzhuan.lib.chart2.demo.databinding.LayoutBarChartBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import java.util.ArrayList

/**
 * Created by Donglua on 17/8/2.
 */
@EpoxyModelClass(layout = R.layout.layout_bar_chart)
abstract class BarChartModel : DataBindingEpoxyModel() {

  private val barDataSet: BarDataSet

  init {

    val barValueList = ArrayList<BarValue>()

    barValueList.add(BarValue(11f))
    barValueList.add(BarValue(10f))
    barValueList.add(BarValue(11f))
    barValueList.add(BarValue(13f))
    barValueList.add(BarValue(11f))
    barValueList.add(BarValue(12f))
    barValueList.add(BarValue(12f))
    //设置柱形图的颜色
    barValueList.add(BarValue(13f).apply { setGradientColors(Color.RED, Color.BLACK) })
    barValueList.add(BarValue(15f).apply { setGradientColors(Color.WHITE, Color.BLACK) })

    barDataSet = BarDataSet(barValueList)
//    设置自动设置柱形图宽
//    barDataSet.isAutoBarWidth = true
    barDataSet.barWidth=80f
  }

  override fun buildView(parent: ViewGroup): View {
    val rootView = super.buildView(parent)

    val barBinding = rootView.tag as LayoutBarChartBinding

    barBinding.barChart.setDataSet(barDataSet)
    barBinding.barChart.axisRight.labelTextColor = Color.BLACK

    return rootView
  }

  override fun setDataBindingVariables(binding: ViewDataBinding) {
    binding as LayoutBarChartBinding

    binding.barChart.animateY(500)
  }
}
