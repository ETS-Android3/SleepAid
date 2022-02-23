package com.example.sleepaid;

import com.example.sleepaid.Database.SleepData.SleepData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static String getFormattedDate(Date date) {
        return new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date);
    }

    public static String getSQLiteDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String getMonth(Date date) {
        return new SimpleDateFormat("MM/yy", Locale.getDefault()).format(date);
    }

    public static String getYear(Date date) {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
    }

    public static double getDoubleFromSleepDataValue(SleepData sleepData) {
        List<Integer> times = DataHandler.getIntsFromString(sleepData.getValue());

        int hours = times.get(0);
        double minutes = times.size() > 1 ? times.get(1) / 60.0 : 0;

        return hours + minutes;
    }

    public static List<Double> getDoublesFromSleepDataValues(List<SleepData> sleepData) {
        List<Double> processedValues = new ArrayList<>();

        for (SleepData s : sleepData) {
            processedValues.add(getDoubleFromSleepDataValue(s));
        }

        return processedValues;
    }
}
