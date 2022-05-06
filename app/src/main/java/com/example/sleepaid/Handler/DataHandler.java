package com.example.sleepaid.Handler;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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

    public static String getFormattedDuration(int hours, int minutes) {
        if (hours == 0) {
            if (minutes == 0) {
                return "0h";
            } else {
                return "0h" + minutes + "m";
            }
        } else {
            if (minutes == 0) {
                return hours + "h";
            } else {
                return hours + "h" + minutes + "m";
            }
        }
    }

    public static String getFormattedDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return date.format(formatter);
    }

    public static String getFullDate(ZonedDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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

    public static float getFloatFromTime(String time) {
        List<Integer> times = DataHandler.getIntsFromString(time);

        int hours = times.get(0);
        float minutes = times.size() > 1 ? times.get(1) / 60.0f : 0.f;

        return hours + minutes;
    }

    public static List<Float> getFloatsFromTimes(List<String> times) {
        List<Float> processedValues = new ArrayList<>();

        for (String t : times) {
            processedValues.add(getFloatFromTime(t));
        }

        return processedValues;
    }

     public static void playAlarmSound(MediaPlayer mediaPlayer, Context context, int resourceId, boolean loop) {
        Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    });
                    mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());

                    AssetFileDescriptor afd = context.getResources().openRawResourceFd(resourceId);
                    if (afd == null) return;

                    mediaPlayer.setDataSource(
                            afd.getFileDescriptor(),
                            afd.getStartOffset(),
                            afd.getLength()
                    );
                    afd.close();

                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());

                    mediaPlayer.setLooping(loop);
                    mediaPlayer.setVolume(1.0f, 1.0f);

                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public static int getSizeInDp(int value, DisplayMetrics displayMetrics) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                displayMetrics
        );
    }
}
