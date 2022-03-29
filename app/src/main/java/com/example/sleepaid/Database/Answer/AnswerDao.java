package com.example.sleepaid.Database.Answer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AnswerDao {
    @Query("SELECT * FROM Answer ORDER BY date, questionId")
    Single<List<Answer>> getAll();

    @Query("SELECT * FROM Answer WHERE Answer.questionId IN (:questionIds) ORDER BY date, questionId")
    Single<List<Answer>> loadAllByQuestionIds(int[] questionIds);

    @Query("SELECT value FROM Answer WHERE Answer.questionId IN (:questionIds) ORDER BY date, questionId")
    Single<List<String>> loadValuesByQuestionIds(int[] questionIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Answer> answers);
}
