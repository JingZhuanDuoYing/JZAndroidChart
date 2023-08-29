package cn.jingzhuan.lib.chart2.drawline;

/**
 * @since 2023-08-29
 * 画线类型
 */
public enum DrawLineType {
    ltNone,

    // 直线
    ltStraightLine,

    // 线段
    ltSegment,

    // 横线
    ltHorizon,

    // 平行线
    ltParallelLine,

    // 阻速线
    ltZuSu,

    // 江恩角度线
    ltJiaoDu,

    // 黄金分割线
    ltHJFG,

    // 波段线
    ltBD,

    // 百分比
    ltPercent,

    // 回归线
    ltHG,

    // 回归带
    ltHGD,

    // 回归通道
    ltHGTD,

    // 周期线
    ltZhouQi,

    // 斐波那挈线
    ltFBNC,

    // 矩形
    ltRect,

    // 弧
    ltArc,

    // 黄金弧
    ltHJArc,

    // 圆
    ltEllipse,

    // 文本
    ltFont,

    // 向下
    ltDow,

    // 向上 兼容老线形，添加线形不能在中间插入，只能追加
    ltUp,

    // 多线段
    ltMultiSegment,

    // 垂直线
    ltVerticalLine,

    // 射线
    ltRaysLine,

    // 終点箭头线
    ltEndAnchorLine,

    // 垂直线段
    ltVerticalSegment,

    // 水平线段
    ltHorizonSegment,

    // 三浪线
    ltThreeWavesLine,

    // 五浪线
    ltFiveWavesLine,

    // 八浪线
    ltEightWavesLine,

    // 头肩形
    ltHeaderLine,

    // M头w底
    ltMWLine,

    // 平行线般
    ltParallelSegment,

    // 幅度尺
    ltRaiseRuler,

    // 波浪尺
    ltWaveRuler,

    // 转折尺
    ltTurnRuler,

    // 量度目标
    ltMeasureGoals,

    // 自由费式线（自定义第一个周期）
    ltCustomFBRC,

    // 自由周期线
    ltCustomZhouQi,

    // 时间尺
    ltTimeRuler,

    // 对称线
    ltSymmetrical,

    // 对称角度线
    ItSymmetricalAngel,

    // 箱体线
    ltBox,

    // 三角形
    ltTriangle,

    // 平行四边形
    ltParallelogram,

    // 价格标注
    ltPriceLabel,

    // 价格线
    ltPriceLine,

    // 三点平行线
    ltParallelLine2,

    ltEnd,
}
