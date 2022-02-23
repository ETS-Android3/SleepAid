package com.example.sleepaid.Database.SleepDataField;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface SleepDataFieldDao {
    @Query("SELECT * FROM SleepDataField ORDER BY fieldId")
    Single<List<SleepDataField>> getAll();

    @Query("SELECT * FROM SleepDataField WHERE field IN (:fields) ORDER BY fieldId")
    Single<List<SleepDataField>> loadAllByFields(String[] fields);
}
