package com.yawar.memo.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.yawar.memo.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeProperties {
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    private static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }
    public String getFormattedDate( long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "yyyy/MM/dd";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
//            return "Today " + DateFormat.format(timeFormatString, smsTime);
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
//            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
            return BaseApp.getInstance().getResources().getString(R.string.yesterday);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
//            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
                        return DateFormat.format(dateTimeFormatString, smsTime).toString();

        }
    }
    public String getDateForLastSeen(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "yyyy/MM/dd";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
//            return "Today " + DateFormat.format(timeFormatString, smsTime);
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
//            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
            return context.getResources().getString(R.string.yesterdayAt)+" "+DateFormat.format(timeFormatString, smsTime).toString();
//            return context.getResources().getString(R.string.yesterdayAt);

        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
//            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
            return DateFormat.format(dateTimeFormatString, smsTime).toString();

        }
    }
       public static String milliSecondsToTimer(Long milliSeconds) {
        String timerString = "";
        String secondString;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minute = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";

        }
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;

        }
        timerString = timerString + minute + ":" + secondString;
        return timerString;

    }

}

