package cn.jingzhuan.lib.chart2;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jingzhuan.lib.chart.utils.FloatUtils;

public  class TimeUtil {

    /**
     *传入的时间格式要和格式化时间对应一致 例如start为2019-7-12 13:35:10  则pattern为yyyy-MM-dd HH:mm:ss 以此类推
     */
    public static boolean isInTime() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date startTime = format.parse("09:30");
            Date endTime = format.parse("14:00");
            return startTime.before(new Date()) && endTime.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
