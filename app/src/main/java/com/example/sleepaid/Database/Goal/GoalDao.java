package com.example.sleepaid.Database.Goal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM Goal ORDER BY goalId")
    Single<List<Goal>> getAll();

    @Query("SELECT * FROM Goal WHERE name IN (:goalNames) ORDER BY goalId")
    Single<List<Goal>> loadAllByNames(String[] goalNames);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Goal> goals);
}
