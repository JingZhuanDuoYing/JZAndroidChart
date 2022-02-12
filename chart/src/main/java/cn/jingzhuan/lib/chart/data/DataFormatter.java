package cn.jingzhuan.lib.chart.data;

import java.util.Locale;

public class DataFormatter {
    public static final String HIDE = "HIDE";
    private int precision = 2; // 数据精度
    private String unit = ""; // 数据单位
    private boolean isHide = false;

    public DataFormatter() {}

    public DataFormatter(boolean isHide) {
        this.isHide = isHide;
    }

    public DataFormatter(int precision, String unit) {
        if (unit == null || !unit.equals("万") || !unit.equals("亿")
                || !unit.equals("%") || !unit.equals("")) {
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
                this.unit = "万";
                break;
            case 2:
                this.unit = "亿";
                break;
            case 3:
                this.unit = "%";
                break;
            case 4:
                this.unit = "*"; // 服务器不指定单位，由客户端根据数值范围决定单位。这时精度默认为2
                this.precision = 2;
                break;
            default:
                this.unit = "";
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

    public DataFormatter(String formatterString) {
        if (formatterString == null || formatterString.length() == 0) {
            return;
        }
        if (formatterString.equals(HIDE)) {
            isHide = true;
        } else {
            String[] formatterList = formatterString.toUpperCase(Locale.ROOT).split("_");
            for (String formatter : formatterList) {
                if (formatter.contains("W")) {
                    unit = "万";
                    String precisionString = formatter.replace("W", "");
                    precision = parsePrecision(precisionString);
                } else if (formatter.contains("Y")) {
                    unit = "亿";
                    String precisionString = formatter.replace("Y", "");
                    precision = parsePrecision(precisionString);
                } else if (formatter.contains("F")) {
//                    unit = "%"; // 在 PERCENT 分支再决定
                    String precisionString = formatter.replace("F", "");
                    precision = parsePrecision(precisionString);
                } else if (formatter.contains("PERCENT")) {
                    unit = "%";
                }
            }

        }
    }

    private int parsePrecision(String precisionString) {
        int result = 2;
        if (precisionString != null && precisionString.length() != 0) {
            try {
                result = Integer.parseInt(precisionString);
            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

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

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }
}
