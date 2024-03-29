package cn.jingzhuan.lib.chart.data;

public class DataFormatter {
    public static final String HIDE = "HIDE";
    public static final String UNIT_TRILLION = "万亿";
    public static final String UNIT_TEN_THOUSAND = "万";
    public static final String UNIT_BILLION = "亿";
    public static final String UNIT_PERCENT = "%"; // 数值先*100，再添加%
    public static final String UNIT_PERCENT_DIRECT = "+%"; // 数值不变，直接添加%
    public static final String UNIT_CALC = "*"; // 不指定单位，由客户端根据数值范围决定基本单位。这时精度默认为2
    public static final String UNIT_CALC_SUFFIX = "*+"; // 指定附加单位，由客户端根据数值范围决定基本单位。这时精度默认为2
    public static final String UNIT_EMPTY = "";
    private int precision = 2; // 数据精度
    private String unit = ""; // 数据基本单位
    private String unitSuffix = ""; // 数据附加单位
    private boolean isHide = false;

    public static DataFormatter TRILLION() {
        return new DataFormatter(UNIT_TRILLION);
    }
    public static DataFormatter TEN_THOUSAND() {
        return new DataFormatter(UNIT_TEN_THOUSAND);
    }
    public static DataFormatter BILLION() {
        return new DataFormatter(UNIT_BILLION);
    }
    public static DataFormatter PERCENT() {
        return new DataFormatter(UNIT_PERCENT);
    }
    public static DataFormatter PERCENT_DIRECT() {
        return new DataFormatter(UNIT_PERCENT_DIRECT);
    }
    public static DataFormatter CALC() {
        return new DataFormatter(UNIT_CALC);
    }
    public static DataFormatter CALC_SUFFIX(String unitSuffix) {
        return new DataFormatter(UNIT_CALC_SUFFIX, unitSuffix);
    }
    public static DataFormatter EMPTY() {
        return new DataFormatter(UNIT_EMPTY);
    }
    public static DataFormatter HIDE() {
        return new DataFormatter(true);
    }

    public DataFormatter() {}

    public DataFormatter(boolean isHide) {
        this.isHide = isHide;
    }

    public DataFormatter(String unit) {
        this(unit, "");
    }
    public DataFormatter(String unit, String unitSuffix) {
        if (unit == null || (!unit.equals(UNIT_TRILLION)
                && !unit.equals(UNIT_TEN_THOUSAND) && !unit.equals(UNIT_BILLION)
                && !unit.equals(UNIT_PERCENT)&& !unit.equals(UNIT_PERCENT_DIRECT)
                && !unit.equals(UNIT_CALC) && !unit.equals(UNIT_CALC_SUFFIX)
                && !unit.equals(UNIT_EMPTY))) {
            unit = "";
        }
        this.precision = 2;
        this.unit = unit;
        this.unitSuffix = unitSuffix;
    }

    public DataFormatter(int precision, String unit) {
        if (unit == null || (!unit.equals(UNIT_TRILLION)
                && !unit.equals(UNIT_TEN_THOUSAND) && !unit.equals(UNIT_BILLION)
                && !unit.equals(UNIT_PERCENT)&& !unit.equals(UNIT_PERCENT_DIRECT)
                && !unit.equals(UNIT_CALC) && !unit.equals(UNIT_EMPTY))) {
            unit = "";
        }
        if (precision < 0) {
            precision = 2;
        }
        this.precision = precision;
        this.unit = unit;
    }

    public DataFormatter(int unit) {
        switch (unit) {
            case 1:
                this.unit = UNIT_TEN_THOUSAND;
                break;
            case 2:
                this.unit = UNIT_BILLION;
                break;
            case 3:
                this.unit = UNIT_PERCENT;
                break;
            case 4:
                this.unit = UNIT_CALC; // 不指定单位，由客户端根据数值范围决定单位。这时精度默认为2
                this.precision = 2;
                break;
            case 5:
                this.unit = UNIT_PERCENT_DIRECT; // 数值不变，直接添加%
                break;
            default:
                this.unit = UNIT_EMPTY;
                this.precision = 2;
                break;
        }
    }

    public DataFormatter(int precision, int unit) {
        this(unit);
        if (precision < 0) {
            precision = 2;
        }
        this.precision = precision;
    }

//    public DataFormatter(String formatterString) {
//        if (formatterString == null || formatterString.length() == 0) {
//            return;
//        }
//        if (formatterString.equals(HIDE)) {
//            isHide = true;
//        } else {
//            String[] formatterList = formatterString.toUpperCase(Locale.ROOT).split("_");
//            for (String formatter : formatterList) {
//                if (formatter.contains("W")) {
//                    unit = UNIT_TEN_THOUSAND;
//                    String precisionString = formatter.replace("W", "");
//                    precision = parsePrecision(precisionString);
//                } else if (formatter.contains("Y")) {
//                    unit = UNIT_BILLION;
//                    String precisionString = formatter.replace("Y", "");
//                    precision = parsePrecision(precisionString);
//                } else if (formatter.contains("F")) {
////                    unit = "%"; // 在 PERCENT 分支再决定
//                    String precisionString = formatter.replace("F", "");
//                    precision = parsePrecision(precisionString);
//                } else if (formatter.contains("PERCENT")) {
//                    unit = UNIT_PERCENT;
//                }
//            }
//
//        }
//    }
//
//    private int parsePrecision(String precisionString) {
//        int result = 2;
//        if (precisionString != null && precisionString.length() != 0) {
//            try {
//                result = Integer.parseInt(precisionString);
//            } catch (NumberFormatException e) {
//            }
//        }
//        return result;
//    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitSuffix() {
        return unitSuffix;
    }

    public void setUnitSuffix(String unitSuffix) {
        this.unitSuffix = unitSuffix;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }
}
