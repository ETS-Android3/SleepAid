package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ConfigurationDao {
    @Query("SELECT * FROM Configuration ORDER BY configurationId")
    Single<List<Configuration>> getAll();

    @Query("SELECT * FROM Configuration WHERE type IN (:configurationTypes) ORDER BY configurationId")
    Single<List<Configuration>> loadAllByTypes(String[] configurationTypes);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Configuration> configurations);
}
