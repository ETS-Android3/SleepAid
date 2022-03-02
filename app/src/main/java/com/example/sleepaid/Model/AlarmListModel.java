package com.example.sleepaid.Model;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;
import java.util.Optional;

@SuppressLint("NewApi")
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
