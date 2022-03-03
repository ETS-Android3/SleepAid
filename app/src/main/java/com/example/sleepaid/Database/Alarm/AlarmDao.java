package com.example.sleepaid.Database.Alarm;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM Alarm ORDER BY time")
    Single<List<Alarm>> getAll();

    @Query("SELECT * FROM Alarm WHERE alarmId IN (:alarmIds) ORDER BY alarmId")
    Single<List<Alarm>> loadAllByIds(int[] alarmIds);

    @Query("SELECT * FROM Alarm WHERE type IN (:types) ORDER BY type, time")
    Single<List<Alarm>> loadAllByTypes(int[] types);

    @Query("SELECT * FROM Alarm WHERE time IN (:times) ORDER BY time")
    Single<List<Alarm>> loadAllBySpecificTimes(String[] times);

    @Query("SELECT * FROM Alarm WHERE time BETWEEN :timeMin And :timeMax ORDER BY time")
    Single<List<Alarm>> loadAllByTimeRange(String timeMin, String timeMax);

    @Query("SELECT * FROM Alarm WHERE time LIKE :hour ORDER BY time")
    Single<List<Alarm>> loadAllByHour(String hour);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Alarm> alarms);
}
