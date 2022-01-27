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
        this.precision = precision;
        this.unit = unit;
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
