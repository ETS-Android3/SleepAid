package com.example.sleepaid.Database.Configuration;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.sleepaid.Database.Alarm.Alarm;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ConfigurationDao {
    @Query("SELECT * FROM Configuration ORDER BY name")
    Single<List<Configuration>> getAll();

    @Query("SELECT * FROM Configuration WHERE name IN (:configurationNames) ORDER BY name")
    Single<List<Configuration>> loadAllByNames(String[] configurationNames);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Configuration> configurations);

    @Update
    Completable update(Configuration configuration);
}
