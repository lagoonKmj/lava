package com.lagoon.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public abstract class DateUtils {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");    

    public static Calendar getCalendar(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }
    public static int getDiffHour(long end, long start) {
        return (int) ( (end - start) / (60*60*1000L) );
    }
    
    public static String getDateFormatString(Date date) {
        return FORMATTER.format(date);
    }
    
    public static String getFormatString(String pattern) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat (pattern);
        String dateString = formatter.format(new java.util.Date());
        return dateString;
    }    
    
    public static String getFormatString(String pattern, Date date) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat (pattern);
        String dateString = formatter.format(date);
        return dateString;
    }
    
    public static Date getDate(String pattern, String strDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = formatter.parseDateTime(strDate);
        Date date = dateTime.toDate();
        return date;
    }
}