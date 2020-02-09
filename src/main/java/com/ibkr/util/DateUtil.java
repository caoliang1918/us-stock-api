package com.ibkr.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by caoliang on 2019/2/26
 */
public class DateUtil {

    public static final String TIME = "HH:mm:ss";
    public static final String DAY = "yyyy-MM-dd";
    public static final String DAT_TIME = "yyyy-MM-dd HH:mm:ss.SSS";


    /**
     * 获取星期
     *
     * @param date
     * @return
     */
    public static Integer getWeek(Date date) {
        Integer[] weekDays = {0, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static String getStringDay(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(DAY);
        return format.format(date);
    }

    public static Date format(TimeZone timeZone, String parrten) {
        return format(new Date(), timeZone, parrten);
    }

    public static Date format(Date date, TimeZone timeZone, String parrten) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME);
        SimpleDateFormat sdf = new SimpleDateFormat(parrten);
        simpleDateFormat.setTimeZone(timeZone);
        String d = DateFormatUtils.format(date, parrten, timeZone);
        try {
            return sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date format(String str , String parrten){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parrten);
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
