package com.example.sleepaid.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface AnswerDao {
    @Query("SELECT * FROM Answer ORDER BY questionId, optionId")
    Single<List<Answer>> getAll();

    @Query("SELECT * FROM Answer WHERE answerId IN (:answerIds) ORDER BY answerId")
    Single<List<Answer>> loadAllByIds(int[] answerIds);

    @Query("SELECT * FROM Answer WHERE optionId IN (:optionIds) ORDER BY optionId")
    Single<List<Answer>> loadAllByOptionIds(int[] optionIds);

    @Query("SELECT value FROM Option INNER JOIN Answer ON Option.optionId = Answer.optionId WHERE Answer.questionId IN (:questionIds) ORDER BY Answer.questionId")
    Single<List<String>> loadValuesByQuestionIds(int[] questionIds);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Answer> answers);
}
