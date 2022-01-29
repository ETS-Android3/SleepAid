package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SleepDataFieldDao {
    @Query("SELECT * FROM SleepDataField ORDER BY fieldId")
    Single<List<SleepDataField>> getAll();

    @Query("SELECT * FROM SleepDataField WHERE field IN (:fields) ORDER BY fieldId")
    Single<List<SleepDataField>> loadAllByFields(String[] fields);
}
