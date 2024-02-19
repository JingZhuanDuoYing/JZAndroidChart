package cn.jingzhuan.lib.chart3.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


object NumberUtils {

    private const val DEF_DIV_SCALE = 10

    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
     * <pre>
     * "3.1415926", 1			--> 3.1
     * "3.1415926", 3			--> 3.142
     * "3.1415926", 4			--> 3.1416
     * "3.1415926", 6			--> 3.141593
     * "1234567891234567.1415926", 3	--> 1234567891234567.142
    </pre> *
     *
     * @param number    类型的数字对象
     * @param precision 小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    fun keepPrecision(number: String, precision: Int): String {
        if (number == "NaN") return "0"
        val bd = BigDecimal(number)
        return bd.setScale(precision, RoundingMode.HALF_UP).toPlainString()
    }

    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。<br></br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br></br>
     * 如果给定的数字没有小数，则转换之后将以0填充；例如：int 123  1 --> 123.0<br></br>
     * **注意：**如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    类型的数字对象
     * @param precision 小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    fun keepPrecision(number: Number, precision: Int): String {
        return keepPrecision(number.toString(), precision)
    }

    /**
     * 对double类型的数值保留指定位数的小数。<br></br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br></br>
     * **注意：**如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return double 如果数值较大，则使用科学计数法表示
     */
    fun keepPrecision(number: Double, precision: Int): Double {
        val bd = BigDecimal(number)
        return bd.setScale(precision, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * 对float类型的数值保留指定位数的小数。<br></br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br></br>
     * **注意：**如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return float 如果数值较大，则使用科学计数法表示
     */
    fun keepPrecision(number: Float, precision: Int): Float {
        return keepPrecision(number.toDouble(), precision).toFloat()
    }


    /**
     * 截取小数点后4位,不舍也不入  舍入模式
     *
     * @param number
     * @param precision
     * @return
     */
    fun keepHut(number: Float, precision: Int): Float {
        val bd = BigDecimal(number.toDouble())
        return bd.setScale(precision, RoundingMode.DOWN).toFloat()
    }

    /**
     * 截取小数点后4位,进位模式
     *
     * @param number
     * @param precision
     * @return
     */
    fun keepAdd(number: Float, precision: Int): Float {
        val bd = BigDecimal(number.toDouble())
        return bd.setScale(precision, RoundingMode.UP).toFloat()
    }

    /**
     * 格式化数字，用逗号分割
     *
     * @param number      1000000.7569 to 1,000,000.76 or
     * @param splitChar   分割符号
     * @param endLength   保存多少小数位
     * @param splitLength 分割位数
     * @return 格式化完成的字符串
     */
    fun formatNumberWithMarkSplit(
        number: Double,
        splitChar: String,
        splitLength: Int,
        endLength: Int
    ): String {
        val tempSplitStr = StringBuilder()
        for (index in 0 until splitLength) tempSplitStr.append("#")
        val tempPattern = StringBuilder(
            tempSplitStr.toString() + splitChar + tempSplitStr.substring(
                0,
                splitLength - 1
            ) + "0."
        ) /*###,##0.*/
        for (index in 0 until endLength) tempPattern.append("0")
        return DecimalFormat(tempPattern.toString()).format(number)
    }

    /**
     * 对double数据进行取精度.
     * @param value  double数据.
     * @param scale  精度位数(保留的小数位数).
     * @param roundingMode  精度取值方式.
     * @return 精度计算后的数据.
     */
    fun round(
        value: Double,
        scale: Int,
        roundingMode: RoundingMode
    ): Double {
        var bd = BigDecimal(value)
        bd = bd.setScale(scale, roundingMode)
        return bd.toDouble()
    }

    /**
     * double 相加
     * @param d1
     * @param d2
     * @return
     */
    fun sum(d1: Double, d2: Double): Double {
        val bd1 = BigDecimal(d1.toString())
        val bd2 = BigDecimal(d2.toString())
        return bd1.add(bd2).toDouble()
    }

    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    fun sub(d1: Double, d2: Double): Double {
        val bd1 = BigDecimal(d1.toString())
        val bd2 = BigDecimal(d2.toString())
        return bd1.subtract(bd2).toDouble()
    }

    /**
     * double 乘法
     * @param d1
     * @param d2
     * @return
     */
    fun mul(d1: Double, d2: Double): Double {
        val bd1 = BigDecimal(d1.toString())
        val bd2 = BigDecimal(d2.toString())
        return bd1.multiply(bd2).toDouble()
    }

    /**
     * double 除法
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    fun div(d1: Double, d2: Double, scale: Int): Double {
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理
        if (d2 == 0.0) {
            return 0.0
        }
        val bd1 = BigDecimal(d1.toString())
        val bd2 = BigDecimal(d2.toString())
        return bd1.divide(bd2, scale, RoundingMode.HALF_UP).toDouble()
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    fun div(v1: Double, v2: Double): Double {
        return div(v1, v2, DEF_DIV_SCALE)
    }
}