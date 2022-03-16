package com.example.sleepaid.Handler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataHandler {
    public static List<Integer> getIntsFromString(String s) {
        Matcher matcher = Pattern.compile("\\d+").matcher(s);

        List<Integer> numbers = new ArrayList<>();

        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }

        return numbers;
    }

    public static List<String> getGoalsFromString(String s) {
        Matcher matcher = Pattern.compile("\\d+\\w*\\W*\\d+").matcher(s);

        List<String> values = new ArrayList<>();

        while (matcher.find()) {
            values.add(matcher.group());
        }

        return values;
    }

    public static String getFormattedTime(int hours, int minutes) {
         String newHour = hours < 10 ?
                "0" + hours :
                Integer.toString(hours);

        String newMinute = minutes < 10 ?
                "0" + minutes :
                Integer.toString(minutes);

        return newHour + ":" + newMinute;
    }

    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date);
    }

    public static String getSQLiteDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String getDay(Date date) {
        return new SimpleDateFormat("dd", Locale.getDefault()).format(date);
    }

    public static String getMonth(Date date) {
        return new SimpleDateFormat("MM/yy", Locale.getDefault()).format(date);
    }

    public static String getYear(Date date) {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
    }

    public static double getDoubleFromTime(String time) {
        List<Integer> times = DataHandler.getIntsFromString(time);

        int hours = times.get(0);
        double minutes = times.size() > 1 ? times.get(1) / 60.0 : 0;

        return hours + minutes;
    }

    public static List<Double> getDoublesFromTimes(List<String> times) {
        List<Double> processedValues = new ArrayList<>();

        for (String t : times) {
            processedValues.add(getDoubleFromTime(t));
        }

        return processedValues;
    }
}
