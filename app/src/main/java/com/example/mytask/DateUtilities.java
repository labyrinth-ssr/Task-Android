package com.example.mytask;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateUtilities {

    public static String testTimestampToString(Long ts) {
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //方法一:优势在于可以灵活的设置字符串的形式。
        String tsStr = sdf.format(ts);
        return  tsStr;
    }

    public static long now() {
        return System.currentTimeMillis();
    }



    public static String dayStringFormat(long msecs) {
        final long ONE_SECOND = 1000L;
        final long ONE_MINUTE = 60000L;
        final long ONE_HOUR = 3600000L;
        final long ONE_DAY = 86400000L;
        final long ONE_MONTH = 2592000000L;
        final long ONE_YEAR = 31536000000L;
//        int gmtOffset = TimeZone.getDefault().getRawOffset();
        long currentTime = System.currentTimeMillis();
        long difference = msecs - currentTime;
        if (difference < ONE_MINUTE) {
            long timeAgo = difference / ONE_SECOND;
            int finalUnits = (int) timeAgo;
            return "Soon";
        } else if (difference < ONE_HOUR) {
            long timeAgo = difference / ONE_MINUTE;
            int finalUnits = (int) timeAgo;
            return  "in "+ finalUnits + " " + "Minutes";
        } else if (difference < ONE_DAY) {
            long timeAgo = difference / ONE_HOUR;
            int finalUnits = (int) timeAgo;
            return "in "+finalUnits + " " + "Hours";
        } else if (difference < ONE_MONTH) {
            long timeAgo = difference / ONE_DAY;
            int finalUnits = (int) timeAgo;
            return  "in "+finalUnits +" " + "Days";
        }
//        else if (difference < ONE_YEAR) {
//            long timeAgo = difference / ONE_MONTH;
//            int finalUnits = (int) timeAgo;
//            return  finalUnits + " " + "Months Ago";
//        }
        else {
//            long timeAgo = difference / ONE_MONTH;
//            int finalUnits = (int) timeAgo;
            return testTimestampToString(msecs);
        }
    }
}
