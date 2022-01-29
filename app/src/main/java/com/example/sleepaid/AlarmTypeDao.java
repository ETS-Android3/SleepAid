package com.example.sleepaid;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AlarmTypeDao {
    @Query("SELECT * FROM AlarmType ORDER BY typeId")
    Single<List<AlarmType>> getAll();

    @Query("SELECT * FROM AlarmType WHERE typeId IN (:typeIds) ORDER BY typeId")
    Single<List<AlarmType>> loadAllByIds(int[] typeIds);

    @Query("SELECT * FROM AlarmType WHERE type IN (:types) ORDER BY type")
    Single<List<AlarmType>> loadAllByTypes(int[] types);
}
