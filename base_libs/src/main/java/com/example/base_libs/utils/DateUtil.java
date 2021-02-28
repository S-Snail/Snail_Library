package com.example.base_libs.utils;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Author Snail
 * @Since 2021/2/28
 * 日期,时间工具类
 */

public class DateUtil {
    private static final int MIN_SECS = 60;
    private static final int HOUR_SECS = MIN_SECS * 60;
    private static final int DAY_SECS = HOUR_SECS * 24;

    public static long TIME_CALIBRATOR = 0L;

    /***
     * 校准的时间戳
     * @return
     */
    public static Long currentTimeMillis() {
        return (System.currentTimeMillis() - TIME_CALIBRATOR);
    }

    /***
     * 同步服务器时间
     * @param strTime
     */
    public static void syncServerTimeMillis(String strTime) {
        long serverTime;
        if (strTime.length() == 10) {
            serverTime = Long.parseLong(strTime) * 1000;
        } else if (strTime.length() == 13) {
            serverTime = Long.parseLong(strTime);
        } else {
            serverTime = System.currentTimeMillis();
        }
        //本次与服务的时间差值
        long cur_diff_time = System.currentTimeMillis() - serverTime;
        if (Math.abs(cur_diff_time) < 5 * 1000) {
            //如果差值小于10秒，以本地为主
            TIME_CALIBRATOR = 0;
        } else {
            //差值大于10秒记下差值
            TIME_CALIBRATOR = cur_diff_time;
        }
    }

    public static String leftTime(long endTimeSeconds) {
        return leftTime(endTimeSeconds, false);
    }

    public static String leftTimeMin(boolean needSecond, long expireTimeSeconds) {
        long leftTime = expireTimeSeconds - curTimeSeconds();
        return leftTime(needSecond, leftTime);
    }

    public static String leftTime(boolean needSecond, long leftTime) {
        int mi = 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = leftTime / dd;
        long hour = (leftTime - day * dd) / hh;
        long minute = (leftTime - day * dd - hour * hh) / mi;
        long seconds = leftTime - day * dd - hour * hh - minute * mi;

        if (hour > 0) {
            return String.format("%s时%s分%s", String.valueOf(hour), String.valueOf(minute), needSecond ? (seconds + "秒") : "");
        } else if (minute > 0) {
            return String.format("%s分%s", String.valueOf(minute), needSecond ? (seconds + "秒") : "");
        } else if (seconds > 0) {
            return needSecond ? String.format("%s秒", seconds) : "1分";
        } else {
            return "";
        }
    }

    @SuppressLint("DefaultLocale")
    public static String leftTime(long endTimeSeconds, boolean playTime) {
        long secs = endTimeSeconds - (playTime ? 0 : curTimeSeconds());
        if (secs <= 0) return "00:00:00";

        if (secs > DAY_SECS) {
            int days = (int) (secs / DAY_SECS);
            return days + "天";
        } else {
            long hour = secs / HOUR_SECS;
            long left = secs % HOUR_SECS;
            long minute = left / MIN_SECS;
            long sec = left % MIN_SECS;

            return String.format("%02d:%02d:%02d", hour, minute, sec);
        }
    }

