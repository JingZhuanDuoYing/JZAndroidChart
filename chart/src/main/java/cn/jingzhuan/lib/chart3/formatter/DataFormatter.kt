package cn.jingzhuan.lib.chart3.formatter

import cn.jingzhuan.lib.chart.data.DataFormatter

/**
 * @since 2023-09-20
 * created by lei
 */
class DataFormatter {

    // 数据精度
    var precision = 2

    // 数据基本单位
    var unit = ""

    // 数据附加单位
    var unitSuffix = ""

    var isHide = false

    constructor()

    constructor(isHide: Boolean) {
        this.isHide = isHide
    }

    @JvmOverloads
    constructor(unit: String?, unitSuffix: String = "") {
        var unitString = unit
        if (unitString == null || (unitString != UNIT_TRILLION
                    && unit != UNIT_TEN_THOUSAND && unitString != UNIT_BILLION
                    && unitString != UNIT_PERCENT && unitString != UNIT_PERCENT_DIRECT
                    && unitString != UNIT_CALC && unitString != UNIT_CALC_SUFFIX
                    && unitString != UNIT_EMPTY)
        ) {
            unitString = ""
        }
        precision = 2
        this.unit = unitString
        this.unitSuffix = unitSuffix
    }

    constructor(precision: Int, unit: String?) {
        var precisionValue = precision
        var unitString = unit
        if (unitString == null || (unitString != UNIT_TRILLION
                    && unitString != UNIT_TEN_THOUSAND && unitString != UNIT_BILLION
                    && unitString != UNIT_PERCENT && unitString != UNIT_PERCENT_DIRECT
                    && unitString != UNIT_CALC && unitString != UNIT_EMPTY)
        ) {
            unitString = ""
        }
        if (precisionValue < 0) {
            precisionValue = 2
        }
        this.precision = precisionValue
        this.unit = unitString
    }

    constructor(unit: Int) {
        when (unit) {
            1 -> this.unit = UNIT_TEN_THOUSAND
            2 -> this.unit = UNIT_BILLION
            3 -> this.unit = UNIT_PERCENT
            4 -> {
                this.unit = UNIT_CALC // 不指定单位，由客户端根据数值范围决定单位。这时精度默认为2
                precision = 2
            }

            5 -> this.unit = UNIT_PERCENT_DIRECT // 数值不变，直接添加%
            else -> {
                this.unit = UNIT_EMPTY
                precision = 2
            }
        }
    }

    constructor(precision: Int, unit: Int) : this(unit) {
        var precisionValue = precision
        if (precisionValue < 0) {
            precisionValue = 2
        }
        this.precision = precisionValue
    }

    companion object {
        const val HIDE = "HIDE"
        const val UNIT_TRILLION = "万亿"
        const val UNIT_TEN_THOUSAND = "万"
        const val UNIT_BILLION = "亿"
        const val UNIT_PERCENT = "%" // 数值先*100，再添加%
        const val UNIT_PERCENT_DIRECT = "+%" // 数值不变，直接添加%
        const val UNIT_CALC = "*" // 不指定单位，由客户端根据数值范围决定基本单位。这时精度默认为2
        const val UNIT_CALC_SUFFIX = "*+" // 指定附加单位，由客户端根据数值范围决定基本单位。这时精度默认为2
        const val UNIT_EMPTY = ""

        fun TRILLION(): DataFormatter {
            return DataFormatter(UNIT_TRILLION)
        }

        fun TEN_THOUSAND(): DataFormatter {
            return DataFormatter(UNIT_TEN_THOUSAND)
        }

        fun BILLION(): DataFormatter {
            return DataFormatter(UNIT_BILLION)
        }

        fun PERCENT(): DataFormatter {
            return DataFormatter(UNIT_PERCENT)
        }

        fun PERCENT_DIRECT(): DataFormatter {
            return DataFormatter(UNIT_PERCENT_DIRECT)
        }

        fun CALC(): DataFormatter {
            return DataFormatter(UNIT_CALC)
        }

        fun CALC_SUFFIX(unitSuffix: String?): DataFormatter {
            return DataFormatter(UNIT_CALC_SUFFIX, unitSuffix)
        }

        fun EMPTY(): DataFormatter {
            return DataFormatter(UNIT_EMPTY)
        }

        fun HIDE(): DataFormatter {
            return DataFormatter(true)
        }
    }
}
