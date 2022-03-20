package com.example.sleepaid.Handler;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public static String getFormattedDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return date.format(formatter);
    }

    public static String getSQLiteDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static String getDay(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return date.format(formatter);
    }

    public static String getMonth(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return date.format(formatter);
    }

    public static String getYear(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
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
