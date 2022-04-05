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

    @Query("SELECT * FROM Answer INNER JOIN Question ON Answer.questionId = Question.questionId WHERE Question.questionnaireId IN (:questionnaireIds) ORDER BY date, questionId")
    Single<List<Answer>> loadAllByQuestionnaireIds(int[] questionnaireIds);

    @Query("SELECT * FROM Answer INNER JOIN Question ON Answer.questionId = Question.questionId WHERE Question.questionnaireId IN (:questionnaireIds) AND Answer.date = :date ORDER BY date, questionId")
    Single<List<Answer>> loadAllByQuestionnaireIdsAndDate(int[] questionnaireIds, String date);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Completable insert(List<Answer> answers);
}
