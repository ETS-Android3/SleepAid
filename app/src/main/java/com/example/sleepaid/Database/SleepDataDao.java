package com.example.sleepaid.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SleepDataDao {
    @Query("SELECT * FROM SleepData ORDER BY date")
    Single<List<SleepData>> getAll();

    @Query("SELECT * FROM SleepData WHERE field IN (:fields) ORDER BY fieldId")
    Single<List<SleepData>> loadAllByFields(String[] fields);

    @Query("SELECT * FROM SleepData WHERE date IN (:dates) ORDER BY date")
    Single<List<SleepData>> loadAllByDates(String[] dates);

    @Query("SELECT * FROM SleepData WHERE date BETWEEN :dateMin AND :dateMax ORDER BY date")
    Single<List<SleepData>> loadAllByDateRange(String dateMin, String dateMax);

    @Query("SELECT * FROM SleepData WHERE date BETWEEN :dateMin AND :dateMax AND field = (:field) ORDER BY date")
    Single<List<SleepData>> loadAllByDateRangeAndType(String dateMin, String dateMax, String field);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<SleepData> sleepData);
}
