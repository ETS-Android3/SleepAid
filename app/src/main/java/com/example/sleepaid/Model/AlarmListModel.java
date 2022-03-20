package com.example.sleepaid.Model;

import com.example.sleepaid.Database.Alarm.Alarm;

import java.util.List;


public class AlarmListModel {
    private int alarmType;

    private List<Alarm> alarmList;

    public AlarmListModel(int alarmType,
                          List<Alarm> alarmList) {
        this.alarmType = alarmType;
        this.alarmList = alarmList;
    }

    public void update(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public int getAlarmType() {
        return this.alarmType;
    }

    public List<Alarm> getAlarmList() {
        return this.alarmList;
    }
}