    public static String dayOfWeek(long timeSec) {
        String[] weekOfDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timeSec * 1000);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) {
            dayOfWeek = 0;
        }
        return weekOfDays[dayOfWeek];
    }

    public static String getTimeStamp(long timeSec) {
        if (justNow(timeSec)) {
            return "刚刚";
        } else if (today(timeSec)) {
            return formatTime8(timeSec);
        } else if (yesterday(timeSec)) {
            return "昨天";
        } else if (thisWeek(timeSec)) {
            return dayOfWeek(timeSec);
        } else if (thisYear(timeSec)) {
            return formatTime7(timeSec);
        } else {
            return formatTime11(timeSec);
        }
    }


    public static boolean isExpire(long endTimeSeconds) {
        return endTimeSeconds * 1000 - curTime() <= 0;
    }

    public static long curTime() {
        return System.currentTimeMillis();
    }

    public static long curTimeSeconds() {
        return curTime() / 1000;
    }

    public static boolean in1Hour(long date, int hour, long targetTimeStamp) {
        long actual = date + (hour) * 3600;
        return actual < (targetTimeStamp + 3600);
    }

    public static boolean today(long timeSec) {
        try {
            long curTimeSec = curTimeSeconds();
            String curDayStr = formatTime10(curTimeSec);
            String realDayStr = formatTime10(timeSec);
            Integer currentD = Integer.parseInt(curDayStr);
            Integer realD = Integer.parseInt(realDayStr);
            return (curTimeSec - timeSec < DAY_SECS) && (currentD - realD == 0);
        } catch (Exception ex) {
            return false;
        }
    }

    public static long monthDelay(long timeSec, int delay) {
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(timeSec * 1000L);
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH) + 1;
        int day = calender.get(Calendar.DAY_OF_MONTH);

        int yearDelay = year;
        int monthDelay = month + delay;
        int dayDelay = day;

        //检查月份
        while (monthDelay > 12) {
            monthDelay -= 12;
            yearDelay++;
        }

        //检查日期
        calender.set(yearDelay, monthDelay - 1, 1);
        int maxDay = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (dayDelay > maxDay) {
            monthDelay++;
            dayDelay -= maxDay;
            if (monthDelay > 12) {
                monthDelay -= 12;
                yearDelay++;
            }
        }
        calender.set(yearDelay, monthDelay - 1, dayDelay);
        return calender.getTimeInMillis() / 1000L;
    }

    private static boolean justNow(long timeSec) {
        return curTimeSeconds() - timeSec <= MIN_SECS;
    }

    private static boolean yesterday(long timeSec) {
        try {
            long curTimeSec = curTimeSeconds();
            String curDayStr = formatTime10(curTimeSec);
            String realDayStr = formatTime10(timeSec);
            Integer currentD = Integer.parseInt(curDayStr);
            Integer realD = Integer.parseInt(realDayStr);
            return (curTimeSec - timeSec < 2 * DAY_SECS) && (currentD - realD == 1);
        } catch (Exception ex) {
            return false;
        }
    }

    private static boolean thisWeek(long timeSec) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timeSec * 1000);
        int realWeekPos = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTimeInMillis(curTime());
        int curWeekPos = calendar.get(Calendar.WEEK_OF_YEAR);
        return thisYear(timeSec) && realWeekPos == curWeekPos;
    }

    private static boolean thisYear(long timeSec) {
        try {
            String curYearStr = formatTime9(curTimeSeconds());
            String realYearStr = formatTime9(timeSec);
            Integer curY = Integer.parseInt(curYearStr);
            Integer realY = Integer.parseInt(realYearStr);
            return curY - realY == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public static long getTimeStamp(String date, String pattern) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
            Date retDate = formatter.parse(date);
            return retDate.getTime() / 1000;
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String formatDate(long timestamp, String pattern) {
        try {
            Date date = new Date(timestamp);
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
            return format.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String formatTime1(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatTime2(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy-MM-dd HH:mm");
    }

    public static String formatTime12(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy.MM.dd HH:mm");
    }

    public static String formatTime3(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy年MM月dd日 HH:mm");
    }

    public static String formatTime4(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy-MM-dd");
    }

    public static String formatTime5(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy.MM.dd");
    }

    public static String formatTime6(long timeSec) {
        return formatDate(timeSec * 1000, "MM月dd日 HH:mm");
    }

    public static String formatTime7(long timeSec) {
        return formatDate(timeSec * 1000, "MM月dd日");
    }

    public static String formatTime8(long timeSec) {
        return formatDate(timeSec * 1000, "HH:mm");
    }

    public static String formatTime9(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy");
    }

    public static String formatTime10(long timeSec) {
        return formatDate(timeSec * 1000, "dd");
    }


    public static String formatTime11(long timeSec) {
        return formatDate(timeSec * 1000, "yyyy年MM月dd日");
    }
}
