package com.example.sleepaid;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AnswerDao {
    @Query("SELECT * FROM Answer")
    Single<List<Answer>> getAll();

    @Query("SELECT * FROM Answer WHERE answerId IN (:answerIds)")
    Single<List<Answer>> loadAllByIds(int[] answerIds);

    @Query("SELECT * FROM Answer WHERE optionId IN (:optionIds)")
    Single<List<Answer>> loadAllByOptionIds(int[] optionIds);

    @Query("SELECT * FROM Answer WHERE questionId IN (:questionIds)")
    Single<List<Answer>> loadAllByQuestionIds(int[] questionIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Answer> answers);
}
