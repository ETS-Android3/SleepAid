package com.example.sleepaid.Service;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlarmAndNotificationService {
    public static List<Alarm> createAlarms(int alarmType, String time) {
        switch (alarmType) {
            case 3:
                return createBedtimeAlarms(time);

            case 2:
                return createNapAlarms(time);

            default:
                return createMorningAlarms(time);
        }
    }

    private static List<Alarm> createMorningAlarms(String time) {
        List<Alarm> alarmList = new ArrayList<>();

        List<Integer> times = DataHandler.getIntsFromString(time);

        for (int i = 0; i < 4; i++) {
            ZonedDateTime newTime = ZonedDateTime.now()
                    .withHour(times.get(0))
                    .withMinute(times.get(1))
                    .plusMinutes(i * 10);

            Alarm morningAlarm = new Alarm(
                    1,
                    "It's time to wake up!",
                    DataHandler.getFormattedTime(newTime.getHour(), newTime.getMinute()),
                    "1111111",
                    "Default",
                    1,
                    1
            );
            alarmList.add(morningAlarm);
        }

        return alarmList;
    }

    private static List<Alarm> createNapAlarms(String time) {
        List<Alarm> alarmList = new ArrayList<>();

        List<Integer> napTimes = DataHandler.getIntsFromString(time);

        Alarm napAlarmBefore = new Alarm(
                2,
                "It's time for your nap!",
                DataHandler.getFormattedTime(napTimes.get(0), napTimes.get(1)),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(napAlarmBefore);

        ZonedDateTime napWakeupTime = ZonedDateTime.now()
                .withHour(napTimes.get(0))
                .withMinute(napTimes.get(1))
                .plusMinutes(30);

        Alarm napAlarmAfter = new Alarm(
                2,
                "It's time to wake up from your nap!",
                DataHandler.getFormattedTime(napWakeupTime.getHour(), napWakeupTime.getMinute()),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(napAlarmAfter);

        return alarmList;
    }

    private static List<Alarm> createBedtimeAlarms(String time) {
        List<Alarm> alarmList = new ArrayList<>();

        List<Integer> times = DataHandler.getIntsFromString(time);

        ZonedDateTime bedHourBefore = ZonedDateTime.now()
                .withHour(times.get(0))
                .withMinute(times.get(1))
                .minusMinutes(30);

        Alarm bedtimeAlarmBefore = new Alarm(
                3,
                "It's almost bedtime!",
                DataHandler.getFormattedTime(bedHourBefore.getHour(), bedHourBefore.getMinute()),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(bedtimeAlarmBefore);

        Alarm bedtimeAlarm = new Alarm(
                3,
                "It's bedtime!",
                DataHandler.getFormattedTime(times.get(0), times.get(1)),
                "1111111",
                "Default",
                1,
                1
        );
        alarmList.add(bedtimeAlarm);

        return alarmList;
    }

    public static List<Notification> createNotifications(int notificationType, String time) {
        if (notificationType == 3) {
            return createBedtimeNotifications(time);
        } else if (notificationType == 1) {
            return createMorningNotifications(time);
        }

        return null;
    }

    private static List<Notification> createMorningNotifications(String time) {
        List<Notification> notificationList = new ArrayList<>();

        List<Integer> times = DataHandler.getIntsFromString(time);

        ZonedDateTime sleepDiaryWakeupTime = ZonedDateTime.now()
                .withHour(times.get(0))
                .withMinute(times.get(1))
                .plusMinutes(45);

        Notification sleepDiaryWakeupTimeNotification = new Notification(
                "It's time to fill in your morning sleep diary!",
                "Tap here to open it.",
                DataHandler.getFormattedTime(sleepDiaryWakeupTime.getHour(), sleepDiaryWakeupTime.getMinute()),
                1,
                R.id.morningSleepDiaryFragment
        );

        notificationList.add(sleepDiaryWakeupTimeNotification);

        ZonedDateTime sleepDiaryWakeupTimeReminder = ZonedDateTime.now()
                .withHour(times.get(0))
                .withMinute(times.get(1))
                .plusHours(2);

        Notification sleepDiaryWakeupTimeReminderNotification = new Notification(
                "You still haven't filled in your morning sleep diary. You can do it until 00:00.",
                "Tap here to open it.",
                DataHandler.getFormattedTime(sleepDiaryWakeupTimeReminder.getHour(), sleepDiaryWakeupTimeReminder.getMinute()),
                1,
                R.id.morningSleepDiaryFragment
        );

        notificationList.add(sleepDiaryWakeupTimeReminderNotification);

        return notificationList;
    }

    private static List<Notification> createBedtimeNotifications(String time) {
        List<Notification> notificationList = new ArrayList<>();

        List<Integer> times = DataHandler.getIntsFromString(time);

        ZonedDateTime unwindHour = ZonedDateTime.now()
                .withHour(times.get(0))
                .withMinute(times.get(1))
                .minusHours(2);

        Notification unwindNotification = new Notification(
                "It's almost bedtime!",
                "There are 2 hours left until your bedtime. How about you take some time to unwind? Tap here for suggestions.",
                DataHandler.getFormattedTime(unwindHour.getHour(), unwindHour.getMinute()),
                1,
                R.id.relaxingActivitiesSuggestionsFragment
        );

        notificationList.add(unwindNotification);

        ZonedDateTime sleepDiaryBedtime = ZonedDateTime.now()
                .withHour(times.get(0))
                .withMinute(times.get(1))
                .minusMinutes(15);

        Notification sleepDiaryBedtimeNotification = new Notification(
                "It's time to fill in your bedtime sleep diary!",
                "Tap here to open it.",
                DataHandler.getFormattedTime(sleepDiaryBedtime.getHour(), sleepDiaryBedtime.getMinute()),
                1,
                R.id.bedtimeSleepDiaryFragment
        );

        notificationList.add(sleepDiaryBedtimeNotification);

        Notification sleepDiaryBedtimeReminderNotification = new Notification(
                "You still haven't filled in your bedtime sleep diary. You can do it until 12:00.",
                "Tap here to open it.",
                DataHandler.getFormattedTime(10, 0),
                1,
                R.id.bedtimeSleepDiaryFragment
        );

        notificationList.add(sleepDiaryBedtimeReminderNotification);

        return notificationList;
    }
}
