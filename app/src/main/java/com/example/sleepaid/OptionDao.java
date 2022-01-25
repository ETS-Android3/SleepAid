package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface OptionDao {
    @Query("SELECT * FROM Option ORDER BY questionId, optionId")
    Single<List<Option>> getAll();

    @Query("SELECT * FROM Option WHERE optionId IN (:optionIds) ORDER BY optionId")
    Single<List<Option>> loadAllByIds(int[] optionIds);

    @Query("SELECT * FROM Option WHERE questionId = (:questionIds) ORDER BY optionId")
    Single<List<Option>> loadAllByQuestionIds(int[] questionIds);

    @Insert
    Completable insert(List<Option> options);
}
